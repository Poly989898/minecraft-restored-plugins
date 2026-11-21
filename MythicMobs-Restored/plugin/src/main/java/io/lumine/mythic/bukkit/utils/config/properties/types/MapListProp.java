package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class MapListProp extends PropertyType<List<Map<?, ?>>> {
   public MapListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected List<Map<?, ?>> compute(ConfigurationSection section, String node) {
      return section != null && section.isSet(node) ? section.getMapList(node) : Collections.emptyList();
   }

   protected List<Map<?, ?>> getDefaultValue() {
      return Collections.emptyList();
   }
}
