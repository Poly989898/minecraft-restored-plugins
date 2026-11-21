package team.unnamed.creative.serialize.minecraft.fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Readable;
import team.unnamed.creative.base.Writable;

final class ZipFileTreeReader implements FileTreeReader {
   private final ZipFile zipFile;
   private final Enumeration<? extends ZipEntry> entries;
   private final Collection<WeakReference<ZipFileEntryReadable>> createdContent = new HashSet();
   private @Nullable ZipEntry currentEntry;
   private @Nullable ZipEntry nextEntry;

   ZipFileTreeReader(@NotNull ZipFile zipFile) {
      this.zipFile = (ZipFile)Objects.requireNonNull(zipFile, "zipFile");
      this.entries = zipFile.entries();
      this.next0();
   }

   private void next0() {
      this.nextEntry = null;

      while(this.entries.hasMoreElements()) {
         ZipEntry entry = (ZipEntry)this.entries.nextElement();
         if (!entry.isDirectory()) {
            this.nextEntry = entry;
            break;
         }
      }

   }

   public boolean hasNext() {
      return this.nextEntry != null;
   }

   public @NotNull String next() {
      if (this.nextEntry == null) {
         throw new NoSuchElementException();
      } else {
         this.currentEntry = this.nextEntry;
         this.next0();
         return this.currentEntry.getName();
      }
   }

   public @NotNull InputStream stream() {
      if (this.currentEntry == null) {
         throw new IllegalStateException("No current entry, call next() first");
      } else {
         try {
            return this.zipFile.getInputStream(this.currentEntry);
         } catch (IOException e) {
            throw new UncheckedIOException("Failed to get input stream for current entry: " + String.valueOf(this.currentEntry), e);
         }
      }
   }

   public @NotNull Readable content() {
      if (this.currentEntry == null) {
         throw new IllegalStateException("No current entry, call next() first");
      } else {
         ZipFileEntryReadable readable = new ZipFileEntryReadable(this.currentEntry);
         this.createdContent.add(new WeakReference(readable));
         return readable;
      }
   }

   public void close() throws IOException {
      for(WeakReference<ZipFileEntryReadable> ref : this.createdContent) {
         ZipFileEntryReadable readable = (ZipFileEntryReadable)ref.get();
         if (readable != null) {
            readable.memoize();
         }
      }

      this.zipFile.close();
   }

   private class ZipFileEntryReadable implements Readable {
      private final ZipEntry entry;
      private byte @Nullable [] memoized;

      ZipFileEntryReadable(final ZipEntry entry) {
         this.entry = (ZipEntry)Objects.requireNonNull(entry, "entry");
      }

      public @NotNull InputStream open() throws IOException {
         return (InputStream)(this.memoized != null ? new ByteArrayInputStream(this.memoized) : ZipFileTreeReader.this.zipFile.getInputStream(this.entry));
      }

      public @NotNull Writable asWritable() {
         return this.memoized != null ? Writable.bytes(this.memoized) : Readable.super.asWritable();
      }

      void memoize() {
         if (this.memoized == null) {
            this.memoized = Readable.super.readAsByteArray();
         }
      }

      public byte @NotNull [] readAsByteArray() {
         return this.memoized != null ? (byte[])this.memoized.clone() : Readable.super.readAsByteArray();
      }

      public @NotNull String readAsUTF8String() throws IOException {
         return this.memoized != null ? new String(this.memoized, StandardCharsets.UTF_8) : Readable.super.readAsUTF8String();
      }
   }
}
