package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.MobStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command<MythicBukkit> {
   public ListCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      List<MythicMob> mobs = new ArrayList<>(MythicBukkit.inst().getMobManager().getMobTypes());
      Collections.sort(mobs);
      if (args.length > 0) {
         sender.sendMessage(ChatColor.GOLD + "Mythic Mob Types found containing " + args[0] + ": ");
      } else {
         sender.sendMessage(ChatColor.GOLD + "Mobs Loaded (" + mobs.size() + "): ");
      }

      int i = 0;
      String s = "";

      for (MythicMob mm : mobs) {
         if (args.length > 0) {
            if (mm.getInternalName().contains(args[0])) {
               String[] parts = mm.getInternalName().split(args[0]);
               if (parts.length == 2) {
                  s = s + ChatColor.GREEN + parts[0] + ChatColor.DARK_GREEN + args[0] + ChatColor.GREEN + parts[1] + ChatColor.GRAY + ", ";
               }

               if (parts.length == 1) {
                  s = s + ChatColor.GREEN + parts[0] + ChatColor.DARK_GREEN + args[0] + ChatColor.GRAY + ", ";
               }

               if (parts.length == 0) {
                  s = s + ChatColor.GOLD + args[0] + ChatColor.GRAY + ", ";
               }

               i++;
            }
         } else {
            s = s + ChatColor.GREEN + mm.getInternalName() + ChatColor.GRAY + ", ";
         }

         if (i == 25) {
            i = 0;
            sender.sendMessage(s);
            s = "";
         }
      }

      List<MobStack> stacks = new ArrayList<>(MythicBukkit.inst().getMobManager().getMobStacks());
      Collections.sort(stacks);

      for (MobStack el : stacks) {
         if (args.length > 0) {
            if (el.getName().contains(args[0])) {
               String[] partsx = el.getName().split(args[0]);
               if (partsx.length == 2) {
                  s = s + ChatColor.GREEN + partsx[0] + ChatColor.DARK_GREEN + args[0] + ChatColor.GREEN + partsx[1] + ChatColor.GRAY + ", ";
               }

               if (partsx.length == 1) {
                  s = s + ChatColor.GREEN + partsx[0] + ChatColor.DARK_GREEN + args[0] + ChatColor.GRAY + ", ";
               }

               if (partsx.length == 0) {
                  s = s + ChatColor.GOLD + args[0] + ChatColor.GRAY + ", ";
               }

               i++;
            }
         } else {
            s = s + ChatColor.RED + el.getName() + ChatColor.GRAY + ", ";
         }

         if (i == 25) {
            i = 0;
            sender.sendMessage(s);
            s = "";
         }
      }

      sender.sendMessage(s);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.list";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "list";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"l"};
   }
}
