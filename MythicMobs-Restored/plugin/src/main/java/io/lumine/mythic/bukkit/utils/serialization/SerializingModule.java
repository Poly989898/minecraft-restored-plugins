package io.lumine.mythic.bukkit.utils.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class SerializingModule<T extends LuminePlugin> extends ReloadableModule<T> {
   private File basedir = null;
   protected Gson GSON;

   public SerializingModule(T plugin, String folderName) {
      super(plugin);
      this.basedir = new File(plugin.getDataFolder(), folderName);
      if (!this.basedir.exists() && !this.basedir.mkdirs()) {
         Log.severe("Couldn't create data folder for " + this.getClass().getName());
      }

      this.GSON = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping().create();
      this.bindModuleWith(plugin);
   }

   public SerializingModule(T plugin, String folderName, Gson gson) {
      super(plugin);
      this.basedir = new File(plugin.getDataFolder(), folderName);
      if (!this.basedir.exists() && !this.basedir.mkdirs()) {
         Log.severe("Couldn't create data folder for " + this.getClass().getName());
      }

      this.GSON = gson;
      this.bindModuleWith(plugin);
   }

   @Override
   protected void doBinding(LuminePlugin plugin) {
   }

   public File getModuleFile(String path) {
      return new File(this.basedir, path);
   }

   public File getModuleDirectory(String path) {
      File dir = this.getModuleFile(path);
      if (!dir.exists() && !dir.mkdirs()) {
         Log.severe("Couldn't create data folder for " + this.getClass().getName());
      }

      return dir;
   }

   public List<File> getModuleFiles() {
      List<File> resultList = new ArrayList<>();
      File[] fList = this.basedir.listFiles();

      for (File file : fList) {
         if (file.isFile()) {
            resultList.add(file);
         } else if (file.isDirectory()) {
            resultList.addAll(this.getModuleFiles(file.getAbsolutePath()));
         }
      }

      return resultList;
   }

   public List<File> getModuleFiles(String path) {
      File directory = this.getModuleDirectory(path);
      List<File> resultList = new ArrayList<>();
      File[] fList = directory.listFiles();

      for (File file : fList) {
         if (file.isFile()) {
            resultList.add(file);
         } else if (file.isDirectory()) {
            resultList.addAll(this.getModuleFiles(file.getAbsolutePath()));
         }
      }

      return resultList;
   }

   public <U> WrappedJsonFile<U> loadFile(File file, Class<U> type) {
      if (!file.exists()) {
         try {
            return new WrappedJsonFile<>(this, file, type.newInstance());
         } catch (ReflectiveOperationException e) {
            Log.severe("Couldn't use default constructor when loading file");
            e.printStackTrace();
         }
      }

      return new WrappedJsonFile<>(this, file, this.loadJson(file, type));
   }

   public <U> U loadJson(File file, Class<U> type) {
      if (!file.exists()) {
         return null;
      } else {
         try {
            Object obj;
            try (FileReader reader = new FileReader(file)) {
               obj = this.GSON.fromJson(reader, type);
            }

            return (U)obj;
         } catch (IOException e) {
            Log.severe("Couldn't load json in module: " + this.getClass().getName());
            e.printStackTrace();
            return null;
         }
      }
   }

   public void saveJson(File file, Object value) {
      try (FileWriter writer = new FileWriter(file)) {
         this.GSON.toJson(value, writer);
      } catch (IOException e) {
         Log.severe("Couldn't save json in module: " + this.getClass().getName());
         e.printStackTrace();
      }
   }

   public void saveFile(File file, String contents) {
      try (FileWriter writer = new FileWriter(file)) {
         writer.write(contents);
      } catch (IOException e) {
         Log.severe("Couldn't save file in module: " + this.getClass().getName());
      }
   }

   public File getBasedir() {
      return this.basedir;
   }

   public Gson getGSON() {
      return this.GSON;
   }
}
