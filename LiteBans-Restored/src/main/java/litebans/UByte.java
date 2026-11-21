package litebans;

public final class UByte implements Comparable {
   public static final UByteCompanion U_BYTE_COMPANION = new UByteCompanion(null);
   private final byte byteValue;
   public static final byte VALUE_0 = 0;
   public static final byte VALUE_MINUS_1 = -1;
   public static final int VALUE_1 = 1;
   public static final int VALUE_8 = 8;

   public static String toStringImpl(byte b) {
      return String.valueOf(b & 255);
   }

   @Override
   public String toString() {
      return toStringImpl(this.byteValue);
   }

   public static int hashCodeImpl(byte b) {
      return Byte.hashCode(b);
   }

   @Override
   public int hashCode() {
      return hashCodeImpl(this.byteValue);
   }

   public static boolean equalsImpl(byte b, Object obj) {
      if (!(obj instanceof UByte)) {
         return false;
      }

      byte unbox2 = ((UByte)obj).unbox();
      return b == unbox2;
   }

   @Override
   public boolean equals(Object obj) {
      return equalsImpl(this.byteValue, obj);
   }

   private UByte(byte b) {
      this.byteValue = b;
   }

   public static byte constructorImpl(byte b) {
      return b;
   }

   public static final UByte box(byte b) {
      return new UByte(b);
   }

   public final byte unbox() {
      return this.byteValue;
   }

   @Override
   public int compareTo(Object obj) {
      byte unbox2 = ((UByte)obj).unbox();
      return Intrinsics.compare(this.unbox() & 0xFF, unbox2 & 0xFF);
   }
}
