package it.ytnoos.lpx.utils.string;


public class StringUtils {
   private StringUtils() {
   }

   public static int length(CharSequence charSequence) {
      return charSequence == null ? 0 : charSequence.length();
   }

   public static boolean startsWithIgnoreCase(CharSequence charSequence, CharSequence charSequence2) {
      return startsWith(charSequence, charSequence2, true);
   }

   public static boolean containsIgnoreCase(CharSequence charSequence, CharSequence charSequence2) {
      if (charSequence != null && charSequence2 != null) {
         int value = charSequence2.length();
         int value2 = charSequence.length() - value;

         for (int value3 = 0; value3 <= value2; value3++) {
            if (CharSequenceUtils.regionMatches(charSequence, true, value3, charSequence2, 0, value)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int countMatches(CharSequence charSequence, CharSequence charSequence2) {
      if (!isEmpty(charSequence) && !isEmpty(charSequence2)) {
         int value = 0;
         int value2 = 0;

         while ((value2 = CharSequenceUtils.indexOf(charSequence, charSequence2, value2)) != -1) {
            value++;
            value2 += charSequence2.length();
         }

         return value;
      } else {
         return 0;
      }
   }

   public static boolean isEmpty(CharSequence charSequence) {
      return charSequence == null || charSequence.length() == 0;
   }

   private static boolean startsWith(CharSequence charSequence, CharSequence charSequence2, boolean flag) {
      if (charSequence != null && charSequence2 != null) {
         int value = charSequence2.length();
         return value > charSequence.length() ? false : CharSequenceUtils.regionMatches(charSequence, flag, 0, charSequence2, 0, value);
      } else {
         return charSequence == charSequence2;
      }
   }

}
