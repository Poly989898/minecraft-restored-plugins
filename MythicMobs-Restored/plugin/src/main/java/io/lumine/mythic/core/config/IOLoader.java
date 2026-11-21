package io.lumine.mythic.core.config;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class IOLoader<T extends JavaPlugin> {
   private T plugin;
   private File file = null;
   private FileConfiguration fileConfig = null;
   private String defaultFile;

   public IOLoader(T plugin, String newfile) {
      this(plugin, newfile, null);
   }

   public IOLoader(T plugin, String newfile, String folder) {
      this.plugin = plugin;
      this.defaultFile = newfile;
      if (folder != null) {
         folder = folder.replace("/", System.getProperty("file.separator"));
         String path = plugin.getDataFolder() + System.getProperty("file.separator") + folder.toLowerCase(Locale.ROOT);
         File dir = new File(path);
         if (!dir.exists()) {
            path = plugin.getDataFolder() + System.getProperty("file.separator") + folder;
            dir = new File(path);
            if (!dir.exists()) {
               path = plugin.getDataFolder() + System.getProperty("file.separator") + folder.toLowerCase(Locale.ROOT);
               dir = new File(path);
               dir.mkdir();
            }
         }

         this.file = new File(path, newfile);
      } else {
         this.file = new File(plugin.getDataFolder(), newfile);
      }

      this.reloadCustomConfig(!this.file.exists() && MythicProvider.get().getConfiguration().getLoadExampleConfigs());
   }

   public IOLoader(T plugin, File newfile, String folder) {
      this.plugin = plugin;
      this.file = newfile;
      this.reloadCustomConfig(this.file == null);
   }

   public void reloadCustomConfig(boolean loadDefaults) {
      if (loadDefaults && this.file != null) {
         this.file = new File(this.file.getParent(), this.defaultFile);

         try {
            this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
         } catch (Throwable e) {
            MythicLogger.errorYMLConfig(this.file, e.getMessage());
            return;
         }

         try (InputStream defConfigStream = this.plugin.getResource(this.defaultFile)) {
            if (defConfigStream != null) {
               try (Reader reader = new InputStreamReader(defConfigStream)) {
                  YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
                  this.fileConfig.setDefaults(defConfig);
               }
            }
         } catch (IOException ex) {
            MythicLogger.error("Failed to load default config for " + this.defaultFile, ex);
         }

         this.getCustomConfig().options().copyDefaults(true);
         MythicLogger.log("File " + this.defaultFile + " not found! Creating a new one...");
         this.saveCustomConfig();
      } else {
         try {
            this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
         } catch (Throwable ex2) {
            MythicLogger.errorYMLConfig(this.file, ex2.getMessage());
         }
      }
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getCustomConfig() {
      if (this.fileConfig == null) {
         this.reloadCustomConfig(this.file == null);
      }

      return this.fileConfig;
   }

   public void saveCustomConfig() {
      if (this.fileConfig != null && this.file != null) {
         try {
            this.getCustomConfig().save(this.file);
         } catch (IOException e) {
            if (MythicProvider.get().getConfiguration().getDebugLevel() > 0) {
               this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.file, (Throwable)e);
            } else {
               MythicLogger.error("Severe Error: Could not save config to " + this.file + ". Turn on debugging if this error keeps occuring for details.");
            }
         }
      }
   }
}
