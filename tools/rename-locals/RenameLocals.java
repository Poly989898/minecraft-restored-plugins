import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Renames decompiler-style local variables and parameters (var1, var2, ...) to meaningful names.
 * Symbol accurate: uses the javac Tree API, every rename is applied to the declaration and to every
 * identifier that resolves to the very same VariableElement. Files are edited by exact offsets.
 */
public class RenameLocals {
    // ---- options
    static Pattern pattern = Pattern.compile("var\\d+");
    static boolean apply = true;
    static Path mapOut;
    static final List<Path> srcDirs = new ArrayList<>();
    static String classpath = "";
    static List<String> extraOpts = new ArrayList<>();

    // ---- javac
    static Trees trees;
    static Types types;
    static Elements elements;

    static final class Decl {
        VariableElement el;
        TreePath path;
        VariableTree tree;
        int pos, start, end;
        String oldName, newName;
        FileData file;
    }

    static final class Scoped {
        final String name; final int start, end;
        Scoped(String n, int s, int e) { name = n; start = s; end = e; }
    }

    static final class Ref {
        final int pos; final Element el;
        Ref(int p, Element e) { pos = p; el = e; }
    }

    static final class FileData {
        Path path;
        CompilationUnitTree cu;
        String text;
        final TreeMap<Integer, String> points = new TreeMap<>();
        final List<Scoped> scoped = new ArrayList<>();
        final List<Decl> decls = new ArrayList<>();
        final List<Ref> refs = new ArrayList<>();
    }

    static final Set<Element> indexUse = new HashSet<>();
    static final Map<Element, Decl> declByEl = new HashMap<>();
    static final Set<String> RESERVED = new HashSet<>(List.of("var", "yield", "record", "sealed", "permits",
            "when", "module", "open", "exports", "requires", "to", "with", "uses", "provides", "opens",
            "transitive", "non", "true", "false", "null", "_"));

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--src" -> srcDirs.add(Paths.get(args[++i]));
                case "--classpath" -> classpath = args[++i];
                case "--pattern" -> pattern = Pattern.compile(args[++i]);
                case "--dry-run" -> apply = false;
                case "--map" -> mapOut = Paths.get(args[++i]);
                case "--opt" -> extraOpts.add(args[++i]);
                default -> { System.err.println("unknown option " + args[i]); System.exit(2); }
            }
        }
        if (srcDirs.isEmpty()) { System.err.println("no --src"); System.exit(2); }
        List<Path> files = new ArrayList<>();
        for (Path d : srcDirs) {
            try (Stream<Path> s = Files.walk(d)) {
                s.filter(p -> p.toString().endsWith(".java")).sorted().forEach(files::add);
            }
        }
        long t0 = System.currentTimeMillis();
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(diag, null, StandardCharsets.UTF_8);
        List<String> opts = new ArrayList<>(List.of("-proc:none", "-Xlint:none", "-encoding", "UTF-8", "-Xmaxerrs", "100000"));
        if (!classpath.isEmpty()) { opts.add("-cp"); opts.add(classpath); }
        opts.addAll(extraOpts);
        JavacTask task = (JavacTask) jc.getTask(null, fm, diag, opts, null, fm.getJavaFileObjectsFromPaths(files));
        Iterable<? extends CompilationUnitTree> cus = task.parse();
        task.analyze();
        long errors = diag.getDiagnostics().stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR).count();
        if (errors > 0) {
            diag.getDiagnostics().stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR).limit(20)
                    .forEach(d -> System.err.println(d));
            System.err.println("compile errors: " + errors + " - refusing to rename");
            System.exit(1);
        }
        trees = Trees.instance(task);
        types = task.getTypes();
        elements = task.getElements();
        SourcePositions sp = trees.getSourcePositions();

        List<FileData> all = new ArrayList<>();
        for (CompilationUnitTree cu : cus) {
            FileData fd = new FileData();
            fd.cu = cu;
            fd.path = Paths.get(cu.getSourceFile().toUri());
            fd.text = cu.getSourceFile().getCharContent(true).toString();
            all.add(fd);
            new Scan(fd, sp).scan(new TreePath(cu), null);
        }
        int total = 0;
        for (FileData fd : all) {
            for (Decl d : fd.decls) declByEl.put(d.el, d);
            total += fd.decls.size();
        }
        long t1 = System.currentTimeMillis();
        // name assignment
        for (FileData fd : all) assign(fd);
        long t2 = System.currentTimeMillis();

        int renamedDecls = 0, refCount = 0, files2 = 0;
        List<String> mapLines = new ArrayList<>();
        for (FileData fd : all) {
            if (fd.decls.isEmpty()) continue;
            List<int[]> edits = new ArrayList<>();
            List<String> news = new ArrayList<>();
            Set<Integer> seen = new HashSet<>();
            for (Decl d : fd.decls) {
                if (seen.add(d.pos)) { edits.add(new int[]{d.pos, d.oldName.length()}); news.add(d.newName); renamedDecls++; }
                mapLines.add(fd.path + ":" + line(fd.text, d.pos) + " " + d.oldName + " -> " + d.newName);
            }
            for (Ref r : fd.refs) {
                Decl d = declByEl.get(r.el);
                if (d == null) continue;
                if (seen.add(r.pos)) { edits.add(new int[]{r.pos, d.oldName.length()}); news.add(d.newName); refCount++; }
            }
            Integer[] order = new Integer[edits.size()];
            for (int i = 0; i < order.length; i++) order[i] = i;
            Arrays.sort(order, (a, b) -> Integer.compare(edits.get(b)[0], edits.get(a)[0]));
            StringBuilder sb = new StringBuilder(fd.text);
            for (int idx : order) {
                int[] e = edits.get(idx);
                String old = sb.substring(e[0], e[0] + e[1]);
                if (!pattern.matcher(old).matches()) throw new IllegalStateException("offset mismatch in " + fd.path + " at " + e[0] + ": '" + old + "'");
                sb.replace(e[0], e[0] + e[1], news.get(idx));
            }
            files2++;
            if (apply) Files.writeString(fd.path, sb.toString(), StandardCharsets.UTF_8);
        }
        if (mapOut != null) Files.write(mapOut, mapLines);
        System.out.println("files scanned: " + all.size() + ", files changed: " + files2 + (apply ? "" : " (dry run)"));
        System.out.println("declarations renamed: " + renamedDecls + " of " + total + ", references rewritten: " + refCount);
        System.out.println("time: analyse " + (t1 - t0) + " ms, naming " + (t2 - t1) + " ms, total " + (System.currentTimeMillis() - t0) + " ms");
    }

    static int line(String text, int pos) {
        int l = 1;
        for (int i = 0; i < pos && i < text.length(); i++) if (text.charAt(i) == '\n') l++;
        return l;
    }

    // ------------------------------------------------------------------ scanning
    static boolean isLocalKind(ElementKind k) {
        return k == ElementKind.LOCAL_VARIABLE || k == ElementKind.PARAMETER || k == ElementKind.EXCEPTION_PARAMETER
                || k == ElementKind.RESOURCE_VARIABLE || k == ElementKind.BINDING_VARIABLE;
    }

    static final class Scan extends TreePathScanner<Void, Void> {
        final FileData fd; final SourcePositions sp;
        Scan(FileData fd, SourcePositions sp) { this.fd = fd; this.sp = sp; }

        @Override public Void visitIdentifier(IdentifierTree t, Void v) {
            long p = sp.getStartPosition(fd.cu, t);
            if (p >= 0) {
                fd.points.put((int) p, t.getName().toString());
                Element e = trees.getElement(getCurrentPath());
                if (e instanceof VariableElement && isLocalKind(e.getKind())) fd.refs.add(new Ref((int) p, e));
            }
            return super.visitIdentifier(t, v);
        }

        @Override public Void visitArrayAccess(ArrayAccessTree t, Void v) {
            markIndex(t.getIndex());
            return super.visitArrayAccess(t, v);
        }

        @Override public Void visitMethodInvocation(MethodInvocationTree t, Void v) {
            String n = methodName(t);
            if (n != null && (n.equals("get") || n.equals("charAt") || n.equals("getItem") || n.equals("getSlot")) && t.getArguments().size() == 1)
                markIndex(t.getArguments().get(0));
            return super.visitMethodInvocation(t, v);
        }

        void markIndex(ExpressionTree e) {
            if (e instanceof IdentifierTree id) {
                // resolve lazily: path of the argument is not current, use the attributed element map
                TreePath p = new TreePath(getCurrentPath(), e);
                Element el = trees.getElement(p);
                if (el != null) indexUse.add(el);
            }
        }

        @Override public Void visitVariable(VariableTree t, Void v) {
            TreePath path = getCurrentPath();
            Element el = trees.getElement(path);
            if (el instanceof VariableElement ve) {
                ElementKind k = ve.getKind();
                int pos = ((JCTree) t).pos;
                String name = t.getName().toString();
                if (isLocalKind(k)) {
                    int end = scopeEnd(path);
                    if (pattern.matcher(name).matches() && !inRecordConstructor(ve)) {
                        Decl d = new Decl();
                        d.el = ve; d.path = path; d.tree = t; d.pos = pos; d.start = pos; d.end = end;
                        d.oldName = name; d.file = fd;
                        fd.decls.add(d);
                    } else {
                        fd.scoped.add(new Scoped(name, pos, end));
                    }
                } else {
                    fd.points.put(pos, name);
                }
            }
            return super.visitVariable(t, v);
        }

        int scopeEnd(TreePath path) {
            TreePath p = path.getParentPath();
            while (p != null) {
                Tree l = p.getLeaf();
                Tree target = null;
                if (l instanceof BlockTree || l instanceof ForLoopTree || l instanceof EnhancedForLoopTree || l instanceof CatchTree
                        || l instanceof LambdaExpressionTree || l instanceof MethodTree || l instanceof TryTree || l instanceof ClassTree) {
                    target = l;
                } else if (l instanceof CaseTree) {
                    // scope of a declaration in a case statement list is the entire switch
                    TreePath q = p.getParentPath();
                    target = q != null ? q.getLeaf() : l;
                }
                if (target != null) {
                    long e = sp.getEndPosition(fd.cu, target);
                    return e >= 0 ? (int) e : Integer.MAX_VALUE;
                }
                p = p.getParentPath();
            }
            return Integer.MAX_VALUE;
        }

        boolean inRecordConstructor(VariableElement ve) {
            Element o = ve.getEnclosingElement();
            return o instanceof ExecutableElement ex && ex.getKind() == ElementKind.CONSTRUCTOR
                    && ex.getEnclosingElement().getKind() == ElementKind.RECORD;
        }
    }

    static String methodName(MethodInvocationTree t) {
        ExpressionTree s = t.getMethodSelect();
        if (s instanceof IdentifierTree i) return i.getName().toString();
        if (s instanceof MemberSelectTree m) return m.getIdentifier().toString();
        return null;
    }

    // ------------------------------------------------------------------ assignment
    static void assign(FileData fd) {
        fd.decls.sort(Comparator.comparingInt(d -> d.pos));
        List<Decl> done = new ArrayList<>();
        for (Decl d : fd.decls) {
            List<String> cands = candidates(d);
            Set<String> forbidden = new HashSet<>();
            for (String n : fd.points.subMap(d.start, d.end).values()) forbidden.add(n);
            for (Scoped s : fd.scoped) if (s.start < d.end && s.end > d.start) forbidden.add(s.name);
            for (Decl o : done) if (o.start < d.end && o.end > d.start) forbidden.add(o.newName);
            String chosen = null;
            for (String c : cands) if (!forbidden.contains(c)) { chosen = c; break; }
            if (chosen == null) {
                String base = cands.get(0);
                for (int n = 2; ; n++) {
                    String c = base + n;
                    if (!forbidden.contains(c)) { chosen = c; break; }
                }
            }
            d.newName = chosen;
            done.add(d);
        }
    }

    static final String[] COUNTERS = {"i", "j", "k", "l", "m", "n"};

    static List<String> candidates(Decl d) {
        List<String> out = new ArrayList<>();
        String n = infer(d);
        if (n == null || n.isEmpty()) n = "value";
        if (n.equals("@counter")) { out.addAll(Arrays.asList(COUNTERS)); return out; }
        out.add(sanitize(n));
        return out;
    }

    static String sanitize(String n) {
        StringBuilder sb = new StringBuilder();
        for (char c : n.toCharArray()) if (Character.isJavaIdentifierPart(c)) sb.append(c);
        if (sb.length() == 0 || !Character.isJavaIdentifierStart(sb.charAt(0))) sb.insert(0, "value");
        String s = sb.toString();
        s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
        if (s.equals("class")) return "clazz";
        if (s.equals("default")) return "defaultValue";
        if (SourceVersion.isKeyword(s, SourceVersion.latest()) || RESERVED.contains(s)) return s + "Value";
        return s;
    }

    static String infer(Decl d) {
        VariableElement el = d.el;
        TypeMirror t = el.asType();
        Tree parent = d.path.getParentPath().getLeaf();
        if (el.getKind() == ElementKind.EXCEPTION_PARAMETER) return "exception";
        if (parent instanceof ForLoopTree fl && fl.getInitializer().contains(d.tree)
                && (t.getKind() == TypeKind.INT || t.getKind() == TypeKind.LONG || t.getKind() == TypeKind.SHORT)) return "@counter";
        if (parent instanceof EnhancedForLoopTree ef && ef.getVariable() == d.tree) {
            String s = singular(exprName(ef.getExpression()));
            if (valid(s)) return s;
            return typeName(t, d);
        }
        if (el.getKind() == ElementKind.PARAMETER && parent instanceof MethodTree mt) {
            Element owner = el.getEnclosingElement();
            if (owner instanceof ExecutableElement ex) {
                int idx = ex.getParameters().indexOf(el);
                String s = overriddenName(ex, idx);
                if (s != null) return s;
            }
        }
        if (d.tree.getInitializer() != null) {
            String s = fromInit(d.path, d.tree.getInitializer());
            if (valid(s)) return s;
        }
        return typeName(t, d);
    }

    static boolean valid(String s) {
        if (s == null || s.isEmpty() || !SourceVersion.isIdentifier(s)) return false;
        return !Set.of("string", "integer", "int", "boolean", "object", "double", "long", "float", "short", "byte",
                "character", "number", "char", "value", "this", "super", "get", "set", "new").contains(s);
    }

    static String overriddenName(ExecutableElement m, int idx) {
        Element owner = m.getEnclosingElement();
        if (!(owner instanceof TypeElement te) || m.getModifiers().contains(Modifier.STATIC) || m.getKind() != ElementKind.METHOD) return null;
        Set<String> seen = new HashSet<>();
        Deque<TypeMirror> q = new ArrayDeque<>(types.directSupertypes(te.asType()));
        while (!q.isEmpty()) {
            TypeMirror st = q.poll();
            Element se = types.asElement(st);
            if (!(se instanceof TypeElement ste) || !seen.add(ste.getQualifiedName().toString())) continue;
            for (Element e : ste.getEnclosedElements()) {
                if (e instanceof ExecutableElement o && o.getSimpleName().equals(m.getSimpleName())
                        && o.getParameters().size() == m.getParameters().size() && elements.overrides(m, o, te)) {
                    String n = o.getParameters().get(idx).getSimpleName().toString();
                    if (!n.matches("arg\\d+") && !pattern.matcher(n).matches() && SourceVersion.isIdentifier(n)) return n;
                }
            }
            q.addAll(types.directSupertypes(st));
        }
        return null;
    }

    // expression -> a name candidate (identifier / getter / field)
    static String exprName(ExpressionTree e) {
        while (e instanceof ParenthesizedTree p) e = p.getExpression();
        if (e instanceof IdentifierTree i) return i.getName().toString();
        if (e instanceof MemberSelectTree m) return m.getIdentifier().toString();
        if (e instanceof MethodInvocationTree mi) {
            String n = methodName(mi);
            if (n == null) return null;
            if (n.equals("keySet")) return "keys";
            if (n.equals("values")) return "values";
            if (n.equals("entrySet")) return "entries";
            for (String pre : GET_PREFIXES) if (n.startsWith(pre) && n.length() > pre.length() && Character.isUpperCase(n.charAt(pre.length())))
                return decap(n.substring(pre.length()));
            return n;
        }
        return null;
    }

    static final String[] GET_PREFIXES = {"get", "create", "build", "make", "load", "find", "read", "parse", "fetch",
            "compute", "calculate", "resolve", "lookup", "new"};
    static final Set<String> BASIC_GETS = Set.of("String", "Int", "Integer", "Boolean", "Double", "Long", "Float", "Short", "Byte",
            "List", "StringList", "IntegerList", "Object", "Map", "Set", "Number", "ConfigurationSection", "Section", "Character", "Bool",
            "Configuration", "Value", "Or", "OrDefault", "Property", "Attribute", "Entry", "Component", "Optional");
    static final Set<String> PLAIN_METHODS = Set.of("size", "length", "name", "id", "key", "level", "amount", "count",
            "index", "type", "ordinal", "result", "path", "content", "text", "message", "location", "world", "block",
            "player", "damage", "slot", "owner");

    static String fromInit(TreePath declPath, ExpressionTree init) {
        ExpressionTree e = init;
        while (true) {
            if (e instanceof ParenthesizedTree p) e = p.getExpression();
            else if (e instanceof TypeCastTree c) e = c.getExpression();
            else break;
        }
        if (e instanceof MethodInvocationTree mi) {
            String n = methodName(mi);
            if (n == null) return null;
            if (n.equals("currentTimeMillis") || n.equals("nanoTime")) return "time";
            for (String pre : GET_PREFIXES) {
                if (!n.startsWith(pre)) continue;
                String rest = n.substring(pre.length());
                if (rest.isEmpty()) {
                    return n.equals("get") ? literalArg(mi) : null;
                }
                if (!Character.isUpperCase(rest.charAt(0))) continue;
                if (BASIC_GETS.contains(rest)) return literalArg(mi);
                if (rest.startsWith("By") || rest.startsWith("Or") || rest.startsWith("From") || rest.startsWith("For")) return null;
                return decap(rest);
            }
            for (String pre : new String[]{"is", "has", "can", "should"}) {
                if (n.length() > pre.length() && n.startsWith(pre) && Character.isUpperCase(n.charAt(pre.length()))) return n;
            }
            if (PLAIN_METHODS.contains(n) && !n.equals("get")) return n;
            return null;
        }
        if (e == init && e instanceof MemberSelectTree ms) {
            Element el = trees.getElement(new TreePath(declPath, ms));
            if (el != null && el.getKind() == ElementKind.FIELD && !el.getModifiers().contains(Modifier.STATIC)) return el.getSimpleName().toString();
        }
        return null;
    }

    static String literalArg(MethodInvocationTree mi) {
        if (mi.getArguments().isEmpty()) return null;
        ExpressionTree a = mi.getArguments().get(0);
        if (a instanceof LiteralTree l && l.getValue() instanceof String s && s.matches("[A-Za-z0-9_.\\- ]{1,32}")) {
            String[] parts = s.split("[^A-Za-z0-9]+");
            StringBuilder sb = new StringBuilder();
            int cnt = 0;
            for (String p : parts) {
                if (p.isEmpty()) continue;
                cnt++;
                sb.append(sb.length() == 0 ? p.toLowerCase().equals(p) ? p : decap(p) : cap(p.toLowerCase().equals(p) ? p : p));
            }
            if (cnt == 0 || cnt > 3) return null;
            String r = sb.toString();
            return Character.isDigit(r.charAt(0)) ? null : r;
        }
        return null;
    }

    // ------------------------------------------------------------------ type based names
    static final Map<String, String> TYPE_NAMES = new HashMap<>();
    static {
        String[][] m = {
                {"ItemStack", "item"}, {"String", "text"}, {"Object", "obj"}, {"CommandSender", "sender"},
                {"ConfigurationSection", "section"}, {"FileConfiguration", "config"}, {"YamlConfiguration", "config"},
                {"Integer", "value"}, {"Long", "value"}, {"Double", "value"}, {"Float", "value"}, {"Short", "value"},
                {"Number", "number"}, {"Boolean", "flag"}, {"Character", "ch"}, {"Byte", "b"}, {"Class", "type"},
                {"StringBuilder", "builder"}, {"Throwable", "throwable"}, {"Runnable", "task"}, {"UUID", "uuid"},
                {"Entry", "entry"}, {"LivingEntity", "entity"}, {"InventoryView", "view"}, {"Plugin", "plugin"},
        };
        for (String[] e : m) TYPE_NAMES.put(e[0], e[1]);
    }

    static String typeName(TypeMirror t, Decl d) {
        switch (t.getKind()) {
            case BOOLEAN: return "flag";
            case INT: return d != null && indexUse.contains(d.el) ? "index" : "count";
            case LONG: case DOUBLE: case FLOAT: case SHORT: return "value";
            case CHAR: return "ch";
            case BYTE: return "b";
            case ARRAY: {
                TypeMirror c = ((ArrayType) t).getComponentType();
                if (c.getKind() == TypeKind.BYTE) return "bytes";
                if (c.getKind() == TypeKind.CHAR) return "chars";
                return plural(typeName(c, null));
            }
            case DECLARED: break;
            default: return "value";
        }
        DeclaredType dt = (DeclaredType) t;
        TypeElement te = (TypeElement) dt.asElement();
        String sn = te.getSimpleName().toString();
        TypeMirror erased = types.erasure(t);
        if (isSub(erased, "java.lang.Throwable")) return "exception";
        if (isSub(erased, "java.util.Map")) return "map";
        if (isSub(erased, "java.util.Collection")) {
            List<? extends TypeMirror> args = dt.getTypeArguments();
            if (args.size() == 1 && args.get(0).getKind() == TypeKind.DECLARED) return plural(typeName(args.get(0), null));
            if (args.size() == 1 && args.get(0).getKind() == TypeKind.WILDCARD) {
                TypeMirror b = ((WildcardType) args.get(0)).getExtendsBound();
                if (b != null && b.getKind() == TypeKind.DECLARED) return plural(typeName(b, null));
            }
            if (isSub(erased, "java.util.Set")) return "set";
            if (isSub(erased, "java.util.Queue")) return "queue";
            if (isSub(erased, "java.util.List")) return "list";
            return "collection";
        }
        if (sn.equals("Optional") && dt.getTypeArguments().size() == 1) {
            return "optional";
        }
        if (sn.endsWith("Event") && sn.length() > 5) return "event";
        String m = TYPE_NAMES.get(sn);
        if (m != null) return m;
        return decapAcronym(sn);
    }

    static boolean isSub(TypeMirror erased, String qn) {
        TypeElement te = elements.getTypeElement(qn);
        return te != null && types.isAssignable(erased, types.erasure(te.asType()));
    }

    static String decapAcronym(String s) {
        int i = 0;
        while (i < s.length() && Character.isUpperCase(s.charAt(i))) i++;
        if (i <= 1) return decap(s);
        if (i == s.length()) return s.toLowerCase();
        return s.substring(0, i - 1).toLowerCase() + s.substring(i - 1);
    }

    static String decap(String s) { return s.isEmpty() ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1); }
    static String cap(String s) { return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1); }

    static String plural(String n) {
        if (n == null || n.isEmpty()) return "values";
        if (n.endsWith("s") || n.endsWith("x") || n.endsWith("ch") || n.endsWith("sh")) return n + "es";
        if (n.endsWith("y") && n.length() > 1 && "aeiou".indexOf(n.charAt(n.length() - 2)) < 0) return n.substring(0, n.length() - 1) + "ies";
        return n + "s";
    }

    static String singular(String n) {
        if (n == null || n.length() < 3) return null;
        for (String suf : new String[]{"List", "Set", "Array", "Collection", "Map", "Queue"})
            if (n.endsWith(suf) && n.length() > suf.length()) return n.substring(0, n.length() - suf.length());
        if (n.endsWith("ies") && n.length() > 4) return n.substring(0, n.length() - 3) + "y";
        if (n.endsWith("sses") || n.endsWith("xes") || n.endsWith("ches") || n.endsWith("shes")) return n.substring(0, n.length() - 2);
        if (n.endsWith("s") && !n.endsWith("ss") && !n.endsWith("us")) return n.substring(0, n.length() - 1);
        return null;
    }
}
