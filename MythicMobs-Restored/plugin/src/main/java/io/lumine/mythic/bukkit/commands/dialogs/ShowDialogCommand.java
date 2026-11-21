package io.lumine.mythic.bukkit.commands.dialogs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ShowDialogCommand extends Command<MythicBukkit> {
   public ShowDialogCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "Usage: /mm dialogs show <dialog> [player] [key=value...]");
         return true;
      } else if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "Dialogs require Mythic Premium.");
         return true;
      } else {
         String dialogName = args[0];
         if (this.getPlugin().getDialogManager().getDialog(dialogName).isEmpty()) {
            CommandHelper.sendError(sender, "Dialog '" + dialogName + "' not found.");
            return true;
         } else {
            Player target = null;
            Map<String, String> values = new LinkedHashMap<>();

            for (int i = 1; i < args.length; i++) {
               int split = args[i].indexOf(61);
               if (split > 0) {
                  values.put(args[i].substring(0, split), args[i].substring(split + 1));
               } else if (target == null) {
                  target = Bukkit.getPlayer(args[i]);
                  if (target == null) {
                     CommandHelper.sendError(sender, "Player '" + args[i] + "' not found.");
                     return true;
                  }
               }
            }

            if (target == null) {
               if (!(sender instanceof Player player)) {
                  CommandHelper.sendError(sender, "Specify a player: /mm dialogs show <dialog> <player>");
                  return true;
               }

               target = player;
            }

            if (!this.getPlugin().getDialogManager().open(target, dialogName, values)) {
               CommandHelper.sendError(sender, "Failed to build showDialog mechanic.");
               return true;
            } else {
               CommandHelper.sendSuccess(sender, "Showing dialog '<gold>" + dialogName + "</gold>' to " + target.getName());
               return true;
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return (List<String>)StringUtil.copyPartialMatches(args[0], this.getPlugin().getDialogManager().getDialogNames(), new ArrayList());
      } else if (args.length != 2) {
         return null;
      } else {
         ArrayList<String> names = new ArrayList<>();

         for (Player p : Bukkit.getOnlinePlayers()) {
            names.add(p.getName());
         }

         return (List<String>)StringUtil.copyPartialMatches(args[1], names, new ArrayList());
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.dialogs.show";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "show";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"s", "open"};
   }
}
