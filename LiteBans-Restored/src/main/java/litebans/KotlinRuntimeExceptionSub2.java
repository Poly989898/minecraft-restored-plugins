package litebans;

import org.jetbrains.annotations.NotNull;

public final class KotlinRuntimeExceptionSub2 extends RuntimeException {
   public KotlinRuntimeExceptionSub2(@NotNull String text) {
      super(text);
   }

   public KotlinRuntimeExceptionSub2(String text, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         text = "";
      }

      this(text);
   }

   public KotlinRuntimeExceptionSub2() {
      this(null, 1, null);
   }
}
