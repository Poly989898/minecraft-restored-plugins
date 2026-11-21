package litebans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

class KotlinUtil33 extends KotlinStub16 {
   public static final byte[] computeBytes(@NotNull File file) {
      try {
         Intrinsics.checkParameterIsNotNull(file, "<this>");
         var fileInputStream = new FileInputStream(file);
         Throwable error = null;

         try {
            int value = 0;
            long value2 = file.length();
            if (value2 > 2147483647L) {
               throw new OutOfMemoryError("File " + file + " is too big (" + value2 + " bytes) to fit in memory.");
            }

            int int2 = (int)value2;
            byte[] bytes = new byte[int2];

            while (int2 > 0) {
               int read2 = fileInputStream.read(bytes, value, int2);
               if (read2 < 0) {
                  break;
               }

               int2 -= read2;
               value += read2;
            }

            byte[] bytes2;
            if (int2 > 0) {
               bytes2 = Arrays.copyOf(bytes, value);
            } else {
               int read3 = fileInputStream.read();
               if (read3 == -1) {
                  bytes2 = bytes;
               } else {
                  ExposedByteArrayOutputStream exposedByteArrayOutputStream = new ExposedByteArrayOutputStream(8193);
                  exposedByteArrayOutputStream.write(read3);
                  IOUtils.computeLongValue(fileInputStream, exposedByteArrayOutputStream, 0, 2, null);
                  int value3 = bytes.length + exposedByteArrayOutputStream.size();
                  if (value3 < 0) {
                     throw new OutOfMemoryError("File " + file + " is too big to fit in memory.");
                  }

                  bytes2 = ArraysKt.computeBytes(exposedByteArrayOutputStream.getBuf(), Arrays.copyOf(bytes, value3), bytes.length, 0, exposedByteArrayOutputStream.size());
               }
            }

            return bytes2;
         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(fileInputStream, error);
         }
      } catch (FileNotFoundException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final void handleFile(@NotNull File file, @NotNull byte[] bytes) {
      try {
         Intrinsics.checkParameterIsNotNull(file, "<this>");
         Intrinsics.checkParameterIsNotNull(bytes, "array");
         var fileOutputStream = new FileOutputStream(file);
         Throwable error = null;

         try {
            fileOutputStream.write(bytes);
         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(fileOutputStream, error);
         }
      } catch (FileNotFoundException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final void handleFile(@NotNull File file, @NotNull String text, @NotNull Charset charset) {
      try {
         Intrinsics.checkParameterIsNotNull(file, "<this>");
         Intrinsics.checkParameterIsNotNull(text, "text");
         Intrinsics.checkParameterIsNotNull(charset, "charset");
         var fileOutputStream = new FileOutputStream(file);
         Throwable error = null;

         try {
            KotlinStub17.handleOutputStream(fileOutputStream, text, charset);
         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(fileOutputStream, error);
         }
      } catch (FileNotFoundException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static void handleFile(File file, String text, Charset charset, int value, Object obj) {
      if ((value & 2) != 0) {
         charset = KotlinCharsets.CHARSET;
      }

      KotlinStub17.handleFile(file, text, charset);
   }

   public static final void handleOutputStream(@NotNull OutputStream outputStream, @NotNull String text, @NotNull Charset charset) {
      try {
         Intrinsics.checkParameterIsNotNull(outputStream, "<this>");
         Intrinsics.checkParameterIsNotNull(text, "text");
         Intrinsics.checkParameterIsNotNull(charset, "charset");
         short value = 8192;
         if (text.length() < 2 * value) {
            outputStream.write(text.getBytes(charset));
         } else {
            CharsetEncoder charsetEncoder = KotlinStub17.computeCharsetEncoder(charset);
            CharBuffer allocate2 = CharBuffer.allocate(value);
            Intrinsics.checkNotNull(charsetEncoder);
            ByteBuffer byteBuffer = KotlinStub17.computeByteBuffer(value, charsetEncoder);
            int value2 = 0;
            byte b = 0;

            while (value2 < text.length()) {
               int value3 = Math.min(value - b, text.length() - value2);
               int value4 = value2 + value3;
               String text2 = text;
               char[] array2 = allocate2.array();
               text2.getChars(value2, value4, array2, b);
               ((Buffer)allocate2).limit(value3 + b);
               CoderResult encode2 = charsetEncoder.encode(allocate2, byteBuffer, value4 == text.length());
               if (!encode2.isUnderflow()) {
                  throw new IllegalStateException("Check failed.");
               }

               outputStream.write(byteBuffer.array(), 0, byteBuffer.position());
               if (allocate2.position() != allocate2.limit()) {
                  allocate2.put(0, allocate2.get());
                  b = 1;
               } else {
                  b = 0;
               }

               ((Buffer)allocate2).clear();
               ((Buffer)byteBuffer).clear();
               value2 = value4;
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final CharsetEncoder computeCharsetEncoder(@NotNull Charset charset) {
      Intrinsics.checkParameterIsNotNull(charset, "<this>");
      return charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
   }

   public static final ByteBuffer computeByteBuffer(int value, @NotNull CharsetEncoder charsetEncoder) {
      Intrinsics.checkParameterIsNotNull(charsetEncoder, "encoder");
      int ceil2 = (int)((float)Math.ceil(charsetEncoder.maxBytesPerChar()));
      return ByteBuffer.allocate(value * ceil2);
   }
}
