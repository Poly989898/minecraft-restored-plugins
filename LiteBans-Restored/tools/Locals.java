import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Step 1c: splits local variables into independent webs. The obfuscator reuses one local slot for unrelated values of different types
 * (int/boolean/String/...), which makes decompilers emit one source variable with conflicting types. For every method we compute reaching
 * definitions (SourceInterpreter), union the definitions that reach a common use (a "web"), and give each web its own slot.
 * Usage: java tools/Locals.java <in.jar> <out.jar>
 */
public class Locals {
    static int methods, webs, failed, mergedParams;
    static List<String> affected = new ArrayList<>();

    public static void main(String[] a) throws Exception {
        try (JarFile in = new JarFile(a[0]); JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] d = in.getInputStream(je).readAllBytes();
                if (je.getName().endsWith(".class")) d = process(d);
                out.putNextEntry(new JarEntry(je.getName())); out.write(d); out.closeEntry();
            }
        }
        try (PrintWriter pw = new PrintWriter(a[1] + ".affected")) { for (String x : affected) pw.println(x); }
        System.out.println("params merged with entry value in " + affected.size() + " methods; methods " + methods + ", extra variable webs " + webs + ", analysis failed " + failed);
    }

    static byte[] process(byte[] b) {
        ClassNode cn = new ClassNode();
        new ClassReader(b).accept(cn, ClassReader.SKIP_FRAMES);
        for (MethodNode mn : cn.methods) {
            if (mn.instructions.size() == 0) continue;
            try { split(cn.name, mn); } catch (Throwable t) { failed++; System.err.println("locals: " + cn.name + "." + mn.name + mn.desc + ": " + t); }
        }
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /** true when the use at index `use` of slot `var` can be reached from method entry along a path with no store to `var` (CFG walk). */
    static boolean entryReaches(MethodNode mn, AbstractInsnNode[] ins, int use, int var) {
        Map<AbstractInsnNode, Integer> idx = new IdentityHashMap<>();
        for (int i = 0; i < ins.length; i++) idx.put(ins[i], i);
        boolean[] seen = new boolean[ins.length];
        Deque<Integer> q = new ArrayDeque<>(); q.add(0); seen[0] = true;
        while (!q.isEmpty()) {
            int i = q.poll(); AbstractInsnNode n = ins[i]; int op = n.getOpcode();
            if (i == use) return true;
            boolean kills = (isStore(op) && ((VarInsnNode) n).var == var) || (op == Opcodes.IINC && ((IincInsnNode) n).var == var);
            if (kills) continue;
            List<Integer> succ = new ArrayList<>();
            if (n instanceof JumpInsnNode j) { succ.add(idx.get(j.label)); if (op != Opcodes.GOTO) { if (i + 1 < ins.length) succ.add(i + 1); } }
            else if (n instanceof TableSwitchInsnNode t) { succ.add(idx.get(t.dflt)); for (LabelNode l : t.labels) succ.add(idx.get(l)); }
            else if (n instanceof LookupSwitchInsnNode t) { succ.add(idx.get(t.dflt)); for (LabelNode l : t.labels) succ.add(idx.get(l)); }
            else if (!(op >= Opcodes.IRETURN && op <= Opcodes.RETURN) && op != Opcodes.ATHROW) { if (i + 1 < ins.length) succ.add(i + 1); }
            for (TryCatchBlockNode tc : mn.tryCatchBlocks) { int s = idx.get(tc.start), e = idx.get(tc.end); if (i >= s && i < e) succ.add(idx.get(tc.handler)); }
            for (int x : succ) if (!seen[x]) { seen[x] = true; q.add(x); }
        }
        return false;
    }

    static boolean isStore(int op) { return op >= Opcodes.ISTORE && op <= Opcodes.ASTORE; }
    static boolean isLoad(int op) { return op >= Opcodes.ILOAD && op <= Opcodes.ALOAD; }
    static boolean wide(int op) { return op == Opcodes.LSTORE || op == Opcodes.DSTORE || op == Opcodes.LLOAD || op == Opcodes.DLOAD; }

    static void split(String owner, MethodNode mn) throws AnalyzerException {
        methods++;
        Analyzer<SourceValue> an = new Analyzer<>(new SourceInterpreter());
        Frame<SourceValue>[] fr = an.analyze(owner, mn);
        InsnList il = mn.instructions;
        AbstractInsnNode[] ins = il.toArray();
        // union-find over definition ids: instruction index for stores/iinc, -(slot+1) for parameters (entry)
        Map<Integer, Integer> parent = new HashMap<>();
        // definitions
        for (int i = 0; i < ins.length; i++) if (isStore(ins[i].getOpcode()) || ins[i].getOpcode() == Opcodes.IINC) parent.put(i, i);
        int nparams = (Type.getArgumentsAndReturnSizes(mn.desc) >> 2) - ((mn.access & Opcodes.ACC_STATIC) != 0 ? 1 : 0) + ((mn.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1);
        for (int p = 0; p < nparams; p++) parent.put(-(p + 1), -(p + 1));
        java.util.function.Function<Integer, Integer> find = new java.util.function.Function<>() {
            public Integer apply(Integer x) { int r = x; while (parent.get(r) != r) r = parent.get(r); int c = x; while (parent.get(c) != r) { int n = parent.get(c); parent.put(c, r); c = n; } return r; }
        };
        // for each use, unify all its reaching definitions
        Map<Integer, List<Integer>> usesDefs = new HashMap<>();
        IdentityHashMap<AbstractInsnNode, Integer> idx = new IdentityHashMap<>();
        for (int i = 0; i < ins.length; i++) idx.put(ins[i], i);
        for (int i = 0; i < ins.length; i++) {
            AbstractInsnNode n = ins[i]; int op = n.getOpcode(); int var;
            if (isLoad(op)) var = ((VarInsnNode) n).var; else if (op == Opcodes.IINC) var = ((IincInsnNode) n).var; else continue;
            Frame<SourceValue> f = fr[i]; if (f == null) continue;
            SourceValue sv = f.getLocal(var);
            if (var < nparams && !sv.insns.isEmpty()) {
                // does a path from the method entry reach this use without a store? (entry value is invisible in SourceValue merges)
                if (entryReaches(mn, ins, i, var)) { String key = owner + "." + mn.name + mn.desc; if (!affected.contains(key)) affected.add(key); }
            }
            List<Integer> defs = new ArrayList<>();
            for (AbstractInsnNode s : sv.insns) defs.add(idx.get(s));
            // parameters have no defining instruction: the analyzer reports no source for them; treat empty set as the entry definition
            if (defs.isEmpty() && var < nparams) defs.add(-(var + 1));
            usesDefs.put(i, defs);
            for (int k = 1; k < defs.size(); k++) { if (!parent.containsKey(defs.get(0)) || !parent.containsKey(defs.get(k))) continue; parent.put(find.apply(defs.get(k)), find.apply(defs.get(0))); }
            if (op == Opcodes.IINC && !defs.isEmpty() && parent.containsKey(defs.get(0))) parent.put(find.apply(i), find.apply(defs.get(0)));
        }
        // a parameter slot is never split: all stores to it join the entry definition (the entry value is invisible in SourceValue merges,
        // so splitting would lose the parameter value on paths that do not reassign it)
        for (int i = 0; i < ins.length; i++) {
            AbstractInsnNode n = ins[i]; int op = n.getOpcode();
            int v2 = isStore(op) ? ((VarInsnNode) n).var : op == Opcodes.IINC ? ((IincInsnNode) n).var : -1;
            if (v2 >= 0 && v2 < nparams && parent.containsKey(-(v2 + 1))) parent.put(find.apply(i), find.apply(-(v2 + 1)));
        }
        // assign slots: webs of parameters keep their slot; each other web gets a fresh slot, but only when its original slot has >1 web
        Map<Integer, Integer> webSlot = new HashMap<>();
        Map<Integer, Integer> webOrigVar = new HashMap<>();
        Map<Integer, Set<Integer>> varWebs = new HashMap<>();
        for (int i = 0; i < ins.length; i++) {
            AbstractInsnNode n = ins[i]; int op = n.getOpcode(); int var;
            if (isStore(op)) var = ((VarInsnNode) n).var; else if (op == Opcodes.IINC) var = ((IincInsnNode) n).var; else continue;
            int w = find.apply(i); webOrigVar.put(w, var); varWebs.computeIfAbsent(var, k -> new LinkedHashSet<>()).add(w);
        }
        for (int p = 0; p < nparams; p++) { int w = find.apply(-(p + 1)); webOrigVar.put(w, p); varWebs.computeIfAbsent(p, k -> new LinkedHashSet<>()).add(w); webSlot.put(w, p); }
        int next = mn.maxLocals;
        boolean changed = false;
        for (var e : varWebs.entrySet()) {
            int var = e.getKey(); boolean first = true;
            for (int w : e.getValue()) {
                if (webSlot.containsKey(w)) { first = false; continue; }
                if (first && (var >= nparams)) { webSlot.put(w, var); first = false; continue; }   // first web keeps the slot
                // width of the slot
                int size = 1;
                for (int i = 0; i < ins.length; i++) if (isStore(ins[i].getOpcode()) && find.apply(i) == w) size = wide(ins[i].getOpcode()) ? 2 : 1;
                webSlot.put(w, next); next += size; webs++; changed = true;
            }
        }
        if (!changed) return;
        for (int i = 0; i < ins.length; i++) {
            AbstractInsnNode n = ins[i]; int op = n.getOpcode();
            if (isStore(op)) { VarInsnNode v = (VarInsnNode) n; v.var = webSlot.get(find.apply(i)); }
            else if (op == Opcodes.IINC) { IincInsnNode v = (IincInsnNode) n; v.var = webSlot.get(find.apply(i)); }
            else if (isLoad(op)) { List<Integer> defs = usesDefs.get(i); if (defs != null && !defs.isEmpty() && parent.containsKey(defs.get(0))) ((VarInsnNode) n).var = webSlot.get(find.apply(defs.get(0))); }
        }
        mn.maxLocals = next;
        mn.localVariables = null;
    }
}
