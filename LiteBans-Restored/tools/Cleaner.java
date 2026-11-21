import org.objectweb.asm.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/**
 * Step 1 of deobfuscation: repairs deliberately illegal access flags in the obfuscated jar so that
 * decompilers can read it (class ACC_STATIC, <clinit> ACC_FINAL, missing ACC_ENUM on enum constants).
 * Usage: java tools/Cleaner.java <in.jar> <out.jar>
 */
public class Cleaner {
    static final int VALID_CLASS = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_INTERFACE
            | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_ANNOTATION | Opcodes.ACC_ENUM;
    static int fixedClass, fixedClinit, fixedEnum, fixedBody, fixedDefault;
    static Set<String> enums = new HashSet<>();

    public static void main(String[] a) throws Exception {
        try (JarFile pre = new JarFile(a[0])) {   // pass 0: find enum classes
            for (Enumeration<JarEntry> e = pre.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                if (!je.getName().endsWith(".class")) continue;
                ClassReader r = new ClassReader(pre.getInputStream(je).readAllBytes());
                if ("java/lang/Enum".equals(r.getSuperName())) enums.add(r.getClassName());
            }
        }
        try (JarFile in = new JarFile(a[0]);
             JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] data = in.getInputStream(je).readAllBytes();
                if (je.getName().endsWith(".class")) data = clean(data);
                JarEntry ne = new JarEntry(je.getName());
                out.putNextEntry(ne);
                out.write(data);
                out.closeEntry();
            }
        }
        System.out.println("class flags fixed: " + fixedClass + ", <clinit> fixed: " + fixedClinit + ", enum constants: " + fixedEnum + ", enum constant bodies: " + fixedBody + ", $default helpers: " + fixedDefault);
    }

    static byte[] clean(byte[] b) {
        ClassReader cr = new ClassReader(b);
        ClassWriter cw = new ClassWriter(0);
        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
            String name; boolean isEnum; boolean body;
            @Override public void visit(int v, int acc, String n, String sig, String sup, String[] itf) {
                name = n; isEnum = "java/lang/Enum".equals(sup); body = enums.contains(sup);
                int na = acc & (VALID_CLASS | 0);
                if (isEnum || body) na |= Opcodes.ACC_ENUM;
                if (body) { na |= Opcodes.ACC_FINAL; }
                if (na != acc) fixedClass++;
                super.visit(v, na, n, sig, sup, itf);
            }
            @Override public void visitEnd() {
                if (body) { super.visitInnerClass(name, null, null, Opcodes.ACC_FINAL | Opcodes.ACC_ENUM); fixedBody++; }
                super.visitEnd();
            }
            @Override public FieldVisitor visitField(int acc, String n, String d, String sig, Object val) {
                // synthetic fields (switch maps, string tables, outer-instance refs) would be hidden by the decompiler; keep only enum $VALUES hidden
                if ((acc & Opcodes.ACC_SYNTHETIC) != 0 && !(isEnum && d.equals("[L" + name + ";"))) acc &= ~Opcodes.ACC_SYNTHETIC;
                if (isEnum && d.equals("L" + name + ";") && (acc & Opcodes.ACC_STATIC) != 0 && (acc & Opcodes.ACC_FINAL) != 0
                        && (acc & Opcodes.ACC_ENUM) == 0 && (acc & Opcodes.ACC_PUBLIC) != 0) { acc |= Opcodes.ACC_ENUM; fixedEnum++; }
                return super.visitField(acc, n, d, sig, val);
            }
            @Override public MethodVisitor visitMethod(int acc, String n, String d, String sig, String[] ex) {
                if (n.equals("<clinit>") && acc != Opcodes.ACC_STATIC) { acc = Opcodes.ACC_STATIC; fixedClinit++; }
                // synthetic-flagged ordinary methods (Kotlin $default helpers, enum valueOf, ...) would be hidden by the decompiler;
                // keep lambda bodies, accessors and bridges synthetic so they are still folded/ignored
                if ((acc & Opcodes.ACC_SYNTHETIC) != 0 && (acc & Opcodes.ACC_BRIDGE) == 0 && !n.startsWith("lambda$") && !n.startsWith("access$")) { acc &= ~Opcodes.ACC_SYNTHETIC; fixedDefault++; }
                // the obfuscator gives parameters duplicate names in MethodParameters (`long a, long a`): drop the attribute
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(acc, n, d, sig, ex)) {
                    @Override public void visitParameter(String pn, int pa) { }
                };
            }
        }, 0);
        return cw.toByteArray();
    }
}
