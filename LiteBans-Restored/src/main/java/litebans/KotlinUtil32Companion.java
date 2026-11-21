package litebans;

import org.jetbrains.annotations.NotNull;

public final class KotlinUtil32Companion {
   private KotlinUtil32Companion() {
   }

   public final String computeText(@NotNull Object[] objs, @NotNull String text) {
      return ArraysKt.joinToStringWithDefaults((Object[])objs, text, null, null, 0, null, null, 62, null);
   }

   public KotlinUtil32Companion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
