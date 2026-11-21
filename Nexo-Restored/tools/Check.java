import java.io.*;
import java.lang.classfile.*;
import java.lang.classfile.constantpool.*;
import java.lang.constant.*;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class Check {
    static URLClassLoader loader;
    static Map<String, ClassModel> mine = new HashMap<>();

    public static void main(String[] a) throws Exception {
        String jar = a[0];
        List<URL> urls = new ArrayList<>();
        urls.add(Path.of(jar).toUri().toURL());
        for (String p : Files.readString(Path.of(a[1])).trim().split(":")) {
            if (p.endsWith("nexo-1.26-patched.jar")) continue;
            urls.add(Path.of(p).toUri().toURL());
        }
        loader = new URLClassLoader(urls.toArray(new URL[0]), null);
        try (JarFile jf = new JarFile(jar)) {
            for (var e : Collections.list(jf.entries())) {
                if (!e.getName().endsWith(".class") || e.getName().startsWith("META-INF")) continue;
                try (InputStream in = jf.getInputStream(e)) {
                    ClassModel cm = ClassFile.of().parse(in.readAllBytes());
                    mine.put(cm.thisClass().asInternalName(), cm);
                }
            }
        }
        Set<String> missing = new TreeSet<>();
        int checked = 0;
        for (var cm : mine.values()) {
            String self = cm.thisClass().asInternalName();
            for (PoolEntry pe : cm.constantPool()) {
                if (pe instanceof MemberRefEntry m) {
                    String owner = m.owner().asInternalName();
                    if (owner.startsWith("[")) continue;
                    String name = m.name().stringValue(), desc = m.type().stringValue();
                    checked++;
                    boolean isMethod = pe instanceof MethodRefEntry || pe instanceof InterfaceMethodRefEntry;
                    Boolean ok = resolve(owner, name, desc, isMethod, new HashSet<>());
                    if (ok == null) missing.add("[class not found] " + owner + "  (from " + self + ")");
                    else if (!ok) missing.add((isMethod ? "[method] " : "[field] ") + owner + "." + name + desc + "  (from " + self + ")");
                }
            }
        }
        System.out.println("checked refs: " + checked + ", classes: " + mine.size() + ", problems: " + missing.size());
        missing.forEach(System.out::println);
    }

    static Boolean resolve(String owner, String name, String desc, boolean isMethod, Set<String> seen) {
        if (!seen.add(owner)) return false;
        ClassModel cm = mine.get(owner);
        if (cm != null) {
            if (isMethod) { for (var m : cm.methods()) if (m.methodName().equalsString(name) && m.methodType().equalsString(desc)) return true; }
            else { for (var f : cm.fields()) if (f.fieldName().equalsString(name) && f.fieldType().equalsString(desc)) return true; }
            if (cm.superclass().isPresent()) { Boolean r = resolve(cm.superclass().get().asInternalName(), name, desc, isMethod, seen); if (r == null || r) return r; }
            for (var itf : cm.interfaces()) { Boolean r = resolve(itf.asInternalName(), name, desc, isMethod, seen); if (r == null || r) return r; }
            return false;
        }
        try {
            Class<?> c = Class.forName(owner.replace('/', '.'), false, loader);
            if (isMethod) {
                MethodType mt = MethodType.fromMethodDescriptorString(desc, loader);
                if (name.equals("<init>")) { for (Constructor<?> k : c.getDeclaredConstructors()) if (Arrays.equals(k.getParameterTypes(), mt.parameterArray())) return true; return false; }
                if (name.equals("<clinit>")) return true;
                for (Class<?> k = c; k != null; k = k.getSuperclass()) {
                    for (Method m : k.getDeclaredMethods()) if (m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), mt.parameterArray())) return true;
                }
                for (Method m : c.getMethods()) if (m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), mt.parameterArray())) return true;
                if (c.isInterface()) for (Method m : Object.class.getMethods()) if (m.getName().equals(name)) return true;
                if (c.isArray()) return true;
                return false;
            } else {
                for (Class<?> k = c; k != null; k = k.getSuperclass()) { try { k.getDeclaredField(name); return true; } catch (NoSuchFieldException ex) {} 
                    for (Class<?> i : k.getInterfaces()) { try { i.getField(name); return true; } catch (NoSuchFieldException ex) {} } }
                return false;
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        } catch (Throwable t) { return true; }
    }
}
