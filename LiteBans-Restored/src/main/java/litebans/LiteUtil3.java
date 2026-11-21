package litebans;

import org.jetbrains.annotations.NotNull;

class LiteUtil3 extends LiteUtil2 {
   public static final Integer computeInteger(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      return StringsKt.computeInteger(text, 10);
   }

   public static final Integer computeInteger(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      KotlinStub13.computeIntValue(value);
      int value2 = text.length();
      if (value2 == 0) {
         return null;
      }

      char charAt2 = text.charAt(0);
      byte b;
      boolean flag;
      int value3;
      if (Intrinsics.compare(charAt2, 48) < 0) {
         if (value2 == 1) {
            return null;
         }

         b = 1;
         switch (charAt2) {
            case '+':
               flag = false;
               value3 = -2147483647;
               break;
            case ',':
            default:
               return null;
            case '-':
               flag = true;
               value3 = Integer.MIN_VALUE;
         }
      } else {
         b = 0;
         flag = false;
         value3 = -2147483647;
      }

      int value4 = -59652323;
      int value5 = value4;
      int value6 = 0;

      for (int i = b; i < value2; i++) {
         int value7 = KotlinStub13.computeIntValue(text.charAt(i), value);
         if (value7 < 0) {
            return null;
         }

         if (value6 < value5) {
            if (value5 != value4) {
               return null;
            }

            value5 = value3 / value;
            if (value6 < value5) {
               return null;
            }
         }

         int value8 = value6 * value;
         if (value8 < value3 + value7) {
            return null;
         }

         value6 = value8 - value7;
      }

      return flag ? value6 : -value6;
   }

   public static final Long computeLong(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      return StringsKt.computeLong(text, 10);
   }

   public static final Long computeLong(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      KotlinStub13.computeIntValue(value);
      int value2 = text.length();
      if (value2 == 0) {
         return null;
      }

      char charAt2 = text.charAt(0);
      byte b;
      boolean flag;
      long value3;
      if (Intrinsics.compare(charAt2, 48) < 0) {
         if (value2 == 1) {
            return null;
         }

         b = 1;
         switch (charAt2) {
            case '+':
               flag = false;
               value3 = -9223372036854775807L;
               break;
            case ',':
            default:
               return null;
            case '-':
               flag = true;
               value3 = Long.MIN_VALUE;
         }
      } else {
         b = 0;
         flag = false;
         value3 = -9223372036854775807L;
      }

      long value4 = -256204778801521550L;
      long value5 = value4;
      long value6 = 0L;

      for (int i = b; i < value2; i++) {
         int value7 = KotlinStub13.computeIntValue(text.charAt(i), value);
         if (value7 < 0) {
            return null;
         }

         if (value6 < value5) {
            if (value5 != value4) {
               return null;
            }

            value5 = value3 / value;
            if (value6 < value5) {
               return null;
            }
         }

         long value8 = value6 * value;
         if (value8 < value3 + value7) {
            return null;
         }

         value6 = value8 - value7;
      }

      return flag ? value6 : -value6;
   }
}
