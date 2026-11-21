package litebans;

public final class BitInputStream {
   public static final long computeLongValue(byte[] bytes, int value, int value2) {
      handleIntValue(value2);
      long value3 = 0L;

      for (int i = 0; i < value2; i++) {
         value3 |= (bytes[value + i] & 255L) << 8 * i;
      }

      return value3;
   }

   public static final void handleBytes(byte[] bytes, long value, int value2, int value3) {
      long value4 = value;

      for (int i = 0; i < value3; i++) {
         bytes[value2 + i] = (byte)(value4 & 255L);
         value4 >>= 8;
      }
   }

   private static final void handleIntValue(int value) {
      if (value > 8) {
         throw new IllegalArgumentException("Can't read more than eight bytes into a long value");
      }
   }
}
