import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.*;

/**
 * Loads (without initialising) every com.ticxo class in a jar and forces method/field linking,
 * so the JVM verifier checks all bytecode. Run it on the original and on the rebuilt jar and
 * compare the failure lists: classes that fail only in the rebuilt jar are regressions.
 *
 * usage: java LoadAll.java <plugin.jar> [extra classpath jars ...]
 */
public class LoadAll {
    public static void main(String[] a) throws Exception {
        List<URL> urls = new ArrayList<>();
        for (String p : a) urls.add(new File(p).toURI().toURL());
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
        int ok = 0;
        Map<String, List<String>> fails = new TreeMap<>();
        try (JarFile jf = new JarFile(a[0])) {
            for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                String n = e.nextElement().getName();
                if (!n.endsWith(".class") || !n.startsWith("com/ticxo/")) continue;
                String cn = n.substring(0, n.length() - 6).replace('/', '.');
                try {
                    Class<?> c = Class.forName(cn, false, cl);
                    c.getDeclaredMethods(); c.getDeclaredFields(); c.getDeclaredConstructors();
                    ok++;
                } catch (Throwable t) {
                    String k = t.getClass().getSimpleName() + ": " + String.valueOf(t.getMessage()).split("\n")[0];
                    fails.computeIfAbsent(k, x -> new ArrayList<>()).add(cn);
                }
            }
        }
        System.out.println("loaded OK: " + ok);
        fails.forEach((k, v) -> System.out.println("FAIL(" + v.size() + ") " + k + "  e.g. " + v.get(0)));
    }
}
