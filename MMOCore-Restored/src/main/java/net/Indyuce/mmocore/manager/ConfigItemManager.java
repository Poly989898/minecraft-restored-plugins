package net.Indyuce.mmocore.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.util.item.ConfigItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

public class ConfigItemManager implements MMOCoreManager {
   private final Map<String, ConfigItem> map = new HashMap<>();

   public void register(ConfigItem item) {
      this.map.put(item.getId(), item);
   }

   @Nullable
   public ConfigItem get(String id) {
      return this.map.get(id);
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.map.clear();
      }

      FileConfiguration config = new ConfigFile("items").getConfig();

      for (String key : config.getKeys(false)) {
         try {
            this.register(new ConfigItem(config.getConfigurationSection(key)));
         } catch (NullPointerException | IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load config item " + key);
         }
      }
   }
}
