package litebans;

import java.io.IOException;
import java.io.InputStream;

public class ArchiveInputStreamSub2 extends InputStream {
   private final InputStream inputStream;
   private long longValue;

   public ArchiveInputStreamSub2(InputStream inputStream, long value) {
      this.inputStream = inputStream;
      this.longValue = value;
   }

   @Override
   public int read() {
      try {
         if (this.longValue > 0L) {
            this.longValue--;
            return this.inputStream.read();
         } else {
            return -1;
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int read(byte[] bytes, int value, int value2) {
      try {
         if (value2 == 0) {
            return 0;
         }

         if (this.longValue == 0L) {
            return -1;
         }

         int value3 = value2;
         if (value3 > this.longValue) {
            value3 = (int)this.longValue;
         }

         int read2 = this.inputStream.read(bytes, value, value3);
         if (read2 >= 0) {
            this.longValue -= read2;
         }

         return read2;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void close() {
   }

   @Override
   public long skip(long value) {
      try {
         long value2 = Math.min(this.longValue, value);
         long skip2 = this.inputStream.skip(value2);
         this.longValue -= skip2;
         return skip2;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
