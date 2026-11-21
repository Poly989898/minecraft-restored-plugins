package litebans;

import java.io.IOException;
import java.io.InputStream;

public final class ArchiveUtil6 {
   private static final int VALUE_8024 = 8024;
   private static final int VALUE_4096 = 4096;
   private static final byte[] BYTES = new byte[4096];

   public static final long computeLongValue(InputStream inputStream, long value) {
      try {
         long value2 = value;

         while (value > 0L) {
            long skip2 = inputStream.skip(value);
            if (skip2 == 0L) {
               break;
            }

            value -= skip2;
         }

         while (value > 0L) {
            int value3 = computeIntValue(inputStream, BYTES, 0, (int)Math.min(value, 4096L));
            if (value3 < 1) {
               break;
            }

            value -= value3;
         }

         return value2 - value;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final int computeIntValue(InputStream inputStream, byte[] bytes) {
      return computeIntValue(inputStream, bytes, 0, bytes.length);
   }

   public static final int computeIntValue(InputStream inputStream, byte[] bytes, int value, int value2) {
      try {
         if (value2 >= 0 && value >= 0 && value2 + value <= bytes.length) {
            int value3 = 0;

            while (value3 != value2) {
               int read2 = inputStream.read(bytes, value + value3, value2 - value3);
               if (read2 == -1) {
                  break;
               }

               value3 += read2;
            }

            return value3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
