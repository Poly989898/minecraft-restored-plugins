package litebans;

import java.io.File;
import org.jetbrains.annotations.NotNull;

public final class ChecksumMismatchException extends RuntimeException {
   public ChecksumMismatchException(@NotNull File file, @NotNull String text, @NotNull String text2) {
      super(
         "A downloaded dependency did not match the expected SHA-256 checksum!"
            + System.lineSeparator()
            + "Local checksum    : "
            + text2
            + System.lineSeparator()
            + "Expected checksum : "
            + text
            + System.lineSeparator()
            + "File              : "
            + file.getAbsolutePath()
      );
   }
}
