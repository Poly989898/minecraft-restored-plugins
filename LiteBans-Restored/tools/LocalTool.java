import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** usage: java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/LocalTool.java clean|rename <cp-file> <src-root> <stubs-root>
 *  clean : removes unused trivial locals (literal initializer, never read/written) and inlines alias locals (`T x = this|y;`)
 *  rename: gives decompiler-style locals/params (varN, a0...) meaningful names */
public class LocalTool {
    record Edit(int start, int end, String text) {}
    static final boolean POLISH = Boolean.getBoolean("polish");
    static final Pattern OBF = POLISH ? Pattern.compile("^\\w*(WithDefaults|Impl)\\d*$") : Pattern.compile("^(var\\d+\\w*|a\\d+)$");
    static final Set<String> KW = Set.of("abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile","while","var","record","yield","true","false","null");

    public static void main(String[] a) throws Exception {
        String mode = a[0];
        String cp = Files.readString(Path.of(a[1])).trim();
        Path root = Path.of(a[2]), stubs = Path.of(a[3]);
        Set<String> excluded = new HashSet<>();
        Path ex = Path.of("tools/fabric-files.txt");
        if (Files.exists(ex)) for (String l : Files.readAllLines(ex)) if (!l.isBlank()) excluded.add(l.trim());
        List<Path> files = new ArrayList<>(), mine = new ArrayList<>();
        try (var s = Files.walk(root)) { s.filter(p -> p.toString().endsWith(".java")).forEach(p -> { if (!p.toString().contains("/litebans/mixin/") && !excluded.contains(p.getFileName().toString())) { files.add(p); mine.add(p.toAbsolutePath().normalize()); } }); }
        try (var s = Files.walk(stubs)) { s.filter(p -> p.toString().endsWith(".java")).forEach(files::add); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final JavacTask task = (JavacTask) jc.getTask(null, fm, diags::add, List.of("-classpath", cp, "-proc:none", "-XDshould-stop.at=FLOW", "-XDshould-stop.ifError=FLOW"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        if (diags.stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR)) { System.err.println("source tree has compile errors"); System.exit(2); }
        Trees trees = Trees.instance(task);
        SourcePositions sp = trees.getSourcePositions();
        Set<Path> mineSet = new HashSet<>(mine);
        int total = 0;
        for (var u : units) {
            Path file = Path.of(u.getSourceFile().toUri()).toAbsolutePath().normalize();
            if (!mineSet.contains(file)) continue;
            String src = u.getSourceFile().getCharContent(true).toString();
            List<Edit> edits = new ArrayList<>();
            if (mode.equals("clean")) clean(u, src, trees, sp, edits); else rename(u, src, trees, sp, edits, task);
            if (edits.isEmpty()) continue;
            TreeMap<Integer, Edit> uniq = new TreeMap<>(Comparator.reverseOrder());
            for (Edit e : edits) uniq.put(e.start(), e);
            // drop overlapping edits
            List<Edit> fin = new ArrayList<>(); int lastStart = Integer.MAX_VALUE;
            for (Edit e : uniq.values()) { if (e.end() > lastStart) continue; fin.add(e); lastStart = e.start(); }
            StringBuilder sb = new StringBuilder(src);
            for (Edit e : fin) { sb.replace(e.start(), e.end(), e.text()); total++; }
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        }
        System.out.println(mode + " edits: " + total);
    }

    // ---------------------------------------------------------------- clean
    static void clean(CompilationUnitTree u, String src, Trees trees, SourcePositions sp, List<Edit> edits) {
        Map<Element, Integer> reads = new HashMap<>(), writes = new HashMap<>();
        Map<Element, VariableTree> decl = new HashMap<>();
        Map<Element, TreePath> declPath = new HashMap<>();
        Map<Element, List<TreePath>> uses = new HashMap<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void visitVariable(VariableTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && e.getKind() == ElementKind.LOCAL_VARIABLE) { decl.put(e, t); declPath.put(e, getCurrentPath()); }
                return super.visitVariable(t, v);
            }
            @Override public Void visitIdentifier(IdentifierTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && (e.getKind() == ElementKind.LOCAL_VARIABLE || e.getKind() == ElementKind.PARAMETER)) {
                    TreePath p = getCurrentPath(); Tree par = p.getParentPath().getLeaf();
                    boolean write = (par instanceof AssignmentTree at && at.getVariable() == t) || (par instanceof CompoundAssignmentTree ct && ct.getVariable() == t)
                        || (par instanceof UnaryTree ut && ut.getExpression() == t && switch (ut.getKind()) { case POSTFIX_INCREMENT, POSTFIX_DECREMENT, PREFIX_INCREMENT, PREFIX_DECREMENT -> true; default -> false; });
                    boolean plainAssign = par instanceof AssignmentTree at2 && at2.getVariable() == t;
                    if (write) writes.merge(e, 1, Integer::sum);
                    if (!plainAssign) reads.merge(e, 1, Integer::sum);
                    uses.computeIfAbsent(e, k -> new ArrayList<>()).add(p);
                }
                return super.visitIdentifier(t, v);
            }
        }.scan(u, null);
        Map<Element, String> aliasInit = new HashMap<>(); Map<Element, Element> aliasElem = new HashMap<>();
        for (var en : decl.entrySet()) {
            Element e = en.getKey(); VariableTree vt = en.getValue();
            TreePath dp = declPath.get(e);
            if (!(dp.getParentPath().getLeaf() instanceof BlockTree || dp.getParentPath().getLeaf() instanceof CaseTree)) continue;
            ExpressionTree init = vt.getInitializer();
            if (init == null) continue;
            int r = reads.getOrDefault(e, 0), w = writes.getOrDefault(e, 0);
            if (r == 0 && w == 0 && pure(init)) { remove(src, sp, u, vt, edits); continue; }
            if (w == 0 && init.getKind() == Tree.Kind.IDENTIFIER) {
                String in = init.toString();
                Element ie = trees.getElement(new TreePath(dp, init));
                boolean ok = in.equals("this") || (ie != null && (ie.getKind() == ElementKind.PARAMETER || ie.getKind() == ElementKind.LOCAL_VARIABLE) && writes.getOrDefault(ie, 0) == 0);
                if (!ok) continue;
                if (ie != null && in.equals("this") == false && !trees.getTypeMirror(dp).toString().equals(ie.asType().toString())) continue;
                if (in.equals("this") && !trees.getTypeMirror(dp).toString().equals(enclosingType(dp))) continue;
                ClassTree declCls = encl(dp);
                boolean safe = true;
                for (TreePath up : uses.getOrDefault(e, List.of())) if (encl(up) != declCls) { safe = false; break; }
                if (!safe) continue;
                aliasInit.put(e, in); if (ie != null && !in.equals("this")) aliasElem.put(e, ie);
            }
        }
        // drop aliases whose source is a local not itself reachable (declared in other scope) is impossible here; resolve chains
        for (var en : aliasInit.entrySet()) {
            Element e = en.getKey(); String text = en.getValue(); Element cur = aliasElem.get(e); int guard = 0;
            while (cur != null && aliasInit.containsKey(cur) && guard++ < 50) { text = aliasInit.get(cur); cur = aliasElem.get(cur); }
            for (TreePath up : uses.getOrDefault(e, List.of())) { int s0 = (int) sp.getStartPosition(u, up.getLeaf()), e0 = (int) sp.getEndPosition(u, up.getLeaf()); if (s0 >= 0 && e0 > s0) edits.add(new Edit(s0, e0, text)); }
            remove(src, sp, u, decl.get(e), edits);
        }
    }
    static String enclosingType(TreePath p) { ClassTree c = encl(p); return c == null ? "" : "litebans." + c.getSimpleName(); }
    static ClassTree encl(TreePath p) { for (TreePath q = p; q != null; q = q.getParentPath()) if (q.getLeaf() instanceof ClassTree c) return c; return null; }
    static boolean pure(ExpressionTree e) {
        return e instanceof LiteralTree || (e instanceof IdentifierTree) || (e instanceof MemberSelectTree ms && ms.getExpression() instanceof IdentifierTree) || (e instanceof ParenthesizedTree p && pure(p.getExpression())) || (e instanceof TypeCastTree c && pure(c.getExpression()))
            || (e instanceof UnaryTree ut && ut.getKind() == Tree.Kind.UNARY_MINUS && pure(ut.getExpression()));
    }
    static void remove(String src, SourcePositions sp, CompilationUnitTree u, Tree t, List<Edit> edits) {
        int s = (int) sp.getStartPosition(u, t), e = (int) sp.getEndPosition(u, t);
        if (s < 0 || e <= s) return;
        if (e < src.length() && src.charAt(e) != ';' && src.charAt(e - 1) != ';') { /* end excludes ; */ int k = e; while (k < src.length() && src.charAt(k) == ' ') k++; if (k < src.length() && src.charAt(k) == ';') e = k + 1; }
        int ls = s; while (ls > 0 && (src.charAt(ls - 1) == ' ' || src.charAt(ls - 1) == '\t')) ls--;
        int le = e; while (le < src.length() && (src.charAt(le) == ' ' || src.charAt(le) == '\t')) le++;
        if ((ls == 0 || src.charAt(ls - 1) == '\n') && le < src.length() && src.charAt(le) == '\n') { s = ls; e = le + 1; }
        edits.add(new Edit(s, e, ""));
    }

    // ---------------------------------------------------------------- rename
    static void rename(CompilationUnitTree u, String src, Trees trees, SourcePositions sp, List<Edit> edits, JavacTask task) {
        new TreePathScanner<Void, Void>() {
            @Override public Void visitMethod(MethodTree mt, Void v) { renameIn(getCurrentPath(), mt, u, src, trees, sp, edits); return null; }
            @Override public Void visitClass(ClassTree ct, Void v) {
                TreePath cp = getCurrentPath();
                for (Tree mem : ct.getMembers()) if (mem instanceof BlockTree || (mem instanceof VariableTree vt && vt.getInitializer() != null)) renameIn(new TreePath(cp, mem), null, u, src, trees, sp, edits);
                return super.visitClass(ct, v);
            }
        }.scan(u, null);
    }

    static void renameIn(TreePath mp, MethodTree mt, CompilationUnitTree u, String src, Trees trees, SourcePositions sp, List<Edit> edits) {
        Map<Element, VariableTree> decls = new LinkedHashMap<>();
        Map<Element, List<int[]>> refs = new HashMap<>();
        Set<String> taken = new HashSet<>();
        Map<Element, TreePath> dpaths = new HashMap<>();
        Map<Element, Integer> loopDepth = new HashMap<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void visitVariable(VariableTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && OBF.matcher(e.getSimpleName()).matches() && e.getKind() != ElementKind.FIELD) { decls.put(e, t); dpaths.put(e, getCurrentPath()); }
                else taken.add(t.getName().toString());
                return super.visitVariable(t, v);
            }
            @Override public Void visitIdentifier(IdentifierTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                String n = t.getName().toString();
                if (e != null && OBF.matcher(n).matches() && (e.getKind() == ElementKind.LOCAL_VARIABLE || e.getKind() == ElementKind.PARAMETER || e.getKind() == ElementKind.EXCEPTION_PARAMETER || e.getKind() == ElementKind.RESOURCE_VARIABLE || e.getKind() == ElementKind.BINDING_VARIABLE)) {
                    refs.computeIfAbsent(e, k -> new ArrayList<>()).add(new int[]{(int) sp.getStartPosition(u, t), (int) sp.getEndPosition(u, t)});
                } else taken.add(n);
                return super.visitIdentifier(t, v);
            }
            @Override public Void visitMemberSelect(MemberSelectTree t, Void v) { taken.add(t.getIdentifier().toString()); return super.visitMemberSelect(t, v); }
            @Override public Void visitMethodInvocation(MethodInvocationTree t, Void v) { return super.visitMethodInvocation(t, v); }
        }.scan(mp, null);
        // class-level names (fields/methods) must not be shadowed ambiguously: collect fields of enclosing classes
        for (TreePath q = mp; q != null; q = q.getParentPath()) if (q.getLeaf() instanceof ClassTree c) for (Tree m : c.getMembers()) if (m instanceof VariableTree f) taken.add(f.getName().toString());
        for (var en : decls.entrySet()) {
            Element e = en.getKey(); VariableTree vt = en.getValue();
            String base = suggest(e, vt, dpaths.get(e), trees);
            String name = base; int i = 2;
            while (taken.contains(name) || KW.contains(name)) name = base + (i++);
            taken.add(name);
            int pos = (int) ((JCTree) vt).pos;
            String old = e.getSimpleName().toString();
            if (pos >= 0 && pos + old.length() <= src.length() && src.startsWith(old, pos)) edits.add(new Edit(pos, pos + old.length(), name));
            for (int[] r : refs.getOrDefault(e, List.of())) if (r[0] >= 0 && src.startsWith(old, r[0]) && r[1] - r[0] == old.length()) edits.add(new Edit(r[0], r[1], name));
        }
    }

    static String suggest(Element e, VariableTree vt, TreePath dp, Trees trees) {
        if (POLISH) { String n = e.getSimpleName().toString().replaceAll("(WithDefaults|Impl)\\d*$", ""); return n.isEmpty() ? "value" : n; }
        TypeMirror tm = e.asType();
        String ty = typeName(tm);
        Tree par = dp.getParentPath().getLeaf();
        if (e.getKind() == ElementKind.EXCEPTION_PARAMETER) return "e";
        if (par instanceof ForLoopTree && tm.getKind() == TypeKind.INT) return "i";
        ExpressionTree init = vt.getInitializer();
        if (par instanceof EnhancedForLoopTree) { String s = singular(ty); return s; }
        if (init != null) { String s = clean(fromExpr(init)); if (s != null) return s; }
        return clean(ty) != null ? clean(ty) : "value";
    }
    static String fromExpr(ExpressionTree e) {
        if (e instanceof ParenthesizedTree p) return fromExpr(p.getExpression());
        if (e instanceof TypeCastTree c) { String s = fromExpr(c.getExpression()); return s != null ? s : decap(c.getType().toString()); }
        if (e instanceof NewClassTree n) return decap(n.getIdentifier().toString().replaceAll("<.*", ""));
        if (e instanceof MethodInvocationTree m) {
            ExpressionTree sel = m.getMethodSelect(); String n = sel instanceof MemberSelectTree ms ? ms.getIdentifier().toString() : sel.toString();
            Matcher mm = Pattern.compile("^(get|find|create|read|parse|build|load|new|fetch|resolve|to|as|make|open)([A-Z]\\w*)$").matcher(n);
            if (mm.matches()) return decap(mm.group(2));
            if (n.matches("^[a-z]\\w*") && n.length() > 3 && !GENERIC.contains(n) && !n.matches("^[a-z]{1,2}(_\\d+)?$")) return n;
        }
        if (e instanceof MemberSelectTree ms && ms.getIdentifier().length() > 2 && !ms.getIdentifier().toString().matches("^[a-zA-Z]{1,2}(_\\w)?$") && !ms.getIdentifier().contentEquals("class")) { String s = ms.getIdentifier().toString(); return s.equals(s.toUpperCase()) ? null : s; }
        return null;
    }
    static final Set<String> GENERIC = Set.of("size","length","next","get","values","valueOf","toString","equals","hashCode","iterator","isEmpty","hasNext","contains","remove","add","put","append","toArray","keySet","entrySet","stream","collect","apply","run","execute","getClass","name","ordinal","currentTimeMillis","nanoTime");
    static String clean(String s) {
        if (s == null) return null;
        s = s.replaceAll("<.*", ""); if (s.contains("[")) return null; if (s.contains(".")) s = s.substring(s.lastIndexOf('.') + 1);
        if (s.isEmpty() || !Character.isJavaIdentifierStart(s.charAt(0)) || !s.chars().allMatch(Character::isJavaIdentifierPart)) return null;
        s = decap(s); if (OBF.matcher(s).matches() || s.matches("^[a-zA-Z]{1,2}_\\w+$") || s.length() > 30) return null;
        return s;
    }
    static String singular(String s) { return s; }
    static String decap(String s) { if (s.isEmpty()) return s; int i = 0; while (i < s.length() && Character.isUpperCase(s.charAt(i))) i++; if (i <= 1) return s.substring(0, 1).toLowerCase() + s.substring(1); if (i == s.length()) return s.toLowerCase(); return s.substring(0, i - 1).toLowerCase() + s.substring(i - 1); }
    static String typeName(TypeMirror tm) {
        switch (tm.getKind()) {
            case BOOLEAN: return "flag"; case INT: return "value"; case LONG: return "value"; case BYTE: return "b"; case SHORT: return "value"; case CHAR: return "c"; case DOUBLE: case FLOAT: return "number";
            case ARRAY: { TypeMirror c = ((ArrayType) tm).getComponentType(); if (c.getKind() == TypeKind.BYTE) return "bytes"; if (c.getKind() == TypeKind.CHAR) return "chars"; if (c.getKind() == TypeKind.INT) return "ints"; String n = typeName(c); return n.endsWith("s") ? n + "Array" : n + "s"; }
            case DECLARED: {
                String n = ((DeclaredType) tm).asElement().getSimpleName().toString();
                return switch (n) { case "String" -> "text"; case "CharSequence" -> "message"; case "Object" -> "obj"; case "List", "ArrayList" -> "list"; case "Map", "HashMap" -> "map"; case "Set", "HashSet" -> "set"; case "Throwable" -> "error"; case "Integer", "Long" -> "boxed"; case "Boolean" -> "flag"; case "Function1" -> "callback"; case "Function0" -> "supplier"; case "StringBuilder" -> "builder"; case "File" -> "file"; case "UUID" -> "uuid"; default -> decap(n.replaceAll("\\d+$", "")); };
            }
            default: return "value";
        }
    }
}
