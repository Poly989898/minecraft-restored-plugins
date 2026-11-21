package litebans;

public class ArchiveExceptionSub extends Exception {
   private static final long VALUE_2772690708123267100 = 2772690708123267100L;

   public ArchiveExceptionSub(String text) {
      super(text);
   }

   public ArchiveExceptionSub(String text, Exception exception) {
      super(text);
      this.initCause(exception);
   }
}
