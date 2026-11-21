package io.lumine.mythic.bukkit.utils.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

public class Folders {
   public static void cospyDirectory(File dir, File targetDir) {
   }

   public static void deleteDirectory(File file) throws IOException {
      if (file.isDirectory()) {
         if (file.list().length == 0) {
            file.delete();
         } else {
            for (String temp : file.list()) {
               File fileDelete = new File(file, temp);
               deleteDirectory(fileDelete);
            }

            if (file.list().length == 0) {
               file.delete();
            }
         }
      } else {
         file.delete();
      }
   }

   public static void moveDirectoryContents(File source, File destination) throws IOException {
      if (source.isDirectory() && destination.isDirectory()) {
         if (!destination.exists()) {
            destination.mkdirs();
         }

         File[] files = source.listFiles();
         if (files != null) {
            for (File file : files) {
               File newFile = new File(destination, file.getName());
               if (file.isDirectory()) {
                  moveDirectoryContents(file, newFile);
               } else {
                  java.nio.file.Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Both parameters must be directories.");
      }
   }
}
