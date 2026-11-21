import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;

/** Decrypts the 6 string constants of it.ytnoos.lpx.utils.ItemUtils_w7 with the class's own decryptor (pure arithmetic + DES, no IO).
 *  Usage: java tools/DecryptW7.java <lpx jar> <classpath file> */
public class DecryptW7 {
    public static void main(String[] a) throws Exception {
        String[] cp = Files.readString(Path.of(a[1])).trim().split(":");
        URL[] urls = new URL[cp.length + 1];
        urls[0] = Path.of(a[0]).toUri().toURL();
        for (int i = 0; i < cp.length; i++) urls[i + 1] = Path.of(cp[i]).toUri().toURL();
        URLClassLoader cl = new URLClassLoader(urls, DecryptW7.class.getClassLoader());
        Class<?> w7 = Class.forName("it.ytnoos.lpx.utils.ItemUtils_w7", true, cl);
        Class<?> c = Class.forName("it.ytnoos.lpx._c", true, cl);
        Object k = c.getMethod("a", long.class, long.class, Object.class).invoke(null, 2574631836881896634L, -8107688862622377857L, w7);
        long var9 = (long) k.getClass().getMethod("a", long.class).invoke(k, 77689374345803L) ^ 32902030012521L;
        long var13 = 55871527293617L;
        Method dec = w7.getDeclaredMethod("a", int.class, long.class);
        dec.setAccessible(true);
        Object[][] sites = {
            {"check2#1", 24015, 5749452027482021889L ^ var13}, {"check2#2", 23962, 5859258496163192917L ^ var13},
            {"clinit#1", 13429, 2622613139759668370L ^ var9}, {"clinit#2", 14097, 4110105563125817335L ^ var9},
            {"clinit#3", 32430, 7759753062182835789L ^ var9}, {"clinit#4", 18733, 5880534611615605199L ^ var9}};
        for (Object[] s : sites) System.out.println(s[0] + " = " + dec.invoke(null, (int) s[1], (long) s[2]));
    }
}
