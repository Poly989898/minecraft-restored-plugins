package io.lumine.mythic.bukkit.utils.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class FileStorageHandler<T> {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
   private final String fileName;
   private final String fileExtension;
   private final File dataFolder;

   public static <T> FileStorageHandler<T> build(
      String fileName, String fileExtension, File dataFolder, final Function<Path, T> loadingFunc, final BiConsumer<Path, T> savingFunc
   ) {
      return new FileStorageHandler<T>(fileName, fileExtension, dataFolder) {
         @Override
         protected T readFromFile(Path path) {
            return loadingFunc.apply(path);
         }

         @Override
         protected void saveToFile(Path path, T t) {
            savingFunc.accept(path, t);
         }
      };
   }

   public FileStorageHandler(String fileName, String fileExtension, File dataFolder) {
      this.fileName = fileName;
      this.fileExtension = fileExtension;
      this.dataFolder = dataFolder;
   }

   protected abstract T readFromFile(Path path);

   protected abstract void saveToFile(Path path, T t);

   public Optional<T> load() {
      File file = new File(this.dataFolder, this.fileName + this.fileExtension);
      return file.exists() ? Optional.ofNullable(this.readFromFile(file.toPath())) : Optional.empty();
   }

   public void saveAndBackup(T data) {
      this.dataFolder.mkdirs();
      File file = new File(this.dataFolder, this.fileName + this.fileExtension);
      if (file.exists()) {
         File backupDir = new File(this.dataFolder, "backups");
         backupDir.mkdirs();
         File backupFile = new File(backupDir, this.fileName + "-" + DATE_FORMAT.format(new Date(System.currentTimeMillis())) + this.fileExtension);

         try {
            Files.move(file.toPath(), backupFile.toPath());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      try {
         file.createNewFile();
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      this.saveToFile(file.toPath(), data);
   }

   public void save(T data) {
      this.dataFolder.mkdirs();
      File file = new File(this.dataFolder, this.fileName + this.fileExtension);
      if (file.exists()) {
         file.delete();
      }

      try {
         file.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.saveToFile(file.toPath(), data);
   }
}
