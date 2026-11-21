package litebans;

import java.io.UnsupportedEncodingException;

public class ArchiveUtil2 {
   private static final int VALUE_255 = 255;

   public static final boolean checkText(String text, byte[] bytes, int value, int value2) {
      byte[] bytes2;
      try {
         bytes2 = text.getBytes("US-ASCII");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }

      return checkBytes(bytes2, 0, bytes2.length, bytes, value, value2, false);
   }

   public static final boolean checkBytes(byte[] bytes, int value, int value2, byte[] bytes2, int value3, int value4, boolean flag) {
      int value5 = value2 < value4 ? value2 : value4;

      for (int i = 0; i < value5; i++) {
         if (bytes[value + i] != bytes2[value3 + i]) {
            return false;
         }
      }

      if (value2 == value4) {
         return true;
      }

      if (!flag) {
         return false;
      }

      if (value2 > value4) {
         for (int i2 = value4; i2 < value2; i2++) {
            if (bytes[value + i2] != 0) {
               return false;
            }
         }
      } else {
         for (int i3 = value2; i3 < value4; i3++) {
            if (bytes2[value3 + i3] != 0) {
               return false;
            }
         }
      }

      return true;
   }

   public static final boolean checkBytes(byte[] bytes, int value) {
      for (int i = 0; i < value; i++) {
         if (bytes[i] != 0) {
            return false;
         }
      }

      return true;
   }
}
