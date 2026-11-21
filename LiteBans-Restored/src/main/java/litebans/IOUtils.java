package litebans;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.jetbrains.annotations.NotNull;

public final class IOUtils {
   public static final long computeLongValue(@NotNull InputStream inputStream, @NotNull OutputStream outputStream, int value) {
      try {
         Intrinsics.checkParameterIsNotNull(inputStream, "<this>");
         Intrinsics.checkParameterIsNotNull(outputStream, "out");
         long value2 = 0L;
         byte[] bytes = new byte[value];

         for (int i = inputStream.read(bytes); i >= 0; i = inputStream.read(bytes)) {
            outputStream.write(bytes, 0, i);
            value2 += i;
         }

         return value2;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static long computeLongValue(InputStream inputStream, OutputStream outputStream, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 8192;
      }

      return computeLongValue(inputStream, outputStream, value);
   }

   public static final byte[] computeBytes(@NotNull InputStream inputStream) {
      try {
         Intrinsics.checkParameterIsNotNull(inputStream, "<this>");
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Math.max(8192, inputStream.available()));
         computeLongValue(inputStream, byteArrayOutputStream, 0, 2, null);
         return byteArrayOutputStream.toByteArray();
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
