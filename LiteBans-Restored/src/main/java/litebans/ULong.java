package litebans;

public final class ULong implements Comparable {
   public static final ULongCompanion U_LONG_COMPANION = new ULongCompanion(null);
   private final short shortValue;
   public static final short VALUE_0 = 0;
   public static final short VALUE_MINUS_1 = -1;
   public static final int VALUE_2 = 2;
   public static final int VALUE_16 = 16;

   public static String toStringImpl(short value) {
      return String.valueOf(value & 65535);
   }

   @Override
   public String toString() {
      return toStringImpl(this.shortValue);
   }

   public static int hashCodeImpl(short value) {
      return Short.hashCode(value);
   }

   @Override
   public int hashCode() {
      return hashCodeImpl(this.shortValue);
   }

   public static boolean equalsImpl(short value, Object obj) {
      if (!(obj instanceof ULong)) {
         return false;
      }

      short unbox2 = ((ULong)obj).unbox();
      return value == unbox2;
   }

   @Override
   public boolean equals(Object obj) {
      return equalsImpl(this.shortValue, obj);
   }

   private ULong(short value) {
      this.shortValue = value;
   }

   public static short constructorImpl(short value) {
      return value;
   }

   public static final ULong box(short value) {
      return new ULong(value);
   }

   public final short unbox() {
      return this.shortValue;
   }

   @Override
   public int compareTo(Object obj) {
      short unbox2 = ((ULong)obj).unbox();
      return Intrinsics.compare(this.unbox() & 65535, unbox2 & 65535);
   }
}
