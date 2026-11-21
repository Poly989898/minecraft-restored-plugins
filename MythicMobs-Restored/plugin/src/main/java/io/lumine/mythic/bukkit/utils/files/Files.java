package io.lumine.mythic.bukkit.utils.files;

import com.google.common.io.ByteStreams;
import io.lumine.mythic.bukkit.utils.config.ConfigurationLoader;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;
import org.bukkit.plugin.java.JavaPlugin;

public class Files {
   public static final String SEP = System.getProperty("file.separator");
   public static File BACKUP_FOLDER = null;

   public static void delete(File file) throws IOException {
      if (file.isDirectory()) {
         if (file.list().length == 0) {
            file.delete();
         } else {
            String[] files = file.list();

            for (String temp : files) {
               File fileDelete = new File(file, temp);
               delete(fileDelete);
            }

            if (file.list().length == 0) {
               file.delete();
            }
         }
      } else {
         file.delete();
      }
   }

   public static List<File> getAll(String directoryName) {
      File directory = new File(directoryName);
      List<File> resultList = new ArrayList<>();
      File[] fList = directory.listFiles();

      for (File file : fList) {
         if (!file.isFile() || !file.getName().endsWith(".yml") && !file.getName().endsWith(".txt")) {
            if (file.isDirectory()) {
               resultList.addAll(getAll(file.getAbsolutePath()));
            }
         } else {
            resultList.add(file);
         }
      }

      return resultList;
   }

   public static String toList(String s, File[] list) {
      for (File f : list) {
         s = s + f.getName() + ", ";
      }

      return s;
   }

   public static List<File> getAllYaml(String directoryName) {
      File directory = new File(directoryName);
      List<File> resultList = new ArrayList<>();
      File[] fList = directory.listFiles();

      for (File file : fList) {
         if (file.isFile() && file.getName().endsWith(".yml")) {
            resultList.add(file);
         } else if (file.isDirectory()) {
            resultList.addAll(getAllYaml(file.getAbsolutePath()));
         }
      }

      return resultList;
   }

   public static List<File> getAll(String directoryName, Collection<String> extensions) {
      File directory = new File(directoryName);
      List<File> resultList = new ArrayList<>();
      File[] fList = directory.listFiles();

      for (File file : fList) {
         if (file.isFile()) {
            String[] split = file.getName().split("\\.");
            if (split.length >= 2) {
               String ext = split[split.length - 1].toLowerCase();
               if (extensions.contains(ext)) {
                  resultList.add(file);
               }
            }
         } else if (file.isDirectory()) {
            resultList.addAll(getAll(file.getAbsolutePath(), extensions));
         }
      }

      return resultList;
   }

   public static <T extends LuminePlugin> List<ConfigurationLoader<T>> getConfigurationLoaders(T plugin, List<File> files, String folder) {
      List<ConfigurationLoader<T>> list = new ArrayList<>();

      for (File file : files) {
         list.add(new ConfigurationLoader<>(plugin, file, folder));
      }

      return list;
   }

   public static void copyResource(JavaPlugin plugin, File file, String path) {
      if (!file.exists()) {
         try {
            OutputStream writer = new FileOutputStream(file);
            InputStream reader = plugin.getResource(path);
            if (reader != null) {
               ByteStreams.copy(reader, writer);
            }

            writer.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static void copyResource(JavaPlugin plugin, File origin, String resourceOrigin, String path) {
      copyResource(plugin, createFile(origin, path), resourceOrigin + "/" + path);
   }

   public static String createPath(String... path) {
      if (path.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(path[0]);

         for (int i = 1; i < path.length; i++) {
            builder.append(SEP).append(path[i]);
         }

         return builder.toString();
      }
   }

   public static File createDirectory(File parent, String... path) {
      String compactPath = createPath(path);
      File file = new File(parent, compactPath);
      if (!file.exists() && !file.mkdirs()) {
         Log.error("Failed to create directory: " + compactPath);
      }

      return file;
   }

   public static File createFile(File parent, String... path) {
      File file = new File(parent, createPath(path));
      if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
         Log.error("Failed to create file.");
      }

      return file;
   }

   public static File createFileOrEmpty(File parent, String... path) {
      String compactPath = createPath(path);
      File file = new File(parent, compactPath);

      try {
         if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() || !file.exists() && !file.createNewFile()) {
            Log.error("Failed to create file: " + compactPath);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return file;
   }

   public static BufferedImage toImage(String data) {
      String[] d = data.split(",");
      return d.length > 1 ? rawToImage(d[1]) : rawToImage(data);
   }

   public static BufferedImage rawToImage(String data) {
      try {
         byte[] imageBytes = Base64.getDecoder().decode(data);
         return ImageIO.read(new ByteArrayInputStream(imageBytes));
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static String removeExtension(String path) {
      int id = path.lastIndexOf(".");
      return id == -1 ? path : path.substring(0, id);
   }

   public static boolean isExtension(String path, String extensions) {
      int id = path.lastIndexOf(".");
      return id == -1 ? false : path.substring(id + 1).equalsIgnoreCase(extensions);
   }

   public static void copyDirectory(Path source, Path target) throws IOException {
      java.nio.file.Files.walk(source).forEach(sourcePath -> {
         Path targetPath = target.resolve(source.relativize(sourcePath));

         try {
            if (java.nio.file.Files.isDirectory(sourcePath)) {
               if (!java.nio.file.Files.exists(targetPath)) {
                  java.nio.file.Files.createDirectories(targetPath);
               }
            } else {
               java.nio.file.Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }
}
