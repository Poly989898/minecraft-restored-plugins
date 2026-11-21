import java.io.*;
import java.lang.classfile.*;
import java.lang.classfile.attribute.InnerClassInfo;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Compile-time shim for a Spigot-mapped server jar (never shipped).
 *
 * The legacy NMS modules were originally built against Paper-patched servers whose access transformers opened
 * private members and which added Paper-only members. Given the exact references found in the original compiled
 * module (see extract_refs.py), this tool
 *   1. opens (public, non-final) every class / field / method that is referenced but not accessible, and
 *   2. adds referenced members / classes that do not exist in the plain Spigot jar (Paper-only), as stubs.
 *
 * usage: java Shim.java in.jar out.jar refs.txt
 */
public class Shim {
    record Ref(String kind, String owner, String name, String desc) {}

    static final Set<String> KEYWORDS = Set.of("do", "if", "for", "int", "new", "try", "case", "else", "goto", "byte", "char", "long", "void",
        "enum", "this", "null", "true", "false", "class", "final", "short", "float", "double", "boolean", "break", "catch", "const", "super",
        "while", "throw", "throws", "import", "package", "return", "static", "switch", "public", "private", "protected", "abstract",
        "interface", "extends", "implements", "native", "volatile", "transient", "synchronized", "strictfp", "instanceof", "assert",
        "default", "continue", "finally");
    static final ClassFile CF = ClassFile.of();
    static final Map<String, byte[]> RAW = new LinkedHashMap<>();
    static final Map<String, ClassModel> MODELS = new HashMap<>();

    static ClassModel model(String n) {
        if (MODELS.containsKey(n)) return MODELS.get(n);
        byte[] b = RAW.get(n);
        ClassModel m = b == null ? null : CF.parse(b);
        MODELS.put(n, m);
        return m;
    }

    static int open(int f) {
        f &= ~(ClassFile.ACC_PRIVATE | ClassFile.ACC_PROTECTED | ClassFile.ACC_FINAL);
        return f | ClassFile.ACC_PUBLIC;
    }

    /** Where is the member declared? returns declaring class, "" if the chain leaves the jar (assume present), null if absent. */
    static String find(String owner, String name, String desc, boolean field, Set<String> seen) {
        if (!seen.add(owner)) return null;
        ClassModel cm = model(owner);
        if (cm == null) return (owner.startsWith("java/") || owner.startsWith("javax/")) ? null : "";
        if (field) {
            for (FieldModel f : cm.fields())
                if (f.fieldName().stringValue().equals(name) && f.fieldType().stringValue().equals(desc)) return owner;
        } else {
            for (MethodModel m : cm.methods())
                if (m.methodName().stringValue().equals(name) && m.methodType().stringValue().equals(desc)) return owner;
        }
        boolean external = false;
        if (cm.superclass().isPresent()) {
            String r = find(cm.superclass().get().asInternalName(), name, desc, field, seen);
            if (r == null) { /* keep looking */ } else if (r.isEmpty()) external = true; else return r;
        }
        for (ClassEntry i : cm.interfaces()) {
            String r = find(i.asInternalName(), name, desc, field, seen);
            if (r == null) { /* keep looking */ } else if (r.isEmpty()) external = true; else return r;
        }
        return external ? "" : null;
    }

    static int memberFlags(String cls, String name, String desc, boolean field) {
        ClassModel cm = model(cls);
        if (cm == null) return ClassFile.ACC_PUBLIC;
        if (field) {
            for (FieldModel f : cm.fields())
                if (f.fieldName().stringValue().equals(name) && f.fieldType().stringValue().equals(desc)) return f.flags().flagsMask();
        } else {
            for (MethodModel m : cm.methods())
                if (m.methodName().stringValue().equals(name) && m.methodType().stringValue().equals(desc)) return m.flags().flagsMask();
        }
        return ClassFile.ACC_PUBLIC;
    }

    static List<String> typesIn(String desc) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while ((i = desc.indexOf('L', i)) >= 0) {
            int j = desc.indexOf(';', i);
            if (j < 0) break;
            out.add(desc.substring(i + 1, j));
            i = j + 1;
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        Path in = Path.of(args[0]), out = Path.of(args[1]);
        List<Ref> refs = new ArrayList<>();
        for (String l : Files.readAllLines(Path.of(args[2]))) {
            String[] p = l.split("\t", -1);
            if (p.length >= 4) refs.add(new Ref(p[0], p[1], p[2], p[3]));
        }
        Map<String, byte[]> other = new LinkedHashMap<>();
        try (ZipFile zf = new ZipFile(in.toFile())) {
            for (Enumeration<? extends ZipEntry> en = zf.entries(); en.hasMoreElements(); ) {
                ZipEntry e = en.nextElement();
                if (e.isDirectory()) continue;
                byte[] d = zf.getInputStream(e).readAllBytes();
                if (e.getName().endsWith(".class")) RAW.put(e.getName().substring(0, e.getName().length() - 6), d);
                else if (!e.getName().matches("META-INF/.*\\.(SF|RSA|DSA)")) other.put(e.getName(), d);
            }
        }

        Set<String> openClasses = new HashSet<>();
        Map<String, Set<String>> openMembers = new HashMap<>();   // class -> {"F:name:desc" | "M:name:desc"}
        Map<String, Set<String>> dropFinal = new HashMap<>();
        Map<String, List<Ref>> add = new LinkedHashMap<>();       // class (existing or new) -> members to add
        Set<String> newClasses = new LinkedHashSet<>();
        Set<String> ifaceNew = new HashSet<>();
        int missingCtor = 0;

        for (Ref r : refs) {
            // classes mentioned anywhere (owner + descriptor types) must be accessible
            List<String> mentioned = new ArrayList<>();
            mentioned.add(r.owner);
            mentioned.addAll(typesIn(r.desc));
            for (String c : mentioned) {
                if (model(c) != null) openClasses.add(c);
            }
            if (r.kind.equals("C")) {
                if (model(r.owner) == null) { newClasses.add(r.owner); }
                continue;
            }
            if (r.kind.equals("X")) {
                // force-add: the class itself must declare the member (an abstract declaration in an interface does not count)
                ClassModel own = model(r.owner);
                boolean declared = own != null && own.methods().stream().anyMatch(m -> m.methodName().stringValue().equals(r.name) && m.methodType().stringValue().equals(r.desc));
                if (!declared) add.computeIfAbsent(r.owner, k -> new ArrayList<>()).add(new Ref("M", r.owner, r.name, r.desc));
                continue;
            }
            boolean isField = "FPGT".contains(r.kind);
            if (model(r.owner) == null) { newClasses.add(r.owner); if (r.kind.equals("I")) ifaceNew.add(r.owner); }
            String decl = find(r.owner, r.name, r.desc, isField, new HashSet<>());
            if (decl == null || (decl.isEmpty() && model(r.owner) == null)) {
                if (r.name.equals("<init>")) { missingCtor++; continue; }
                add.computeIfAbsent(r.owner, k -> new ArrayList<>()).add(r);
                for (String c : typesIn(r.desc)) if (model(c) == null && c.startsWith("net/minecraft/")) newClasses.add(c);
            } else if (!decl.isEmpty()) {
                openClasses.add(decl);
                // protected/public members stay as they are: widening a protected method breaks overrides in subclasses
                if ((memberFlags(decl, r.name, r.desc, isField) & (ClassFile.ACC_PROTECTED | ClassFile.ACC_PUBLIC)) == 0)
                    openMembers.computeIfAbsent(decl, k -> new HashSet<>()).add((isField ? "F:" : "M:") + r.name + ":" + r.desc);
            }
        }

        int changed = 0, created = 0, addedMembers = 0;
        Map<String, byte[]> result = new LinkedHashMap<>();
        for (Map.Entry<String, byte[]> e : RAW.entrySet()) {
            String cn = e.getKey();
            boolean touched = openClasses.contains(cn) || openMembers.containsKey(cn) || add.containsKey(cn);
            if (!touched) { result.put(cn, e.getValue()); continue; }
            ClassModel cm = model(cn);
            Set<String> om = openMembers.getOrDefault(cn, Set.of());
            List<Ref> toAdd = add.getOrDefault(cn, List.of());
            boolean isIface = (cm.flags().flagsMask() & ClassFile.ACC_INTERFACE) != 0;
            ClassTransform t = (cb, ce) -> {
                switch (ce) {
                    case AccessFlags af when openClasses.contains(cn) -> cb.withFlags(open(af.flagsMask()) & ~ClassFile.ACC_FINAL);
                    case FieldModel fm when om.contains("F:" + fm.fieldName().stringValue() + ":" + fm.fieldType().stringValue()) ->
                        cb.transformField(fm, (fb, fe) -> { if (fe instanceof AccessFlags a) fb.withFlags(open(a.flagsMask())); else fb.with(fe); });
                    case MethodModel mm when om.contains("M:" + mm.methodName().stringValue() + ":" + mm.methodType().stringValue()) ->
                        cb.transformMethod(mm, (mb, me) -> { if (me instanceof AccessFlags a) mb.withFlags(open(a.flagsMask())); else mb.with(me); });
                    case InnerClassesAttribute ica -> cb.with(InnerClassesAttribute.of(ica.classes().stream().map(ic ->
                        openClasses.contains(ic.innerClass().asInternalName())
                            ? InnerClassInfo.of(ic.innerClass(), ic.outerClass(), ic.innerName(), open(ic.flagsMask()) & ~ClassFile.ACC_FINAL)
                            : ic).toList()));
                    default -> cb.with(ce);
                }
            };
            ClassTransform adder = ClassTransform.endHandler(cb -> {
                for (Ref r : toAdd) addMember(cb, r, isIface);
            });
            result.put(cn, CF.transformClass(cm, t.andThen(adder)));
            changed++;
            addedMembers += toAdd.size();
        }
        for (String nc : newClasses) {
            if (RAW.containsKey(nc)) continue;
            List<Ref> toAdd = add.getOrDefault(nc, List.of());
            boolean iface = ifaceNew.contains(nc);
            byte[] b = CF.build(ClassDesc.ofInternalName(nc), cb -> {
                cb.withFlags(iface ? (ClassFile.ACC_PUBLIC | ClassFile.ACC_INTERFACE | ClassFile.ACC_ABSTRACT) : (ClassFile.ACC_PUBLIC | ClassFile.ACC_SUPER));
                cb.withSuperclass(ConstantDescs.CD_Object);
                for (Ref r : toAdd) addMember(cb, r, iface);
            });
            result.put(nc, b);
            created++;
            addedMembers += toAdd.size();
        }

        // methods whose obfuscated name is a Java keyword cannot be called from source: expose them as name_ (see Unrename.java)
        int renamed = 0;
        for (Map.Entry<String, byte[]> e : result.entrySet()) {
            ClassModel cm = CF.parse(e.getValue());
            boolean has = cm.methods().stream().anyMatch(m -> KEYWORDS.contains(m.methodName().stringValue()));
            if (!has) continue;
            e.setValue(CF.transformClass(cm, (cb, ce) -> {
                if (ce instanceof MethodModel mm && KEYWORDS.contains(mm.methodName().stringValue()))
                    cb.withMethod(mm.methodName().stringValue() + "_", mm.methodTypeSymbol(), mm.flags().flagsMask(), mb -> mm.forEach(mb::with));
                else cb.with(ce);
            }));
            renamed++;
        }
        System.out.println("renamed keyword-named methods in " + renamed + " classes");
        try (ZipOutputStream zo = new ZipOutputStream(Files.newOutputStream(out))) {
            for (Map.Entry<String, byte[]> e : result.entrySet()) {
                // the Spigot server jar bundles an old copy of the Bukkit API; the matching Paper API must win
                if (e.getKey().startsWith("org/bukkit/") && !e.getKey().startsWith("org/bukkit/craftbukkit/")) continue;
                zo.putNextEntry(new ZipEntry(e.getKey() + ".class"));
                zo.write(e.getValue());
                zo.closeEntry();
            }
            for (Map.Entry<String, byte[]> e : other.entrySet()) {
                zo.putNextEntry(new ZipEntry(e.getKey()));
                zo.write(e.getValue());
                zo.closeEntry();
            }
        }
        System.out.println("opened/changed " + changed + " classes, created " + created + " classes, added " + addedMembers
            + " missing members (" + missingCtor + " missing constructors skipped) -> " + out);
    }

    static void addMember(ClassBuilder cb, Ref r, boolean iface) {
        switch (r.kind) {
            case "F", "P" -> cb.withField(r.name, ClassDesc.ofDescriptor(r.desc), ClassFile.ACC_PUBLIC);
            case "G", "T" -> cb.withField(r.name, ClassDesc.ofDescriptor(r.desc), ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC);
            default -> {
                boolean st = r.kind.equals("S");
                int fl = ClassFile.ACC_PUBLIC | (st ? ClassFile.ACC_STATIC : 0) | (iface && !st ? ClassFile.ACC_ABSTRACT : ClassFile.ACC_NATIVE);
                cb.withMethod(r.name, MethodTypeDesc.ofDescriptor(r.desc), fl, mb -> {});
            }
        }
    }
}
