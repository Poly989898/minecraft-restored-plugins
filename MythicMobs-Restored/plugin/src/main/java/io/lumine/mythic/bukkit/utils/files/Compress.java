package io.lumine.mythic.bukkit.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {
   private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
      File folder = new File(srcFolder);

      for (String fileName : folder.list()) {
         if (path.equals("")) {
            addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
         } else {
            addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
         }
      }
   }

   private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
      File folder = new File(srcFile);
      if (folder.isDirectory()) {
         addFolderToZip(path, srcFile, zip);
      } else {
         byte[] buffer = new byte[1024];
         FileInputStream in = new FileInputStream(srcFile);
         zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));

         int len;
         while ((len = in.read(buffer)) > 0) {
            zip.write(buffer, 0, len);
         }

         in.close();
      }
   }
}
