package litebans;

import java.io.IOException;

public class ArchiveException extends IOException {
   private static final long VALUE_1_3 = 1L;

   public ArchiveException() {
   }

   public ArchiveException(String text) {
      super(text);
   }

   public ArchiveException(String text, Throwable error) {
      super(text);
      this.initCause(error);
   }
}
