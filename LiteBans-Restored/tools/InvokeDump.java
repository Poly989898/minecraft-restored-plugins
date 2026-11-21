import org.objectweb.asm.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

/** Dumps, per class, every invoke instruction as `class<TAB>owner<TAB>name<TAB>desc` (for comparing the restored build against the original bytecode).
 * Usage: java tools/InvokeDump.java <jar-or-classes-dir> */
public class InvokeDump {
    public static void main(String[] a) throws Exception {
        Path p = Path.of(a[0]);
        if (Files.isDirectory(p)) {
            try (var s = Files.walk(p)) { for (Path f : (Iterable<Path>) s.filter(x -> x.toString().endsWith(".class"))::iterator) dump(Files.readAllBytes(f)); }
        } else try (JarFile j = new JarFile(a[0])) {
            for (Enumeration<JarEntry> e = j.entries(); e.hasMoreElements();) { JarEntry je = e.nextElement(); if (je.getName().endsWith(".class")) dump(j.getInputStream(je).readAllBytes()); }
        }
    }
    static void dump(byte[] b) {
        ClassReader cr = new ClassReader(b);
        String cn = cr.getClassName();
        cr.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override public MethodVisitor visitMethod(int acc, String mn, String md, String sig, String[] ex) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if (owner.startsWith("litebans/")) System.out.println(cn + "\t" + owner + "\t" + name + "\t" + desc);
                    }
                };
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }
}
