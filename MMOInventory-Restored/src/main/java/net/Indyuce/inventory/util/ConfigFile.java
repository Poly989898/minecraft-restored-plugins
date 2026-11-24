package net.Indyuce.inventory.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.inventory.MMOInventory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigFile {
   private final File file;
   private final FileConfiguration config;

   public ConfigFile(UUID uuid) {
      this(MMOInventory.plugin, "/userdata", uuid.toString());
   }

   public ConfigFile(String name) {
      this(MMOInventory.plugin, "", name);
   }

   public ConfigFile(String folder, String name) {
      this(MMOInventory.plugin, folder, name);
   }

   public ConfigFile(Plugin plugin, String folder, String name) {
      this.config = YamlConfiguration.loadConfiguration(this.file = new File(plugin.getDataFolder() + folder, name + ".yml"));
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public void save() {
      try {
         this.config.save(this.file);
      } catch (IOException e2) {
         MMOInventory.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.file.getName() + ".yml!");
      }
   }
}
