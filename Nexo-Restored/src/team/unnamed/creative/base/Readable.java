package team.unnamed.creative.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Readable {
   @NotNull InputStream open() throws IOException;

   default void readAndWrite(@NotNull OutputStream output) {
      Objects.requireNonNull(output, "output");

      try {
         InputStream input = this.open();

         try {
            byte[] buf = new byte[1024];

            int len;
            while((len = input.read(buf)) != -1) {
               output.write(buf, 0, len);
            }
         } catch (Throwable var6) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (input != null) {
            input.close();
         }

      } catch (IOException e) {
         throw new UncheckedIOException("Failed to read and write", e);
      }
   }

   default byte @NotNull [] readAsByteArray() {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      this.readAndWrite(output);
      return output.toByteArray();
   }

   default @NotNull String readAsUTF8String() throws IOException {
      return new String(this.readAsByteArray(), StandardCharsets.UTF_8);
   }

   default @NotNull Writable asWritable() {
      return Writable.inputStream(this::open);
   }

   static @NotNull Readable resource(@NotNull ClassLoader loader, @NotNull String name) {
      Objects.requireNonNull(loader, "loader");
      Objects.requireNonNull(name, "name");
      return () -> {
         InputStream input = loader.getResourceAsStream(name);
         if (input == null) {
            throw new IOException("Resource not found: " + name);
         } else {
            return input;
         }
      };
   }

   static @NotNull Readable file(@NotNull File file) {
      Objects.requireNonNull(file, "file");
      return () -> Files.newInputStream(file.toPath());
   }

   static @NotNull Readable path(@NotNull Path path, @NotNull OpenOption... options) {
      Objects.requireNonNull(path, "path");
      Objects.requireNonNull(options, "options");
      return () -> Files.newInputStream(path, options);
   }

   static @NotNull Readable copyInputStream(@NotNull InputStream inputStream) {
      Objects.requireNonNull(inputStream, "inputStream");
      final byte[] bytes = (() -> inputStream).readAsByteArray();
      return new Readable() {
         public @NotNull InputStream open() {
            return new ByteArrayInputStream(bytes);
         }

         public byte @NotNull [] readAsByteArray() {
            return (byte[])bytes.clone();
         }

         public @NotNull String readAsUTF8String() {
            return new String(bytes, StandardCharsets.UTF_8);
         }

         public @NotNull String toString() {
            return "Readable.copyInputStream";
         }
      };
   }

   static @NotNull Readable bytes(byte @NotNull [] bytes) {
      Objects.requireNonNull(bytes, "bytes");
      final byte[] b = (byte[])bytes.clone();
      return new Readable() {
         public @NotNull InputStream open() {
            return new ByteArrayInputStream(b);
         }

         public byte @NotNull [] readAsByteArray() {
            return (byte[])b.clone();
         }

         public @NotNull String readAsUTF8String() {
            return new String(b, StandardCharsets.UTF_8);
         }
      };
   }

   static @NotNull Readable stringUtf8(final @NotNull String string) {
      Objects.requireNonNull(string, "string");
      final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      return new Readable() {
         public @NotNull InputStream open() {
            return new ByteArrayInputStream(bytes);
         }

         public byte @NotNull [] readAsByteArray() {
            return (byte[])bytes.clone();
         }

         public @NotNull String readAsUTF8String() {
            return string;
         }

         public @NotNull String toString() {
            return "Readable { type='utf8', value='" + string + "' }";
         }
      };
   }
}
