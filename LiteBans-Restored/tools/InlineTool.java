import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;
import javax.lang.model.element.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Inlines private static helper methods that are used by exactly one expression lambda `x -> helper(args)` into that lambda.
 *  usage: java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/InlineTool.java <cp-file> <src-root> <stubs-root> */
public class InlineTool {
    record Edit(int start, int end, String text) {}
    public static void main(String[] a) throws Exception {
        String cp = Files.readString(Path.of(a[0])).trim();
        Path root = Path.of(a[1]), stubs = Path.of(a[2]);
        Set<String> excluded = new HashSet<>();
        Path ex = Path.of("tools/fabric-files.txt");
        if (Files.exists(ex)) for (String l : Files.readAllLines(ex)) if (!l.isBlank()) excluded.add(l.trim());
        List<Path> files = new ArrayList<>(); Set<Path> mine = new HashSet<>();
        try (var s = Files.walk(root)) { s.filter(p -> p.toString().endsWith(".java")).forEach(p -> { if (!p.toString().contains("/litebans/mixin/") && !excluded.contains(p.getFileName().toString())) { files.add(p); mine.add(p.toAbsolutePath().normalize()); } }); }
        try (var s = Files.walk(stubs)) { s.filter(p -> p.toString().endsWith(".java")).forEach(files::add); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final JavacTask task = (JavacTask) jc.getTask(null, fm, diags::add, List.of("-classpath", cp, "-proc:none", "-XDshould-stop.at=FLOW", "-XDshould-stop.ifError=FLOW"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        if (diags.stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR)) { System.err.println("source tree has compile errors"); System.exit(2); }
        Trees trees = Trees.instance(task); SourcePositions sp = trees.getSourcePositions();
        int total = 0;
        for (var u : units) {
            Path file = Path.of(u.getSourceFile().toUri()).toAbsolutePath().normalize();
            if (!mine.contains(file)) continue;
            String src = u.getSourceFile().getCharContent(true).toString();
            List<Edit> edits = new ArrayList<>();
            process(u, src, trees, sp, edits);
            if (edits.isEmpty()) continue;
            TreeMap<Integer, Edit> uniq = new TreeMap<>(Comparator.reverseOrder());
            for (Edit e : edits) uniq.put(e.start(), e);
            List<Edit> fin = new ArrayList<>(); int last = Integer.MAX_VALUE;
            for (Edit e : uniq.values()) { if (e.end() > last) continue; fin.add(e); last = e.start(); }
            StringBuilder sb = new StringBuilder(src);
            for (Edit e : fin) { sb.replace(e.start(), e.end(), e.text()); total++; }
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        }
        System.out.println("inline edits: " + total);
    }

    static void process(CompilationUnitTree u, String src, Trees trees, SourcePositions sp, List<Edit> edits) {
        Map<Element, MethodTree> helpers = new HashMap<>(); Map<Element, TreePath> helperPath = new HashMap<>();
        Map<Element, Integer> refs = new HashMap<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void visitMethod(MethodTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && e.getKind() == ElementKind.METHOD && e.getModifiers().contains(Modifier.PRIVATE) && e.getModifiers().contains(Modifier.STATIC) && t.getBody() != null) { helpers.put(e, t); helperPath.put(e, getCurrentPath()); }
                return super.visitMethod(t, v);
            }
            @Override public Void visitIdentifier(IdentifierTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && e.getKind() == ElementKind.METHOD) refs.merge(e, 1, Integer::sum);
                return super.visitIdentifier(t, v);
            }
            @Override public Void visitMemberReference(MemberReferenceTree t, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null) refs.merge(e, 5, Integer::sum);
                return super.visitMemberReference(t, v);
            }
        }.scan(u, null);
        Set<Element> done = new HashSet<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void visitLambdaExpression(LambdaExpressionTree lt, Void v) {
                try { tryInline(lt, getCurrentPath()); } catch (RuntimeException ex) { /* skip */ }
                return super.visitLambdaExpression(lt, v);
            }
            void tryInline(LambdaExpressionTree lt, TreePath lp) {
                if (!(lt.getBody() instanceof MethodInvocationTree mi)) return;
                if (!(mi.getMethodSelect() instanceof IdentifierTree)) return;
                Element he = trees.getElement(new TreePath(lp, mi.getMethodSelect()));
                // path for identifier inside invocation
                TreePath miPath = new TreePath(lp, mi);
                he = trees.getElement(new TreePath(miPath, mi.getMethodSelect()));
                if (he == null || !helpers.containsKey(he) || refs.getOrDefault(he, 0) != 1 || done.contains(he)) return;
                MethodTree hm = helpers.get(he); TreePath hp = helperPath.get(he);
                // helper must not contain lambdas (handled in a later pass)
                boolean[] hasLambda = {false};
                new TreeScanner<Void, Void>() { @Override public Void visitLambdaExpression(LambdaExpressionTree t, Void x) { hasLambda[0] = true; return null; } }.scan(hm.getBody(), null);
                if (hasLambda[0]) return;
                // the helper must not be inside the lambda (recursion)
                for (TreePath q = lp; q != null; q = q.getParentPath()) if (q.getLeaf() == hm) return;
                List<? extends VariableTree> lparams = lt.getParameters();
                List<? extends ExpressionTree> args = mi.getArguments();
                List<? extends VariableTree> hparams = hm.getParameters();
                if (args.size() != hparams.size()) return;
                Set<String> lnames = new HashSet<>(); for (VariableTree p : lparams) lnames.add(p.getName().toString());
                // enclosing names
                Set<String> taken = new HashSet<>();
                for (TreePath q = lp; q != null; q = q.getParentPath()) {
                    Tree leaf = q.getLeaf();
                    if (leaf instanceof MethodTree m2) { for (VariableTree p : m2.getParameters()) taken.add(p.getName().toString()); collect(m2.getBody(), taken); break; }
                    if (leaf instanceof ClassTree) break;
                }
                for (TreePath q = lp; q != null; q = q.getParentPath()) if (q.getLeaf() instanceof ClassTree c) { for (Tree m : c.getMembers()) if (m instanceof VariableTree f) taken.add(f.getName().toString()); break; }
                // substitution per helper param
                Map<Element, String> subst = new HashMap<>(); List<String> preLines = new ArrayList<>(); Set<String> preNames = new HashSet<>();
                Map<Element, Integer> uses = new HashMap<>();
                String bodySrc = src.substring((int) sp.getStartPosition(u, hm.getBody()), (int) sp.getEndPosition(u, hm.getBody()));
                int bodyStart = (int) sp.getStartPosition(u, hm.getBody());
                List<int[]> idRanges = new ArrayList<>(); List<Element> idEls = new ArrayList<>();
                List<Element> declEls = new ArrayList<>(); List<int[]> declRanges = new ArrayList<>();
                new TreePathScanner<Void, Void>() {
                    @Override public Void visitIdentifier(IdentifierTree t, Void x) {
                        Element e = trees.getElement(getCurrentPath());
                        if (e != null && (e.getKind() == ElementKind.PARAMETER || e.getKind() == ElementKind.LOCAL_VARIABLE || e.getKind() == ElementKind.EXCEPTION_PARAMETER || e.getKind() == ElementKind.RESOURCE_VARIABLE)) {
                            idEls.add(e); idRanges.add(new int[]{(int) sp.getStartPosition(u, t) - bodyStart, (int) sp.getEndPosition(u, t) - bodyStart});
                        }
                        return super.visitIdentifier(t, x);
                    }
                    @Override public Void visitVariable(VariableTree t, Void x) {
                        Element e = trees.getElement(getCurrentPath());
                        if (e != null && e.getKind() != ElementKind.PARAMETER) { int pos = ((JCTree) t).pos - bodyStart; declEls.add(e); declRanges.add(new int[]{pos, pos + t.getName().length()}); }
                        return super.visitVariable(t, x);
                    }
                }.scan(new TreePath(hp, hm.getBody()), null);
                for (Element e : idEls) uses.merge(e, 1, Integer::sum);
                String lambdaParamUsed = null;
                for (int i = 0; i < args.size(); i++) {
                    ExpressionTree arg = args.get(i); Element pe = trees.getElement(new TreePath(hp, hparams.get(i)));
                    String text = src.substring((int) sp.getStartPosition(u, arg), (int) sp.getEndPosition(u, arg));
                    ExpressionTree core = arg; while (core instanceof ParenthesizedTree pt) core = pt.getExpression();
                    boolean cast = false; ExpressionTree inner = core;
                    if (core instanceof TypeCastTree ct) { cast = true; inner = ct.getExpression(); while (inner instanceof ParenthesizedTree pt2) inner = pt2.getExpression(); }
                    boolean isLambdaParam = inner instanceof IdentifierTree id && lnames.contains(id.getName().toString());
                    if (isLambdaParam) {
                        String ln = ((IdentifierTree) inner).getName().toString();
                        if (!cast) subst.put(pe, ln);
                        else {
                            if (uses.getOrDefault(pe, 0) <= 1) subst.put(pe, "(" + text + ")");
                            else { String nm = hparams.get(i).getName().toString(); int kk = 2; String base0 = nm; while (taken.contains(nm) || lnames.contains(nm) || preNames.contains(nm)) nm = base0 + (kk++); preNames.add(nm); preLines.add(typeText(src, sp, u, hparams.get(i)) + " " + nm + " = " + text + ";"); subst.put(pe, nm); }
                        }
                    } else {
                        if (!(inner instanceof IdentifierTree || inner instanceof LiteralTree || (inner instanceof MemberSelectTree ms && ms.getExpression() instanceof IdentifierTree))) return;
                        subst.put(pe, cast ? "(" + text + ")" : text);
                    }
                }
                // rename helper locals clashing with enclosing names or lambda params
                Map<Element, String> renames = new HashMap<>(); Set<String> used = new HashSet<>(taken); used.addAll(lnames);
                for (String pl : preLines) used.add(pl.split(" ")[1]);
                for (Element de : declEls) { String n = de.getSimpleName().toString(); String nn = n; int k = 2; while (used.contains(nn)) nn = n + (k++); used.add(nn); if (!nn.equals(n)) renames.put(de, nn); else used.add(n); }
                // build body text
                TreeMap<Integer, Object[]> reps = new TreeMap<>(Comparator.reverseOrder());
                for (int i = 0; i < idEls.size(); i++) { Element e = idEls.get(i); String r = subst.containsKey(e) ? subst.get(e) : renames.get(e); if (r != null) reps.put(idRanges.get(i)[0], new Object[]{idRanges.get(i)[1], r}); }
                for (int i = 0; i < declEls.size(); i++) { String r = renames.get(declEls.get(i)); if (r != null) reps.put(declRanges.get(i)[0], new Object[]{declRanges.get(i)[1], r}); }
                StringBuilder body = new StringBuilder(bodySrc);
                for (var en : reps.entrySet()) body.replace(en.getKey(), (Integer) en.getValue()[0], (String) en.getValue()[1]);
                String bt = body.toString();
                // expression form when the body is a single return
                List<? extends StatementTree> st = hm.getBody().getStatements();
                String lamParams = lparams.size() == 1 ? lparams.get(0).getName().toString() : "(" + String.join(", ", lparams.stream().map(p -> p.getName().toString()).toList()) + ")";
                String newLambda;
                if (preLines.isEmpty() && st.size() == 1 && st.get(0) instanceof ReturnTree rt && rt.getExpression() != null) {
                    int rs = (int) sp.getStartPosition(u, rt.getExpression()) - bodyStart, re = (int) sp.getEndPosition(u, rt.getExpression()) - bodyStart;
                    // positions shift because of replacements: recompute on the replaced text by substring between "return " and last ';'
                    String t = bt.strip(); t = t.substring(1, t.length() - 1).strip();
                    if (t.startsWith("return ") && t.endsWith(";")) { newLambda = lamParams + " -> " + t.substring(7, t.length() - 1).strip(); }
                    else return;
                } else {
                    StringBuilder b = new StringBuilder(lamParams + " -> {\n");
                    for (String pl : preLines) b.append(pl).append('\n');
                    String t = bt.strip(); t = t.substring(1, t.length() - 1);
                    b.append(t.strip()).append("\n}");
                    newLambda = b.toString();
                }
                int ls = (int) sp.getStartPosition(u, lt), le = (int) sp.getEndPosition(u, lt);
                edits.add(new Edit(ls, le, newLambda));
                int ms = (int) sp.getStartPosition(u, hm), me = (int) sp.getEndPosition(u, hm);
                while (me < src.length() && src.charAt(me) == '\n') me++;
                int lstart = ms; while (lstart > 0 && (src.charAt(lstart - 1) == ' ' || src.charAt(lstart - 1) == '\t')) lstart--;
                edits.add(new Edit(lstart, me, ""));
                done.add(he);
            }
            void collect(Tree body, Set<String> names) {
                if (body == null) return;
                new TreeScanner<Void, Void>() {
                    @Override public Void visitVariable(VariableTree t, Void x) { names.add(t.getName().toString()); return super.visitVariable(t, x); }
                }.scan(body, null);
            }
        }.scan(u, null);
    }
    static String typeText(String src, SourcePositions sp, CompilationUnitTree u, VariableTree v) {
        Tree t = v.getType(); return src.substring((int) sp.getStartPosition(u, t), (int) sp.getEndPosition(u, t));
    }
}
