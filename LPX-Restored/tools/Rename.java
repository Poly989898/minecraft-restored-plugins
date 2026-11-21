import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Symbol-aware renamer for the decompiled sources (javac Tree API, no regex over source text for references).
 * usage: java tools/Rename.java <mapping file> <classpath> <src dir> [--locals] [--dry]
 * mapping lines (# comments):
 *   class  <qualified.Name> <NewSimpleName>        (renames the file too)
 *   method [Owner#]<name>/<paramCount> <newName>   (owner = simple name of the declaring class or of a supertype it overrides; without owner: every method of that name and arity)
 *   field  <OwnerSimpleName>#<name> <newName>
 * --locals: renames compiler-style locals/parameters (var1, var2x, ...) to names derived from their types.
 */
public class Rename {
    record Edit(Path file, int start, int end, String text) {}
    static Map<String,String> classMap = new HashMap<>(), methodMap = new HashMap<>(), fieldMap = new HashMap<>();
    static List<Edit> edits = new ArrayList<>();
    static Map<String,CompilationUnitTree> units = new HashMap<>();
    static Trees trees; static Elements elements; static Types types;
    static boolean locals, dry;
    static final Pattern VARNAME = Pattern.compile("var\\d+x*\\d*");

    public static void main(String[] a) throws Exception {
        for (String l : Files.readAllLines(Path.of(a[0]))) {
            l = l.strip(); if (l.isEmpty() || l.startsWith("#")) continue;
            String[] p = l.split("\\s+");
            switch (p[0]) { case "class" -> classMap.put(p[1], p[2]); case "method" -> methodMap.put(p[1], p[2]); case "field" -> fieldMap.put(p[1], p[2]); default -> throw new RuntimeException(l); }
        }
        String cp = a[1]; Path src = Path.of(a[2]);
        for (int i = 3; i < a.length; i++) { if (a[i].equals("--locals")) locals = true; if (a[i].equals("--dry")) dry = true; }
        List<File> files; try (Stream<Path> st = Files.walk(src)) { files = st.filter(x -> x.toString().endsWith(".java")).map(Path::toFile).collect(Collectors.toList()); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-proc:none", "-nowarn", "-cp", cp, "-Xlint:none"), null, fm.getJavaFileObjectsFromFiles(files));
        Iterable<? extends CompilationUnitTree> cus = task.parse();
        task.analyze();
        trees = Trees.instance(task); elements = task.getElements(); types = task.getTypes();
        for (CompilationUnitTree cu : cus) new Scan(cu).scan(cu, null);
        // apply
        Map<Path,List<Edit>> by = edits.stream().distinct().collect(Collectors.groupingBy(Edit::file));
        int n = 0;
        for (var e : by.entrySet()) {
            String s = Files.readString(e.getKey());
            List<Edit> l = e.getValue().stream().sorted(Comparator.comparingInt(Edit::start).reversed()).collect(Collectors.toList());
            int last = Integer.MAX_VALUE;
            for (Edit x : l) { if (x.end() > last) { System.err.println("overlap in " + e.getKey() + " at " + x.start() + " " + x + " vs last " + last); continue; } s = s.substring(0, x.start()) + x.text() + s.substring(x.end()); last = x.start(); n++; }
            if (!dry) Files.writeString(e.getKey(), s);
        }
        // file renames
        for (var c : classMap.entrySet()) {
            String q = c.getKey(); Path f = src.resolve(q.replace('.', '/') + ".java");
            if (Files.exists(f) && !dry) Files.move(f, f.resolveSibling(c.getValue() + ".java"));
        }
        System.out.println("edits: " + n + (dry ? " (dry)" : ""));
    }

    static class Scan extends TreePathScanner<Void,Void> {
        final CompilationUnitTree cu; final Path file; final String text; final SourcePositions sp = trees.getSourcePositions();
        Map<Element,String> localNames = new HashMap<>();
        Scan(CompilationUnitTree cu) throws IOException { this.cu = cu; this.file = Path.of(cu.getSourceFile().toUri()); this.text = cu.getSourceFile().getCharContent(true).toString(); }

        String newName(Element e) {
            if (e == null) return null;
            switch (e.getKind()) {
                case CLASS, INTERFACE, ENUM, RECORD, ANNOTATION_TYPE: { return classMap.get(((TypeElement) e).getQualifiedName().toString()); }
                case METHOD: { ExecutableElement m = (ExecutableElement) e; if (!ours(m)) return null; return methodName(m); }
                case FIELD, ENUM_CONSTANT, RECORD_COMPONENT: {
                    Element o = e.getEnclosingElement();
                    String qualified = o.getEnclosingElement() instanceof TypeElement oo ? oo.getSimpleName() + "." + o.getSimpleName() : o.getSimpleName().toString();
                    String r = fieldMap.get(qualified + "#" + e.getSimpleName());
                    return r != null ? r : fieldMap.get(o.getSimpleName() + "#" + e.getSimpleName());
                }
                case LOCAL_VARIABLE, PARAMETER, EXCEPTION_PARAMETER, RESOURCE_VARIABLE, BINDING_VARIABLE: return localNames.get(e);
                case CONSTRUCTOR: { Element o = e.getEnclosingElement(); return classMap.get(((TypeElement) o).getQualifiedName().toString()); }
                default: return null;
            }
        }
        String expect;
        boolean oldOk(int s, int e) { return expect == null || text.substring(s, e).equals(expect); }
        String methodName(ExecutableElement m) {
            String key = m.getSimpleName() + "/" + m.getParameters().size();
            String r = methodMap.get(key);
            for (String owner : owners(m)) { String q = methodMap.get(owner + "#" + key); if (q != null) return q; }
            return r;
        }
        /** simple names of the declaring class and of every supertype method it overrides/implements */
        List<String> owners(ExecutableElement m) {
            List<String> res = new ArrayList<>();
            TypeElement te = (TypeElement) m.getEnclosingElement(); res.add(te.getSimpleName().toString());
            Deque<TypeMirror> q = new ArrayDeque<>(); q.add(te.asType()); Set<String> seen = new HashSet<>();
            while (!q.isEmpty()) {
                TypeMirror t = q.poll();
                for (TypeMirror sup : types.directSupertypes(t)) {
                    Element se = types.asElement(sup); if (!(se instanceof TypeElement ste) || !seen.add(ste.getQualifiedName().toString())) continue;
                    for (Element x : ste.getEnclosedElements()) if (x instanceof ExecutableElement xm && xm.getSimpleName().equals(m.getSimpleName()) && xm.getParameters().size() == m.getParameters().size()) res.add(ste.getSimpleName().toString());
                    q.add(sup);
                }
            }
            return res;
        }
        boolean ours(Element e) { Element t = e; while (t != null && !(t instanceof PackageElement)) t = t.getEnclosingElement(); return t != null && ((PackageElement) t).getQualifiedName().toString().startsWith("it.ytnoos"); }
        void edit(int s, int e, String t) { if (s >= 0 && e > s && e <= text.length() && !oldOk(s, e)) return; if (s >= 0 && e > s) { if (System.getenv("RDBG") != null && s == Integer.parseInt(System.getenv("RDBG"))) System.err.println("DBG " + t + " " + new Throwable().getStackTrace()[1].getMethodName() + " " + getCurrentPath().getLeaf().getKind()); edits.add(new Edit(file, s, e, t)); } }

        @Override public Void visitIdentifier(IdentifierTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (nn != null && t.getName().contentEquals(e.getSimpleName()) ) { int s = (int) sp.getStartPosition(cu, t); expect = t.getName().toString(); edit(s, s + t.getName().length(), nn); expect = null; }
            else if (nn != null && e.getKind() == ElementKind.CONSTRUCTOR) { }
            return super.visitIdentifier(t, v);
        }
        @Override public Void visitMemberSelect(MemberSelectTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (nn != null && t.getIdentifier().contentEquals(e.getSimpleName())) { int en = (int) sp.getEndPosition(cu, t); expect = t.getIdentifier().toString(); edit(en - t.getIdentifier().length(), en, nn); expect = null; }
            return super.visitMemberSelect(t, v);
        }
        @Override public Void visitMemberReference(MemberReferenceTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (nn != null && e.getKind() == ElementKind.METHOD) { int en = (int) sp.getEndPosition(cu, t); edit(en - t.getName().length(), en, nn); }
            return super.visitMemberReference(t, v);
        }
        @Override public Void visitClass(ClassTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (nn != null) {
                int s = (int) sp.getStartPosition(cu, t);
                Matcher m = Pattern.compile("\\b(class|interface|enum|record|@interface)\\s+(" + Pattern.quote(e.getSimpleName().toString()) + ")\\b").matcher(text); 
                if (m.find(s)) edit(m.start(2), m.end(2), nn);
            }
            return super.visitClass(t, v);
        }
        @Override public Void visitMethod(MethodTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (e != null && e.getKind() == ElementKind.CONSTRUCTOR && e.getEnclosingElement().getSimpleName().length() == 0) return null; // synthetic ctor of an anonymous class
            int s = (int) sp.getStartPosition(cu, t);
            if (nn != null && e != null) {
                String old = e.getKind() == ElementKind.CONSTRUCTOR ? e.getEnclosingElement().getSimpleName().toString() : e.getSimpleName().toString();
                Matcher m = Pattern.compile("(?<![\\w$])(" + Pattern.quote(old) + ")\\s*\\(").matcher(text);
                int from = t.getReturnType() != null ? (int) sp.getEndPosition(cu, t.getReturnType()) : s;
                if (from < 0) from = s;
                if (m.find(from)) edit(m.start(1), m.end(1), nn);
            }
            if (locals) assignLocals(t, getCurrentPath());
            return super.visitMethod(t, v);
        }
        @Override public Void visitVariable(VariableTree t, Void v) {
            Element e = trees.getElement(getCurrentPath()); String nn = newName(e);
            if (nn != null && e != null) {
                int from = t.getType() != null ? (int) sp.getEndPosition(cu, t.getType()) : (int) sp.getStartPosition(cu, t);
                if (from < 0) from = (int) sp.getStartPosition(cu, t);
                Matcher m = Pattern.compile("(?<![\\w$])(" + Pattern.quote(e.getSimpleName().toString()) + ")(?![\\w$])").matcher(text);
                int vs = (int) sp.getStartPosition(cu, t), ve = (int) sp.getEndPosition(cu, t);
                if (m.find(from) && m.start(1) >= vs && (ve < 0 || m.end(1) <= ve)) edit(m.start(1), m.end(1), nn);
            }
            return super.visitVariable(t, v);
        }

        // --- locals ---
        void assignLocals(MethodTree mt, TreePath path) {
            Set<String> used = new HashSet<>();
            List<VariableTree> vars = new ArrayList<>();
            new TreeScanner<Void,Void>() {
                public Void visitVariable(VariableTree t, Void x) { used.add(t.getName().toString()); if (VARNAME.matcher(t.getName()).matches()) vars.add(t); return super.visitVariable(t, x); }
            }.scan(mt, null);
            // names visible from the class: all members incl. inherited
            TreePath cp = path; while (cp != null && !(cp.getLeaf() instanceof ClassTree)) cp = cp.getParentPath();
            Set<String> reserved = new HashSet<>(used);
            if (cp != null) { Element ce = trees.getElement(cp); if (ce instanceof TypeElement te) { for (Element m : elements.getAllMembers(te)) reserved.add(m.getSimpleName().toString()); } }
            // outer classes' members too
            for (TreePath p = cp == null ? null : cp.getParentPath(); p != null; p = p.getParentPath()) if (p.getLeaf() instanceof ClassTree) { Element ce = trees.getElement(p); if (ce instanceof TypeElement te) for (Element m : elements.getAllMembers(te)) reserved.add(m.getSimpleName().toString()); }
            reserved.addAll(JAVA_KEYWORDS);
            for (VariableTree vt : vars) {
                TreePath vp = trees.getPath(cu, vt);
                Element e = trees.getElement(vp); if (e == null || localNames.containsKey(e)) continue;
                String base = baseName(e.asType());
                String name = base; int i = 2;
                while (reserved.contains(name)) name = base + (i++);
                reserved.add(name);
                localNames.put(e, name);
            }
        }
        // identifiers of locals inside this method are renamed by visitIdentifier/visitVariable via localNames
    }
    static final Set<String> JAVA_KEYWORDS = Set.of("abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile","while","var","record","yield");

    static String baseName(TypeMirror t) {
        switch (t.getKind()) {
            case INT: return "value"; case LONG: return "longValue"; case BOOLEAN: return "flag"; case DOUBLE: return "number"; case FLOAT: return "floatValue";
            case BYTE: return "b"; case SHORT: return "shortValue"; case CHAR: return "ch";
            case ARRAY: { TypeMirror c = ((ArrayType) t).getComponentType(); String b = baseName(c); return c.getKind().isPrimitive() ? b + "s" : (b.endsWith("s") ? b + "es" : b + "s"); }
            case DECLARED: { String n = ((DeclaredType) t).asElement().getSimpleName().toString(); return decap(n); }
            case TYPEVAR: return "value";
            default: return "value";
        }
    }
    static String decap(String n) {
        if (n.isEmpty()) return "value";
        if (n.length() > 1 && Character.isUpperCase(n.charAt(0)) && Character.isUpperCase(n.charAt(1))) { // acronym prefix e.g. UUID, NBTCompound
            int i = 0; while (i < n.length() && Character.isUpperCase(n.charAt(i))) i++;
            if (i == n.length()) return n.toLowerCase();
            return n.substring(0, i - 1).toLowerCase() + n.substring(i - 1);
        }
        String r = Character.toLowerCase(n.charAt(0)) + n.substring(1);
        return r;
    }
}
