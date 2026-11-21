package litebans;

public class KotlinRandom {
   private final long[] longs;

   public KotlinRandom(long value) {
      long[] values = new long[]{value, value};
      this.longs = values;
   }

   public KotlinRandom(long value, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 1) != 0) {
         value = System.nanoTime();
      }

      this(value);
   }

   public final long[] getState() {
      return this.longs;
   }

   public final void setSeed(long value) {
      this.setState(value, value);
   }

   public final void setState(long value, long value2) {
      this.longs[0] = value;
      this.longs[1] = value2;
   }

   public final long nextLong() {
      long value = this.longs[0];
      long value2 = this.longs[1];
      long value3 = value ^ value << 23;
      long value4 = value3 ^ value2 ^ value3 >> 17 ^ value2 >> 26;
      this.setState(value2, value4);
      return value4 + value2;
   }

   public int nextInt(int value) {
      if (value <= 0) {
         String text = String.valueOf(value);
         throw new IllegalArgumentException(text.toString());
      } else {
         return (int)Math.abs(this.nextLong() % value);
      }
   }

   public final int nextIntLenient(int value) {
      return (int)Math.abs(this.nextLong() % value);
   }

   public final boolean nextBoolean() {
      return this.nextIntLenient(2) == 0;
   }

   public KotlinRandom() {
      this(0L, 1, null);
   }
}
