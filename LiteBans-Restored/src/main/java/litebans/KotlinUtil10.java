package litebans;

public final class KotlinUtil10 {
   private static final int computeIntValue(int value, int value2) {
      int value3 = value % value2;
      return value3 >= 0 ? value3 : value3 + value2;
   }

   private static final int computeIntValue(int value, int value2, int value3) {
      return computeIntValue(computeIntValue(value, value3) - computeIntValue(value2, value3), value3);
   }

   public static final int computeIntValue2(int value, int value2, int value3) {
      int value4;
      if (value3 > 0) {
         value4 = value >= value2 ? value2 : value2 - computeIntValue(value2, value, value3);
      } else {
         if (value3 >= 0) {
            throw new IllegalArgumentException("Step is zero.");
         }

         value4 = value <= value2 ? value2 : value2 + computeIntValue(value, value2, -value3);
      }

      return value4;
   }
}
