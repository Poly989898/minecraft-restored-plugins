import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
public class Verify {
    public static void main(String[] a) throws Exception {
        List<URL> urls = new ArrayList<>();
        urls.add(Path.of(a[0]).toUri().toURL());
        for (String p : Files.readString(Path.of(a[1])).trim().split(":")) { if (p.endsWith("nexo-1.26-patched.jar")) continue; urls.add(Path.of(p).toUri().toURL()); }
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
        Path root = Path.of(a[2]);
        List<String> names;
        try (Stream<Path> s = Files.walk(root)) { names = s.filter(p -> p.toString().endsWith(".class")).map(p -> root.relativize(p).toString().replace('/', '.').replaceAll("\\.class$", "")).sorted().collect(Collectors.toList()); }
        int ok = 0; Map<String,List<String>> bad = new TreeMap<>();
        for (String n : names) {
            try { Class<?> c = Class.forName(n, false, cl); c.getDeclaredMethods(); c.getDeclaredFields(); ok++; }
            catch (Throwable t) { bad.computeIfAbsent(t.getClass().getSimpleName() + ": " + String.valueOf(t.getMessage()).split("\n")[0], k -> new ArrayList<>()).add(n); }
        }
        System.out.println("verified ok: " + ok + " of " + names.size());
        bad.forEach((k, v) -> System.out.println(v.size() + "x " + k + "   e.g. " + v.get(0)));
    }
}
