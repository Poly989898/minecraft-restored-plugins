package litebans;

public final class LiteData {
   public static final LiteHelper5 LITE_HELPER = new LiteHelper5(null);
   private final int intValue;
   private final int intValue2;
   private final int intValue3;

   public LiteData(int value, int value2, int value3) {
      this.intValue = value;
      this.intValue2 = value2;
      this.intValue3 = value3;
   }

   public final boolean checkIntValue(int value, int value2, int value3) {
      if (this.intValue > value) {
         return true;
      } else if (this.intValue2 > value2) {
         return true;
      } else {
         return this.intValue2 < value2 ? false : this.intValue3 >= value3;
      }
   }

   @Override
   public String toString() {
      return this.intValue >= 100 && this.intValue2 == 0 && this.intValue3 == 0 ? "" + '#' + this.intValue : "" + this.intValue + '.' + this.intValue2 + '.' + this.intValue3;
   }
}
