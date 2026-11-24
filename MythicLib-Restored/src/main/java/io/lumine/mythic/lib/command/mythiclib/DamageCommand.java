package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageCommand extends CommandTreeNode {
   public DamageCommand(CommandTreeNode parent) {
      super(parent, "damage");
      this.addParameter(Parameter.PLAYER.key("damager"));
      this.addParameter(Parameter.PLAYER.key("target"));
      this.addParameter(Parameter.AMOUNT.key("value"));
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      World world = sender instanceof Entity ? ((Entity)sender).getWorld() : null;
      LivingEntity damager = this.entity(world, args[1]);
      if (!(damager instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "Damager must be a player. Please provide either player name or UUID");
         return CommandTreeNode.CommandResult.FAILURE;
      }

      LivingEntity target = this.entity(world, args[2]);
      if (target == null) {
         sender.sendMessage(ChatColor.RED + "Could not find target. Please provide either player name or UUID");
         return CommandTreeNode.CommandResult.FAILURE;
      }

      double value;
      try {
         value = Double.parseDouble(args[3]);
      } catch (Throwable throwable) {
         return CommandTreeNode.CommandResult.FAILURE;
      }

      PlayerMetadata player = MMOPlayerData.get(damager.getUniqueId()).getStatMap().cache(EquipmentSlot.MAIN_HAND);
      DamageMetadata damage = new DamageMetadata(value);
      AttackMetadata attack = new AttackMetadata(damage, target, player);
      MythicLib.plugin.getDamage().registerAttack(attack);
      return CommandTreeNode.CommandResult.SUCCESS;
   }

   @Nullable
   private LivingEntity entity(@Nullable World world, @NotNull String input) {
      Player found = Bukkit.getPlayer(input);
      if (found != null) {
         return found;
      }

      try {
         UUID uuid = UUID.fromString(input);
         Entity temp = world == null ? Bukkit.getEntity(uuid) : world.getEntity(uuid);
         if (temp instanceof LivingEntity) {
            return (LivingEntity)temp;
         }
      } catch (Throwable exception) {
      }

      return null;
   }
}
