package litebans;

class CharsKt__CharKt extends CharsKtLocale {
   public static final boolean checkCharValue(char c, char c2, boolean flag) {
      if (c == c2) {
         return true;
      }

      if (!flag) {
         return false;
      }

      char upperCase = Character.toUpperCase(c);
      char upperCase2 = Character.toUpperCase(c2);
      return upperCase == upperCase2 || Character.toLowerCase(upperCase) == Character.toLowerCase(upperCase2);
   }
}
