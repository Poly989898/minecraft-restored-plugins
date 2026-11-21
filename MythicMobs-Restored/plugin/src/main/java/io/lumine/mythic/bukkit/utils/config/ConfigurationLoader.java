package io.lumine.mythic.bukkit.utils.config;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationLoader<T extends LuminePlugin> {
   private FileConfiguration DataConfig = null;
   private File data = null;
   private T plugin;
   private String file;
   public File thefile;

   public ConfigurationLoader(T plugin, String newfile) {
      this(plugin, newfile, null, false);
   }

   public ConfigurationLoader(T plugin, String newfile, boolean isFileAbsolute) {
      this(plugin, newfile, null, isFileAbsolute);
   }

   public ConfigurationLoader(T plugin, String newfile, String folder) {
      this(plugin, newfile, folder, false);
   }

   public ConfigurationLoader(T plugin, String newfile, String folder, boolean isFileAbsolute) {
      this.plugin = plugin;
      this.file = newfile;
      if (isFileAbsolute) {
         this.thefile = new File(newfile);
      } else if (folder != null) {
         folder = folder.replace("/", System.getProperty("file.separator"));
         this.thefile = new File(plugin.getDataFolder() + System.getProperty("file.separator") + folder, newfile);
      } else {
         this.thefile = new File(plugin.getDataFolder(), newfile);
      }

      if (this.thefile.exists()) {
         this.data = this.thefile;
      }

      this.reloadCustomConfig();
   }

   public ConfigurationLoader(T plugin, File newfile, String folder) {
      this.plugin = plugin;
      this.data = newfile;
      this.thefile = newfile;
      this.reloadCustomConfig();
   }

   public ConfigurationLoader(T plugin, InputStream newfile) {
      this.plugin = plugin;
      this.data = null;
      this.thefile = null;
      this.DataConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(newfile));
   }

   public void reloadCustomConfig() {
      if (this.data == null) {
         if (this.thefile == null) {
            return;
         }

         this.data = this.thefile;
         this.DataConfig = YamlConfiguration.loadConfiguration(this.data);
         InputStream defConfigStream = this.plugin.getResource(this.file);
         if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(this.data);
            this.DataConfig.setDefaults(defConfig);
         }
      } else {
         this.DataConfig = YamlConfiguration.loadConfiguration(this.data);
      }
   }

   public FileConfiguration getCustomConfig() {
      if (this.DataConfig == null) {
         this.reloadCustomConfig();
      }

      return this.DataConfig;
   }

   public void saveCustomConfig() {
      if (this.DataConfig != null && this.data != null) {
         try {
            this.getCustomConfig().save(this.data);
         } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.data, (Throwable)e);
            e.printStackTrace();
         }
      }
   }

   public File getFile() {
      return this.thefile;
   }
}
