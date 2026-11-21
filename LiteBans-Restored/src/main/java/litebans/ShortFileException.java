package litebans;

public class ShortFileException extends ArchiveException {
   private static final long VALUE_1_2 = 1L;

   public ShortFileException() {
      super("unexpected EOF");
   }
}
