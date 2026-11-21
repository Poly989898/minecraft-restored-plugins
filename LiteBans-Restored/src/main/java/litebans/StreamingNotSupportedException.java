package litebans;

public class StreamingNotSupportedException extends ArchiveExceptionSub {
   private static final long VALUE_1 = 1L;
   private final String text2;

   public StreamingNotSupportedException(String text) {
      super("The " + text + " doesn't support streaming.");
      this.text2 = text;
   }
}
