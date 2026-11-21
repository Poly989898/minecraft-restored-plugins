import java.lang.classfile.*;
import java.lang.classfile.instruction.InvokeInstruction;
import java.nio.file.*;
import java.util.*;

/** Post-compile step: calls to name_ (keyword-named server methods exposed by Shim.java) are turned back into name. */
public class Unrename {
    static final Set<String> KEYWORDS = Set.of("do", "if", "for", "int", "new", "try", "case", "else", "goto", "byte", "char", "long", "void", "enum", "this", "null", "true", "false", "class", "final", "short", "float", "double", "boolean", "break", "catch", "const", "super", "while", "throw", "throws", "import", "package", "return", "static", "switch", "public", "private", "protected", "abstract", "interface", "extends", "implements", "native", "volatile", "transient", "synchronized", "strictfp", "instanceof", "assert", "default", "continue", "finally");
    public static void main(String[] a) throws Exception {
        ClassFile cf = ClassFile.of();
        int[] n = {0};
        try (var st = Files.walk(Path.of(a[0]))) {
            for (Path p : (Iterable<Path>) st.filter(x -> x.toString().endsWith(".class"))::iterator) {
                ClassModel cm = cf.parse(Files.readAllBytes(p));
                boolean[] hit = {false};
                byte[] out = cf.transformClass(cm, ClassTransform.transformingMethodBodies((cob, coe) -> {
                    if (coe instanceof InvokeInstruction ii && ii.name().stringValue().endsWith("_")
                        && KEYWORDS.contains(ii.name().stringValue().substring(0, ii.name().stringValue().length() - 1))
                        && ii.owner().asInternalName().startsWith("net/minecraft/")) {
                        String nm = ii.name().stringValue();
                        cob.invoke(ii.opcode(), ii.owner().asSymbol(), nm.substring(0, nm.length() - 1), ii.typeSymbol(), ii.isInterface());
                        hit[0] = true; n[0]++;
                    } else cob.with(coe);
                }));
                if (hit[0]) Files.write(p, out);
            }
        }
        System.out.println("Unrename: restored " + n[0] + " keyword-named calls");
    }
}
