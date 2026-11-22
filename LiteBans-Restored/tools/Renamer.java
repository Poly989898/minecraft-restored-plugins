import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Symbol-accurate renamer for the restored sources (javac attribution, nothing is guessed by text search).
 * Needs a compiling source tree. Usage (JDK 25):
 *   java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/Renamer.java <classpath-file> <src-root> <stubs-root> <mapping> [--dry]
 * Mapping lines (package litebans, blank lines and # comments allowed):
 *   C OldClass NewClass
 *   M OldClass.method(ParamType,ParamType) newName      (parameter types are simple names, erased; `()` for none)
 *   F OldClass.field newName
 * Renaming a method renames its whole override family. Renaming a class also renames its file and the names in files that are not
 * part of the build (tools/fabric-files.txt, mixin/) by identifier match.
 */
public class Renamer {
    record Edit(int start, int end, String text) {}

    public static void main(String[] a) throws Exception {
        String cp = Files.readString(Path.of(a[0])).trim();
        Path root = Path.of(a[1]), stubs = Path.of(a[2]);
        boolean dry = a.length > 4 && a[4].equals("--dry");
        List<String[]> maps = new ArrayList<>();
        for (String l : Files.readAllLines(Path.of(a[3]))) { l = l.strip(); if (!l.isEmpty() && !l.startsWith("#")) maps.add(l.split("\\s+")); }

        Set<String> excluded = new HashSet<>();
        Path ex = Path.of("tools/fabric-files.txt");
        if (Files.exists(ex)) for (String l : Files.readAllLines(ex)) if (!l.isBlank()) excluded.add(l.trim());
        List<Path> files = new ArrayList<>(), offBuild = new ArrayList<>();
        try (var s = Files.walk(root)) { s.filter(p -> p.toString().endsWith(".java")).forEach(p -> { if (p.toString().replace('\\', '/').contains("/litebans/mixin/") || excluded.contains(p.getFileName().toString())) offBuild.add(p); else files.add(p); }); }
        try (var s = Files.walk(stubs)) { s.filter(p -> p.toString().endsWith(".java")).forEach(files::add); }

        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final JavacTask task = (JavacTask) jc.getTask(null, fm, diags::add, List.of("-classpath", cp, "-proc:none", "-XDshould-stop.at=FLOW", "-XDshould-stop.ifError=FLOW"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        long errs = diags.stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR).count();
        if (errs > 0) { System.err.println("source tree has " + errs + " compile errors; renamer needs a clean tree"); System.exit(2); }
        Trees trees = Trees.instance(task);
        javax.lang.model.util.Elements els = task.getElements();

        // ---- resolve targets
        Map<Element, String> targets = new LinkedHashMap<>();
        Map<String, String> classRenames = new LinkedHashMap<>();
        Map<String, TypeElement> classes = new HashMap<>();
        for (var u : units) for (Tree t : u.getTypeDecls()) if (t instanceof ClassTree) { TypeElement te = (TypeElement) trees.getElement(TreePath.getPath(u, t)); classes.put(te.getSimpleName().toString(), te); }
        for (String[] m : maps) {
            switch (m[0]) {
                case "C" -> { TypeElement te = classes.get(m[1]); if (te == null) { System.err.println("no class " + m[1]); continue; } targets.put(te, m[2]); classRenames.put(m[1], m[2]); }
                case "F" -> {
                    String[] p = m[1].split("\\."); TypeElement te = classes.get(p[0]); if (te == null) { System.err.println("no class " + p[0]); continue; }
                    boolean ok = false;
                    for (Element e : te.getEnclosedElements()) if ((e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.ENUM_CONSTANT) && e.getSimpleName().contentEquals(p[1])) { targets.put(e, m[2]); ok = true; }
                    if (!ok) System.err.println("no field " + m[1]);
                }
                case "M" -> {
                    Matcher mm = Pattern.compile("(\\w+)\\.([\\w$]+)(?:\\((.*)\\))?").matcher(m[1]);
                    if (!mm.matches()) { System.err.println("bad mapping " + m[1]); continue; }
                    TypeElement te = classes.get(mm.group(1)); if (te == null) { System.err.println("no class " + mm.group(1)); continue; }
                    boolean ok = false;
                    for (Element e : te.getEnclosedElements()) {
                        if (!(e instanceof ExecutableElement ee) || e.getKind() != ElementKind.METHOD || !e.getSimpleName().contentEquals(mm.group(2))) continue;
                        if (mm.group(3) != null) {
                            String have = ee.getParameters().stream().map(pp -> task.getTypes().erasure(pp.asType()).toString().replaceAll("^.*\\.", "")).collect(java.util.stream.Collectors.joining(","));
                            if (!have.equals(mm.group(3).replace(" ", ""))) continue;
                        }
                        targets.put(e, m[2]); ok = true;
                    }
                    if (!ok) System.err.println("no method " + m[1]);
                }
                default -> System.err.println("bad mapping line " + String.join(" ", m));
            }
        }
        // method families: add every method that overrides / is overridden by a target
        Map<Element, String> family = new LinkedHashMap<>(targets);
        List<ExecutableElement> allMethods = new ArrayList<>();
        for (var u : units) new TreePathScanner<Void, Void>() {
            @Override public Void visitMethod(MethodTree mt, Void v) { Element e = trees.getElement(getCurrentPath()); if (e instanceof ExecutableElement ee && ee.getKind() == ElementKind.METHOD) allMethods.add(ee); return super.visitMethod(mt, v); }
        }.scan(u, null);
        boolean grew = true;
        while (grew) {
            grew = false;
            for (var e : new ArrayList<>(family.entrySet())) {
                if (!(e.getKey() instanceof ExecutableElement tm) || tm.getKind() != ElementKind.METHOD) continue;
                for (ExecutableElement m : allMethods) {
                    if (family.containsKey(m) || !m.getSimpleName().contentEquals(tm.getSimpleName())) continue;
                    TypeElement mc = (TypeElement) m.getEnclosingElement(), tc = (TypeElement) tm.getEnclosingElement();
                    if (els.overrides(m, tm, mc) || els.overrides(tm, m, tc) || sameSig(m, tm, task) && related(mc, tc, task)) { family.put(m, e.getValue()); grew = true; }
                }
            }
        }

        // unify override components: every method of one override family must get the same name (first target wins)
        {
            List<Element> order = new ArrayList<>(targets.keySet());
            Map<String, List<ExecutableElement>> byName = new HashMap<>();
            for (Element e : family.keySet()) if (e instanceof ExecutableElement ee && ee.getKind() == ElementKind.METHOD) byName.computeIfAbsent(ee.getSimpleName().toString(), k -> new ArrayList<>()).add(ee);
            int unified = 0;
            for (var grp : byName.values()) {
                int n = grp.size(); int[] parent = new int[n]; for (int i = 0; i < n; i++) parent[i] = i;
                for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) {
                    ExecutableElement a1 = grp.get(i), b1 = grp.get(j);
                    TypeElement ac = (TypeElement) a1.getEnclosingElement(), bc = (TypeElement) b1.getEnclosingElement();
                    if (els.overrides(a1, b1, ac) || els.overrides(b1, a1, bc) || sameSig(a1, b1, task) && related(ac, bc, task)) { int ri = i; while (parent[ri] != ri) ri = parent[ri]; int rj = j; while (parent[rj] != rj) rj = parent[rj]; parent[rj] = ri; }
                }
                Map<Integer, List<ExecutableElement>> comp = new HashMap<>();
                for (int i = 0; i < n; i++) { int r = i; while (parent[r] != r) r = parent[r]; comp.computeIfAbsent(r, k -> new ArrayList<>()).add(grp.get(i)); }
                for (var c : comp.values()) {
                    Set<String> names = new HashSet<>(); for (ExecutableElement m : c) names.add(family.get(m));
                    if (names.size() <= 1) continue;
                    String chosen = null; int best = Integer.MAX_VALUE;
                    for (ExecutableElement m : c) { int idx = order.indexOf(m); if (idx >= 0 && idx < best) { best = idx; chosen = family.get(m); } }
                    if (chosen == null) chosen = family.get(c.get(0));
                    for (ExecutableElement m : c) family.put(m, chosen);
                    unified++;
                }
            }
            if (unified > 0) System.err.println("unified override families: " + unified);
        }

        // ---- collect edits
        Map<Path, List<Edit>> edits = new HashMap<>();
        SourcePositions sp = trees.getSourcePositions();
        for (var u : units) {
            Path file = Path.of(u.getSourceFile().toUri());
            String src = u.getSourceFile().getCharContent(true).toString();
            List<Edit> list = edits.computeIfAbsent(file, k -> new ArrayList<>());
            new TreePathScanner<Void, Void>() {
                void add(Element e, int start, int end) {
                    String to = family.get(e); if (to == null) return;
                    // validate: the text at the range must be the old name (skips implicit constructors and other synthetic trees)
                    if (start >= 0 && end > start && end <= src.length() && src.substring(start, end).equals(e.getSimpleName().toString())) list.add(new Edit(start, end, to));
                }
                @Override public Void visitIdentifier(IdentifierTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) add(e, (int) sp.getStartPosition(u, t), (int) sp.getEndPosition(u, t));
                    // constructor / type usage resolves to the class itself
                    return super.visitIdentifier(t, v);
                }
                @Override public Void visitMemberSelect(MemberSelectTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) { int en = (int) sp.getEndPosition(u, t); add(e, en - t.getIdentifier().length(), en); }
                    return super.visitMemberSelect(t, v);
                }
                @Override public Void visitMemberReference(MemberReferenceTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) { int en = (int) sp.getEndPosition(u, t); add(e, en - t.getName().length(), en); }
                    return super.visitMemberReference(t, v);
                }
                @Override public Void visitMethod(MethodTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) {
                        int pos = ((JCTree) t).pos;
                        if (e.getKind() == ElementKind.CONSTRUCTOR) { Element cls = e.getEnclosingElement(); if (family.containsKey(cls)) add(cls, pos, pos + cls.getSimpleName().length()); }
                        else add(e, pos, pos + e.getSimpleName().length());
                    }
                    return super.visitMethod(t, v);
                }
                @Override public Void visitVariable(VariableTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) { int pos = ((JCTree) t).pos; add(e, pos, pos + e.getSimpleName().length()); }
                    return super.visitVariable(t, v);
                }
                @Override public Void visitClass(ClassTree t, Void v) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null && family.containsKey(e)) {
                        int st = (int) sp.getStartPosition(u, t);
                        Matcher m = Pattern.compile("\\b(class|interface|enum|record)\\s+" + Pattern.quote(e.getSimpleName().toString()) + "\\b").matcher(src);
                        if (m.find(st)) add(e, m.end() - e.getSimpleName().length(), m.end());
                    }
                    return super.visitClass(t, v);
                }
            }.scan(u, null);
        }

        // ---- apply
        int total = 0;
        for (var e : edits.entrySet()) {
            List<Edit> l = e.getValue(); if (l.isEmpty()) continue;
            Map<Integer, Edit> uniq = new TreeMap<>(Comparator.reverseOrder()); for (Edit x : l) uniq.put(x.start(), x);
            String src = Files.readString(e.getKey(), StandardCharsets.UTF_8); StringBuilder sb = new StringBuilder(src);
            for (Edit x : uniq.values()) { sb.replace(x.start(), x.end(), x.text()); total++; }
            if (!dry) Files.writeString(e.getKey(), sb.toString(), StandardCharsets.UTF_8);
        }
        // files outside the build: identifier-level class rename
        for (Path p : offBuild) {
            String s = Files.readString(p, StandardCharsets.UTF_8), o = s;
            for (var cr : classRenames.entrySet()) if (cr.getKey().length() >= 2) s = s.replaceAll("(?<![\\w$.])" + Pattern.quote(cr.getKey()) + "(?![\\w$])", Matcher.quoteReplacement(cr.getValue()));
            if (!s.equals(o)) { total++; if (!dry) Files.writeString(p, s, StandardCharsets.UTF_8); }
        }
        // rename class files and the fabric exclusion list
        if (!dry) {
            for (var cr : classRenames.entrySet()) {
                Path from = root.resolve("litebans/" + cr.getKey() + ".java"), to = root.resolve("litebans/" + cr.getValue() + ".java");
                if (Files.exists(from)) Files.move(from, to);
            }
            if (Files.exists(ex)) {
                List<String> l = new ArrayList<>();
                for (String x : Files.readAllLines(ex)) { String n = x.trim(); if (n.endsWith(".java") && classRenames.containsKey(n.substring(0, n.length() - 5))) n = classRenames.get(n.substring(0, n.length() - 5)) + ".java"; l.add(n); }
                Files.write(ex, l);
            }
        }
        System.out.println((dry ? "[dry] " : "") + "edits: " + total + ", targets: " + targets.size() + ", family: " + family.size());
    }

    static boolean sameSig(ExecutableElement a, ExecutableElement b, JavacTask t) {
        if (a.getParameters().size() != b.getParameters().size()) return false;
        for (int i = 0; i < a.getParameters().size(); i++)
            if (!t.getTypes().isSameType(t.getTypes().erasure(a.getParameters().get(i).asType()), t.getTypes().erasure(b.getParameters().get(i).asType()))) return false;
        return true;
    }
    static boolean related(TypeElement x, TypeElement y, JavacTask t) {
        return t.getTypes().isSubtype(t.getTypes().erasure(x.asType()), t.getTypes().erasure(y.asType())) || t.getTypes().isSubtype(t.getTypes().erasure(y.asType()), t.getTypes().erasure(x.asType()));
    }
}
