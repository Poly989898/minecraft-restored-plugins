import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Finds overload traps: a call used as a statement (result discarded) that javac resolved to a non-void overload while a void overload of the
 * same name/arity is applicable. The decompiler lost the argument cast that selected the void overload in the original bytecode.
 * With --fix, casts the arguments to the void overload's parameter types.   usage: TrapFix <cp-file> <src-root> <stubs-root> [--fix]
 */
public class TrapFix {
    record Edit(int start, int end, String text) {}
    public static void main(String[] a) throws Exception {
        String cp = Files.readString(Path.of(a[0])).trim(); boolean fix = a.length > 3 && a[3].equals("--fix");
        List<Path> files = new ArrayList<>();
        Set<String> excluded = new HashSet<>();
        for (String l : Files.readAllLines(Path.of("tools/fabric-files.txt"))) if (!l.isBlank()) excluded.add(l.trim());
        for (String r : new String[]{a[1], a[2]}) try (var s = Files.walk(Path.of(r))) { s.filter(p -> p.toString().endsWith(".java") && !p.toString().contains("/mixin/") && !excluded.contains(p.getFileName().toString())).forEach(files::add); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-classpath", cp, "-proc:none"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse(); task.analyze();
        Trees trees = Trees.instance(task); SourcePositions sp = trees.getSourcePositions();
        var types = task.getTypes(); var els = task.getElements();
        int found = 0;
        for (CompilationUnitTree u : units) {
            String src = u.getSourceFile().getCharContent(true).toString(); List<Edit> eds = new ArrayList<>();
            new TreePathScanner<Void, Void>() {
                @Override public Void visitExpressionStatement(ExpressionStatementTree es, Void v) {
                    if (es.getExpression() instanceof MethodInvocationTree mi) {
                        Element el = trees.getElement(new TreePath(new TreePath(getCurrentPath(), mi), mi.getMethodSelect()));
                        if (el instanceof ExecutableElement ee && ee.getReturnType().getKind() != TypeKind.VOID && ee.getKind() == ElementKind.METHOD) {
                            TypeElement owner = (TypeElement) ee.getEnclosingElement();
                            List<ExecutableElement> voids = new ArrayList<>();
                            for (Element m : els.getAllMembers(owner)) {
                                if (m instanceof ExecutableElement o && o != ee && o.getKind() == ElementKind.METHOD && o.getSimpleName().equals(ee.getSimpleName())
                                        && o.getReturnType().getKind() == TypeKind.VOID && o.getParameters().size() == mi.getArguments().size()
                                        && o.getModifiers().contains(Modifier.STATIC) == ee.getModifiers().contains(Modifier.STATIC)) {
                                    boolean ok = true;
                                    for (int i = 0; i < mi.getArguments().size() && ok; i++) {
                                        TypeMirror at = trees.getTypeMirror(new TreePath(new TreePath(getCurrentPath(), mi), mi.getArguments().get(i)));
                                        if (at == null || at.getKind() == TypeKind.ERROR) continue;
                                        ok = types.isAssignable(at, o.getParameters().get(i).asType());
                                    }
                                    if (ok) voids.add(o);
                                }
                            }
                            if (voids.size() == 1) {
                                ExecutableElement o = voids.get(0);
                                System.out.println(u.getSourceFile().getName().replaceAll(".*/", "") + ": " + src.substring((int) sp.getStartPosition(u, es), Math.min(src.length(), (int) sp.getStartPosition(u, es) + 70)).replace('\n', ' ') + "   -> void overload " + o);
                                if (fix) for (int i = 0; i < mi.getArguments().size(); i++) {
                                    ExpressionTree arg = mi.getArguments().get(i);
                                    TypeMirror at = trees.getTypeMirror(new TreePath(new TreePath(getCurrentPath(), mi), arg));
                                    TypeMirror pt = o.getParameters().get(i).asType();
                                    if (at != null && !types.isSameType(at, pt) && !pt.toString().contains("<")) {
                                        int s = (int) sp.getStartPosition(u, arg), e = (int) sp.getEndPosition(u, arg);
                                        eds.add(new Edit(s, e, "(" + types.erasure(pt) + ") " + src.substring(s, e)));
                                    }
                                }
                            }
                        }
                    }
                    return super.visitExpressionStatement(es, v);
                }
            }.scan(u, null);
            found += eds.size();
            if (fix && !eds.isEmpty()) {
                eds.sort((x, y) -> y.start() - x.start()); StringBuilder sb = new StringBuilder(src); int last = Integer.MAX_VALUE;
                for (Edit e : eds) { if (e.end() > last) continue; sb.replace(e.start(), e.end(), e.text()); last = e.start(); }
                Files.writeString(Path.of(u.getSourceFile().toUri()), sb.toString(), StandardCharsets.UTF_8);
            }
        }
        System.out.println("cast edits: " + found);
    }
}
