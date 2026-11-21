import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Step 1b: string decryption (static; nothing from the jar is executed).
 * LiteBans keeps its strings in per-class `static String[]` tables filled by `hl.a(encrypted, key)`. On the path that is actually taken
 * (the hash guards in hl.a are always true for int hashes) the decryptor is simply char' = (char)((char ^ key) & 0xFFFF).
 * We read the (encrypted, key) pairs from the table initializer methods, decrypt them here, and replace every
 * `GETSTATIC table; <int>; AALOAD` in the bytecode by the constant. The table field, its initializer method and the call to it
 * in <clinit> are removed afterwards.
 * Usage: java tools/Strings.java <in.jar> <out.jar> [dump.txt]
 */
public class Strings {
    public static void main(String[] a) throws Exception {
        Map<String, ClassNode> classes = new LinkedHashMap<>();
        Map<String, byte[]> other = new LinkedHashMap<>();
        try (JarFile in = new JarFile(a[0])) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] d = in.getInputStream(je).readAllBytes();
                if (je.getName().endsWith(".class")) { ClassNode cn = new ClassNode(); new ClassReader(d).accept(cn, 0); classes.put(cn.name, cn); }
                else if (!je.isDirectory()) other.put(je.getName(), d);
            }
        }
        Map<String, String[]> tables = new HashMap<>();   // owner.field -> decrypted values
        int pairs = 0;
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                Map<Integer, String> byIdx = new TreeMap<>();
                int size = -1;
                for (AbstractInsnNode in = mn.instructions.getFirst(); in != null; in = in.getNext()) {
                    if (in instanceof TypeInsnNode t && t.getOpcode() == Opcodes.ANEWARRAY && t.desc.equals("java/lang/String")) {
                        Integer n = intOf(in.getPrevious());
                        size = n == null ? -1 : n;
                        byIdx = new TreeMap<>();
                    } else if (in instanceof MethodInsnNode m && m.getOpcode() == Opcodes.INVOKESTATIC
                            && m.desc.equals("(Ljava/lang/Object;I)Ljava/lang/String;") && classes.containsKey(m.owner)) {
                        AbstractInsnNode k = in.getPrevious();
                        AbstractInsnNode sv = k == null ? null : k.getPrevious();
                        AbstractInsnNode ix = sv == null ? null : sv.getPrevious();
                        Integer key = intOf(k), idx = intOf(ix);
                        if (key != null && idx != null && sv instanceof LdcInsnNode l && l.cst instanceof String enc) {
                            StringBuilder sb = new StringBuilder();
                            for (char c : enc.toCharArray()) sb.append((char) ((c ^ key) & 0xFFFF));
                            byIdx.put(idx, sb.toString());
                            pairs++;
                        }
                    } else if (in instanceof FieldInsnNode f && f.getOpcode() == Opcodes.PUTSTATIC && f.desc.equals("[Ljava/lang/String;")
                            && size >= 0 && !byIdx.isEmpty()) {
                        String[] t = new String[size];
                        for (var e : byIdx.entrySet()) if (e.getKey() < t.length) t[e.getKey()] = e.getValue();
                        tables.put(f.owner + "." + f.name, t);
                        size = -1;
                        byIdx = new TreeMap<>();
                    }
                }
            }
        }
        // replace table reads by constants
        int replaced = 0;
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                InsnList il = mn.instructions;
                for (AbstractInsnNode in = il.getFirst(); in != null;) {
                    AbstractInsnNode nx = in.getNext();
                    if (in instanceof FieldInsnNode f && f.getOpcode() == Opcodes.GETSTATIC && tables.containsKey(f.owner + "." + f.name)) {
                        AbstractInsnNode idx = f.getNext();
                        AbstractInsnNode aal = idx == null ? null : idx.getNext();
                        Integer n = intOf(idx);
                        String[] t = tables.get(f.owner + "." + f.name);
                        if (n != null && aal != null && aal.getOpcode() == Opcodes.AALOAD && n >= 0 && n < t.length && t[n] != null) {
                            il.insertBefore(f, new LdcInsnNode(t[n]));
                            nx = aal.getNext();
                            il.remove(f); il.remove(idx); il.remove(aal);
                            replaced++;
                        }
                    }
                    in = nx;
                }
            }
        }
        // drop tables that nothing reads any more: initializer methods, calls to them, fields
        int removedM = 0, removedF = 0;
        for (ClassNode cn : classes.values()) {
            Set<String> tf = new HashSet<>();
            for (FieldNode f : cn.fields) if (tables.containsKey(cn.name + "." + f.name)) tf.add(f.name);
            if (tf.isEmpty()) continue;
            Set<String> initM = new HashSet<>();
            for (MethodNode mn : cn.methods)
                for (AbstractInsnNode in : mn.instructions)
                    if (in instanceof FieldInsnNode f && f.getOpcode() == Opcodes.PUTSTATIC && f.owner.equals(cn.name) && tf.contains(f.name)) initM.add(mn.name + mn.desc);
            boolean stillRead = false;
            for (MethodNode mn : cn.methods) {
                if (initM.contains(mn.name + mn.desc)) continue;
                for (AbstractInsnNode in : mn.instructions)
                    if (in instanceof FieldInsnNode f && f.getOpcode() == Opcodes.GETSTATIC && f.owner.equals(cn.name) && tf.contains(f.name)) stillRead = true;
            }
            if (stillRead) { System.err.println("table still read in " + cn.name); continue; }
            boolean clinitIsInit = initM.contains("<clinit>()V");
            if (clinitIsInit) { System.err.println("table initialised directly in <clinit>, kept: " + cn.name); continue; }
            cn.methods.removeIf(mn -> initM.contains(mn.name + mn.desc));
            removedM += initM.size();
            for (MethodNode mn : cn.methods)
                for (AbstractInsnNode in = mn.instructions.getFirst(); in != null;) {
                    AbstractInsnNode nx = in.getNext();
                    if (in instanceof MethodInsnNode m && m.getOpcode() == Opcodes.INVOKESTATIC && m.owner.equals(cn.name) && initM.contains(m.name + m.desc)) mn.instructions.remove(in);
                    in = nx;
                }
            int before = cn.fields.size();
            cn.fields.removeIf(f -> tf.contains(f.name));
            removedF += before - cn.fields.size();
        }
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (ClassNode cn : classes.values()) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                out.putNextEntry(new JarEntry(cn.name + ".class")); out.write(cw.toByteArray()); out.closeEntry();
            }
            for (var e : other.entrySet()) { out.putNextEntry(new JarEntry(e.getKey())); out.write(e.getValue()); out.closeEntry(); }
        }
        if (a.length > 2) try (PrintWriter pw = new PrintWriter(a[2])) {
            tables.forEach((k, v) -> { pw.println("## " + k); for (int i = 0; i < v.length; i++) pw.println(i + "\t" + v[i]); });
        }
        int unfilled = 0;
        for (String[] t : tables.values()) for (String s : t) if (s == null) unfilled++;
        System.out.println("pairs " + pairs + ", tables " + tables.size() + ", strings replaced " + replaced + ", init methods removed " + removedM
                + ", fields removed " + removedF + ", unfilled " + unfilled);
    }

    static Integer intOf(AbstractInsnNode n) {
        if (n == null) return null;
        int op = n.getOpcode();
        if (op >= Opcodes.ICONST_M1 && op <= Opcodes.ICONST_5) return op - Opcodes.ICONST_0;
        if (n instanceof IntInsnNode i && (op == Opcodes.BIPUSH || op == Opcodes.SIPUSH)) return i.operand;
        if (n instanceof LdcInsnNode l && l.cst instanceof Integer v) return v;
        return null;
    }
}
