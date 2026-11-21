package com.ticxo.modelengine.api.utils;

import com.google.common.io.ByteStreams;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.bukkit.plugin.java.JavaPlugin;

public class TFile {
   public static final String SEP = System.getProperty("file.separator");

   public static File copyResource(JavaPlugin plugin, File file, String path) {
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

      return file;
   }

   public static File copyResource(JavaPlugin plugin, File origin, String resourceOrigin, String path) {
      return copyResource(plugin, createFile(origin, path), resourceOrigin + "/" + path);
   }

   public static File copyResource(JavaPlugin plugin, File origin, String resourceOrigin, String srcPath, String destPath) {
      return copyResource(plugin, createFile(origin, destPath), resourceOrigin + "/" + srcPath);
   }

   public static File copyResourceTemplated(
      JavaPlugin plugin, File origin, String resourceOrigin, String srcPath, String destPath, Map<String, String> replacements
   ) {
      File file = createFile(origin, destPath);
      if (file.exists()) {
         return file;
      }

      try (InputStream reader = plugin.getResource(resourceOrigin + "/" + srcPath)) {
         if (reader == null) {
            return file;
         }

         String content = new String(reader.readAllBytes(), StandardCharsets.UTF_8);

         for (Entry<String, String> entry : replacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
         }

         Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
      } catch (IOException e) {
         e.printStackTrace();
      }

      return file;
   }

   public static String createPath(String... path) {
      if (path.length == 0) {
         return "";
      }

      StringBuilder builder = new StringBuilder(path[0]);

      for (int i = 1; i < path.length; i++) {
         builder.append(SEP).append(path[i]);
      }

      return builder.toString();
   }

   public static File createDirectory(File parent, String... path) {
      String compactPath = createPath(path);
      File file = new File(parent, compactPath);
      if (!file.exists() && !file.mkdirs()) {
         TLogger.log("Failed to create directory: " + compactPath);
      }

      return file;
   }

   public static File createFile(File parent, String... path) {
      String compactPath = createPath(path);
      File file = new File(parent, compactPath);
      if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
         TLogger.log("Failed to create file: " + compactPath);
      }

      return file;
   }

   public static File createFileOrEmpty(File parent, String... path) {
      String compactPath = createPath(path);
      File file = new File(parent, compactPath);

      try {
         if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() || !file.exists() && !file.createNewFile()) {
            TLogger.log("Failed to create file: " + compactPath);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return file;
   }

   public static File createFile(File parent, String scope, ResourceLocation location, String extension) {
      File file = new File(parent.getPath() + SEP + location.getNamespace() + SEP + scope, location.getPath() + "." + extension);
      if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
         TLogger.log("Failed to create file: " + file.getPath());
      }

      return file;
   }

   public static void recreateFile(File file) {
      try {
         if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() || !file.exists() && !file.createNewFile()) {
            TLogger.log("Failed to create file: " + file.getName());
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
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

   public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
      zipFile(fileToZip, fileName, zipOut, true);
   }

   public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, boolean directoryEntries) throws IOException {
      if (!fileToZip.isHidden()) {
         if (fileToZip.isDirectory()) {
            if (directoryEntries) {
               if (fileName.endsWith("/")) {
                  zipOut.putNextEntry(new ZipEntry(fileName));
               } else {
                  zipOut.putNextEntry(new ZipEntry(fileName + "/"));
               }

               zipOut.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            if (children != null) {
               Arrays.sort(children);

               for (File childFile : children) {
                  zipFile(childFile, fileName + "/" + childFile.getName(), zipOut, directoryEntries);
               }
            }
         } else {
            try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileToZip), 65536)) {
               ZipEntry zipEntry = new ZipEntry(fileName);
               zipOut.putNextEntry(zipEntry);
               byte[] bytes = new byte[65536];

               int length;
               while ((length = fis.read(bytes)) >= 0) {
                  zipOut.write(bytes, 0, length);
               }
            }
         }
      }
   }

   public static boolean delete(File file) {
      if (!file.isDirectory()) {
         return file.delete();
      }

      File[] list = file.listFiles();
      if (list == null) {
         return file.delete();
      }

      boolean success = true;

      for (File f : list) {
         success &= delete(f);
      }

      return success & file.delete();
   }
}
