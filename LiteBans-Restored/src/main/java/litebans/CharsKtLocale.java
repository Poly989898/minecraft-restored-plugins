package litebans;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

class CharsKtLocale {
   public static final boolean checkCharValue(char c) {
      return Character.isWhitespace(c) || Character.isSpaceChar(c);
   }

   public static final String computeText(char c, @NotNull Locale locale) {
      Intrinsics.checkParameterIsNotNull(locale, "locale");
      String text = String.valueOf(c);
      Intrinsics.checkNotNullExpressionValue(text, "");
      return text.toUpperCase(locale);
   }

   public static final String computeText2(char c, @NotNull Locale locale) {
      Intrinsics.checkParameterIsNotNull(locale, "locale");
      String text = KotlinStub13.computeText(c, locale);
      if (text.length() > 1) {
         String text2;
         if (c == 329) {
            text2 = text;
         } else {
            char charAt2 = text.charAt(0);
            byte b2 = 1;
            Intrinsics.checkNotNullExpressionValue(text, "");
            String substring2 = text.substring(b2);
            Intrinsics.checkNotNullExpressionValue(substring2, "");
            String lowerCase = substring2.toLowerCase(Locale.ROOT);
            text2 = charAt2 + lowerCase;
         }

         return text2;
      } else {
         String text3 = String.valueOf(c);
         Intrinsics.checkNotNullExpressionValue(text3, "");
         return !Intrinsics.areEqual(text, text3.toUpperCase(Locale.ROOT)) ? text : String.valueOf(Character.toTitleCase(c));
      }
   }

   public static final int computeIntValue(char c, int value) {
      return Character.digit((int)c, value);
   }

   public static final int computeIntValue(int value) {
      if (2 <= value ? value >= 37 : true) {
         throw new IllegalArgumentException("radix " + value + " was not in valid range " + new KotlinIntProgressionSub(2, 36));
      } else {
         return value;
      }
   }
}
