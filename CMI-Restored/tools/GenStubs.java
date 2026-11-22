import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.*;

/**
 * Generates compile-only stubs for external classes that CMI references but that are not on the build classpath
 * (optional plugin APIs such as DiscordSRV, LuckPerms v2, Jobs, ...). The members are taken from the bytecode
 * references of the original CMI jar, so they match what the original was compiled against (erased types only).
 * Stubs are compiled from stubs/java and never packaged into the plugin jar.
 *
 * usage: java --class-path <asm jars> tools/GenStubs.java <original CMI jar> <classpath file> <stubs out dir>
 */
public class GenStubs {
    static class Member {
        String name, desc;
        boolean isStatic;
        Member(String n, String d, boolean s) { name = n; desc = d; isStatic = s; }
        @Override public boolean equals(Object o) { return o instanceof Member m && m.name.equals(name) && m.desc.equals(desc) && m.isStatic == isStatic; }
        @Override public int hashCode() { return Objects.hash(name, desc, isStatic); }
    }

    static class Owner {
        String internal;
        boolean itf;
        boolean annotation;
        Set<Member> methods = new LinkedHashSet<>();
        Set<Member> fields = new LinkedHashSet<>();
        Set<String> ctors = new LinkedHashSet<>();
    }

    static Map<String, Owner> owners = new TreeMap<>();
    static Set<String> known = new HashSet<>();

    public static void main(String[] a) throws Exception {
        Path jar = Path.of(a[0]);
        List<String> cpEntries = Arrays.stream(Files.readString(Path.of(a[1])).trim().split(";")).filter(s -> !s.isBlank()).collect(Collectors.toList());
        Path out = Path.of(a[2]);

        for (String entry : cpEntries) {
            Path p = Path.of(entry);
            if (!Files.exists(p) || !entry.endsWith(".jar")) continue;
            try (JarFile jf = new JarFile(p.toFile())) {
                jf.stream().filter(e -> e.getName().endsWith(".class")).forEach(e -> known.add(e.getName().replace(".class", "")));
            }
        }
        // classes of the original jar are the plugin itself
        try (JarFile jf = new JarFile(jar.toFile())) {
            jf.stream().filter(e -> e.getName().endsWith(".class")).forEach(e -> known.add(e.getName().replace(".class", "")));
            for (var en : jf.stream().filter(e -> e.getName().startsWith("com/Zrips/") && e.getName().endsWith(".class")).collect(Collectors.toList())) {
                try (InputStream in = jf.getInputStream(en)) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(in.readAllBytes()).accept(cn, ClassReader.SKIP_DEBUG);
                    collect(cn);
                }
            }
        }

        // candidates: referenced external classes that are not on the classpath
        Set<String> stub = new TreeSet<>();
        for (String name : owners.keySet()) if (wanted(name)) stub.add(name);
        // descriptors may mention further external classes
        Deque<String> work = new ArrayDeque<>(stub);
        while (!work.isEmpty()) {
            String n = work.poll();
            Owner o = owners.get(n);
            if (o == null) continue;
            for (Member m : o.methods) for (String t : typesIn(m.desc)) if (wanted(t) && stub.add(t)) work.add(t);
            for (Member m : o.fields) for (String t : typesIn(m.desc)) if (wanted(t) && stub.add(t)) work.add(t);
            for (String d : o.ctors) for (String t : typesIn(d)) if (wanted(t) && stub.add(t)) work.add(t);
        }

        // group nested (Outer$Inner) under the top-level class
        Map<String, List<String>> tops = new TreeMap<>();
        for (String n : stub) {
            String top = n.contains("$") ? n.substring(0, n.indexOf('$')) : n;
            tops.computeIfAbsent(top, k -> new ArrayList<>()).add(n);
        }
        int files = 0;
        for (var e : tops.entrySet()) {
            String top = e.getKey();
            Path file = out.resolve(top + ".java");
            Files.createDirectories(file.getParent());
            StringBuilder sb = new StringBuilder();
            String pkg = top.contains("/") ? top.substring(0, top.lastIndexOf('/')).replace('/', '.') : "";
            if (!pkg.isEmpty()) sb.append("package ").append(pkg).append(";\n\n");
            sb.append("// Compile-only stub generated from the original CMI bytecode. Not packaged. Not verified against the real plugin.\n");
            writeClass(sb, top, "", owners.get(top), e.getValue(), 0);
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
            files++;
        }
        System.out.println("external classes: " + stub.size() + ", stub files: " + files);
    }

    /** External = not known on the classpath, not a plugin class, not a JDK/NMS class, and not an array. */
    static boolean wanted(String internal) {
        if (internal.startsWith("[")) return false;
        if (known.contains(internal) || !owners.containsKey(internal)) return false;
        return !(internal.startsWith("java/") || internal.startsWith("javax/") || internal.startsWith("com/Zrips/")
                || internal.startsWith("net/minecraft/") || internal.startsWith("org/bukkit/") || internal.startsWith("com/mojang/"));
    }

    static void addAnnotation(org.objectweb.asm.tree.AnnotationNode an) {
        owner(Type.getType(an.desc).getInternalName()).annotation = true;
        if (an.values != null) {
            for (int i = 1; i < an.values.size(); i += 2) {
                Object v = an.values.get(i);
                String elemName = (String) an.values.get(i - 1);
                String elemDesc = null;
                if (v instanceof String[] e && e.length == 2 && e[0].startsWith("L")) {
                    elemDesc = e[0];
                } else if (v instanceof Integer) elemDesc = "I";
                else if (v instanceof Long) elemDesc = "J";
                else if (v instanceof Boolean) elemDesc = "Z";
                else if (v instanceof Double) elemDesc = "D";
                else if (v instanceof Float) elemDesc = "F";
                else if (v instanceof String) elemDesc = "Ljava/lang/String;";
                if (elemDesc != null) {
                    owner(Type.getType(an.desc).getInternalName()).methods.add(new Member(elemName, "()" + elemDesc, false));
                }
                if (v instanceof String[] e && e.length == 2 && e[0].startsWith("L")) {
                    Owner eo = owner(Type.getType(e[0]).getInternalName());
                    eo.fields.add(new Member(e[1], e[0], true));
                }
            }
        }
    }

    static void collect(ClassNode cn) {
    // annotation types and enum constants used in annotation values
    List<org.objectweb.asm.tree.AnnotationNode> anns = new ArrayList<>();
    if (cn.visibleAnnotations != null) anns.addAll(cn.visibleAnnotations);
    for (MethodNode mn0 : cn.methods) if (mn0.visibleAnnotations != null) anns.addAll(mn0.visibleAnnotations);
    for (FieldNode fn0 : cn.fields) if (fn0.visibleAnnotations != null) anns.addAll(fn0.visibleAnnotations);
    for (org.objectweb.asm.tree.AnnotationNode an : anns) addAnnotation(an);
        for (MethodNode mn : cn.methods) {
            for (String t : typesIn(mn.desc)) owner(t);
            if (mn.tryCatchBlocks != null) for (var tc : mn.tryCatchBlocks) if (tc.type != null) owner(tc.type);
            if (mn.instructions == null) continue;
            for (AbstractInsnNode ins : mn.instructions) {
                if (ins instanceof MethodInsnNode mi) {
                    Owner o = owner(mi.owner);
                    if (mi.getOpcode() == org.objectweb.asm.Opcodes.INVOKEINTERFACE) o.itf = true;
                    for (String t : typesIn(mi.desc)) owner(t);
                    if (mi.name.equals("<init>")) o.ctors.add(mi.desc);
                    else o.methods.add(new Member(mi.name, mi.desc, mi.getOpcode() == org.objectweb.asm.Opcodes.INVOKESTATIC));
                } else if (ins instanceof FieldInsnNode fi) {
                    Owner o = owner(fi.owner);
                    for (String t : typesIn(fi.desc)) owner(t);
                    o.fields.add(new Member(fi.name, fi.desc, fi.getOpcode() == org.objectweb.asm.Opcodes.GETSTATIC || fi.getOpcode() == org.objectweb.asm.Opcodes.PUTSTATIC));
                } else if (ins instanceof TypeInsnNode ti) {
                    owner(ti.desc);
                }
            }
        }
        for (FieldNode fn : cn.fields) for (String t : typesIn(fn.desc)) owner(t);
    }

    static Owner owner(String internal) {
        if (internal.startsWith("[")) {
            String el = Type.getType(internal).getElementType().getInternalName();
            return owner(el);
        }
        return owners.computeIfAbsent(internal, k -> { Owner o = new Owner(); o.internal = k; return o; });
    }

    /** Internal class names mentioned in a method or field descriptor. */
    static List<String> typesIn(String desc) {
        List<String> out = new ArrayList<>();
        if (desc.startsWith("(")) {
            for (Type t : Type.getArgumentTypes(desc)) addType(t, out);
            addType(Type.getReturnType(desc), out);
        } else {
            addType(Type.getType(desc), out);
        }
        return out;
    }

    static void addType(Type t, List<String> out) {
        if (t.getSort() == Type.OBJECT) out.add(t.getInternalName());
        if (t.getSort() == Type.ARRAY) addType(t.getElementType(), out);
    }

    static String javaType(String desc) {
        Type t = Type.getType(desc);
        return javaType(t);
    }

    static String javaType(Type t) {
        switch (t.getSort()) {
            case Type.VOID: return "void";
            case Type.BOOLEAN: return "boolean";
            case Type.BYTE: return "byte";
            case Type.CHAR: return "char";
            case Type.SHORT: return "short";
            case Type.INT: return "int";
            case Type.LONG: return "long";
            case Type.FLOAT: return "float";
            case Type.DOUBLE: return "double";
            case Type.ARRAY: return javaType(t.getElementType()) + "[]".repeat(t.getDimensions());
            default: return t.getInternalName().replace('/', '.').replace('$', '.');
        }
    }

    static String defaultValue(Type t) {
        switch (t.getSort()) {
            case Type.VOID: return "";
            case Type.BOOLEAN: return "false";
            case Type.CHAR: return "'\\0'";
            case Type.BYTE: case Type.SHORT: case Type.INT: return "0";
            case Type.LONG: return "0L";
            case Type.FLOAT: return "0F";
            case Type.DOUBLE: return "0D";
            default: return "null";
        }
    }

    static void writeClass(StringBuilder sb, String internal, String indent, Owner o, List<String> nested, int depth) {
        String simple = internal.substring(internal.lastIndexOf('/') + 1);
        if (simple.contains("$")) simple = simple.substring(simple.lastIndexOf('$') + 1);
        boolean itf = o != null && o.itf;
        boolean enumLike = o != null && !o.annotation && !itf && o.fields.stream().anyMatch(f -> f.isStatic && f.desc.equals("L" + internal + ";"));
        String kind = o != null && o.annotation ? "@interface" : enumLike ? "enum" : itf ? "interface" : "class";
        if (enumLike) {
            sb.append(indent).append("public enum ").append(simple).append(" {\n");
            String consts = o.fields.stream().filter(f -> f.isStatic && f.desc.equals("L" + internal + ";")).map(f -> f.name).distinct().collect(Collectors.joining(", "));
            sb.append(indent).append("    ").append(consts).append(";\n");
            sb.append(indent).append("}\n");
            return;
        }
        String mods = depth == 0 ? "public " : "public static ";
        sb.append(indent).append(mods).append(kind).append(' ').append(simple).append(" {\n");
        String in2 = indent + "    ";
        if (o != null) {
            for (Member f : o.fields) {
                Type t = Type.getType(f.desc);
                sb.append(in2).append("public ");
                if (f.isStatic) sb.append("static ");
                if (f.isStatic || itf) sb.append("final ");
                sb.append(javaType(t)).append(' ').append(f.name);
                if (f.isStatic || itf) sb.append(" = ").append(defaultValue(t));
                sb.append(";\n");
            }
            for (String d : o.ctors) {
                if (itf) continue;
                sb.append(in2).append("public ").append(simple).append('(').append(params(d)).append(") { throw new UnsupportedOperationException(); }\n");
            }
            for (Member m : o.methods) {
                Type rt = Type.getReturnType(m.desc);
                if (o.annotation) {
                    sb.append(in2).append(javaType(rt)).append(" ").append(m.name).append("()");
                    if (rt.getSort() != Type.OBJECT && rt.getSort() != Type.ARRAY) sb.append(" default ").append(defaultValue(rt));
                    sb.append(";\n");
                    continue;
                }
                sb.append(in2).append("public ");
                if (m.isStatic) sb.append("static ");
                sb.append(javaType(rt)).append(' ').append(m.name).append('(').append(params(m.desc)).append(')');
                if (m.isStatic || !itf) sb.append(" { throw new UnsupportedOperationException(); }\n");
                else sb.append(";\n");
            }
        }
        for (String n : nested) {
            if (n.equals(internal)) continue;
            String inner = n.substring(n.lastIndexOf('$') + 1);
            // nested stubs are generated under their outer class
            Owner io = owners.get(n);
            writeClass(sb, n, in2, io, List.of(), depth + 1);
        }
        sb.append(indent).append("}\n");
    }

    static String params(String desc) {
        Type[] args = Type.getArgumentTypes(desc);
        return IntStream.range(0, args.length).mapToObj(i -> javaType(args[i]) + " p" + i).collect(Collectors.joining(", "));
    }
}
