package io.lumine.mythic.core.skills.placeholders.papi;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.PAPIPlaceholder;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableManager;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicPAPIPlaceholder(
   placeholder = "var"
)
public class VariablePlaceholder extends PAPIPlaceholder<MythicBukkit> {
   public VariablePlaceholder(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public String onPlaceholderRequest(OfflinePlayer player, String[] args) {
      if (args.length > 1) {
         Player maybePlayer = Bukkit.getPlayer(args[0]);
         if (maybePlayer != null) {
            AbstractPlayer abstractPlayer = BukkitAdapter.adapt(maybePlayer);
            VariableRegistry registry = this.getManager().getRegistry(VariableScope.CASTER, abstractPlayer);
            return registry == null ? "" : registry.get(args[1]).toString();
         } else {
            try {
               Entity maybeEntity = Bukkit.getEntity(UUID.fromString(args[0]));
               if (maybeEntity != null) {
                  AbstractEntity abstractEntity = BukkitAdapter.adapt(maybeEntity);
                  VariableRegistry registry = this.getManager().getRegistry(VariableScope.CASTER, abstractEntity);
                  if (registry == null) {
                     return "";
                  }

                  return registry.get(args[1]).toString();
               }
            } catch (Exception e) {
               e.printStackTrace();
            }

            return "[Error]";
         }
      } else if (args.length == 1) {
         AbstractPlayer abstractPlayer = BukkitAdapter.adapt(player.getPlayer());
         VariableRegistry registry = this.getManager().getRegistry(VariableScope.CASTER, abstractPlayer);
         if (registry == null) {
            return "";
         } else {
            Variable maybeVariable = registry.get(args[0]);
            return maybeVariable != null ? maybeVariable.toString() : "[Undefined]";
         }
      } else {
         return "[Undefined]";
      }
   }

   protected VariableManager getManager() {
      return this.getPlugin().getVariableManager();
   }
}
