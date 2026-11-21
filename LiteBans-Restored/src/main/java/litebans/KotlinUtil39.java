package litebans;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil39 extends KotlinStub21 {
   public static final void handleList(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      if (list.size() > 1) {
         Collections.sort(list);
      }
   }

   public static final void handleList(@NotNull List list, @NotNull Comparator comparator) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      Intrinsics.checkParameterIsNotNull(comparator, "comparator");
      if (list.size() > 1) {
         Collections.sort(list, comparator);
      }
   }
}
