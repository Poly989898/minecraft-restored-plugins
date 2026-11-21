package io.lumine.mythic.bukkit.utils.papi;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.HashMap;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public abstract class PlaceholderManager<T extends LuminePlugin> extends PlaceholderExpansion {
   private final T plugin;
   private final Map<String, Placeholder<T>> placeholders = new HashMap<>();

   public PlaceholderManager(T plugin) {
      this.plugin = plugin;
      this.register();
   }

   public String parse(String string) {
      return PlaceholderAPI.setPlaceholders(null, string);
   }

   public String parse(String string, Player player) {
      return PlaceholderAPI.setPlaceholders(player, string);
   }

   public void registerPlaceholders(Placeholder<T>... placeholders) {
      for (Placeholder<T> placeholder : placeholders) {
         this.placeholders.put(placeholder.getName(), placeholder);
      }
   }

   public String onPlaceholderRequest(Player player, String identifier) {
      String[] parts = identifier.split("_");
      if (parts.length == 0) {
         return "";
      } else {
         Placeholder<T> placeholder = this.placeholders.get(parts[0]);
         if (placeholder == null) {
            return "";
         } else {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            return placeholder.onPlaceholderRequest(player, args);
         }
      }
   }

   public String getAuthor() {
      return "Lumine Studios";
   }

   public abstract String getIdentifier();

   public String getVersion() {
      return "0.0.1";
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }
}
