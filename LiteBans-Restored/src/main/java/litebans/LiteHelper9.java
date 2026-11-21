package litebans;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public class LiteHelper9 {
   private static final char[] TEXT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
   private static final int[] INTS = new int[256];
   private static final byte[] return2 = new byte[0];
   private static final char[] return3 = new char[0];

   public static @Nullable byte[] computeBytes(String text) {
      int value = text != null ? text.length() : 0;
      if (value == 0) {
         return return2;
      }

      int value2 = 0;

      for (int i = 0; i < value; i++) {
         if (INTS[text.charAt(i)] < 0) {
            value2++;
         }
      }

      if ((value - value2) % 4 != 0) {
         return null;
      }

      int value3 = 0;
      int value4 = value;

      while (value4 > 1) {
         if (INTS[text.charAt(--value4)] > 0) {
            break;
         }

         if (text.charAt(value4) == '=') {
            value3++;
         }
      }

      int value5 = ((value - value2) * 6 >> 3) - value3;
      byte[] bytes = new byte[value5];
      int value6 = 0;
      int value7 = 0;

      while (value7 < value5) {
         int value8 = 0;

         for (int i2 = 0; i2 < 4; i2++) {
            int value9 = INTS[text.charAt(value6++)];
            if (value9 >= 0) {
               value8 |= value9 << 18 - i2 * 6;
            } else {
               i2--;
            }
         }

         bytes[value7++] = (byte)(value8 >> 16);
         if (value7 < value5) {
            bytes[value7++] = (byte)(value8 >> 8);
            if (value7 < value5) {
               bytes[value7++] = (byte)value8;
            }
         }
      }

      return bytes;
   }

   public static char[] computeChars(byte[] bytes, boolean flag) {
      int value = bytes != null ? bytes.length : 0;
      if (bytes != null && value != 0) {
         int value2 = value / 3 * 3;
         int value3 = (value - 1) / 3 + 1 << 2;
         int value4 = value3 + (flag ? (value3 - 1) / 76 << 1 : 0);
         char[] chars = new char[value4];
         int value5 = 0;
         int value6 = 0;
         int value7 = 0;

         while (value5 < value2) {
            int value8 = (bytes[value5++] & 255) << 16 | (bytes[value5++] & 255) << 8 | bytes[value5++] & 255;
            chars[value6++] = TEXT[value8 >>> 18 & 63];
            chars[value6++] = TEXT[value8 >>> 12 & 63];
            chars[value6++] = TEXT[value8 >>> 6 & 63];
            chars[value6++] = TEXT[value8 & 63];
            if (flag) {
               if (++value7 == 19 && value6 < value4 - 2) {
                  chars[value6++] = '\r';
                  chars[value6++] = '\n';
                  value7 = 0;
               }
            }
         }

         int value9 = value - value2;
         if (value9 > 0) {
            int value10 = (bytes[value2] & 255) << 10 | (value9 == 2 ? (bytes[value - 1] & 255) << 2 : 0);
            chars[value4 - 4] = TEXT[value10 >> 12];
            chars[value4 - 3] = TEXT[value10 >>> 6 & 63];
            chars[value4 - 2] = value9 == 2 ? TEXT[value10 & 63] : '=';
            chars[value4 - 1] = '=';
         }

         return chars;
      } else {
         return return3;
      }
   }

   public static String computeText(byte[] bytes, boolean flag) {
      return new String(computeChars(bytes, flag));
   }

   static {
      Arrays.fill(INTS, -1);
      int value = 0;
      int length2 = TEXT.length;

      while (value < length2) {
         INTS[TEXT[value]] = value++;
      }

      INTS[61] = 0;
   }
}
