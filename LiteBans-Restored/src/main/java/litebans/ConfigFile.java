package litebans;

import java.io.FileNotFoundException;
import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigFile implements ConfigKeyReader, Closeable {
   protected Platform platform;
   private File file;
   private Throwable error;
   private String configText = "";

   public ConfigFile(Platform platform) {
      this.platform = platform;
   }

   public static InputStream openResource(@NotNull String text, @Nullable String text2) {
      if (text2 == null) {
         text2 = ClassUtil.getJarPath(ConfigFile.class);
      }

      try {
         JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(new File(text2).toPath()));

         JarEntry jarEntry;
         while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(text)) {
               return jarInputStream;
            }
         }

         jarInputStream.close();
         return null;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static String computeText(@NonNull String text, @Nullable String text2) {
      if (text == null) {
         throw new NullPointerException("resource is marked non-null but is null");
      }

      try {
         InputStream resource = openResource(text, text2);

         String text3;
         try {
            text3 = readStream(resource);
         } catch (Throwable e) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable e2) {
                  e.addSuppressed(e2);
               }
            }

            throw e;
         }

         if (resource != null) {
            resource.close();
         }

         return text3;
      } catch (IOException e3) {
         e3.printStackTrace();
         return "#Header 404" + System.lineSeparator();
      }
   }

   public static String readStream(@Nullable InputStream inputStream) {
      try {
         String lineSeparator2 = System.lineSeparator();
         if (inputStream == null) {
            return "";
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024);

         try {
            StringBuilder stringBuilder = new StringBuilder();

            while (true) {
               String line = bufferedReader.readLine();
               if (line == null || !line.startsWith("#")) {
                  return stringBuilder.toString();
               }

               stringBuilder.append(line).append(lineSeparator2);
            }
         } finally {
            if (bufferedReader != null) {
               bufferedReader.close();
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static void saveResource(@NotNull File file, @NotNull String text, @Nullable String text2, boolean flag) {
      try {
         InputStream resource = openResource(text, text2);

         label132: {
            try {
               if (resource == null) {
                  break label132;
               }

               File file2 = new File(file, text);
               int lastIndexOf2 = text.lastIndexOf(47);
               File file3 = new File(file, text.substring(0, lastIndexOf2 >= 0 ? lastIndexOf2 : 0));
               if (!file3.exists() && !file3.mkdirs()) {
                  throw new IOException("mkdir failed: " + file3.getAbsolutePath());
               }

               if (!file2.exists() || file2.length() == 0L || flag) {
                  FileOutputStream fileOutputStream = new FileOutputStream(file2);

                  try {
                     byte[] bytes = new byte[8192];

                     int value;
                     while ((value = resource.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, value);
                     }
                  } finally {
                     if (fileOutputStream != null) {
                        fileOutputStream.close();
                     }
                  }
               }
            } catch (Throwable e) {
               if (resource != null) {
                  try {
                     resource.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (resource != null) {
               resource.close();
            }

            return;
         }

         if (resource != null) {
            resource.close();
         }
      } catch (Throwable e3) {
         throw SneakyThrow.<RuntimeException>rethrow(e3);
      }
   }

   public static String normalizeLineEndings(String text) {
      String lineSeparator2 = System.lineSeparator();
      StringBuilder stringBuilder = new StringBuilder();
      String[] split2 = text.split("\\r?\\n");

      for (String text2 : split2) {
         if (!text2.startsWith("#")) {
            stringBuilder.append(text2).append(lineSeparator2);
         }
      }

      return stringBuilder.toString();
   }

   public ConfigFile throwIfFailed() {
      if (this.error != null) {
         throw new RuntimeException(this.error);
      } else {
         return this;
      }
   }

   @Override
   public boolean contains(String text) {
      return this.get(text) != null;
   }

   @Override
   public boolean isSet(String text) {
      return this.get(text) != null;
   }

   public abstract ConfigFile load(File file);

   public abstract ConfigFile load(InputStream inputStream);

   public abstract ConfigFile reload();

   public abstract ConfigFile save();

   public abstract ConfigFile save(File file);

   public ConfigFile computeConfigFile(String text) {
      this.configText = text;
      return this;
   }

   public abstract ConfigFile clear();

   @Override
   public void close() {
      this.clear();
   }

   public void writeFile(File file, String text) {
      try {
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);

         try {
            outputStreamWriter.write(this.obtainText() + normalizeLineEndings(text));
            outputStreamWriter.flush();
         } finally {
            if (outputStreamWriter != null) {
               outputStreamWriter.close();
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public File getFile() {
      return this.file;
   }

   public void setFile(File file) {
      this.file = file;
   }

   public Throwable getError() {
      return this.error;
   }

   public void setError(Throwable error) {
      this.error = error;
   }

   public String obtainText() {
      return this.configText;
   }
}
