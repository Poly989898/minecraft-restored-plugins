package team.unnamed.creative.serialize.minecraft.fs;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.jetbrains.annotations.NotNull;

final class ZipInputStreamFileTreeReader implements FileTreeReader {
   private final ZipInputStream zip;
   private ZipEntry current;
   private boolean consumed;

   public ZipInputStreamFileTreeReader(ZipInputStream zip) {
      this.zip = zip;
   }

   private void nextEntry() {
      try {
         ZipEntry entry;
         do {
            entry = this.zip.getNextEntry();
         } while(entry != null && entry.isDirectory());

         this.current = entry;
         this.consumed = false;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public boolean hasNext() {
      if (this.current == null) {
         if (this.consumed) {
            return false;
         } else {
            this.nextEntry();
            return this.current != null;
         }
      } else if (this.consumed) {
         this.nextEntry();
         return this.current != null;
      } else {
         return true;
      }
   }

   public String next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("No more elements");
      } else {
         this.consumed = true;
         return this.current.getName();
      }
   }

   public @NotNull InputStream stream() {
      return new FilterInputStream(this.zip) {
         public void close() throws IOException {
            ZipInputStreamFileTreeReader.this.zip.closeEntry();
         }
      };
   }

   public void close() throws IOException {
      this.zip.close();
   }
}
