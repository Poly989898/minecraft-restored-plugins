package litebans;

class RangesKt extends KotlinStub3 {
   public static final IntProgression downTo(int value, int value2) {
      return IntProgression.INT_PROGRESSION_COMPANION.computeIntProgression(value, value2, -1);
   }

   public static final KotlinIntProgressionSub until(int value, int value2) {
      return value2 <= Integer.MIN_VALUE ? KotlinIntProgressionSub.KOTLIN_INT_PROGRESSION_SUB_COMPANION.obtainKotlinIntProgressionSub() : new KotlinIntProgressionSub(value, value2 - 1);
   }

   public static final int coerceAtLeast(int value, int value2) {
      return value < value2 ? value2 : value;
   }

   public static final int coerceAtMost(int value, int value2) {
      return value > value2 ? value2 : value;
   }

   public static final int coerceIn(int value, int value2, int value3) {
      if (value2 > value3) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + value3 + " is less than minimum " + value2 + 46);
      } else if (value < value2) {
         return value2;
      } else {
         return value > value3 ? value3 : value;
      }
   }
}
