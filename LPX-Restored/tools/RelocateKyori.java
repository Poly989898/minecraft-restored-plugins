import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * The jar bundles Adventure/examination/option under net/kyori/..., but Paper 26.3 provides its own (newer, API-incompatible: e.g. no TranslatableComponent.args())
 * Adventure under the same names, and the parent classloader wins -> NoSuchMethodError in PacketEvents and LPX checks.
 * This moves every bundled net/kyori class to it/ytnoos/lpx/lib/kyori (the shade prefix the original used) and remaps all references
 * (class refs, descriptors, LDC strings, META-INF/services). References to kyori classes that are NOT bundled (e.g. ComponentLogger,
 * provided by Paper) are mapped to net/kyori/.
 * Usage: java -cp <asm>:<asm-commons> tools/RelocateKyori.java <unpacked jar dir>
 */
public class RelocateKyori {
    static final String OLD = "net/kyori/", NEW = "it/ytnoos/lpx/lib/kyori/";
    public static void main(String[] a) throws Exception {
        Path root = Path.of(a[0]);
        Set<String> bundled = new HashSet<>();
        try (Stream<Path> st = Files.walk(root.resolve("net/kyori"))) {
            st.filter(x -> x.toString().endsWith(".class")).forEach(x -> {
                String n = root.relativize(x).toString().replace(File.separatorChar, '/');
                bundled.add(n.substring(0, n.length() - 6));
            });
        }
        Remapper rm = new Remapper() {
            public String map(String t) {
                if (t.startsWith(NEW)) { String o = OLD + t.substring(NEW.length()); return bundled.contains(o) ? t : o; }
                if (t.startsWith(OLD) && bundled.contains(t)) return NEW + t.substring(OLD.length());
                return t;
            }
        };
        int cls = 0, str = 0;
        List<Path> files;
        try (Stream<Path> st = Files.walk(root)) { files = st.filter(x -> x.toString().endsWith(".class")).collect(Collectors.toList()); }
        for (Path p : files) {
            byte[] in = Files.readAllBytes(p);
            ClassReader r = new ClassReader(in);
            ClassWriter w = new ClassWriter(0);
            int[] n = {0};
            r.accept(new ClassRemapper(new ClassVisitor(Opcodes.ASM9, w) {
                public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                    return new MethodVisitor(Opcodes.ASM9, super.visitMethod(acc, name, desc, sig, ex)) {
                        public void visitLdcInsn(Object v) {
                            if (v instanceof String s) { String t = fixString(s, bundled); if (!t.equals(s)) { n[0]++; v = t; } }
                            super.visitLdcInsn(v);
                        }
                    };
                }
            }, rm), 0);
            byte[] out = w.toByteArray();
            String name = root.relativize(p).toString().replace(File.separatorChar, '/');
            String base = name.substring(0, name.length() - 6);
            boolean move = bundled.contains(base);
            if (!Arrays.equals(in, out) || move) {
                Path dst = move ? root.resolve(NEW + base.substring(OLD.length()) + ".class") : p;
                Files.createDirectories(dst.getParent());
                Files.write(dst, out);
                if (move) Files.delete(p);
                cls++; str += n[0];
            }
        }
        // META-INF/services: rename and rewrite content
        Path svc = root.resolve("META-INF/services");
        if (Files.isDirectory(svc)) {
            for (Path p : Files.list(svc).collect(Collectors.toList())) {
                String fn = p.getFileName().toString();
                if (!fn.startsWith("net.kyori.")) continue;
                String body = Files.readString(p).replace("net.kyori.", NEW.replace('/', '.'));
                Files.writeString(svc.resolve(NEW.replace('/', '.') + fn.substring("net.kyori.".length())), body);
                Files.delete(p);
            }
        }
        // remove empty net/kyori dirs
        try (Stream<Path> st = Files.walk(root.resolve("net/kyori"))) {
            for (Path p : st.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) if (Files.isDirectory(p)) try { Files.delete(p); } catch (DirectoryNotEmptyException e) { }
        } catch (NoSuchFileException e) { }
        System.out.println("relocated/changed classes: " + cls + ", strings: " + str);
    }
    static String fixString(String s, Set<String> bundled) {
        String d = "net.kyori.", nd = "it.ytnoos.lpx.lib.kyori.";
        if (s.startsWith(d)) { String c = s.substring(0, s.length()).replace('.', '/'); return bundled.contains(c) ? nd + s.substring(d.length()) : s; }
        if (s.startsWith(nd)) { String c = (d + s.substring(nd.length())).replace('.', '/'); return bundled.contains(c) ? s : d + s.substring(nd.length()); }
        return s;
    }
}
