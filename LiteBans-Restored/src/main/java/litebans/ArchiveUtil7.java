package litebans;

import java.io.IOException;
import java.math.BigInteger;

public class ArchiveUtil7 {
   private static final int VALUE_255 = 255;
   static final ZipEncoding ZIP_ENCODING = ZipEncodingHelper.computeZipEncoding(null);
   static final ZipEncoding ZIP_ENCODING_2 = new FallbackZipEncoding();

   public static final long parseOctal(byte[] bytes, int value, int value2) {
      long value3 = 0L;
      int value4 = value + value2;
      int value5 = value;
      if (value2 < 2) {
         throw new IllegalArgumentException("Length " + value2 + " must be at least 2");
      }

      if (bytes[value5] == 0) {
         return 0L;
      }

      while (value5 < value4 && bytes[value5] == 32) {
         value5++;
      }

      for (byte b = bytes[value4 - 1]; value5 < value4 && (b == 0 || b == 32); b = bytes[value4 - 1]) {
         value4--;
      }

      while (value5 < value4) {
         byte b2 = bytes[value5];
         if (b2 < 48 || b2 > 55) {
            throw new IllegalArgumentException(exceptionMessage(bytes, value, value2, value5, b2));
         }

         value3 = (value3 << 3) + (b2 - 48);
         value5++;
      }

      return value3;
   }

   public static final long parseOctalOrBinary(byte[] bytes, int value, int value2) {
      if ((bytes[value] & 128) == 0) {
         return parseOctal(bytes, value, value2);
      }

      boolean flag = bytes[value] == -1;
      return value2 < 9 ? parseBinaryLong(bytes, value, value2, flag) : parseBinaryBigInteger(bytes, value, value2, flag);
   }

   private static final long parseBinaryLong(byte[] bytes, int value, int value2, boolean flag) {
      if (value2 >= 9) {
         throw new IllegalArgumentException("At offset " + value + ", " + value2 + " byte binary number exceeds maximum signed long value");
      }

      long value3 = 0L;

      for (int i = 1; i < value2; i++) {
         value3 = (value3 << 8) + (bytes[value + i] & 0xFF);
      }

      if (flag) {
         long value4 = value3 - 1L;
         value3 = value4 ^ (long)Math.pow(2.0, (value2 - 1) * 8.0) - 1L;
      }

      return flag ? -value3 : value3;
   }

   private static final long parseBinaryBigInteger(byte[] bytes, int value, int value2, boolean flag) {
      byte[] bytes2 = new byte[value2 - 1];
      System.arraycopy(bytes, value + 1, bytes2, 0, value2 - 1);
      BigInteger bigInteger = new BigInteger(bytes2);
      if (flag) {
         bigInteger = bigInteger.add(BigInteger.valueOf(-1L)).not();
      }

      if (bigInteger.bitLength() > 63) {
         throw new IllegalArgumentException("At offset " + value + ", " + value2 + " byte binary number exceeds maximum signed long value");
      } else {
         return flag ? -bigInteger.longValue() : bigInteger.longValue();
      }
   }

   public static final boolean parseBoolean(byte[] bytes, int value) {
      return bytes[value] == 1;
   }

   private static final String exceptionMessage(byte[] bytes, int value, int value2, int value3, byte b) {
      String string = new String(bytes, value, value2);
      String replaceAll2 = string.replaceAll("\u0000", "{NUL}");
      return "Invalid byte " + b + " at offset " + (value3 - value) + " in '" + replaceAll2 + "' len=" + value2;
   }

   public static final String parseName(byte[] bytes, int value, int value2) {
      try {
         return parseName(bytes, value, value2, ZIP_ENCODING);
      } catch (Throwable e) {
         if (!(e instanceof IOException)) {
            throw SneakyThrow.<RuntimeException>rethrow(e);
         }
         try {
            return parseName(bytes, value, value2, ZIP_ENCODING_2);
         } catch (Throwable e2) {
            if (!(e2 instanceof IOException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            IOException ioException = (IOException) e2;
            throw new RuntimeException(ioException);
         }
      }
   }

   public static final String parseName(byte[] bytes, int value, int value2, ZipEncoding zipEncoding) {
      int value3 = 0;

      for (int i = value; value3 < value2 && bytes[i] != 0; i++) {
         value3++;
      }

      if (value3 > 0) {
         byte[] bytes2 = new byte[value3];
         System.arraycopy(bytes, value, bytes2, 0, value3);
         return zipEncoding.computeText(bytes2);
      } else {
         return "";
      }
   }

   public static final TarArchiveStructSparse parseSparse(byte[] bytes, int value) {
      long octalOrBinary = parseOctalOrBinary(bytes, value, 12);
      long octalOrBinary2 = parseOctalOrBinary(bytes, value + 12, 12);
      return new TarArchiveStructSparse(octalOrBinary, octalOrBinary2);
   }

   public static final boolean verifyCheckSum(byte[] bytes) {
      long octal = parseOctal(bytes, 148, 8);
      long value = 0L;
      long value2 = 0L;

      for (int i = 0; i < bytes.length; i++) {
         byte b = bytes[i];
         if (148 <= i && i < 156) {
            b = 32;
         }

         value += 0xFF & b;
         value2 += b;
      }

      return octal == value || octal == value2;
   }
}
