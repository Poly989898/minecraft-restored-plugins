package net.Indyuce.mmocore.api;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.guild.provided.Guild;
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
      this(MMOCore.plugin, "/userdata", uuid.toString());
   }

   public ConfigFile(Guild guild) {
      this(MMOCore.plugin, "/guilds", guild.getId());
   }

   public ConfigFile(String name) {
      this(MMOCore.plugin, "", name);
   }

   public ConfigFile(String folder, String name) {
      this(MMOCore.plugin, folder, name);
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
         MMOCore.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.name + ".yml: " + exception.getMessage());
      }
   }

   public void delete() {
      if (this.file.exists() && !this.file.delete()) {
         MMOCore.plugin.getLogger().log(Level.SEVERE, "Could not delete " + this.name + ".yml.");
      }
   }
}
