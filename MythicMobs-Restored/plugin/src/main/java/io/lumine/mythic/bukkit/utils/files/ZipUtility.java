package io.lumine.mythic.bukkit.utils.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtility {
   private static final int BUFFER_SIZE = 4096;

   public void unzip(String zipFilePath, String destDirectory) throws IOException {
      File destDir = new File(destDirectory);
      if (!destDir.exists()) {
         destDir.mkdir();
      }

      ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));

      for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
         String filePath = destDirectory + File.separator + entry.getName();
         if (!entry.isDirectory()) {
            this.extractFile(zipIn, filePath);
         } else {
            File dir = new File(filePath);
            dir.mkdir();
         }

         zipIn.closeEntry();
      }

      zipIn.close();
   }

   public void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
      byte[] bytesIn = new byte[4096];
      int read = 0;

      while ((read = zipIn.read(bytesIn)) != -1) {
         bos.write(bytesIn, 0, read);
      }

      bos.close();
   }

   public static void zipFolder(File source, File output) throws ZipException, IOException {
      AppZip appZip = new AppZip(source);
      appZip.zipIt(output);
   }

   public static boolean isValid(File file) {
      ZipFile zipfile = null;
      ZipInputStream zis = null;

      boolean flag;
      try {
         zipfile = new ZipFile(file);
         zis = new ZipInputStream(new FileInputStream(file));
         ZipEntry ze = zis.getNextEntry();
         if (ze != null) {
            while (ze != null) {
               zipfile.getInputStream(ze);
               ze.getCrc();
               ze.getCompressedSize();
               ze.getName();
               ze = zis.getNextEntry();
            }

            return true;
         }

         flag = false;
      } catch (ZipException e) {
         return false;
      } catch (IOException ex) {
         return false;
      } finally {
         try {
            if (zipfile != null) {
               zipfile.close();
               ZipFile zipFile = null;
            }
         } catch (IOException ex2) {
            return false;
         }

         try {
            if (zis != null) {
               zis.close();
               ZipInputStream zipInputStream = null;
            }
         } catch (IOException ex3) {
            return false;
         }
      }

      return flag;
   }
}
