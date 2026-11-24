package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigFile {
   private final File file;
   private final String name;
   private final FileConfiguration config;

   public ConfigFile(String name) {
      this(MythicLib.plugin, "", name);
   }

   public ConfigFile(String folder, String name) {
      this(MythicLib.plugin, folder, name);
   }

   public ConfigFile(Plugin plugin, String folder, String name) {
      this.config = YamlConfiguration.loadConfiguration(this.file = new File(plugin.getDataFolder() + folder, (this.name = name) + ".yml"));
   }

   public boolean exists() {
      return this.file.exists();
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public void save() {
      try {
         this.config.save(this.file);
      } catch (IOException exception) {
         MythicLib.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.name + ".yml: " + exception.getMessage());
      }
   }

   public void delete() {
      if (this.file.exists() && !this.file.delete()) {
         MythicLib.plugin.getLogger().log(Level.SEVERE, "Could not delete " + this.name + ".yml.");
      }
   }
}
