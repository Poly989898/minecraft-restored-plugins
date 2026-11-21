package io.lumine.mythic.core.skills.placeholders.papi;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.PAPIPlaceholder;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@MythicPAPIPlaceholder(
   placeholder = "stat"
)
public class StatPlaceholder extends PAPIPlaceholder<MythicBukkit> {
   public StatPlaceholder(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public String onPlaceholderRequest(OfflinePlayer player, String[] args) {
      if (args.length < 1) {
         return "[Invalid]";
      } else {
         String statString = String.join("_", args);
         Optional<StatType> stat = MythicBukkit.inst().getStatManager().getStat(statString.toUpperCase());
         if (stat.isEmpty()) {
            return "0";
         } else {
            Optional<StatRegistry> statRegistry = this.getPlugin().getStatManager().getStatRegistry(BukkitAdapter.adapt((Player)player));
            if (statRegistry.isEmpty()) {
               return "[Invalid]";
            } else {
               double statValue = statRegistry.get().get(stat.get());
               return String.valueOf(statValue);
            }
         }
      }
   }
}
