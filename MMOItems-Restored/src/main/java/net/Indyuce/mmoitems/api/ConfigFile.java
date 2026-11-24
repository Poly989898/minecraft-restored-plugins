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

   public ConfigFile(String var1) {
      this(MMOItems.plugin, "", var1);
   }

   public ConfigFile(Plugin var1, String var2) {
      this(var1, "", var2);
   }

   public ConfigFile(String var1, String var2) {
      this(MMOItems.plugin, var1, var2);
   }

   public ConfigFile(Plugin var1, String var2, String var3) {
      this.plugin = var1;
      this.path = var2;
      this.name = var3;
      File var4 = new File(var1.getDataFolder() + var2, var3 + ".yml");
      this.exists = var4.exists();
      this.config = YamlConfiguration.loadConfiguration(var4);
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
      } catch (IOException var2) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not save " + this.name + ".yml: " + var2.getMessage());
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
      } catch (IOException var2) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not generate " + this.name + ".yml: " + var2.getMessage());
      }
   }

   public void registerTemplateEdition(ItemReference var1) {
      this.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(var1.getType(), var1.getId());
   }
}
