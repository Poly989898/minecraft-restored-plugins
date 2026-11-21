package team.unnamed.creative.serialize.minecraft.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Readable;

public interface FileTreeReader extends AutoCloseable {
   boolean hasNext();

   String next();

   @NotNull InputStream stream();

   default @NotNull Readable content() {
      return Readable.copyInputStream(this.stream());
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default @NotNull InputStream input() {
      return this.stream();
   }

   void close() throws IOException;

   static FileTreeReader zip(ZipInputStream zip) {
      return new ZipInputStreamFileTreeReader(zip);
   }

   static @NotNull FileTreeReader zip(@NotNull ZipFile zipFile) {
      return new ZipFileTreeReader(zipFile);
   }

   static FileTreeReader directory(File root) {
      return new DirectoryFileTreeReader(root);
   }
}
