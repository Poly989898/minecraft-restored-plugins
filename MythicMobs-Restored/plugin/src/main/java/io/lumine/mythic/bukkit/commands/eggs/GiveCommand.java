package io.lumine.mythic.bukkit.commands.eggs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public class GiveCommand extends Command<MythicBukkit> {
   public GiveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optSilent = false;
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         if (args[0].contains("s")) {
            optSilent = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      if (args.length < 2) {
         CommandHelper.sendError(sender, "Command Syntax: /mm egg give <name> <mob_name> [amount]");
         return true;
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(args[1]).orElseGet(() -> null);
         int amount = 1;
         if (args.length > 2) {
            try {
               amount = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
               CommandHelper.sendError(sender, "Amount provided is not a number");
               return true;
            }
         }

         if (target == null) {
            CommandHelper.sendError(sender, "That player is not online");
            return true;
         } else if (mm == null) {
            if (!optSilent) {
               sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "No Mythic Mob loaded with the name " + args[2] + ".");
            }

            return true;
         } else {
            BukkitItemStack egg = this.getPlugin().getMobManager().getEggManager().createMythicEgg(mm);
            ItemStack bukkitEgg = egg.amount(amount).getItemStack();
            target.getInventory().addItem(new ItemStack[]{bukkitEgg});
            if (!optSilent) {
               CommandHelper.sendSuccess(sender, "Gave <aqua>" + amount + "<green> Mythic Eggs to <aqua>" + target.getName() + "<green>!");
            }

            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         args = Arrays.copyOfRange(args, 1, args.length);
      }

      if (args.length != 1) {
         return args.length == 2
            ? CollectionUtils.filterMatches(
               args[0],
               this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(),
               2
            )
            : null;
      } else {
         List<String> players = new ArrayList<>();

         for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getName());
         }

         return (List<String>)StringUtil.copyPartialMatches(args[0], players, new ArrayList());
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.eggs.give";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "give";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"g", "gi"};
   }
}
