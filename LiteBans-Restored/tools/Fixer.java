import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Source-level fixer driven by javac diagnostics and attributed types:
 *  1. `cannot find symbol: variable varN` (declaration lost by the decompiler) -> declares varN at the top of the method,
 *     type taken from the first assignment's right-hand side.
 *  2. invalid method references on raw types (generic signatures were stripped by the obfuscator) -> explicit lambda with casts.
 * Usage: java tools/Fixer.java <classpath-file> <source-root> [stubs-root]
 */
public class Fixer {
    record Edit(int start, int end, String text) {}

    public static void main(String[] a) throws Exception {
        String cp = Files.readString(Path.of(a[0])).trim();
        Path root = Path.of(a[1]);
        for (int round = 1; round <= 6; round++) {
            int n = pass(cp, root, a.length > 2 ? Path.of(a[2]) : null);
            System.out.println("round " + round + ": " + n + " edits");
            if (n == 0) break;
        }
    }

    static int pass(String cp, Path root, Path stubs) throws Exception {
        List<Path> files = new ArrayList<>();
        for (Path r : stubs == null ? List.of(root) : List.of(root, stubs))
            try (var s = Files.walk(r)) { s.filter(p -> p.toString().endsWith(".java")).forEach(files::add); }
        Set<String> excluded = new HashSet<>();
        Path ex = Path.of("tools/fabric-files.txt");
        if (Files.exists(ex)) for (String l : Files.readAllLines(ex)) if (!l.isBlank()) excluded.add(l.trim());
        files.removeIf(p -> p.toString().contains("/litebans/mixin/") || excluded.contains(p.getFileName().toString()));
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final JavacTask task = (JavacTask) jc.getTask(null, fm, diags::add,
                List.of("-classpath", cp, "-proc:none", "-Xmaxerrs", "1000000", "-XDshould-stop.at=FLOW", "-XDshould-stop.ifError=FLOW"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        Trees trees = Trees.instance(task);
        SourcePositions sp = trees.getSourcePositions();
        Map<String, List<Edit>> edits = new HashMap<>();
        Map<String, CompilationUnitTree> byFile = new HashMap<>();
        for (CompilationUnitTree u : units) byFile.put(u.getSourceFile().toUri().getPath(), u);

        Map<String, Set<String>> wantedVars = new HashMap<>();   // file -> var names with cant.resolve
        Map<String, Set<Long>> mrefErr = new HashMap<>();
        record Mis(long start, long end, String from, String to) {}
        record Missing(long pos, String method, String params, String owner) {}
        Map<String, List<Missing>> missing = new HashMap<>();
        record Unrep(long pos, String type) {}
        Map<String, List<Unrep>> unrep = new HashMap<>();
        Map<String, List<Mis>> mis = new HashMap<>();
        for (var d : diags) {
            if (d.getKind() != Diagnostic.Kind.ERROR || d.getSource() == null) continue;
            String f = d.getSource().toUri().getPath();
            String msg = d.getMessage(Locale.ROOT);
            if (System.getenv("FIXER_DEBUG") != null && msg.contains("does not override abstract")) System.err.println("DIAG " + d.getCode() + " :: " + msg.replace("\n", " | "));
            if (d.getCode().equals("compiler.err.cant.resolve.location") || d.getCode().equals("compiler.err.cant.resolve")) {
                Matcher m = Pattern.compile("symbol:\\s+variable (var\\d+)").matcher(msg);
                if (m.find()) wantedVars.computeIfAbsent(f, k -> new HashSet<>()).add(m.group(1) + "@" + d.getPosition());
            }
            if (d.getCode().equals("compiler.err.unreported.exception.need.to.catch.or.throw") && System.getenv("FIXER_THROWS") != null && f.endsWith("/" + System.getenv("FIXER_THROWS") + ".java")) {
                Matcher mm = Pattern.compile("unreported exception ([\\w.$]+);").matcher(msg);
                if (mm.find()) unrep.computeIfAbsent(f, k -> new ArrayList<>()).add(new Unrep(d.getPosition(), mm.group(1)));
            }
            if (d.getCode().equals("compiler.err.does.not.override.abstract")) {
                Matcher mm = Pattern.compile("does not override abstract method (\\w+)\\((.*)\\) in ([\\w.]+)").matcher(msg.replaceAll("@[\\w.]+\\s*", ""));
                if (mm.find()) missing.computeIfAbsent(f, k -> new ArrayList<>()).add(new Missing(d.getPosition(), mm.group(1), mm.group(2), mm.group(3).replaceAll("^.*\\.", "")));
            }
            if (d.getCode().equals("compiler.err.prob.found.req")) {
                Matcher mm = Pattern.compile("incompatible types: (\\S+) cannot be converted to (\\S+)").matcher(msg);
                if (mm.find() && d.getStartPosition() >= 0 && d.getEndPosition() >= d.getStartPosition()) mis.computeIfAbsent(f, k -> new ArrayList<>()).add(new Mis(d.getStartPosition(), d.getEndPosition(), norm(mm.group(1)), norm(mm.group(2))));
            }
            if (msg.contains("method reference") || d.getCode().contains("invalid.mref") || d.getCode().contains("prob.found.req") || d.getCode().contains("cant.apply") || d.getCode().contains("ref.ambiguous"))
                mrefErr.computeIfAbsent(f, k -> new HashSet<>()).add(d.getPosition());
        }
        int total = 0;
        for (var e : byFile.entrySet()) {
            String f = e.getKey(); CompilationUnitTree cu = e.getValue();
            String src = cu.getSourceFile().getCharContent(true).toString();
            List<Edit> eds = new ArrayList<>();
            Set<String> vars = wantedVars.getOrDefault(f, Set.of());
            Set<Long> mpos = mrefErr.getOrDefault(f, Set.of());
            List<Mis> ms = mis.getOrDefault(f, List.of());
            List<Missing> mi0 = missing.getOrDefault(f, List.of());
            List<Unrep> ur0 = unrep.getOrDefault(f, List.of());
            if (vars.isEmpty() && mpos.isEmpty() && ms.isEmpty() && mi0.isEmpty() && ur0.isEmpty()) continue;
            new TreePathScanner<Void, Void>() {
                MethodTree curMethod;
                Set<String> declared = new HashSet<>();
                @Override public Void visitClass(ClassTree ct, Void v) {
                    long cs = sp.getStartPosition(cu, ct), ce = sp.getEndPosition(cu, ct);
                    TypeElement te = (TypeElement) trees.getElement(getCurrentPath());
                    StringBuilder stubs = new StringBuilder();
                    for (Missing mm : mi0) {
                        if (mm.pos() < cs || mm.pos() > ce) continue;
                        if (te == null) continue;
                        // only the declaration header of this class (not of nested classes)
                        for (TypeMirror sup : task.getTypes().directSupertypes(te.asType())) { }
                        Set<String> done = new HashSet<>();
                        List<TypeElement> work = new ArrayList<>(); work.add(te);
                        for (int i = 0; i < work.size(); i++) for (TypeMirror sup : task.getTypes().directSupertypes(work.get(i).asType())) if (sup instanceof DeclaredType dt) work.add((TypeElement) dt.asElement());
                        for (TypeElement w : work) {
                            if (!w.getSimpleName().contentEquals(mm.owner())) continue;
                            for (Element m : w.getEnclosedElements()) {
                                if (!(m instanceof ExecutableElement ee) || !ee.getSimpleName().contentEquals(mm.method()) || !ee.getModifiers().contains(Modifier.ABSTRACT)) continue;
                                String sig = ee.getParameters().stream().map(pp -> typeStr(pp.asType()).replaceAll("<.*", "").replaceAll("^.*\\.", "")).collect(java.util.stream.Collectors.joining(","));
                                String want = mm.params().replaceAll("<[^>]*>", "").replaceAll("\\s", "");
                                String have = ee.getParameters().stream().map(pp -> task.getTypes().erasure(pp.asType()).toString().replaceAll("^.*\\.", "")).collect(java.util.stream.Collectors.joining(","));
                                if (!have.equals(want.replaceAll("[\\w.]*\\.", ""))) continue;
                                if (!done.add(ee.toString())) continue;
                                String vis = ee.getModifiers().contains(Modifier.PROTECTED) ? "protected" : "public";
                                StringBuilder ps = new StringBuilder(); int k = 0;
                                for (VariableElement pe : ee.getParameters()) { if (k > 0) ps.append(", "); ps.append(task.getTypes().erasure(pe.asType())).append(" a").append(k++); }
                                stubs.append("\n   // not implemented in the original jar (AbstractMethodError there as well)\n   ").append(vis).append(' ')
                                     .append(task.getTypes().erasure(ee.getReturnType())).append(' ').append(ee.getSimpleName()).append('(').append(ps).append(") {\n      throw new AbstractMethodError();\n   }\n");
                            }
                        }
                    }
                    if (stubs.length() > 0) { int endPos = (int) ce - 1; eds.add(new Edit(endPos, endPos, stubs.toString())); }
                    return super.visitClass(ct, v);
                }
                @Override public Void visitMethod(MethodTree mt, Void v) {
                    MethodTree old = curMethod; curMethod = mt;
                    if (!ur0.isEmpty() && mt.getBody() != null) {
                        long s1 = sp.getStartPosition(cu, mt.getBody()), e1 = sp.getEndPosition(cu, mt.getBody());
                        Set<String> types = new TreeSet<>();
                        for (Unrep u : ur0) if (u.pos() >= s1 && u.pos() <= e1 && !insideLambdaOrNested(mt, u.pos())) types.add(u.type());
                        for (var th : mt.getThrows()) types.remove(th.toString());
                        if (!types.isEmpty()) {
                            String ins = (mt.getThrows().isEmpty() ? " throws " : ", ") + String.join(", ", types);
                            long at = mt.getThrows().isEmpty() ? s1 : sp.getEndPosition(cu, mt.getThrows().get(mt.getThrows().size() - 1));
                            eds.add(new Edit((int) at - (mt.getThrows().isEmpty() ? 0 : 0), (int) at, ins + (mt.getThrows().isEmpty() ? " " : "")));
                        }
                    }
                    // undeclared vars used in this method
                    Set<String> names = new TreeSet<>();
                    long s0 = sp.getStartPosition(cu, mt), e0 = sp.getEndPosition(cu, mt);
                    for (String w : vars) { String[] p = w.split("@"); long pos = Long.parseLong(p[1]); if (pos >= s0 && pos <= e0) names.add(p[0]); }
                    if (!names.isEmpty() && mt.getBody() != null) {
                        StringBuilder decl = new StringBuilder();
                        for (String nm : names) {
                            String type = inferType(mt, nm);
                            decl.append("\n      ").append(type).append(' ').append(nm).append(" = ").append(def(type)).append(';');
                        }
                        eds.add(new Edit((int) sp.getStartPosition(cu, mt.getBody()) + 1, (int) sp.getStartPosition(cu, mt.getBody()) + 1, decl.toString()));
                    }
                    super.visitMethod(mt, v);
                    curMethod = old; return null;
                }
                boolean insideLambdaOrNested(MethodTree mt, long pos) {
                    final boolean[] r = {false};
                    new TreeScanner<Void, Void>() {
                        @Override public Void visitLambdaExpression(LambdaExpressionTree l, Void v) { if (pos >= sp.getStartPosition(cu, l) && pos <= sp.getEndPosition(cu, l)) r[0] = true; return super.visitLambdaExpression(l, v); }
                        @Override public Void visitClass(ClassTree c, Void v) { if (pos >= sp.getStartPosition(cu, c) && pos <= sp.getEndPosition(cu, c)) r[0] = true; return super.visitClass(c, v); }
                    }.scan(mt.getBody(), null);
                    return r[0];
                }
                String inferType(MethodTree mt, String nm) {
                    final String[] res = {"Object"};
                    new TreePathScanner<Void, Void>() {
                        boolean done;
                        @Override public Void visitAssignment(AssignmentTree at, Void v) {
                            if (!done && at.getVariable() instanceof IdentifierTree id && id.getName().contentEquals(nm)) {
                                TreePath p = new TreePath(getCurrentPath(), at.getExpression());
                                TypeMirror t = trees.getTypeMirror(p);
                                if (t != null && t.getKind() != TypeKind.ERROR && t.getKind() != TypeKind.NULL) { res[0] = typeStr(t); done = true; }
                            }
                            return super.visitAssignment(at, v);
                        }
                    }.scan(new TreePath(trees.getPath(cu, mt), mt.getBody()), null);
                    return res[0];
                }
                @Override public Void visitMemberReference(MemberReferenceTree mr, Void v) {
                    long s = sp.getStartPosition(cu, mr), en = sp.getEndPosition(cu, mr);
                    boolean err = false;
                    TreePath sp0 = getCurrentPath();
                    while (sp0 != null && !(sp0.getLeaf() instanceof StatementTree)) sp0 = sp0.getParentPath();
                    long ss = sp0 == null ? s : sp.getStartPosition(cu, sp0.getLeaf()), se = sp0 == null ? en : sp.getEndPosition(cu, sp0.getLeaf());
                    for (long q : mpos) if (q >= ss && q <= se) err = true;
                    if (err) { String lam = lambdaFor(mr, getCurrentPath()); if (lam != null) eds.add(new Edit((int) s, (int) en, lam)); }
                    return super.visitMemberReference(mr, v);
                }

                /** Arity of the functional interface a method reference is passed to, derived from the candidates of the enclosing call. */
                int targetArity(MemberReferenceTree mr, TreePath path) {
                    Tree parent = path.getParentPath().getLeaf();
                    Set<Integer> ar = new HashSet<>();
                    javax.lang.model.util.Elements els = task.getElements();
                    if (parent instanceof MethodInvocationTree mi) {
                        int idx = mi.getArguments().indexOf(mr);
                        ExpressionTree sel = mi.getMethodSelect();
                        TypeElement owner = null; String mname = null;
                        TreePath pp = path.getParentPath();
                        if (sel instanceof MemberSelectTree ms) {
                            mname = ms.getIdentifier().toString();
                            TreePath selp = new TreePath(new TreePath(pp, sel), ms.getExpression());
                            Element el = trees.getElement(selp);
                            TypeMirror t = el instanceof TypeElement ? el.asType() : trees.getTypeMirror(selp);
                            if (t instanceof DeclaredType dt) owner = (TypeElement) dt.asElement();
                        } else if (sel instanceof IdentifierTree id) {
                            mname = id.getName().toString();
                            TreePath q = path; while (q != null && !(q.getLeaf() instanceof ClassTree)) q = q.getParentPath();
                            if (q != null) owner = (TypeElement) trees.getElement(q);
                        }
                        if (owner == null || mname == null) return -1;
                        for (Element m : els.getAllMembers(owner))
                            if (m instanceof ExecutableElement ee && ee.getKind() == ElementKind.METHOD && ee.getSimpleName().contentEquals(mname)
                                    && ee.getParameters().size() == mi.getArguments().size()) {
                                int a = arityOf(ee.getParameters().get(idx).asType(), els);
                                if (a >= 0) ar.add(a);
                            }
                    } else if (parent instanceof NewClassTree nc) {
                        int idx = nc.getArguments().indexOf(mr);
                        TypeMirror t = trees.getTypeMirror(new TreePath(path.getParentPath(), nc.getIdentifier()));
                        if (t instanceof DeclaredType dt) for (Element m : dt.asElement().getEnclosedElements())
                            if (m instanceof ExecutableElement ee && ee.getKind() == ElementKind.CONSTRUCTOR && ee.getParameters().size() == nc.getArguments().size()) {
                                int a = arityOf(ee.getParameters().get(idx).asType(), els);
                                if (a >= 0) ar.add(a);
                            }
                    } else if (parent instanceof VariableTree vt) {
                        TypeMirror t = trees.getTypeMirror(new TreePath(path.getParentPath(), vt.getType()));
                        if (t != null) { int a = arityOf(t, els); if (a >= 0) ar.add(a); }
                    }
                    return ar.size() == 1 ? ar.iterator().next() : -1;
                }
                int arityOf(TypeMirror t, javax.lang.model.util.Elements els) {
                    if (!(t instanceof DeclaredType dt) || dt.asElement().getKind() != ElementKind.INTERFACE) return -1;
                    int found = -1, cnt = 0;
                    for (Element m : els.getAllMembers((TypeElement) dt.asElement()))
                        if (m instanceof ExecutableElement ee && ee.getModifiers().contains(Modifier.ABSTRACT)
                                && !(ee.getSimpleName().contentEquals("equals") || ee.getSimpleName().contentEquals("hashCode") || ee.getSimpleName().contentEquals("toString"))) { found = ee.getParameters().size(); cnt++; }
                    return cnt == 1 ? found : -1;
                }
                String lambdaFor(MemberReferenceTree mr, TreePath path) {
                    if (mr.getMode() != MemberReferenceTree.ReferenceMode.INVOKE) return null;
                    String name = mr.getName().toString();
                    ExpressionTree q = mr.getQualifierExpression();
                    TypeElement cls; boolean bound; String recv;
                    TreePath qp = new TreePath(path, q);
                    Element qe = trees.getElement(qp);
                    boolean isType = qe instanceof TypeElement;
                    if (isType) { cls = (TypeElement) qe; bound = false; recv = cls.getQualifiedName().toString(); }
                    else { TypeMirror t = trees.getTypeMirror(qp); if (t == null || !(t instanceof DeclaredType dt)) return null; cls = (TypeElement) dt.asElement(); bound = true; recv = q.toString(); }
                    int k = targetArity(mr, path);
                    if (System.getenv("FIXER_DEBUG") != null) System.err.println("mref " + mr + " k=" + k + " in " + cu.getSourceFile().getName());
                    List<ExecutableElement> cands = new ArrayList<>();
                    for (Element m : cls.getEnclosedElements()) if (m instanceof ExecutableElement ee && m.getKind() == ElementKind.METHOD && ee.getSimpleName().contentEquals(name)) cands.add(ee);
                    TypeElement cur = null;
                    for (TreePath q2 = path; q2 != null; q2 = q2.getParentPath()) if (q2.getLeaf() instanceof ClassTree) { cur = (TypeElement) trees.getElement(q2); break; }
                    // each candidate yields (captured leading args, lambda arity, unbound?)
                    ExecutableElement pick = null; int pickCaptured = 0; boolean pickUnbound = false; int matches = 0;
                    for (ExecutableElement c : cands) {
                        boolean st = c.getModifiers().contains(Modifier.STATIC); int p = c.getParameters().size();
                        int captured = 0; boolean unb = false; boolean ok;
                        if (!isType) {
                            if (!st) ok = (k < 0 || p == k);
                            else { captured = 1; ok = k >= 0 && p - k == 1; }   // static helper whose first argument is the (bound) receiver
                        }
                        else if (st) {
                            if (k < 0) { ok = true; }
                            else { captured = p - k; ok = captured >= 0; if (captured == 1) { ok = cur != null && c.getParameters().get(0).asType().toString().equals(cur.asType().toString()); }
                                   else if (captured > 1) ok = false; }
                        } else { unb = true; ok = (k < 0 || p + 1 == k); }
                        if (ok) { matches++; pick = c; pickCaptured = captured; pickUnbound = unb; }
                    }
                    if (matches != 1) return null;
                    ExecutableElement c = pick;
                    int p = c.getParameters().size();
                    int n = pickUnbound ? p + 1 : p - pickCaptured;
                    List<String> ps = new ArrayList<>(), call = new ArrayList<>();
                    for (int i = 0; i < n; i++) ps.add("a" + i);
                    if (pickCaptured == 1) call.add(isType ? "this" : recv);
                    for (int i = 0; i < p; i++) {
                        if (i < pickCaptured) continue;
                        int ai = pickUnbound ? i + 1 : i - pickCaptured;
                        call.add("(" + box(typeStr(c.getParameters().get(i).asType())) + ") a" + ai);
                    }
                    boolean staticCall = pick.getModifiers().contains(Modifier.STATIC);
                    String target = pickUnbound ? "((" + recv + ") a0)" : (staticCall ? cls.getQualifiedName().toString() : recv);
                    return "(" + String.join(", ", ps) + ") -> " + target + "." + name + "(" + String.join(", ", call) + ")";
                }
                @Override public Void visitVariable(VariableTree vt, Void v) {
                    String ty = vt.getType().toString();
                    if (vt.getType() instanceof PrimitiveTypeTree && ty.matches("int|byte|short|char") && getCurrentPath().getParentPath().getLeaf() instanceof BlockTree) {
                        String nm = vt.getName().toString();
                        // scope: the rest of the enclosing block
                        long bs = sp.getStartPosition(cu, vt), be = sp.getEndPosition(cu, getCurrentPath().getParentPath().getLeaf());
                        boolean flip = false;
                        for (Mis m : ms) {
                            if (m.start() < bs || m.end() > be) continue;
                            String txt = src.substring((int) m.start(), (int) m.end());
                            if (m.from().equals("boolean") && m.to().matches("int|byte|short|char") && (txt.equals(nm) || isInitOrAssignRhs(vt, m))) flip = true;
                            if (m.from().matches("int|byte|short|char|Integer") && m.to().equals("boolean") && txt.equals(nm)) flip = true;
                        }
                        if (flip) { long ts = sp.getStartPosition(cu, vt.getType()), te = sp.getEndPosition(cu, vt.getType()); eds.add(new Edit((int) ts, (int) te, "boolean")); }
                    }
                    return super.visitVariable(vt, v);
                }
                boolean isInitOrAssignRhs(VariableTree vt, Mis m) {
                    if (vt.getInitializer() != null) { long s = sp.getStartPosition(cu, vt.getInitializer()), e = sp.getEndPosition(cu, vt.getInitializer()); if (m.start() >= s && m.end() <= e) return true; }
                    // assignment `name = <expr>` whose rhs contains the mismatch
                    String nm = vt.getName().toString(); int from = (int) m.start();
                    int ls = src.lastIndexOf('\n', from) + 1; String line = src.substring(ls, from);
                    return line.matches("\\s*" + nm + "\\s*=\\s*.*");
                }
                @Override public Void visitEnhancedForLoop(EnhancedForLoopTree ef, Void v) {
                    long s = sp.getStartPosition(cu, ef.getExpression()), en = sp.getEndPosition(cu, ef.getExpression());
                    if (System.getenv("FIXER_DEBUG") != null) for (Mis m : ms) System.err.println("for-expr " + s + "-" + en + " vs mis " + m);
                    for (Mis m : ms) if (m.start() == s && m.end() == en && m.from().equals("Object")) {
                        String el = ef.getVariable().getType().toString();
                        eds.add(new Edit((int) s, (int) en, "(java.lang.Iterable<" + box(el) + ">) " + src.substring((int) s, (int) en)));
                    }
                    return super.visitEnhancedForLoop(ef, v);
                }
                @Override public Void visitLiteral(LiteralTree lt, Void v) {
                    long s = sp.getStartPosition(cu, lt), en = sp.getEndPosition(cu, lt);
                    for (Mis m : ms) if (m.start() == s && m.end() == en) {
                        if (m.from().equals("boolean") && m.to().matches("int|byte|short|long|char")) eds.add(new Edit((int) s, (int) en, lt.getValue().equals(Boolean.TRUE) ? "1" : "0"));
                        else if (m.from().matches("int|byte|short|long") && m.to().equals("boolean") && lt.getValue() instanceof Number nn) eds.add(new Edit((int) s, (int) en, nn.intValue() != 0 ? "true" : "false"));
                    }
                    return super.visitLiteral(lt, v);
                }
                Set<String> castDone = new HashSet<>();
                @Override public Void visitMethodInvocation(MethodInvocationTree mi, Void v) {
                    long s0 = sp.getStartPosition(cu, mi), e0 = sp.getEndPosition(cu, mi);
                    boolean err = false; for (long q : mpos) if (q >= s0 && q <= e0 && s0 >= 0) err = true;
                    if (err) {
                        TreePath cur = getCurrentPath();
                        ExpressionTree sel = mi.getMethodSelect();
                        TypeElement owner = null; String mname = null;
                        if (sel instanceof MemberSelectTree ms2) {
                            mname = ms2.getIdentifier().toString();
                            TreePath selp = new TreePath(new TreePath(cur, sel), ms2.getExpression());
                            Element el = trees.getElement(selp);
                            TypeMirror t = el instanceof TypeElement ? el.asType() : trees.getTypeMirror(selp);
                            if (t instanceof DeclaredType dt) owner = (TypeElement) dt.asElement();
                        } else if (sel instanceof IdentifierTree id2) {
                            mname = id2.getName().toString();
                            TreePath q2 = cur; while (q2 != null && !(q2.getLeaf() instanceof ClassTree)) q2 = q2.getParentPath();
                            if (q2 != null) owner = (TypeElement) trees.getElement(q2);
                        }
                        if (owner != null && mname != null) {
                            List<ExecutableElement> cs = new ArrayList<>();
                            for (Element m : task.getElements().getAllMembers(owner))
                                if (m instanceof ExecutableElement ee && ee.getKind() == ElementKind.METHOD && ee.getSimpleName().contentEquals(mname) && ee.getParameters().size() == mi.getArguments().size()) cs.add(ee);
                            if (cs.size() > 1) {
                                // keep candidates compatible with every argument whose type is known (not Object)
                                javax.lang.model.util.Types tys = task.getTypes();
                                List<ExecutableElement> keep = new ArrayList<>();
                                for (ExecutableElement c : cs) {
                                    boolean okc = true;
                                    for (int i = 0; i < mi.getArguments().size() && okc; i++) {
                                        TypeMirror at = trees.getTypeMirror(new TreePath(cur, mi.getArguments().get(i)));
                                        if (at == null || at.getKind() == TypeKind.ERROR || at.toString().equals("java.lang.Object") || at.getKind() == TypeKind.NULL) continue;
                                        try { okc = tys.isAssignable(tys.erasure(at), tys.erasure(c.getParameters().get(i).asType())); } catch (Exception ex) { okc = false; }
                                    }
                                    if (okc) keep.add(c);
                                }
                                cs = keep;
                            }
                            if (cs.size() == 1) {
                                ExecutableElement c = cs.get(0);
                                for (int i = 0; i < mi.getArguments().size(); i++) {
                                    ExpressionTree ar = mi.getArguments().get(i);
                                    TypeMirror at = trees.getTypeMirror(new TreePath(cur, ar));
                                    String pt = typeStr(c.getParameters().get(i).asType());
                                    if ((at == null || at.getKind() == TypeKind.ERROR || at.toString().equals("java.lang.Object")) && ar instanceof IdentifierTree && !pt.equals("Object") && !pt.equals("java.lang.Object") && !pt.contains("<")) {
                                        long as = sp.getStartPosition(cu, ar), ae = sp.getEndPosition(cu, ar);
                                        if (as >= 0 && ae > as && castDone.add(as + ":" + ae)) eds.add(new Edit((int) as, (int) ae, "(" + box(pt) + ") " + src.substring((int) as, (int) ae)));
                                    }
                                }
                            }
                        }
                    }
                    return super.visitMethodInvocation(mi, v);
                }
                @Override public Void scan(Tree t, Void v) {
                    if (t instanceof ExpressionTree && !(t instanceof LiteralTree) && !(t instanceof LambdaExpressionTree) && !(t instanceof MemberReferenceTree)) {
                        long s = sp.getStartPosition(cu, t), en = sp.getEndPosition(cu, t);
                        if (s < 0 || en < 0) return super.scan(t, v);
                        for (Mis m : ms) if (m.start() == s && m.end() == en && (m.from().equals("Object") || m.from().equals("Object[]") || m.from().equals("CharSequence") || m.from().equals("Iterable") || m.from().equals("Collection"))
                                && !m.to().contains("<") && !m.to().equals("Object") && !m.to().matches("int|long|boolean|byte|short|char|float|double|Void|FunctionReference")
                                && castDone.add(s + ":" + en))
                            eds.add(new Edit((int) s, (int) en, "(" + m.to() + ") " + src.substring((int) s, (int) en)));
                    }
                    return super.scan(t, v);
                }
            }.scan(cu, null);
            if (eds.isEmpty()) continue;
            eds.removeIf(x -> x.start() < 0 || x.end() < x.start() || x.end() > src.length());
            eds.sort((x, y) -> y.start() - x.start());
            StringBuilder sb = new StringBuilder(src);
            int last = Integer.MAX_VALUE;
            for (Edit ed : eds) { if (ed.end() > last) continue; sb.replace(ed.start(), ed.end(), ed.text()); last = ed.start(); total++; }
            Files.writeString(Path.of(f), sb.toString(), StandardCharsets.UTF_8);
        }
        return total;
    }

    static String norm(String t) { return t.replaceAll("java\\.lang\\.(?=[A-Z])", "").replace("litebans.", ""); }
    static String typeStr(TypeMirror t) {
        String s = t.toString();
        s = s.replaceAll("@[\\w.]+(\\([^)]*\\))?\\s*", "");     // annotations
        if (t.getKind() == TypeKind.TYPEVAR || t.getKind() == TypeKind.WILDCARD || t.getKind() == TypeKind.INTERSECTION || s.contains("capture")) return "Object";
        return s;
    }
    static String box(String t) {
        return switch (t) { case "int" -> "Integer"; case "long" -> "Long"; case "boolean" -> "Boolean"; case "byte" -> "Byte"; case "short" -> "Short";
            case "char" -> "Character"; case "float" -> "Float"; case "double" -> "Double"; default -> t; };
    }
    static String def(String t) {
        return switch (t) { case "int", "short", "byte", "char" -> "0"; case "long" -> "0L"; case "boolean" -> "false"; case "float" -> "0.0F"; case "double" -> "0.0"; default -> "null"; };
    }
}
