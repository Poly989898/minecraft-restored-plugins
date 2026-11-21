package io.lumine.mythic.bukkit.utils.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
   public static int extractFolder(String zipFile, String extractFolder) {
      ZipFile zip = null;
      int modifications = 0;

      try {
         int BUFFER = 2048;
         File file = new File(zipFile);
         zip = new ZipFile(file);
         String newPath = extractFolder;
         Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
         boolean needToMakePath = true;

         while (zipFileEntries.hasMoreElements()) {
            ZipEntry entry = zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            if (!destFile.exists()) {
               if (needToMakePath) {
                  needToMakePath ^= true;
                  new File(newPath).mkdirs();
               }

               modifications++;
               File destinationParent = destFile.getParentFile();
               destinationParent.mkdirs();
               if (!entry.isDirectory()) {
                  BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                  byte[] data = new byte[BUFFER];
                  FileOutputStream fos = new FileOutputStream(destFile);
                  BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                  int currentByte;
                  while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                     dest.write(data, 0, currentByte);
                  }

                  dest.flush();
                  dest.close();
                  fos.flush();
                  fos.close();
                  is.close();
               } else {
                  destFile.mkdirs();
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (zip != null) {
               zip.close();
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      return modifications;
   }
}
