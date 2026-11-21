package litebans;

import org.jetbrains.annotations.NotNull;

public final class SqlValidation {
   public static final String computeText(@NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text = "Failed requirement.";
            throw new IllegalArgumentException(text.toString());
         }
      }

      return SqlText.constructorImpl(message + "={false}");
   }

   public static final void handleMessage(@NotNull CharSequence message) {
      if (StringsKt.containsWithDefaults(message, ' ', false, 2, null)
         || StringsKt.containsWithDefaults(message, '=', false, 2, null)
         || StringsKt.containsWithDefaults(message, '>', false, 2, null)
         || StringsKt.containsWithDefaults(message, '<', false, 2, null)
         || StringsKt.containsWithDefaults(message, ',', false, 2, null)) {
         String text = "Failed requirement.";
         throw new IllegalArgumentException(text.toString());
      }
   }

   public static final void handleMessage2(@NotNull CharSequence message) {
      if (!StringsKt.containsWithDefaults(message, ' ', false, 2, null)) {
         String text = "Failed requirement.";
         throw new IllegalArgumentException(text.toString());
      }
   }

   public static final void handleMessage3(@NotNull CharSequence message) {
      if (!StringsKt.containsWithDefaults(message, '=', false, 2, null)) {
         String text = "Failed requirement.";
         throw new IllegalArgumentException(text.toString());
      }
   }
}
