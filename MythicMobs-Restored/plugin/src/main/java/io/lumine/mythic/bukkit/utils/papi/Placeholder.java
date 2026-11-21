package io.lumine.mythic.bukkit.utils.papi;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public abstract class Placeholder<T extends LuminePlugin> {
   protected final T plugin;
   protected final Map<String, Placeholder<T>> nestedPlaceholders = new HashMap<>();

   public Placeholder(T plugin) {
      this.plugin = plugin;
   }

   public Placeholder(Placeholder<T> parent) {
      this.plugin = parent.getPlugin();
   }

   public void registerNestedPlaceholders(Placeholder<T>... placeholders) {
      for (Placeholder<T> placeholder : placeholders) {
         this.nestedPlaceholders.put(placeholder.getName(), placeholder);
      }
   }

   public String processPlaceholder(Player player, String[] args) {
      if (this.requiresPlayer() && player == null) {
         return "";
      } else {
         if (args.length > 0) {
            Placeholder<T> sub = this.nestedPlaceholders.get(args[0].toLowerCase());
            if (sub != null) {
               return this.processPlaceholder(player, Arrays.copyOfRange(args, 1, args.length));
            }
         }

         return this.onPlaceholderRequest(player, args);
      }
   }

   public abstract String onPlaceholderRequest(Player player, String[] strs);

   public abstract String getName();

   public abstract boolean requiresPlayer();

   protected String processNestedPlaceholders(Player player, String[] args) {
      if (args.length == 0) {
         return "";
      } else {
         Placeholder<T> placeholder = this.nestedPlaceholders.get(args[0]);
         if (placeholder == null) {
            return "";
         } else {
            String[] nestedArgs = new String[args.length - 1];
            System.arraycopy(args, 1, nestedArgs, 0, nestedArgs.length);
            return placeholder.onPlaceholderRequest(player, nestedArgs);
         }
      }
   }

   public T getPlugin() {
      return this.plugin;
   }
}
