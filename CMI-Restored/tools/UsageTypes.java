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
 * Heuristic: a local variable declared with a raw generic type, e.g. {@code HashMap map = new HashMap();}, gets type
 * arguments from how it is used in the same method: {@code map.put(k, v)} gives K and V, {@code list.add(e)} gives E.
 * The decompiler erased these arguments, so this is a reconstruction. Compile errors show where a guess is wrong.
 *
 * usage: java tools/UsageTypes.java <classpath> <src dir> [--dry]
 */
public class UsageTypes {
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

        Map<Path, List<Edit>> byFile = new HashMap<>();
        int changes = 0;
        for (CompilationUnitTree cu : cus) {
            Path file = Path.of(cu.getSourceFile().toUri());
            Names names = new Names(cu);
            List<Edit> edits = new ArrayList<>();
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitVariable(VariableTree v, Void x) {
                    TreePath p = getCurrentPath();
                    boolean local = p.getParentPath().getLeaf() instanceof BlockTree || p.getParentPath().getLeaf() instanceof CaseTree;
                    if (local && v.getType() != null && (v.getType() instanceof IdentifierTree || v.getType() instanceof MemberSelectTree)
                            && v.getInitializer() instanceof NewClassTree nc && nc.getClassBody() == null
                            && (nc.getIdentifier() instanceof IdentifierTree || nc.getIdentifier() instanceof MemberSelectTree)
                            && bareRaw(p, trees)) {
                        TypeElement decl = (TypeElement) ((DeclaredType) trees.getElement(p).asType()).asElement();
                        int want = decl.getTypeParameters().size();
                        List<String> args = inferArgs(p, want, decl, trees, types, elements, names, cu);
                        if (args != null) {
                            SourcePositions sp = trees.getSourcePositions();
                            int s = (int) sp.getStartPosition(cu, v.getType());
                            int e = (int) sp.getEndPosition(cu, v.getType());
                            String text = typeText(v.getType(), names) + "<" + String.join(", ", args) + ">";
                            edits.add(new Edit(file, s, e, text));
                        }
                    }
                    return super.visitVariable(v, x);
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

    /** Finds uses of the variable inside its enclosing block and derives the type arguments. */
    static List<String> inferArgs(TreePath declPath, int want, TypeElement decl, Trees trees, Types types, Elements elements, Names names, CompilationUnitTree cu) {
        if (want == 0) return null;
        Element varEl = trees.getElement(declPath);
        TreePath block = declPath.getParentPath();
        TypeMirror[] found = new TypeMirror[want];
        boolean[] conflict = new boolean[1];
        new TreePathScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree mi, Void x) {
                if (mi.getMethodSelect() instanceof MemberSelectTree ms && ms.getExpression() instanceof IdentifierTree id
                        && trees.getElement(new TreePath(getCurrentPath(), id)) != null
                        && trees.getElement(new TreePath(getCurrentPath(), id)).equals(varEl)) {
                    String name = ms.getIdentifier().toString();
                    List<? extends ExpressionTree> as = mi.getArguments();
                    List<TypeMirror> argT = as.stream().map(t -> trees.getTypeMirror(new TreePath(getCurrentPath(), t))).map(t -> t == null ? null : boxed(t, types)).collect(Collectors.toList());
                    if (want == 2 && name.equals("put") && as.size() == 2) {
                        record(0, argT.get(0)); record(1, argT.get(1));
                    } else if (want == 1 && (name.equals("add") || name.equals("push") || name.equals("offer")) && as.size() == 1) {
                        record(0, argT.get(0));
                    }
                }
                return super.visitMethodInvocation(mi, x);
            }

            void record(int idx, TypeMirror t) {
                if (t == null || t.getKind() != TypeKind.DECLARED) return;
                if (found[idx] == null) found[idx] = t;
                else if (!types.isSameType(found[idx], t)) conflict[0] = true;
            }
        }.scan(block, null);
        if (conflict[0]) return null;
        List<String> out = new ArrayList<>();
        for (TypeMirror t : found) {
            if (t == null) return null;
            if (t.toString().contains("capture") || t.getKind() != TypeKind.DECLARED) return null;
            out.add(names.text((DeclaredType) t));
        }
        return out;
    }

    static TypeMirror boxed(TypeMirror t, Types types) {
        if (t.getKind().isPrimitive()) return types.boxedClass((PrimitiveType) t).asType();
        return t;
    }

    static boolean bareRaw(TreePath path, Trees trees) {
        Element el = trees.getElement(path);
        if (el == null) return false;
        TypeMirror t = el.asType();
        if (t.getKind() != TypeKind.DECLARED) return false;
        DeclaredType dt = (DeclaredType) t;
        return dt.getTypeArguments().isEmpty() && !((TypeElement) dt.asElement()).getTypeParameters().isEmpty();
    }

    static String typeText(Tree t, Names names) {
        return t.toString();
    }

    /** Source text of a type: simple name when imported, same package or java.lang; else qualified. */
    static class Names {
        final String pkg;
        final Set<String> imported = new HashSet<>();

        Names(CompilationUnitTree cu) {
            pkg = cu.getPackageName() == null ? "" : cu.getPackageName().toString();
            for (ImportTree it : cu.getImports()) {
                if (!it.isStatic()) imported.add(it.getQualifiedIdentifier().toString());
            }
        }

        String text(DeclaredType dt) {
            TypeElement te = (TypeElement) dt.asElement();
            String qn = te.getQualifiedName().toString();
            String simple = te.getSimpleName().toString();
            String pkgName = qn.contains(".") ? qn.substring(0, qn.lastIndexOf('.')) : "";
            // nested types: use the outer simple name only if the outer is visible; keep it simple: qualified
            boolean visible = te.getNestingKind() == NestingKind.TOP_LEVEL
                    && (pkgName.equals("java.lang") || pkgName.equals(pkg) || imported.contains(qn));
            String base = visible ? simple : qn;
            if (!dt.getTypeArguments().isEmpty()) {
                base += "<" + dt.getTypeArguments().stream().map(x -> x instanceof DeclaredType d ? text(d) : x.toString()).collect(Collectors.joining(", ")) + ">";
            }
            return base;
        }
    }
}
