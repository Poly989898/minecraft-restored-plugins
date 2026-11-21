import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Original (non-recompiled) classes of the jar (PacketEvents, bStats, ...) contain calls into the obfuscator's helper classes
 * (e.g. it/ytnoos/lpx/_c). After src/ classes got readable names, these references must follow.
 * Remaps every "class <old> <new>" line of tools/mappings.txt in all classes of an unpacked jar.
 * Usage: java -cp <asm>:<asm-commons> tools/RemapClasses.java <mappings.txt> <unpacked jar dir>
 */
public class RemapClasses {
    public static void main(String[] a) throws Exception {
        Map<String,String> m = new HashMap<>();
        for (String l : Files.readAllLines(Path.of(a[0]))) {
            String[] p = l.strip().split("\\s+");
            if (p.length == 3 && p[0].equals("class")) { String o = p[1].replace('.', '/'); m.put(o, o.substring(0, o.lastIndexOf('/') + 1) + p[2]); }
        }
        Remapper rm = new Remapper() { public String map(String t) { return m.getOrDefault(t, t); } };
        Path root = Path.of(a[1]); int n = 0;
        List<Path> files; try (Stream<Path> st = Files.walk(root)) { files = st.filter(x -> x.toString().endsWith(".class")).collect(Collectors.toList()); }
        for (Path p : files) {
            byte[] in = Files.readAllBytes(p);
            ClassWriter w = new ClassWriter(0);
            new ClassReader(in).accept(new ClassRemapper(w, rm), 0);
            byte[] out = w.toByteArray();
            if (!Arrays.equals(in, out)) { Files.write(p, out); n++; }
        }
        System.out.println("classes with remapped references: " + n);
    }
}
