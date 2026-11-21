import org.objectweb.asm.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Repairs the deliberately stripped access flags of the LPX jar so that classes used across packages
 * can be compiled against and decompiled with imports: adds ACC_PUBLIC to every top-level it/ytnoos class
 * that lacks it and removes ACC_SYNTHETIC from classes that are not lambdas/anonymous helpers.
 * Usage: java -cp <asm jars> tools/MakePublic.java <in.jar> <out.jar>
 */
public class MakePublic {
    public static void main(String[] a) throws Exception {
        int fixed = 0;
        try (JarFile in = new JarFile(a[0]); JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] data = in.getInputStream(je).readAllBytes();
                String n = je.getName();
                if (n.startsWith("it/ytnoos/") && n.endsWith(".class") && n.indexOf('$') < 0) {
                    ClassReader r = new ClassReader(data);
                    int acc = r.getAccess();
                    int na = (acc | Opcodes.ACC_PUBLIC) & ~Opcodes.ACC_SYNTHETIC;
                    if (na != acc) {
                        ClassWriter w = new ClassWriter(0);
                        final int fa = na;
                        r.accept(new ClassVisitor(Opcodes.ASM9, w) {
                            public void visit(int v, int acc2, String name, String sig, String sup, String[] ifs) {
                                super.visit(v, fa, name, sig, sup, ifs);
                            }
                        }, 0);
                        data = w.toByteArray();
                        fixed++;
                    }
                }
                out.putNextEntry(new JarEntry(n));
                out.write(data);
                out.closeEntry();
            }
        }
        System.out.println("top-level classes fixed: " + fixed);
    }
}
