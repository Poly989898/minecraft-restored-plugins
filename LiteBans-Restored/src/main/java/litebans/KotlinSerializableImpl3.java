package litebans;

import java.io.Serializable;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

final class KotlinSerializableImpl3 implements Serializable {
   public static final KotlinSerializableImpl3Companion KOTLIN_SERIALIZABLE_IMPL3_COMPANION = new KotlinSerializableImpl3Companion(null);
   private final String text2;
   private final int intValue;
   private static final long VALUE_0 = 0L;

   public KotlinSerializableImpl3(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "pattern");
      super();
      this.text2 = text;
      this.intValue = value;
   }

   private final Object obtainObj() {
      return new Regex(Pattern.compile(this.text2, this.intValue));
   }
}
