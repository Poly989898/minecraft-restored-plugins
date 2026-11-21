package io.lumine.mythic.bukkit.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class AppZip {
   private List<String> fileList = new ArrayList<>();
   private File source;

   public AppZip(File source) {
      this.source = source;
   }

   public void zipIt(File zipFile) throws IOException, ZipException {
      this.generateFileList(this.source);
      byte[] buffer = new byte[1024];
      FileOutputStream fos = new FileOutputStream(zipFile);
      ZipOutputStream zos = new ZipOutputStream(fos);

      for (String file : this.fileList) {
         ZipEntry ze = new ZipEntry(file);
         zos.putNextEntry(ze);
         FileInputStream in = new FileInputStream(this.source.getAbsolutePath() + File.separator + file);

         int len;
         while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
         }

         in.close();
      }

      zos.closeEntry();
      zos.close();
   }

   public void generateFileList(File node) {
      if (node.isFile()) {
         this.fileList.add(this.generateZipEntry(node.getAbsoluteFile().toString()));
      }

      if (node.isDirectory()) {
         String[] subNote = node.list();

         for (String filename : subNote) {
            this.generateFileList(new File(node, filename));
         }
      }
   }

   private String generateZipEntry(String file) {
      return file.substring(this.source.getAbsolutePath().length() + 1, file.length());
   }
}
