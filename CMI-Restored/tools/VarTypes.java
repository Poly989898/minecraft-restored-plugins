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
import java.util.stream.*;

/**
 * Restores generic types lost by the decompiler: a local variable or for-each variable declared with a raw type
 * (e.g. {@code Map map = ...}) is changed to {@code var map = ...} when javac infers a parameterized type for the
 * initializer or for the iterable element. The declared type stays the same except for its type arguments.
 *
 * usage: java tools/VarTypes.java <classpath> <src dir> [--dry]
 * Run repeatedly until it reports 0 changes (one change can make another declaration inferable).
 */
public class VarTypes {
    record Edit(Path file, int start, int end, String text) {}

    public static void main(String[] a) throws Exception {
        String cp = a[0];
        Path src = Path.of(a[1]);
        boolean dry = a.length > 2 && a[2].equals("--dry");
        List<File> files;
        try (Stream<Path> st = Files.walk(src)) {
            files = st.filter(x -> x.toString().endsWith(".java")).map(Path::toFile).collect(Collectors.toList());
        }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-proc:none", "-nowarn", "-cp", cp, "-Xlint:none", "-XDshould-stop.ifError=FLOW"), null, fm.getJavaFileObjectsFromFiles(files));
        Iterable<? extends CompilationUnitTree> cus = task.parse();
        task.analyze();
        Trees trees = Trees.instance(task);
        Types types = task.getTypes();
        Elements elements = task.getElements();
        TypeElement iterable = elements.getTypeElement("java.lang.Iterable");

        Map<Path, List<Edit>> byFile = new HashMap<>();
        int changes = 0;
        for (CompilationUnitTree cu : cus) {
            Path file = Path.of(cu.getSourceFile().toUri());
            List<Edit> edits = new ArrayList<>();
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitVariable(VariableTree v, Void x) {
                    TreePath path = getCurrentPath();
                    Tree type = v.getType();
                    ExpressionTree init = v.getInitializer();
                    if (type != null && init != null && isBareRaw(path, trees) && !(init instanceof NewClassTree nc && nc.getClassBody() != null)) {
                        TypeMirror initT = typeOf(new TreePath(path, init), trees);
                        TypeMirror declT = trees.getElement(path).asType();
                        if (isParameterized(initT) && types.isAssignable(initT, types.erasure(declT))) {
                            addReplace(edits, cu, trees, type);
                        }
                    }
                    return super.visitVariable(v, x);
                }

                @Override
                public Void visitEnhancedForLoop(EnhancedForLoopTree f, Void x) {
                    VariableTree v = f.getVariable();
                    TreePath vp = new TreePath(getCurrentPath(), v);
                    if (v.getType() != null && isBareRaw(vp, trees)) {
                        TypeMirror iterT = typeOf(new TreePath(getCurrentPath(), f.getExpression()), trees);
                        if (iterT != null && iterT.getKind() == TypeKind.DECLARED) {
                            DeclaredType sup = findSupertype(types, iterT, iterable);
                            if (sup != null && !sup.getTypeArguments().isEmpty()) {
                                TypeMirror elem = sup.getTypeArguments().get(0);
                                TypeMirror declT = trees.getElement(vp).asType();
                                if (isParameterized(elem) && types.isAssignable(elem, types.erasure(declT))) {
                                    addReplace(edits, cu, trees, v.getType());
                                }
                            }
                        }
                    }
                    return super.visitEnhancedForLoop(f, x);
                }
            }.scan(cu, null);
            if (!edits.isEmpty()) {
                byFile.put(file, edits);
                changes += edits.size();
            }
        }

        for (var e : byFile.entrySet()) {
            String s = Files.readString(e.getKey());
            List<Edit> l = e.getValue().stream().sorted(Comparator.comparingInt(Edit::start).reversed()).collect(Collectors.toList());
            for (Edit x : l) s = s.substring(0, x.start()) + x.text() + s.substring(x.end());
            if (!dry) Files.writeString(e.getKey(), s);
        }
        System.out.println("changes: " + changes + (dry ? " (dry run)" : ""));
    }

    /** True when the declared type is a raw use of a generic class (no type arguments written). */
    static boolean isBareRaw(TreePath path, Trees trees) {
        Element el = trees.getElement(path);
        if (el == null) return false;
        TypeMirror t = el.asType();
        if (t.getKind() != TypeKind.DECLARED) return false;
        DeclaredType dt = (DeclaredType) t;
        return dt.getTypeArguments().isEmpty() && !((TypeElement) dt.asElement()).getTypeParameters().isEmpty();
    }

    /** The supertype of t whose element is target (e.g. Iterable<E>), searching directSupertypes. */
    static DeclaredType findSupertype(Types types, TypeMirror t, TypeElement target) {
        Deque<TypeMirror> queue = new ArrayDeque<>();
        queue.add(t);
        Set<String> seen = new HashSet<>();
        while (!queue.isEmpty()) {
            TypeMirror cur = queue.poll();
            if (cur.getKind() != TypeKind.DECLARED || !seen.add(cur.toString())) continue;
            DeclaredType dt = (DeclaredType) cur;
            if (dt.asElement().equals(target)) return dt;
            queue.addAll(types.directSupertypes(cur));
        }
        return null;
    }

    static boolean isParameterized(TypeMirror t) {
        if (t == null || t.getKind() != TypeKind.DECLARED) return false;
        return !((DeclaredType) t).getTypeArguments().isEmpty();
    }

    static TypeMirror typeOf(TreePath p, Trees trees) {
        try {
            return trees.getTypeMirror(p);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    static void addReplace(List<Edit> edits, CompilationUnitTree cu, Trees trees, Tree type) {
        SourcePositions sp = trees.getSourcePositions();
        int s = (int) sp.getStartPosition(cu, type);
        int e = (int) sp.getEndPosition(cu, type);
        if (s < 0 || e < 0) return;
        edits.add(new Edit(Path.of(cu.getSourceFile().toUri()), s, e, "var"));
    }
}
