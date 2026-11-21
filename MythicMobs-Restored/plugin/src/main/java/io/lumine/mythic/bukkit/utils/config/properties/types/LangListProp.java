package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;

public class LangListProp extends PropertyType<List<String>> {
   public LangListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   public List<String> get(Map<String, Object> replacements) {
      List<String> list = super.get();
      ListIterator<String> it = list.listIterator();

      while (it.hasNext()) {
         it.set(LangProp.replaceTokens(it.next(), replacements));
      }

      return list;
   }

   public List<String> get(PropertyHolder holder, Map<String, Object> replacements) {
      return this.get(holder.getPropertyNode(), replacements);
   }

   public List<String> get(String holder, Map<String, Object> replacements) {
      List<String> list = super.get(holder);
      ListIterator<String> it = list.listIterator();

      while (it.hasNext()) {
         it.set(LangProp.replaceTokens(it.next(), replacements));
      }

      return list;
   }

   protected List<String> compute(ConfigurationSection section, String node) {
      return section.getStringList(node).stream().map(line -> Text.colorizeLegacy(Text.colorize(line))).collect(Collectors.toList());
   }

   protected List<String> getDefaultValue() {
      return Collections.emptyList();
   }
}
