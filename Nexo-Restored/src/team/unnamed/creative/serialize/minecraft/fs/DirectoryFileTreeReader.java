package team.unnamed.creative.serialize.minecraft.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DirectoryFileTreeReader implements FileTreeReader {
   private final File root;
   private final List<File> folders = new ArrayList();
   private int folderCursor;
   private File @Nullable [] files;
   private int fileCursor;
   private InputStream currentStream;

   DirectoryFileTreeReader(File root) {
      this.root = root;
      this.folders.add(root);
   }

   public boolean hasNext() {
      while(true) {
         if (this.files == null) {
            if (this.folderCursor >= this.folders.size()) {
               return false;
            }

            File folder = (File)this.folders.get(this.folderCursor++);
            this.files = folder.listFiles();
            if (this.files == null) {
               throw new IllegalStateException("Null children from file " + String.valueOf(folder));
            }
         }

         while(this.fileCursor < this.files.length) {
            File file = this.files[this.fileCursor];
            if (!file.isDirectory()) {
               return true;
            }

            this.folders.add(file);
            ++this.fileCursor;
         }

         this.files = null;
         this.fileCursor = 0;
      }
   }

   public String next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("No more elements");
      } else {
         if (this.currentStream != null) {
            Streams.closeUnchecked(this.currentStream);
            this.currentStream = null;
         }

         if (this.files != null && this.fileCursor < this.files.length) {
            File current = this.files[this.fileCursor++];

            try {
               this.currentStream = new FileInputStream(current);
            } catch (IOException e) {
               throw new IllegalStateException("Couldn't open InputStream for: " + String.valueOf(current), e);
            }

            return relativize(this.root, current);
         } else {
            throw new NoSuchElementException("No more elements");
         }
      }
   }

   public @NotNull InputStream stream() {
      return this.currentStream;
   }

   public void close() {
      if (this.currentStream != null) {
         Streams.closeUnchecked(this.currentStream);
         this.currentStream = null;
      }

   }

   private static String relativize(File parent, File child) {
      StringBuilder builder = new StringBuilder();
      builder.append(child.getName());

      while((child = child.getParentFile()) != null) {
         if (parent.equals(child)) {
            return builder.toString();
         }

         builder.insert(0, child.getName() + "/");
      }

      throw new IllegalStateException("");
   }
}
