package litebans;

class KotlinUtil25 extends KotlinStub12 {
   private static final int VALUE_1073741824 = 1073741824;

   public static final int computeIntValue(int value) {
      return value < 0 ? value : (value < 3 ? value + 1 : (value < 1073741824 ? (int)(value / 0.75F + 1.0F) : Integer.MAX_VALUE));
   }
}
