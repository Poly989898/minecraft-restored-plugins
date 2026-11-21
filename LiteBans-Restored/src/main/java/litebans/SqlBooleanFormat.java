package litebans;

import org.jetbrains.annotations.NotNull;

public final class SqlBooleanFormat {
   public static final String computeText(@NotNull String text, @NotNull String text2) {
      SqlValidation.handleMessage(text2);
      return SqlText.set(text, text2 + "={true}");
   }
}
