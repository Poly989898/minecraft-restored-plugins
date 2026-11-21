package team.unnamed.creative.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Writable {
   Writable EMPTY = new Writable() {
      public void write(@NotNull OutputStream output) {
      }

      public @NotNull String toString() {
         return "Writable.EMPTY";
      }
   };
   int DEFAULT_BUFFER_LENGTH = 1024;

   void write(@NotNull OutputStream var1) throws IOException;

   default byte @NotNull [] toByteArray() throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      this.write(output);
      return output.toByteArray();
   }

   default @NotNull String toUTF8String() throws IOException {
      return new String(this.toByteArray(), StandardCharsets.UTF_8);
   }

   static @NotNull Writable resource(@NotNull ClassLoader loader, @NotNull String name) {
      Objects.requireNonNull(loader, "loader");
      Objects.requireNonNull(name, "name");
      return inputStream(() -> {
         InputStream resource = loader.getResourceAsStream(name);
         if (resource == null) {
            throw new IOException("Resource not found: " + name);
         } else {
            return resource;
         }
      });
   }

   static @NotNull Writable file(@NotNull File file) {
      Objects.requireNonNull(file, "file");
      return inputStream(() -> Files.newInputStream(file.toPath()));
   }

   static @NotNull Writable path(@NotNull Path path, @NotNull OpenOption... options) {
      Objects.requireNonNull(path, "path");
      Objects.requireNonNull(options, "options");
      return inputStream(() -> Files.newInputStream(path, options));
   }

   static @NotNull Writable inputStream(@NotNull Callable<InputStream> inputStreamSupplier) {
      Objects.requireNonNull(inputStreamSupplier, "inputStreamSupplier");
      return (output) -> {
         try {
            InputStream input = (InputStream)inputStreamSupplier.call();

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
            throw e;
         } catch (Exception e) {
            throw new IOException("Failed to open InputStream", e);
         }
      };
   }

   static @NotNull Writable copyInputStream(@NotNull InputStream inputStream) throws IOException {
      Objects.requireNonNull(inputStream, "inputStream");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];

      int len;
      while((len = inputStream.read(buf)) != -1) {
         output.write(buf, 0, len);
      }

      final byte[] bytes = output.toByteArray();
      return new Writable() {
         public void write(@NotNull OutputStream output) throws IOException {
            Objects.requireNonNull(output, "output");
            output.write(bytes);
         }

         public byte @NotNull [] toByteArray() {
            return (byte[])bytes.clone();
         }

         public @NotNull String toString() {
            return "Writable.copyInputStream";
         }
      };
   }

   static @NotNull Writable bytes(byte @NotNull [] bytes) {
      final byte[] b = (byte[])bytes.clone();
      return new Writable() {
         public void write(@NotNull OutputStream output) throws IOException {
            Objects.requireNonNull(output, "output");
            output.write(b);
         }

         public byte @NotNull [] toByteArray() {
            return (byte[])b.clone();
         }

         public @NotNull String toUTF8String() {
            return new String(b, StandardCharsets.UTF_8);
         }
      };
   }

   static @NotNull Writable stringUtf8(final @NotNull String string) {
      Objects.requireNonNull(string, "string");
      final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      return new Writable() {
         public void write(@NotNull OutputStream output) throws IOException {
            Objects.requireNonNull(output, "output");
            output.write(bytes);
         }

         public byte @NotNull [] toByteArray() {
            return (byte[])bytes.clone();
         }

         public @NotNull String toUTF8String() {
            return string;
         }

         public @NotNull String toString() {
            return "Writable { type='utf8', value='" + string + "' }";
         }
      };
   }
}
