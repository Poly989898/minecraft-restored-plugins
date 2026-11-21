package litebans;

import org.jetbrains.annotations.NotNull;

public final class KotlinUtil9 {
   public static final String computeText(@NotNull Regex regex, @NotNull CharSequence message, @NotNull String text) {
      return regex.getPattern().matcher(message).replaceAll(text);
   }
}
