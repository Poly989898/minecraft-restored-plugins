import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Lists methods that override a supertype method but carry no @Override (accidental overrides after renames). usage: java OverrideCheck <cp-file> <src-root> <stubs-root> */
public class OverrideCheck {
    public static void main(String[] a) throws Exception {
        String cp = Files.readString(Path.of(a[0])).trim();
        Set<String> excluded = new HashSet<>();
        Path ex = Path.of("tools/fabric-files.txt");
        if (Files.exists(ex)) for (String l : Files.readAllLines(ex)) if (!l.isBlank()) excluded.add(l.trim());
        List<Path> files = new ArrayList<>();
        try (var s = Files.walk(Path.of(a[1]))) { s.filter(p -> p.toString().endsWith(".java") && !p.toString().contains("/litebans/mixin/") && !excluded.contains(p.getFileName().toString())).forEach(files::add); }
        try (var s = Files.walk(Path.of(a[2]))) { s.filter(p -> p.toString().endsWith(".java")).forEach(files::add); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-classpath", cp, "-proc:none"), null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> units = task.parse(); task.analyze();
        Trees trees = Trees.instance(task); Elements els = task.getElements(); Types types = task.getTypes();
        int[] n = {0};
        for (var u : units) new TreePathScanner<Void, Void>() {
            @Override public Void visitMethod(MethodTree mt, Void v) {
                Element e = trees.getElement(getCurrentPath());
                if (e instanceof ExecutableElement m && m.getKind() == ElementKind.METHOD && !m.getModifiers().contains(Modifier.STATIC) && !m.getModifiers().contains(Modifier.PRIVATE)) {
                    boolean hasOv = mt.getModifiers().getAnnotations().stream().anyMatch(an -> an.getAnnotationType().toString().endsWith("Override"));
                    if (!hasOv) {
                        TypeElement cls = (TypeElement) m.getEnclosingElement();
                        Deque<TypeMirror> q = new ArrayDeque<>(types.directSupertypes(cls.asType())); Set<String> seen = new HashSet<>();
                        while (!q.isEmpty()) {
                            TypeMirror t = q.poll(); if (!seen.add(t.toString())) continue;
                            Element te = types.asElement(t); if (te == null) continue;
                            for (Element me : te.getEnclosedElements()) if (me instanceof ExecutableElement sm && sm.getSimpleName().contentEquals(m.getSimpleName()) && els.overrides(m, sm, cls) && !te.getSimpleName().contentEquals("Object")) {
                                System.out.println(cls.getSimpleName() + "." + m + " overrides " + te.getSimpleName() + "." + sm); n[0]++;
                            }
                            q.addAll(types.directSupertypes(t));
                        }
                    }
                }
                return super.visitMethod(mt, v);
            }
        }.scan(u, null);
        System.out.println("overrides without @Override: " + n[0]);
    }
}
