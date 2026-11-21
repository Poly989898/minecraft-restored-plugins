package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignalCommand extends Command<MythicBukkit> {
   public SignalCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      UUID uuid;
      String signal;
      try {
         uuid = UUID.fromString(args[0]);
         signal = args[1];
      } catch (Exception e) {
         sender.sendMessage(ChatColor.RED + "Signal skill must be in format: /mm signal <uuid> <signal>");
         return true;
      }

      if (!MythicBukkit.inst().getMobManager().isActiveMob(uuid)) {
         sender.sendMessage(ChatColor.RED + "Failed to send Signal: UUID does not correspond to a Mythic Mob.");
         return true;
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getActiveMob(uuid).get();
         AbstractEntity trigger = null;
         if (sender instanceof Player) {
            if (!sender.hasPermission("mythicmobs.signal")) {
               sender.sendMessage(ChatColor.RED + "Failed to send Signal: You do not have permission to use that command!");
               return true;
            }

            trigger = BukkitAdapter.adapt((Player)sender);
         }

         am.signalMob(trigger, signal);
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.signal";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "signal";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"si"};
   }
}
