package litebans;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class KotlinUtil7 extends LiteUtil3 {
   public static final boolean checkText(@Nullable String text, @Nullable String text2, boolean flag) {
      if (text == null) {
         return text2 == null;
      } else {
         return !flag ? text.equals(text2) : text.equalsIgnoreCase(text2);
      }
   }

   public static final String computeText(@NotNull String text, char c, char c2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      if (!flag) {
         return text.replace(c, c2);
      }

      int value = text.length();
      StringBuilder stringBuilder = new StringBuilder(value);
      CharSequence message = text;

      for (int i = 0; i < message.length(); i++) {
         char charAt2 = message.charAt(i);
         stringBuilder.append(KotlinStub13.checkCharValue(charAt2, c, flag) ? c2 : charAt2);
      }

      return stringBuilder.toString();
   }

   public static String computeText(String text, char c, char c2, boolean flag, int value, Object obj) {
      if ((value & 4) != 0) {
         flag = false;
      }

      return StringsKt.computeText(text, c, c2, flag);
   }

   public static final String computeText(@NotNull String text, @NotNull String text2, @NotNull String text3, boolean flag) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "oldValue");
      Intrinsics.checkParameterIsNotNull(text3, "newValue");
      String text4 = text;
      int indexOf2 = StringsKt.indexOf(text4, text2, 0, flag);
      if (indexOf2 < 0) {
         return text4;
      }

      int value = text2.length();
      int value2 = KotlinRangesKtSub.coerceAtLeast(value, 1);
      int value3 = text4.length() - value + text3.length();
      if (value3 < 0) {
         throw new OutOfMemoryError();
      }

      StringBuilder stringBuilder = new StringBuilder(value3);
      int value4 = 0;

      do {
         stringBuilder.append(text4, value4, indexOf2).append(text3);
         value4 = indexOf2 + value;
         if (indexOf2 >= text4.length()) {
            break;
         }

         indexOf2 = StringsKt.indexOf(text4, text2, indexOf2 + value2, flag);
      } while (indexOf2 > 0);

      return stringBuilder.append(text4, value4, text4.length()).toString();
   }

   public static String computeText(String text, String text2, String text3, boolean flag, int value, Object obj) {
      if ((value & 4) != 0) {
         flag = false;
      }

      return StringsKt.computeText(text, text2, text3, flag);
   }

   public static final boolean checkText2(@NotNull String text, @NotNull String text2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "prefix");
      return !flag ? text.startsWith(text2) : StringsKt.checkText(text, 0, text2, 0, text2.length(), flag);
   }

   public static boolean checkText(String text, String text2, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.checkText2(text, text2, flag);
   }

   public static final boolean checkText3(@NotNull String text, @NotNull String text2, boolean flag) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "suffix");
      return !flag ? text.endsWith(text2) : StringsKt.checkText(text, text.length() - text2.length(), text2, 0, text2.length(), true);
   }

   public static boolean checkText2(String text, String text2, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return StringsKt.checkText3(text, text2, flag);
   }

   public static final boolean checkText(@NotNull String text, int value, @NotNull String text2, int value2, int value3, boolean flag) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(text2, "other");
      return !flag ? text.regionMatches(value, text2, value2, value3) : text.regionMatches(flag, value, text2, value2, value3);
   }

   public static final String computeText(@NotNull String text, @NotNull Locale locale) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      Intrinsics.checkParameterIsNotNull(locale, "locale");
      if (text.length() > 0) {
         char charAt2 = text.charAt(0);
         if (Character.isLowerCase(charAt2)) {
            StringBuilder stringBuilder = new StringBuilder();
            char titleCase = Character.toTitleCase(charAt2);
            if (titleCase != Character.toUpperCase(charAt2)) {
               stringBuilder.append(titleCase);
            } else {
               String text2 = text;
               byte b = 0;
               byte b2 = 1;
               String substring2 = text2.substring(b, b2);
               Intrinsics.checkNotNullExpressionValue(substring2, "");
               stringBuilder.append(substring2.toUpperCase(locale));
            }

            String text3 = text;
            byte b3 = 1;
            stringBuilder.append(text3.substring(b3));
            return stringBuilder.toString();
         }
      }

      return text;
   }
}
