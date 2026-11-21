package io.lumine.mythic.core.skills.placeholders.papi.variables;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.papi.VariablePlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import org.bukkit.OfflinePlayer;

@MythicPAPIPlaceholder(
   placeholder = "var_global"
)
public class GlobalVariablePlaceholder extends VariablePlaceholder {
   public GlobalVariablePlaceholder(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public String onPlaceholderRequest(OfflinePlayer player, String[] args) {
      return this.getManager().getGlobalRegistry().get(args[0]).toString();
   }
}
