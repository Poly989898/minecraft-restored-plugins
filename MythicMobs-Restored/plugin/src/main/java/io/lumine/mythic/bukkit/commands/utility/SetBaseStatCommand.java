package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBaseStatCommand extends Command<MythicBukkit> {
   public SetBaseStatCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Optional<StatType> maybeStat = this.getPlugin().getStatManager().getStat(args[0]);
      if (maybeStat.isEmpty()) {
         return true;
      } else {
         StatType stat = maybeStat.get();
         OptionalDouble maybeAmount = Numbers.parseDouble(args[1]);
         if (maybeAmount.isEmpty()) {
            return true;
         } else {
            double amount = maybeAmount.getAsDouble();
            Player target;
            if (args.length > 2) {
               target = Bukkit.getPlayer(args[2]);
            } else {
               target = (Player)sender;
            }

            if (target == null) {
               CommandHelper.sendError(sender, "Target not found");
               return true;
            } else {
               PlayerData profile = this.getPlugin().getPlayerManager().getProfile(target);
               StatRegistry stats = profile.getStatRegistry();
               stats.putBaseValue(stat, amount);
               CommandHelper.sendSuccess(sender, "Set base stat");
               return true;
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getStatManager().getStats().keySet(), 2);
      } else {
         return args.length == 3
            ? CollectionUtils.filterMatches(args[2], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList(), 2)
            : Collections.emptyList();
      }
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
      return "setbasestat";
   }
}
