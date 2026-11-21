import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.regex.Pattern;

/**
 * Step 2 of deobfuscation (bytecode level). Input: jar cleaned by Cleaner. Output: jar with
 *  - enum constants renamed to their real names (taken from the string passed to the constructor in <clinit>),
 *  - methods/fields with obfuscated short names made unique (the obfuscator overloads by return type, which Java source cannot express),
 *  - manual mappings from mappings/manual.map applied on top (these always win).
 * Writes the full effective mapping to <out>.map. Format:  C old new | F owner name desc new | M owner name desc new
 * Usage: java tools/Deobf.java <in.jar> <out.jar> [manual.map]
 */
public class Deobf {
    static final Pattern SHORT = Pattern.compile("^[A-Za-z0-9_$]{1,2}$");
    static Map<String, ClassNode> classes = new LinkedHashMap<>();
    static Map<String, String> clsMap = new LinkedHashMap<>();
    static Map<String, String> fieldMap = new LinkedHashMap<>();   // owner.name.desc
    static Map<String, String> methodMap = new LinkedHashMap<>();  // owner.name.desc
    static Map<String, String> bodies = new LinkedHashMap<>();    // enum-constant body class -> "parent CONST"
    static Map<String, byte[]> other = new LinkedHashMap<>();

    public static void main(String[] a) throws Exception {
        try (JarFile in = new JarFile(a[0])) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] d = in.getInputStream(je).readAllBytes();
                if (je.getName().endsWith(".class")) {
                    ClassNode cn = new ClassNode(); new ClassReader(d).accept(cn, 0); classes.put(cn.name, cn);
                } else if (!je.isDirectory()) other.put(je.getName(), d);
            }
        }
        Map<String, String> manualC = new HashMap<>(), manualF = new HashMap<>(), manualM = new HashMap<>();
        if (a.length > 2 && Files.exists(Path.of(a[2]))) {
            for (String l : Files.readAllLines(Path.of(a[2]))) {
                l = l.strip(); if (l.isEmpty() || l.startsWith("#")) continue;
                String[] p = l.split("\\s+");
                if (p[0].equals("C")) manualC.put(p[1], p[2]);
                else if (p[0].equals("F")) manualF.put(p[1] + "." + p[2] + "." + p[3], p[4]);
                else if (p[0].equals("M")) manualM.put(p[1] + "." + p[2] + "." + p[3], p[4]);
            }
        }
        enumNames();
        uniqueMethods();
        uniqueFields();
        // manual overrides
        clsMap.putAll(manualC);
        fieldMap.putAll(manualF);
        methodMap.putAll(manualM);
        // method manual mappings must apply to the whole override family: propagate to same name+desc in related classes
        propagateManualMethods(manualM);

        Remapper rm = new Remapper(Opcodes.ASM9) {
            @Override public String map(String n) { return clsMap.getOrDefault(n, n); }
            @Override public String mapFieldName(String o, String n, String d) { return resolve(fieldMap, o, n, d); }
            @Override public String mapMethodName(String o, String n, String d) { return resolve(methodMap, o, n, d); }
        };
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (ClassNode cn : classes.values()) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(new ClassRemapper(cw, rm));
                String nn = rm.map(cn.name);
                out.putNextEntry(new JarEntry(nn + ".class")); out.write(cw.toByteArray()); out.closeEntry();
            }
            for (var e : other.entrySet()) { out.putNextEntry(new JarEntry(e.getKey())); out.write(e.getValue()); out.closeEntry(); }
        }
        try (PrintWriter pw = new PrintWriter(a[1] + ".map")) {
            clsMap.forEach((k, v) -> pw.println("C " + k + " " + v));
            fieldMap.forEach((k, v) -> { int i = k.indexOf('.'), j = k.indexOf('.', i + 1); pw.println("F " + k.substring(0, i) + " " + k.substring(i + 1, j) + " " + k.substring(j + 1) + " " + v); });
            methodMap.forEach((k, v) -> { int i = k.indexOf('.'), j = k.indexOf('.', i + 1); pw.println("M " + k.substring(0, i) + " " + k.substring(i + 1, j) + " " + k.substring(j + 1) + " " + v); });
        }
        try (PrintWriter pw = new PrintWriter(a[1] + ".bodies")) { bodies.forEach((k, v) -> pw.println(rm.map(k) + " " + rm.map(v.split(" ")[0]) + " " + v.split(" ")[1])); }
        System.out.println("classes " + clsMap.size() + ", fields " + fieldMap.size() + ", methods " + methodMap.size());
    }

    /** Call sites may name a subclass as owner; look the member up in the owner and then in its ancestors. */
    static String resolve(Map<String, String> m, String owner, String n, String d) {
        String r = m.get(owner + "." + n + "." + d);
        if (r != null) return r;
        for (String a : ancestors(owner)) { r = m.get(a + "." + n + "." + d); if (r != null) return r; }
        return n;
    }

    // ---- hierarchy helpers
    static Set<String> related(String c) {
        Set<String> r = new HashSet<>(); Deque<String> q = new ArrayDeque<>(); q.add(c);
        while (!q.isEmpty()) { String x = q.poll(); if (!r.add(x)) continue; ClassNode n = classes.get(x); if (n == null) continue;
            if (n.superName != null) q.add(n.superName); if (n.interfaces != null) q.addAll(n.interfaces); }
        // and descendants
        boolean ch = true;
        while (ch) { ch = false; for (ClassNode n : classes.values()) { if (r.contains(n.name)) continue;
            if ((n.superName != null && r.contains(n.superName)) || (n.interfaces != null && n.interfaces.stream().anyMatch(r::contains))) { r.add(n.name); ch = true; } } }
        return r;
    }
    static Map<String, Set<String>> relCache = new HashMap<>();
    static Set<String> rel(String c) { return relCache.computeIfAbsent(c, Deobf::related); }

    // ---- enum constant names
    static void enumNames() {
        for (ClassNode cn : classes.values()) {
            if ((cn.access & Opcodes.ACC_ENUM) == 0) continue;
            for (MethodNode mn : cn.methods) {
                if (!mn.name.equals("<clinit>")) continue;
                AbstractInsnNode prevStr = null; String lastNew = null;
                for (AbstractInsnNode in = mn.instructions.getFirst(); in != null; in = in.getNext()) {
                    if (in instanceof TypeInsnNode t && t.getOpcode() == Opcodes.NEW) { prevStr = null; lastNew = t.desc; }
                    else if (in instanceof LdcInsnNode l && l.cst instanceof String && prevStr == null) prevStr = l;
                    else if (in instanceof FieldInsnNode f && f.getOpcode() == Opcodes.PUTSTATIC && f.owner.equals(cn.name) && prevStr != null) {
                        String s = (String) ((LdcInsnNode) prevStr).cst;
                        if (s.matches("[A-Za-z_][A-Za-z0-9_]*") && f.desc.equals("L" + cn.name + ";")) { fieldMap.put(cn.name + "." + f.name + "." + f.desc, s);
                            if (lastNew != null && !lastNew.equals(cn.name)) bodies.put(lastNew, cn.name + " " + s); }
                        prevStr = null;
                    }
                }
            }
        }
    }

    // ---- methods: families = same name+desc related by inheritance
    static Map<String, String> parent = new HashMap<>();
    static String find(String x) { parent.putIfAbsent(x, x); String p = parent.get(x); if (p.equals(x)) return x; String r = find(p); parent.put(x, r); return r; }
    static void union(String a, String b) { parent.put(find(a), find(b)); }

    static void uniqueMethods() {
        // key owner|name|desc ; union with same name+desc in any related class
        Map<String, List<String>> byNameDesc = new HashMap<>();
        for (ClassNode cn : classes.values()) for (MethodNode m : cn.methods) {
            if (m.name.startsWith("<")) continue;
            if ((m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0) { find(cn.name + "|" + m.name + "|" + m.desc); continue; }
            byNameDesc.computeIfAbsent(m.name + "|" + m.desc, k -> new ArrayList<>()).add(cn.name);
        }
        for (var e : byNameDesc.entrySet()) {
            List<String> owners = e.getValue();
            for (int i = 0; i < owners.size(); i++) for (int j = i + 1; j < owners.size(); j++)
                if (rel(owners.get(i)).contains(owners.get(j)) && inheritsLine(owners.get(i), owners.get(j)))
                    union(owners.get(i) + "|" + e.getKey(), owners.get(j) + "|" + e.getKey());
            for (String o : owners) find(o + "|" + e.getKey());
        }
        // families
        Map<String, List<String>> fam = new LinkedHashMap<>();
        for (String k : new ArrayList<>(parent.keySet())) fam.computeIfAbsent(find(k), x -> new ArrayList<>()).add(k);
        // assigned: class -> set of "name|paramdesc" used (declared or inherited through related)
        Map<String, Set<String>> used = new HashMap<>();
        // first reserve all non-renamed (long/lib) names
        List<List<String>> famList = new ArrayList<>(fam.values());
        famList.sort(Comparator.comparing(l -> l.stream().min(String::compareTo).get()));
        // pass 1: families whose name is not obfuscated (or touches non-jar supertypes) keep name
        List<List<String>> toRename = new ArrayList<>();
        for (List<String> f : famList) {
            String[] p = f.get(0).split("\\|", 3);
            boolean keep = !SHORT.matcher(p[1]).matches() || (classes.get(p[0]).access & Opcodes.ACC_ANNOTATION) != 0;
            if (keep) reserve(used, f, p[1]); else toRename.add(f);
        }
        for (List<String> f : toRename) {
            String[] p = f.get(0).split("\\|", 3);
            String base = p[1]; String pd = p[2].substring(0, p[2].indexOf(')') + 1);
            String nn = base; int n = 1;
            while (conflict(used, f, nn + "|" + pd)) nn = base + "_" + (n++);
            reserveWithPd(used, f, nn + "|" + pd);
            if (!nn.equals(base)) for (String k : f) { String[] q = k.split("\\|", 3); methodMap.put(q[0] + "." + q[1] + "." + q[2], nn); }
        }
    }
    static boolean inheritsLine(String a, String b) { // a and b are in the same super/sub chain (one is an ancestor of the other)
        return ancestors(a).contains(b) || ancestors(b).contains(a);
    }
    static Map<String, Set<String>> ancCache = new HashMap<>();
    static Set<String> ancestors(String c) {
        return ancCache.computeIfAbsent(c, k -> { Set<String> r = new HashSet<>(); Deque<String> q = new ArrayDeque<>(); q.add(k);
            while (!q.isEmpty()) { String x = q.poll(); ClassNode n = classes.get(x); if (n == null) continue;
                if (n.superName != null && r.add(n.superName)) q.add(n.superName);
                if (n.interfaces != null) for (String i : n.interfaces) if (r.add(i)) q.add(i); }
            return r; });
    }
    static void reserve(Map<String, Set<String>> used, List<String> f, String name) {
        for (String k : f) { String[] q = k.split("\\|", 3); String pd = q[2].substring(0, q[2].indexOf(')') + 1);
            for (String c : rel(q[0])) used.computeIfAbsent(c, x -> new HashSet<>()).add(name + "|" + pd); }
    }
    static void reserveWithPd(Map<String, Set<String>> used, List<String> f, String key) {
        for (String k : f) { String c0 = k.split("\\|", 3)[0]; for (String c : rel(c0)) used.computeIfAbsent(c, x -> new HashSet<>()).add(key); }
    }
    static boolean conflict(Map<String, Set<String>> used, List<String> f, String key) {
        for (String k : f) { String c0 = k.split("\\|", 3)[0]; for (String c : rel(c0)) if (used.getOrDefault(c, Set.of()).contains(key)) return true; }
        return false;
    }

    // ---- fields: unique name per declaring class (hiding across classes is legal)
    static void uniqueFields() {
        for (ClassNode cn : classes.values()) {
            Set<String> seen = new HashSet<>();
            for (FieldNode f : cn.fields) {
                if (SHORT.matcher(f.name).matches() && classes.containsKey("litebans/" + f.name) && !fieldMap.containsKey(cn.name + "." + f.name + "." + f.desc))
                    fieldMap.put(cn.name + "." + f.name + "." + f.desc, f.name + "_f");   // a field named like a class shadows the class in source
                String eff = fieldMap.getOrDefault(cn.name + "." + f.name + "." + f.desc, f.name);
                if (seen.add(eff)) continue;
                int n = 1; String nn = f.name + "_" + n;
                while (!seen.add(nn)) nn = f.name + "_" + (++n);
                fieldMap.put(cn.name + "." + f.name + "." + f.desc, nn);
            }
        }
    }

    static void propagateManualMethods(Map<String, String> manual) {
        for (var e : new ArrayList<>(manual.entrySet())) {
            String[] k = e.getKey().split("\\.", 3);
            for (String r : rel(k[0])) { ClassNode n = classes.get(r); if (n == null) continue;
                for (MethodNode m : n.methods) if (m.name.equals(k[1]) && m.desc.equals(k[2])) methodMap.put(r + "." + m.name + "." + m.desc, e.getValue()); }
        }
    }
}
