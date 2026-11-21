package litebans;

public final class KotlinHelper7 {
   private final int intValue;
   private final int intValue2;
   private final int intValue3;

   public KotlinHelper7(int value, int value2, int value3) {
      this.intValue = value;
      this.intValue2 = value2;
      this.intValue3 = value3;
   }

   public KotlinHelper7(int value, int value2, int value3, int value4, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value4 & 1) != 0) {
         value = 0;
      }

      if ((value4 & 2) != 0) {
         value2 = 0;
      }

      if ((value4 & 4) != 0) {
         value3 = 0;
      }

      this(value, value2, value3);
   }

   public final int getIntValue() {
      return this.intValue;
   }

   public final int getIntValue2() {
      return this.intValue2;
   }

   public final int getIntValue3() {
      return this.intValue3;
   }

   public KotlinHelper7() {
      this(0, 0, 0, 7, null);
   }
}
