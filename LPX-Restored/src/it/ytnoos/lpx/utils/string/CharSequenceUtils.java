package it.ytnoos.lpx.utils.string;

import it.ytnoos.lpx.WrappedRuntimeException;

public class CharSequenceUtils {

   public CharSequenceUtils() {
   }

   public static CharSequence subSequence(CharSequence charSequence, int value) {
      return charSequence == null ? null : charSequence.subSequence(value, charSequence.length());
   }


   static int indexOf(CharSequence charSequence, CharSequence charSequence2, int value) {
      if (charSequence instanceof String) {
         return ((String)charSequence).indexOf(charSequence2.toString(), value);
      } else if (charSequence instanceof StringBuilder) {
         return ((StringBuilder)charSequence).indexOf(charSequence2.toString(), value);
      } else {
         return charSequence instanceof StringBuffer ? ((StringBuffer)charSequence).indexOf(charSequence2.toString(), value) : charSequence.toString().indexOf(charSequence2.toString(), value);
      }
   }

   static boolean regionMatches(CharSequence charSequence, boolean flag, int value, CharSequence charSequence2, int value2, int value3) {
      if (charSequence instanceof String && charSequence2 instanceof String) {
         return ((String)charSequence).regionMatches(flag, value, (String)charSequence2, value2, value3);
      }

      int value4 = value;
      int value5 = value2;
      int value6 = value3;
      int value7 = charSequence.length() - value;
      int value8 = charSequence2.length() - value2;
      if (value >= 0 && value2 >= 0 && value3 >= 0) {
         if (value7 >= value3 && value8 >= value3) {
            while (value6-- > 0) {
               char ch = charSequence.charAt(value4++);
               char ch2 = charSequence2.charAt(value5++);
               if (ch != ch2) {
                  if (!flag) {
                     return false;
                  }

                  char ch3 = Character.toUpperCase(ch);
                  char ch4 = Character.toUpperCase(ch2);
                  if (ch3 != ch4 && Character.toLowerCase(ch3) != Character.toLowerCase(ch4)) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
