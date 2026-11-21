package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends Command<MythicBukkit> {
   public KillCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      int radius = 0;
      boolean exact = false;
      boolean literally = false;
      boolean vanilla = false;
      boolean faction = false;
      if (args != null && args.length > 1) {
         for (; args[0].startsWith("-"); args = Arrays.copyOfRange(args, 1, args.length)) {
            if (args[0].contains("p")) {
               literally = true;
            }

            if (args[0].contains("e")) {
               exact = true;
            }

            if (args[0].contains("v")) {
               vanilla = true;
            }

            if (args[0].contains("f")) {
               faction = true;
            }
         }
      }

      if (args.length == 0) {
         CommandHelper.sendError(sender, "Must supply mob type to kill (or use <yellow>killall<red>)");
         return true;
      } else {
         String type = args[0].trim();
         if (args.length > 1 && sender instanceof Player) {
            radius = Integer.parseInt(args[1]);
         }

         int count = 0;

         for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
            if (am != null
               && (!faction || am.getFaction() != null && am.getFaction().equalsIgnoreCase(type))
               && (exact || am.getType().getInternalName().contains(type))
               && (!exact || am.getType().getInternalName().equalsIgnoreCase(type))) {
               if (ServerVersion.isFolia()) {
                  Schedulers.of(BukkitAdapter.adapt(am.getEntity().getLocation())).run(() -> {
                     am.setDespawned();
                     this.getPlugin().getMobManager().unregisterActiveMob(am);
                     am.getEntity().remove();
                  });
               } else {
                  am.setDespawned();
                  this.getPlugin().getMobManager().unregisterActiveMob(am);
                  am.getEntity().remove();
               }

               count++;
            }
         }

         if (faction) {
            CommandHelper.sendSuccess(sender, "Removed <aqua>" + count + "<green> mobs matching faction <yellow>" + type);
         } else {
            String killedTypes = ChatColor.GREEN + "";

            for (MythicMob mm : this.getPlugin().getMobManager().getMobTypes()) {
               if ((exact || mm.getInternalName().contains(type)) && (!exact || mm.getInternalName().equalsIgnoreCase(type))) {
                  String[] parts = mm.getInternalName().split(type);
                  if (parts.length == 2) {
                     killedTypes = killedTypes + ChatColor.RED + parts[0] + ChatColor.DARK_RED + type + ChatColor.RED + parts[1] + ChatColor.GRAY + ", ";
                  }

                  if (parts.length == 1) {
                     killedTypes = killedTypes + ChatColor.RED + parts[0] + ChatColor.DARK_RED + type + ChatColor.GRAY + ", ";
                  }

                  if (parts.length == 0) {
                     killedTypes = killedTypes + ChatColor.DARK_RED + type + ChatColor.GRAY + ", ";
                  }
               }
            }

            CommandHelper.sendHeader(sender);
            CommandHelper.sendSuccess(sender, "Removed " + count + " mobs of types: ");
            CommandHelper.send(sender, killedTypes);
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         args = Arrays.copyOfRange(args, 1, args.length);
      }

      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(), 2
         )
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.kill";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "kill";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"k"};
   }
}
