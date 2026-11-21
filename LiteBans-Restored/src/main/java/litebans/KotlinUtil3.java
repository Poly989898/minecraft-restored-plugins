package litebans;

import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;

class KotlinUtil3 extends KotlinStub6 {
   public static final char computeCharValue(@NotNull CharSequence message) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      if (message.length() == 0) {
         throw new NoSuchElementException("Char sequence is empty.");
      } else {
         return message.charAt(0);
      }
   }

   public static final Character computeCharacter(@NotNull CharSequence message, int value) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      return (0 <= value ? value < message.length() : false) ? message.charAt(value) : null;
   }

   public static final char computeCharValue2(@NotNull CharSequence message) {
      Intrinsics.checkParameterIsNotNull(message, "<this>");
      if (message.length() == 0) {
         throw new NoSuchElementException("Char sequence is empty.");
      } else {
         return message.charAt(StringsKt.getLastIndex(message));
      }
   }

   public static final String computeText(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      if (value < 0) {
         String text2 = "Requested character count " + value + " is less than zero.";
         throw new IllegalArgumentException(text2.toString());
      } else {
         return StringsKt.computeText2(text, KotlinRangesKtSub.coerceAtLeast(text.length() - value, 0));
      }
   }

   public static final String computeText2(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      if (value < 0) {
         String text2 = "Requested character count " + value + " is less than zero.";
         throw new IllegalArgumentException(text2.toString());
      } else {
         String text3 = text;
         byte b = 0;
         int value2 = KotlinRangesKtSub.coerceAtMost(value, text.length());
         return text3.substring(b, value2);
      }
   }
}
