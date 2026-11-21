package team.unnamed.creative.serialize.minecraft.fs;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

final class Streams {
   private Streams() {
   }

   public static void closeUnchecked(Closeable closeable) {
      try {
         closeable.close();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public static void deleteContents(File folder) {
      File[] children = folder.listFiles();
      if (children != null) {
         for(File child : children) {
            if (!Files.isSymbolicLink(child.toPath())) {
               deleteContents(child);
               child.delete();
            }
         }
      }

   }
}
