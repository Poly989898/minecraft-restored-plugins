import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/** Prints static fields (decrypted by the class's own static initializer) of one class of the LPX jar.
 *  Usage (one-off JVM, bytecode verification off because the original PlayerCheck does not verify):
 *  java -XX:+UnlockDiagnosticVMOptions -XX:-BytecodeVerificationRemote tools/ReadStatics.java <jar> <cp file> <class> <field>... */
public class ReadStatics {
    public static void main(String[] a) throws Exception {
        String[] cp = Files.readString(Path.of(a[1])).trim().split(":");
        URL[] urls = new URL[cp.length + 1];
        urls[0] = Path.of(a[0]).toUri().toURL();
        for (int i = 0; i < cp.length; i++) urls[i + 1] = Path.of(cp[i]).toUri().toURL();
        URLClassLoader cl = new URLClassLoader(urls, ReadStatics.class.getClassLoader());
        Class<?> c = Class.forName(a[2], true, cl);
        for (int i = 3; i < a.length; i++) {
            Field f = c.getDeclaredField(a[i]);
            f.setAccessible(true);
            Object v = f.get(null);
            System.out.println(a[i] + " = " + (v instanceof Object[] ? Arrays.toString((Object[]) v) : v));
        }
    }
}
