package io.lumine.mythic.core.skills.placeholders.papi.variables;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.papi.VariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import org.bukkit.OfflinePlayer;

@MythicPAPIPlaceholder(
   placeholder = "var_world"
)
public class WorldVariablePlaceholder extends VariablePlaceholder {
   public WorldVariablePlaceholder(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public String onPlaceholderRequest(OfflinePlayer player, String[] args) {
      if (args.length > 1) {
         String worldName = args[0];
         AbstractWorld world = this.getPlugin().getBootstrap().getWorld(worldName);
         return world == null
            ? "[Invalid World]"
            : this.getManager().getRegistry(VariableScope.WORLD, new AbstractLocation(world, 0.0, 0.0, 0.0)).get(args[1]).toString();
      } else if (player == null) {
         return "";
      } else {
         AbstractPlayer abstractPlayer = BukkitAdapter.adapt(player.getPlayer());
         return this.getManager().getRegistry(VariableScope.WORLD, abstractPlayer).get(args[0]).toString();
      }
   }
}
