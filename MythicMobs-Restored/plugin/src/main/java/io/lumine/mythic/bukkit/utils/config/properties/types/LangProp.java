package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class LangProp extends StringProp {
   private final String defaultValue;

   public LangProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = "";
   }

   public LangProp(LuminePlugin plugin, Object file, String node, String defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   public String get(Map<String, Object> replacements) {
      return replaceTokens(super.get(), replacements);
   }

   public String get(PropertyHolder holder, Map<String, Object> replacements) {
      return this.get(holder.getPropertyNode(), replacements);
   }

   public String get(String holder, Map<String, Object> replacements) {
      return replaceTokens(super.get(holder), replacements);
   }

   @Override
   protected String compute(ConfigurationSection section, String node) {
      return Text.colorizeLegacy(Text.colorize(super.compute(section, node)));
   }

   protected static String replaceTokens(String src, Map<String, Object> replacements) {
      if (src.indexOf(60) != -1 && src.indexOf(62) != -1) {
         StringBuilder builder = new StringBuilder(src);
         int openIx = -1;

         for (int i = 0; i < builder.length(); i++) {
            if (openIx == -1) {
               if (builder.charAt(i) == '{') {
                  openIx = i;
               }
            } else if (builder.charAt(i) == '}') {
               String label = builder.substring(openIx + 1, i);
               Object object = replacements.get(label);
               if (object != null) {
                  String replacement = replacements.get(label).toString();
                  builder.replace(openIx, i + 1, replacement);
                  int lengthDiff = replacement.length() - (i + 1 - openIx);
                  i += lengthDiff;
               }

               openIx = -1;
            }
         }

         return builder.toString();
      } else {
         return src;
      }
   }
}
