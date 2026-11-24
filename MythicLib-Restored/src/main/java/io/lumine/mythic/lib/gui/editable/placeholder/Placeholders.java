package io.lumine.mythic.lib.gui.editable.placeholder;

import io.lumine.mythic.lib.MythicLib;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.OfflinePlayer;

/** Values substituted for {key} in the name and the lore of GUI items. */
public class Placeholders {
   private final Map<String, String> placeholders = new HashMap<>();
   private String fallback;

   public void register(String key, Object value) {
      this.placeholders.put(key, value.toString());
   }

   /** Text used for the keys that were not registered; they are left untouched when no fallback is set. */
   public void setFallback(String fallback) {
      this.fallback = fallback;
   }

   /** Replaces {key} by the registered values, then lets the placeholder parser (PlaceholderAPI, colors) process the text. */
   public String apply(OfflinePlayer player, String str) {
      StringBuilder builder = new StringBuilder(str);
      int index = 0;
      while (true) {
         int open = builder.indexOf("{", index);
         if (open < 0) {
            break;
         }

         int close = builder.indexOf("}", open);
         if (close < 0) {
            break;
         }

         String key = builder.substring(open + 1, close);
         String value = this.parsePlaceholder(key);
         if (value == null) {
            value = this.fallback;
         }

         if (value != null) {
            builder.replace(open, close + 1, value);
            index = open + value.length();
         } else {
            index = open + 1;
         }
      }

      return MythicLib.plugin.getPlaceholderParser().parse(player, builder.toString());
   }

   public String parsePlaceholder(String key) {
      return this.placeholders.get(key);
   }
}
