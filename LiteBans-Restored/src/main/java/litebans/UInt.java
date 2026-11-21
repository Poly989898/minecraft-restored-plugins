package litebans;

public final class UInt implements Comparable {
   public static final UIntCompanion U_INT_COMPANION = new UIntCompanion(null);
   private final int intValue;
   public static final int VALUE_0 = 0;
   public static final int VALUE_MINUS_1 = -1;
   public static final int VALUE_4 = 4;
   public static final int VALUE_32 = 32;

   public static String toStringImpl(int value) {
      return String.valueOf(value & 4294967295L);
   }

   @Override
   public String toString() {
      return toStringImpl(this.intValue);
   }

   public static int hashCodeImpl(int value) {
      return Integer.hashCode(value);
   }

   @Override
   public int hashCode() {
      return hashCodeImpl(this.intValue);
   }

   public static boolean equalsImpl(int value, Object obj) {
      if (!(obj instanceof UInt)) {
         return false;
      }

      int unbox2 = ((UInt)obj).unbox();
      return value == unbox2;
   }

   @Override
   public boolean equals(Object obj) {
      return equalsImpl(this.intValue, obj);
   }

   private UInt(int value) {
      this.intValue = value;
   }

   public static int constructorImpl(int value) {
      return value;
   }

   public static final UInt box(int value) {
      return new UInt(value);
   }

   public final int unbox() {
      return this.intValue;
   }

   @Override
   public int compareTo(Object obj) {
      int unbox2 = ((UInt)obj).unbox();
      return KotlinUtil14.computeIntValue(this.unbox(), unbox2);
   }
}
