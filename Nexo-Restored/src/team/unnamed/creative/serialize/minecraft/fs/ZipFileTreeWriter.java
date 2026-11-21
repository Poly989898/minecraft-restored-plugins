package team.unnamed.creative.serialize.minecraft.fs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;

final class ZipFileTreeWriter implements FileTreeWriter {
   private final Set<String> names = new HashSet();
   private final ZipOutputStream output;
   private final ZipEntryLifecycleHandler entryLifecycleHandler;
   private ZipEntryOutputStream current;

   ZipFileTreeWriter(ZipOutputStream output, ZipEntryLifecycleHandler entryLifecycleHandler) {
      this.output = output;
      this.entryLifecycleHandler = entryLifecycleHandler;
   }

   public boolean exists(String path) {
      return this.names.contains(path);
   }

   public OutputStream openStream(String path) {
      if (!this.names.add(path)) {
         throw new IllegalStateException("File " + path + " already exists!");
      } else {
         try {
            if (this.current != null) {
               this.current.close();
               this.current = null;
            }

            ZipEntry entry = this.entryLifecycleHandler.create(path);
            this.output.putNextEntry(entry);
            this.current = new ZipEntryOutputStream(entry);
            return this.current;
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      }
   }

   public void write(String path, Writable data) {
      if (!this.names.add(path)) {
         throw new IllegalStateException("File " + path + " already exists!");
      } else {
         try {
            if (this.current != null) {
               this.current.close();
               this.current = null;
            }

            ZipEntry entry = this.entryLifecycleHandler.create(path);
            this.output.putNextEntry(entry);
            data.write(this.output);
            this.names.add(path);
            this.output.closeEntry();
            this.entryLifecycleHandler.onClose(entry);
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      }
   }

   public void finish() {
      try {
         if (this.current != null) {
            this.current.close();
            this.current = null;
         }

         this.output.finish();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void close() {
      try {
         if (this.current != null) {
            this.current.close();
            this.current = null;
         }

         this.output.close();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   private class ZipEntryOutputStream extends OutputStream {
      private ZipEntry entry;
      private boolean closed;

      private ZipEntryOutputStream(ZipEntry entry) {
         this.entry = entry;
      }

      public void write(byte @NotNull [] b) throws IOException {
         this.ensureValid();
         ZipFileTreeWriter.this.output.write(b);
      }

      public void write(byte @NotNull [] b, int off, int len) throws IOException {
         this.ensureValid();
         ZipFileTreeWriter.this.output.write(b, off, len);
      }

      public void write(int b) throws IOException {
         this.ensureValid();
         ZipFileTreeWriter.this.output.write(b);
      }

      public void flush() throws IOException {
         this.ensureValid();
         ZipFileTreeWriter.this.output.flush();
      }

      public void close() throws IOException {
         if (!this.closed) {
            ZipFileTreeWriter.this.output.closeEntry();
            ZipFileTreeWriter.this.entryLifecycleHandler.onClose(this.entry);
            this.entry = null;
            this.closed = true;
         }

      }

      private void ensureValid() throws IOException {
         if (this.closed) {
            if (ZipFileTreeWriter.this.current != this) {
               throw new IOException("A new output stream has been opened, this one is no longer usable");
            } else {
               throw new IOException("Stream closed");
            }
         }
      }
   }
}
