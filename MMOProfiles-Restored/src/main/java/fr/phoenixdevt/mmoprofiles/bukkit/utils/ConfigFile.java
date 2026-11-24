package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConfigFile {
   private final File file;
   private final String name;
   private final FileConfiguration config;

   public ConfigFile(Player player) {
      this(player.getUniqueId());
   }

   public ConfigFile(UUID uuid) {
      this(MMOProfiles.plugin, "/userdata", uuid.toString());
   }

   public ConfigFile(String name) {
      this(MMOProfiles.plugin, "", name);
   }

   public ConfigFile(String folder, String name) {
      this(MMOProfiles.plugin, folder, name);
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
         MMOProfiles.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.name + ".yml: " + exception.getMessage());
      }
   }

   public void delete() {
      if (this.file.exists() && !this.file.delete()) {
         MMOProfiles.plugin.getLogger().log(Level.SEVERE, "Could not delete " + this.name + ".yml.");
      }
   }
}
