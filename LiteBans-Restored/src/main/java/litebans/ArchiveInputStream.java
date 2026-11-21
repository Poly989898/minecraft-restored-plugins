package litebans;

import java.io.IOException;
import java.io.InputStream;

public abstract class ArchiveInputStream extends InputStream {
   private final byte[] bytes16 = new byte[1];
   private static final int VALUE_255 = 255;
   private long longValue20 = 0L;

   public abstract ArchiveEntry getNextEntry();

   @Override
   public int read() {
      try {
         int read2 = this.read(this.bytes16, 0, 1);
         return read2 == -1 ? -1 : this.bytes16[0] & 0xFF;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   protected void handleIntValue4(int value) {
      this.handleLongValue2(value);
   }

   protected void handleLongValue2(long value) {
      if (value != -1L) {
         this.longValue20 += value;
      }
   }

   protected void handleLongValue3(long value) {
      this.longValue20 -= value;
   }

   public long obtainLongValue2() {
      return this.longValue20;
   }
}
