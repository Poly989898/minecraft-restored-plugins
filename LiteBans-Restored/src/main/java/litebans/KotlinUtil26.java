package litebans;

import org.jetbrains.annotations.NotNull;

public final class KotlinUtil26 {
   public static final KotlinHelper11 KOTLIN_HELPER = new KotlinHelper11(null);
   private final byte[] bytes2;
   private byte byteValue;
   private short shortValue;
   public static final int VALUE_10 = 10;
   public static final int VALUE_10_2 = 10;
   public static final int VALUE_30 = 30;
   public static final int VALUE_1600 = 1600;
   public static final int VALUE_600 = 600;
   public static final int VALUE_16000 = 16000;
   public static final int VALUE_568 = 568;
   public static final int VALUE_70 = 70;
   private static final Pair return2 = TuplesKt.computePair(ULong.box((short)0), StringMatching.obtainBytes());

   private KotlinUtil26(byte[] bytes, byte b, short value) {
      this.bytes2 = bytes;
      this.byteValue = b;
      this.shortValue = value;
   }

   public KotlinUtil26(byte[] bytes, byte b, short value, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 2) != 0) {
         b = 0;
      }

      if ((value2 & 4) != 0) {
         value = 0;
      }

      this(bytes, b, value, null);
   }

   public final byte[] getBytes2() {
      return this.bytes2;
   }

   public final byte getByteValue() {
      return this.byteValue;
   }

   public final void setByteValue(byte b) {
      this.byteValue = b;
   }

   public final int obtainIntValue() {
      return this.bytes2.length;
   }

   public final boolean checkBytes(@NotNull byte[] bytes, int value, int value2) {
      return this.obtainIntValue() == value2 - value && this.bytes2[0] == bytes[value] ? StringMatching.bytesEqual(this.bytes2, bytes, value, value2) : false;
   }

   public final long obtainLongValue() {
      long value = this.obtainIntValue() - 2;
      long value2 = this.shortValue & 65535L;
      long value3 = value + 1L;
      long value4 = value * value3;
      long value5 = value4 * value4 * value4;
      long value6 = value2 * value2;
      long value7 = value6 * value6 * value5 * value5 * value5;
      return (value4 << 30) + (value7 >> 10);
   }

   @Override
   public String toString() {
      return super.toString();
   }

   public KotlinUtil26(byte[] bytes, byte b, short value, DefaultConstructorMarker defaultConstructorMarker) {
      this(bytes, b, value);
   }

   public static final Pair obtainPair() {
      return return2;
   }
}
