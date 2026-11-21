import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Step 1d: method references with captured arguments. A decompiler cannot print `Foo::bar` when the invokedynamic also captures values
 * (it silently drops them). For every LambdaMetafactory call whose implementation is not already a `lambda$` method of the same class,
 * we add a synthetic static `lambda$wrapN(captured..., params...)` that forwards to the implementation and point the call site at it;
 * the decompiler then prints a real lambda with the captured variables.
 * Usage: java tools/Lambdas.java <in.jar> <out.jar>
 */
public class Lambdas {
    static int wrapped;

    public static void main(String[] a) throws Exception {
        try (JarFile in = new JarFile(a[0]); JarOutputStream out = new JarOutputStream(new FileOutputStream(a[1]))) {
            for (Enumeration<JarEntry> e = in.entries(); e.hasMoreElements();) {
                JarEntry je = e.nextElement();
                byte[] d = in.getInputStream(je).readAllBytes();
                if (je.getName().endsWith(".class")) d = process(d);
                out.putNextEntry(new JarEntry(je.getName())); out.write(d); out.closeEntry();
            }
        }
        System.out.println("wrapped lambda call sites: " + wrapped);
    }

    static byte[] process(byte[] b) {
        ClassNode cn = new ClassNode();
        new ClassReader(b).accept(cn, ClassReader.SKIP_FRAMES);
        List<MethodNode> added = new ArrayList<>();
        int n = 0;
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode in : mn.instructions) {
                if (!(in instanceof InvokeDynamicInsnNode id) || !id.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) continue;
                if (id.bsmArgs.length < 3 || !(id.bsmArgs[1] instanceof Handle impl) || !(id.bsmArgs[2] instanceof Type inst)) continue;
                if (impl.getOwner().equals(cn.name) && impl.getName().startsWith("lambda$")) continue;
                Type[] captured = Type.getArgumentTypes(id.desc);
                boolean instance = impl.getTag() == Opcodes.H_INVOKEVIRTUAL || impl.getTag() == Opcodes.H_INVOKEINTERFACE || impl.getTag() == Opcodes.H_INVOKESPECIAL;
                // `receiver::method` with a single captured receiver prints fine as a method reference
                if (instance && captured.length == 1 && Type.getArgumentTypes(impl.getDesc()).length == Type.getArgumentTypes(inst.getDescriptor()).length) continue;
                Type[] params = Type.getArgumentTypes(inst.getDescriptor());
                Type ret = Type.getReturnType(inst.getDescriptor());
                Type[] all = new Type[captured.length + params.length];
                System.arraycopy(captured, 0, all, 0, captured.length);
                System.arraycopy(params, 0, all, captured.length, params.length);
                String name = "lambda$wrap" + (n++);
                String desc = Type.getMethodDescriptor(ret, all);
                MethodNode w = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name, desc, null, null);
                InsnList il = w.instructions;
                if (impl.getTag() == Opcodes.H_NEWINVOKESPECIAL) { il.add(new TypeInsnNode(Opcodes.NEW, impl.getOwner())); il.add(new InsnNode(Opcodes.DUP)); }
                int slot = 0;
                for (Type t : all) { il.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot)); slot += t.getSize(); }
                int op = switch (impl.getTag()) {
                    case Opcodes.H_INVOKESTATIC -> Opcodes.INVOKESTATIC;
                    case Opcodes.H_INVOKEINTERFACE -> Opcodes.INVOKEINTERFACE;
                    case Opcodes.H_INVOKESPECIAL, Opcodes.H_NEWINVOKESPECIAL -> Opcodes.INVOKESPECIAL;
                    default -> Opcodes.INVOKEVIRTUAL;
                };
                il.add(new MethodInsnNode(op, impl.getOwner(), impl.getName(), impl.getDesc(), impl.isInterface()));
                Type implRet = impl.getTag() == Opcodes.H_NEWINVOKESPECIAL ? Type.getObjectType(impl.getOwner()) : Type.getReturnType(impl.getDesc());
                if (ret.getSort() == Type.VOID) { if (implRet.getSort() != Type.VOID) il.add(new InsnNode(implRet.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP)); il.add(new InsnNode(Opcodes.RETURN)); }
                else il.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
                w.maxStack = slot + 4; w.maxLocals = slot;
                added.add(w);
                id.bsmArgs[1] = new Handle(Opcodes.H_INVOKESTATIC, cn.name, name, desc, (cn.access & Opcodes.ACC_INTERFACE) != 0);
                wrapped++;
            }
        }
        cn.methods.addAll(added);
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
