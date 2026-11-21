package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class NodeListProp extends PropertyType<Set<String>> {
   public NodeListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Set<String> compute(ConfigurationSection section, String node) {
      if (section != null && section.isSet(node)) {
         ConfigurationSection subSection = section.getConfigurationSection(node);
         return subSection == null ? Collections.emptySet() : subSection.getKeys(false);
      } else {
         return Collections.emptySet();
      }
   }

   protected Set<String> getDefaultValue() {
      return Collections.emptySet();
   }

   public static Collection<String> getNodes(File file) {
      return getNodes(file, "");
   }

   public static Collection<String> getNodes(File file, String node) {
      try {
         YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
         return config.getConfigurationSection(node).getKeys(false);
      } catch (Error | Exception e) {
         e.printStackTrace();
         return Collections.emptyList();
      }
   }
}
