package net.Indyuce.mmoitems.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.block.WorldGenTemplate;
import net.Indyuce.mmoitems.api.world.MMOBlockPopulator;
import net.Indyuce.mmoitems.listener.WorldGenerationListener;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WorldGenManager implements Reloadable {
   private final Map<String, WorldGenTemplate> templates = new HashMap<>();
   private final Map<CustomBlock, WorldGenTemplate> assigned = new HashMap<>();
   private WorldGenerationListener listener;

   public WorldGenManager() {
      this.reload();
   }

   public WorldGenTemplate getOrThrow(String var1) {
      Validate.isTrue(this.templates.containsKey(var1), "Could not find gen template with ID '" + var1 + "'");
      return this.templates.get(var1);
   }

   public void assign(CustomBlock var1, WorldGenTemplate var2) {
      Validate.notNull(var2, "Cannot assign a null template to a custom block");
      this.assigned.put(var1, var2);
   }

   @Override
   public void reload() {
      if (this.listener != null) {
         HandlerList.unregisterAll(this.listener);
      }

      this.assigned.clear();
      this.templates.clear();
      FileConfiguration var1 = new ConfigFile("gen-templates").getConfig();

      for (String var3 : var1.getKeys(false)) {
         try {
            WorldGenTemplate var4 = new WorldGenTemplate(var1.getConfigurationSection(var3));
            this.templates.put(var4.getId(), var4);
         } catch (IllegalArgumentException var5) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occurred when loading gen template '" + var3 + "': " + var5.getMessage());
         }
      }

      if (MMOItems.plugin.getLanguage().worldGenEnabled) {
         this.listener = new WorldGenerationListener(this);
         Bukkit.getPluginManager().registerEvents(this.listener, MMOItems.plugin);
      }
   }

   public void unload() {
      if (this.listener != null) {
         HandlerList.unregisterAll(this.listener);
      }
   }

   public MMOBlockPopulator populator(@NotNull World var1) {
      return new MMOBlockPopulator(var1, this);
   }

   public Map<CustomBlock, WorldGenTemplate> assigned() {
      return this.assigned;
   }
}
