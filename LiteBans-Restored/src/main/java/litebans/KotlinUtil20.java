package litebans;

import org.jetbrains.annotations.NotNull;

public final class KotlinUtil20 {
   public static final UByte computeUByte(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      return computeUByte(text, 10);
   }

   public static final UByte computeUByte(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      UInt uInt = computeUInt(text, value);
      if (uInt != null) {
         int unbox2 = uInt.unbox();
         byte b = -1;
         return Integer.compareUnsigned(unbox2, UInt.constructorImpl(b & 255)) > 0 ? null : UByte.box(UByte.constructorImpl((byte)unbox2));
      } else {
         return null;
      }
   }

   public static final UInt computeUInt(@NotNull String text, int value) {
      Intrinsics.checkParameterIsNotNull(text, "<this>");
      KotlinStub13.computeIntValue(value);
      int value2 = text.length();
      if (value2 == 0) {
         return null;
      }

      byte b = -1;
      char charAt2 = text.charAt(0);
      byte b2;
      if (Intrinsics.compare(charAt2, 48) < 0) {
         if (value2 == 1 || charAt2 != '+') {
            return null;
         }

         b2 = 1;
      } else {
         b2 = 0;
      }

      int value3 = 119304647;
      int value4 = value3;
      int constructor = UInt.constructorImpl(value);
      int value5 = 0;

      for (int i = b2; i < value2; i++) {
         int value6 = KotlinStub13.computeIntValue(text.charAt(i), value);
         if (value6 < 0) {
            return null;
         }

         if (Integer.compareUnsigned(value5, value4) > 0) {
            if (value4 != value3) {
               return null;
            }

            value4 = Integer.divideUnsigned(b, constructor);
            if (Integer.compareUnsigned(value5, value4) > 0) {
               return null;
            }
         }

         int constructor2 = UInt.constructorImpl(value5 * constructor);
         value5 = UInt.constructorImpl(constructor2 + UInt.constructorImpl(value6));
         if (Integer.compareUnsigned(value5, constructor2) < 0) {
            return null;
         }
      }

      return UInt.box(value5);
   }
}
