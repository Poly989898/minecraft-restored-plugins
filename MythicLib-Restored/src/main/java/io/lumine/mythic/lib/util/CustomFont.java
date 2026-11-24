package io.lumine.mythic.lib.util;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class CustomFont {
   private final char[] characters = new char[10];
   private final char separator;
   private final char middle;

   public CustomFont(ConfigurationSection config) {
      for (int i = 0; i < 10; i++) {
         this.characters[i] = config.getString(String.valueOf(i), "?").charAt(0);
      }

      this.separator = config.getString("dot", "?").charAt(0);
      this.middle = config.contains("inter") ? config.getString("inter").charAt(0) : '\u0000';
   }

   @NotNull
   public String format(String raw) {
      StringBuilder builder = new StringBuilder();

      for (char c : raw.toCharArray()) {
         int index = c - '0';
         if (this.middle != 0 && builder.length() != 0) {
            builder.append(this.middle);
         }

         builder.append(index >= 0 && index <= 9 ? this.characters[index] : this.separator);
      }

      return builder.toString();
   }
}
