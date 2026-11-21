package litebans;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public final class FileUtils {
   public static final File computeFile(@NotNull File file) {
      try {
         File file2 = file;
         if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create file: " + file.getAbsolutePath());
         } else {
            return file2;
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final File computeFile2(@NotNull File file) {
      try {
         File file2 = file;
         if (file.exists() && !file.delete()) {
            throw new IOException("Could not delete file: " + file.getAbsolutePath());
         } else {
            return file2;
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final File computeFile3(@NotNull File file) {
      try {
         File file2 = file;
         if (!file.exists() && !file.mkdirs()) {
            throw new IOException("mkdir failed: " + file.getAbsolutePath());
         } else if (!file.isDirectory()) {
            throw new IOException("Not a directory: " + file.getAbsolutePath());
         } else {
            return file2;
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
