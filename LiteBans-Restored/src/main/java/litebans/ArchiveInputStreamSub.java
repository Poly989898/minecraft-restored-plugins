package litebans;

import java.io.InputStream;

class ArchiveInputStreamSub extends InputStream {
   private ArchiveInputStreamSub() {
   }

   @Override
   public int read() {
      return 0;
   }

   @Override
   public long skip(long value) {
      return value;
   }

   ArchiveInputStreamSub(ArchiveComparatorImpl archiveComparator) {
      this();
   }
}
