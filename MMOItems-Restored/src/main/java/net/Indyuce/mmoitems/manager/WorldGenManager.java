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

   public WorldGenTemplate getOrThrow(String text) {
      Validate.isTrue(this.templates.containsKey(text), "Could not find gen template with ID '" + text + "'");
      return this.templates.get(text);
   }

   public void assign(CustomBlock customBlock, WorldGenTemplate worldGenTemplate) {
      Validate.notNull(worldGenTemplate, "Cannot assign a null template to a custom block");
      this.assigned.put(customBlock, worldGenTemplate);
   }

   @Override
   public void reload() {
      if (this.listener != null) {
         HandlerList.unregisterAll(this.listener);
      }

      this.assigned.clear();
      this.templates.clear();
      FileConfiguration config = new ConfigFile("gen-templates").getConfig();

      for (String key : config.getKeys(false)) {
         try {
            WorldGenTemplate worldGenTemplate = new WorldGenTemplate(config.getConfigurationSection(key));
            this.templates.put(worldGenTemplate.getId(), worldGenTemplate);
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occurred when loading gen template '" + key + "': " + exception.getMessage());
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

   public MMOBlockPopulator populator(@NotNull World world) {
      return new MMOBlockPopulator(world, this);
   }

   public Map<CustomBlock, WorldGenTemplate> assigned() {
      return this.assigned;
   }
}
