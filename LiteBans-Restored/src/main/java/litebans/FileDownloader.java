package litebans;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FileDownloader {
   private FileDownloader() {
   }

   public final int obtainIntValue() {
      return ConfigModule.obtainIntValue3();
   }

   public final void handleIntValue(int value) {
      ConfigModule.handleIntValue2(value);
   }

   public final void handleText(@NotNull String text, @NotNull String text2, @NotNull String text3, @NotNull Object[] objs) {
      try {

         File file2 = new File(text);
         if (!file2.exists()) {
            URLConnection connection2 = new URL(text2).openConnection();
            InputStream inputStream2 = connection2.getInputStream();
            short value17 = 8192;
            InputStream inputStream3 = inputStream2 instanceof BufferedInputStream ? (BufferedInputStream)inputStream2 : new BufferedInputStream(inputStream2, value17);
            if (StringsKt.checkText2(text2, ".gz", false, 2, null)) {
               inputStream3 = new GZIPInputStream(inputStream3);
            }

            if (StringsKt.checkText2(text2, "tar.gz", false, 2, null)) {
               ArchiveInputStream archiveInputStream = new ArchiveHelper5("UTF-8").createArchiveInputStream("tar", inputStream3);
               Intrinsics.checkNotNullExpressionValue(archiveInputStream, "");
               inputStream3 = (TarArchiveInputStream)archiveInputStream;
               this.handleTarArchiveInputStream((TarArchiveInputStream) inputStream3, text3);
            }

            var inputStream4 = inputStream3;
            Throwable error2 = null;

            try {

               try {
                  KotlinStub17.handleFile(file2, IOUtils.computeBytes(inputStream3));
               } catch (Throwable e9) {
                  if (!(e9 instanceof IOException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e9);
                  }
                  IOException ioException = (IOException) e9;
                  Boolean flag5 = ConfigModule.FILE_DOWNLOADER.checkFile(file2, ioException);
               }
            } catch (Throwable e10) {
               error2 = e10;
               throw SneakyThrow.<RuntimeException>rethrow(e10);
            } finally {
               CloseableKt.handleCloseable(inputStream4, error2);
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static void handleFileDownloader(FileDownloader fileDownloader, String text, String text2, String text3, Object[] objs, int value, Object obj) {
      if ((value & 4) != 0) {
         text3 = text;
      }

      if ((value & 8) != 0) {
         objs = new Object[0];
      }

      fileDownloader.handleText(text, text2, text3, objs);
   }

   public final String computeText(@NotNull byte[] bytes) {
      try {
         MessageDigest instance = MessageDigest.getInstance("SHA-256");
         if (instance != null) {
            instance.reset();
            byte[] digest2 = instance.digest(bytes);
            instance.reset();
            return this.computeText2(digest2);
         } else {
            return "";
         }
      } catch (NoSuchAlgorithmException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final String computeText2(@Nullable byte[] bytes) {
      BigInteger bigInteger = new BigInteger(1, bytes);
      StringBuilder stringBuilder = new StringBuilder(bigInteger.toString(16));

      while (stringBuilder.length() < 64) {
         stringBuilder.insert(0, '0');
      }

      return stringBuilder.toString();
   }

   private final boolean checkFile(File file, IOException ioException) {
      try {
         try {
            file.delete();
         } finally {
            ;
         }

         throw ioException;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleTarArchiveInputStream(TarArchiveInputStream tarArchiveInputStream, String text) {
      try {
         boolean flag = false;

         while (true) {
            ArchiveEntry nextEntry = tarArchiveInputStream.getNextEntry();
            TarArchiveEntry tarArchiveEntry = nextEntry instanceof TarArchiveEntry ? (TarArchiveEntry)nextEntry : null;
            if ((nextEntry instanceof TarArchiveEntry ? (TarArchiveEntry)nextEntry : null) == null) {
               break;
            }

            if ((Intrinsics.areEqual(tarArchiveEntry.getName(), text) || StringsKt.checkText2(tarArchiveEntry.getName(), '/' + text, false, 2, null)) && !tarArchiveEntry.isDirectory()) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            throw new IOException("Could not find \"" + text + "\" in archive");
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private static final void handlePlatform(Platform platform, String text, String text2) {
      LiteSender console = platform.getConsole();
      console.sendMessage("[LiteBans] A plugin update is available! Your version: " + text + ", latest version: " + text2);
      console.sendMessage("[LiteBans] Updates can be downloaded from here:");
      console.sendMessage("[LiteBans] https://www.spigotmc.org/resources/litebans.3715/");
   }

   public FileDownloader(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
