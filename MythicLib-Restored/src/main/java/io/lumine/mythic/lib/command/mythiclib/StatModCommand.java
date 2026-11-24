package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class StatModCommand extends CommandTreeNode {
   @Deprecated
   public StatModCommand(CommandTreeNode parent) {
      super(parent, "statmod");
      this.addParameter(Parameter.PLAYER);
      this.addParameter(Parameter.STAT);
      this.addParameter(Parameter.AMOUNT.key("value"));
      this.addParameter(Parameter.DURATION_TICKS.optional(true));
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      sender.sendMessage("/ml statmod is deprecated. Use instead /ml tempstat ...");
      if (args.length < 4) {
         return CommandTreeNode.CommandResult.THROW_USAGE;
      }

      Player target = Bukkit.getPlayer(args[1]);
      if (target == null) {
         sender.sendMessage(ChatColor.RED + "Player not found.");
         return CommandTreeNode.CommandResult.FAILURE;
      }

      String statName = UtilityMethods.enumName(args[2]);
      MMOPlayerData playerData = MMOPlayerData.get(target);
      ModifierType type = args[3].toCharArray()[args[3].length() - 1] == '%' ? ModifierType.RELATIVE : ModifierType.FLAT;
      double value = Double.parseDouble(type == ModifierType.RELATIVE ? args[3].substring(0, args[3].length() - 1) : args[3]);
      long duration = args.length > 4 ? Math.max(1L, (long)Double.parseDouble(args[4])) : 0L;
      if (duration <= 0L) {
         new StatModifier(UUID.randomUUID().toString(), statName, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER).register(playerData);
      } else {
         new TemporaryStatModifier(UUID.randomUUID().toString(), statName, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER)
            .register(playerData, duration);
      }

      return CommandTreeNode.CommandResult.SUCCESS;
   }
}
