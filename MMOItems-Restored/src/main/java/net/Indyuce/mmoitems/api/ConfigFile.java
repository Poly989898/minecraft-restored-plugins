package net.Indyuce.mmoitems.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.ItemReference;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigFile {
   private final Plugin plugin;
   private final String path;
   private final String name;
   private final boolean exists;
   private final FileConfiguration config;

   public ConfigFile(String text) {
      this(MMOItems.plugin, "", text);
   }

   public ConfigFile(Plugin plugin, String text) {
      this(plugin, "", text);
   }

   public ConfigFile(String text, String fileName) {
      this(MMOItems.plugin, text, fileName);
   }

   public ConfigFile(Plugin plugin, String text, String fileName) {
      this.plugin = plugin;
      this.path = text;
      this.name = fileName;
      File file = new File(plugin.getDataFolder() + text, fileName + ".yml");
      this.exists = file.exists();
      this.config = YamlConfiguration.loadConfiguration(file);
   }

   @NotNull
   public FileConfiguration getConfig() {
      return this.config;
   }

   public boolean exists() {
      return this.exists;
   }

   public void save() {
      try {
         this.config.save(new File(this.plugin.getDataFolder() + this.path, this.name + ".yml"));
      } catch (IOException exception) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.name + ".yml: " + exception.getMessage());
      }
   }

   public void setup() {
      try {
         if (!new File(this.plugin.getDataFolder() + this.path).exists()) {
            new File(this.plugin.getDataFolder() + this.path).mkdir();
         }

         if (!new File(this.plugin.getDataFolder() + this.path, this.name + ".yml").exists()) {
            new File(this.plugin.getDataFolder() + this.path, this.name + ".yml").createNewFile();
         }
      } catch (IOException exception) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not generate " + this.name + ".yml: " + exception.getMessage());
      }
   }

   public void registerTemplateEdition(ItemReference itemReference) {
      this.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(itemReference.getType(), itemReference.getId());
   }
}
