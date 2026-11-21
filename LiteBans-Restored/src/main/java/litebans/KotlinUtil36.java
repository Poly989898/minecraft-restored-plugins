package litebans;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

class KotlinUtil36 extends KotlinUtil35 {
   public static final int computeIntValue(@NotNull Iterable iterable, int value) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      return iterable instanceof Collection ? ((Collection)iterable).size() : value;
   }
}
