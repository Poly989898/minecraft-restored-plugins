package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetStatsCommand extends Command<MythicBukkit> {
   public GetStatsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      Player target;
      if (args.length > 0) {
         target = Bukkit.getPlayer(args[0]);
      } else {
         target = p;
      }

      if (target == null) {
         CommandHelper.sendError(sender, "Target not found");
         return true;
      } else {
         PlayerData profile = this.getPlugin().getPlayerManager().getProfile(target);
         StatRegistry stats = profile.getStatRegistry();
         CommandHelper.sendEditorMessage(sender, "Stats for player " + target.getName() + ":");
         CommandHelper.send(sender, " ");

         for (Entry<StatType, StatRegistry.StatMap> entry : stats.getStatMaps().entrySet()) {
            StatType stat = entry.getKey();
            StatRegistry.StatMap map = entry.getValue();
            double baseValue = map.getBaseValue();
            double value = stats.get(stat);
            CommandHelper.send(sender, stat.getKey() + " " + value + " (base " + baseValue + ")");
            if (!map.getAdditives().isEmpty()) {
               CommandHelper.send(sender, "\u00b7 Additive Modifiers");

               for (Entry<StatSource, Double> sourceEntry : map.getAdditives().entrySet()) {
                  CommandHelper.send(sender, "\u27f6 " + sourceEntry.getKey().getClass().getName() + " " + sourceEntry.getValue());
               }
            }

            if (!map.getAdditiveMultipliers().isEmpty()) {
               CommandHelper.send(sender, "\u00b7 Additive Multiplier Modifiers");

               for (Entry<StatSource, Double> sourceEntry : map.getAdditiveMultipliers().entrySet()) {
                  CommandHelper.send(sender, "\u27f6 " + sourceEntry.getKey().getClass().getName() + " " + sourceEntry.getValue());
               }
            }

            if (!map.getCompoundMultipliers().isEmpty()) {
               CommandHelper.send(sender, "\u00b7 Compound Multiplier Modifiers");

               for (Entry<StatSource, Double> sourceEntry : map.getCompoundMultipliers().entrySet()) {
                  CommandHelper.send(sender, "\u27f6 " + sourceEntry.getKey().getClass().getName() + " " + sourceEntry.getValue());
               }
            }

            if (!map.getSetters().isEmpty()) {
               CommandHelper.send(sender, "\u00b7 Setter Modifiers");

               for (Entry<StatSource, Double> sourceEntry : map.getSetters().entrySet()) {
                  CommandHelper.send(sender, "\u27f6 " + sourceEntry.getKey().getClass().getName() + " " + sourceEntry.getValue());
               }
            }
         }

         CommandHelper.sendFooter(sender);
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getstats";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getstats";
   }
}
