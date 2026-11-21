package it.ytnoos.lpx.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig {
   private final File file;
   private final String name;
   private FileConfiguration configuration;

   public CustomConfig(JavaPlugin javaPlugin, String string, boolean flag) {
      if (!string.endsWith(".yml")) {
         string = string + ".yml";
      }

      this.name = string;
      if (flag) {
         File file2 = Bukkit.getServer().getWorldContainer();
         File file3 = file2.getAbsoluteFile();
         String string2 = file3.getParentFile().getParent();
         this.file = new File(string2 + "/config/", string);
      } else {
         this.file = new File(javaPlugin.getDataFolder(), string);
      }

      this.load(javaPlugin);
   }

   private void load(JavaPlugin javaPlugin) {
      if (!this.file.exists()) {
         this.file.getParentFile().mkdirs();
         javaPlugin.saveResource(this.name, false);
      }

      this.configuration = new YamlConfiguration();

      try {
         this.configuration.load(this.file);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void save() {
      try {
         this.configuration.save(this.file);
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getConfiguration() {
      return this.configuration;
   }

   public int getInt(String string) {
      return this.configuration.getInt(string);
   }

   public int getInt(String string, int value) {
      return this.configuration.getInt(string, value);
   }

   public String getString(String string) {
      return this.configuration.getString(string);
   }

   public String getString(String string, String string2) {
      return this.configuration.getString(string, string2);
   }

   public double getDouble(String string) {
      return this.configuration.getDouble(string);
   }

   public double getDouble(String string, double number) {
      return this.configuration.getDouble(string, number);
   }

   public boolean getBoolean(String string) {
      return this.configuration.getBoolean(string);
   }

   public boolean getBoolean(String string, boolean flag) {
      return this.configuration.getBoolean(string, flag);
   }

   public long getLong(String string) {
      return this.configuration.getLong(string);
   }

   public long getLong(String string, long longValue) {
      return this.configuration.getLong(string, longValue);
   }

   public List getList(String string) {
      return this.configuration.getList(string);
   }

   public List getList(String string, List list) {
      return this.configuration.getList(string, list);
   }

   public List getStringList(String string) {
      return this.configuration.getStringList(string);
   }

   public ConfigurationSection getSection(String string) {
      return this.configuration.getConfigurationSection(string);
   }
}
