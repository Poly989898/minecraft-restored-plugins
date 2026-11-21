package team.unnamed.creative.serialize.minecraft.fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import team.unnamed.creative.base.Writable;

final class DirectoryFileTreeWriter implements FileTreeWriter {
   private final Set<String> names = new HashSet();
   private final File root;
   private OutputStream stream;

   DirectoryFileTreeWriter(File root, boolean clear) {
      this.root = root;
      if (clear) {
         Streams.deleteContents(root);
      }

   }

   public boolean exists(String path) {
      return this.names.contains(path);
   }

   public OutputStream openStream(String path) {
      if (this.stream != null) {
         Streams.closeUnchecked(this.stream);
      }

      File file = this.getFile(path);
      if (this.names.contains(path)) {
         throw new IllegalStateException("File " + path + " alreadyexists!");
      } else {
         if (file.exists()) {
            file.delete();
         } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
               parent.mkdirs();
            }
         }

         try {
            file.createNewFile();
            this.stream = new FileOutputStream(file);
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }

         this.names.add(path);
         return this.stream;
      }
   }

   public void write(String path, Writable data) {
      try {
         OutputStream output = this.openStream(path);

         try {
            data.write(output);
            this.names.add(path);
         } catch (Throwable var7) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (output != null) {
            output.close();
         }

      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void finish() {
      if (this.stream != null) {
         Streams.closeUnchecked(this.stream);
      }

   }

   public void close() {
      this.finish();
   }

   private File getFile(String path) {
      return new File(this.root, path);
   }
}
