package io.lumine.mythic.bukkit.commands.menus;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.menus.custom.CustomMenu;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommand extends Command<MythicBukkit> {
   public OpenCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (!MythicBukkit.isVolatile() && this.getPlugin().getCompatibility().getRpg().isEmpty()) {
         CommandHelper.sendError(sender, "Custom Menus require Mythic Premium or MythicRPG");
         return true;
      } else {
         Optional<CustomMenu> maybeMenu = this.getPlugin().getMenuManager().getMenu(args[0]);
         if (maybeMenu.isEmpty()) {
            CommandHelper.sendError(sender, "Custom menu not found.");
            return true;
         } else {
            CustomMenu menu = maybeMenu.get();
            Player player;
            if (args.length > 1) {
               player = Bukkit.getPlayer(args[1]);
            } else {
               player = (Player)sender;
            }

            if (player == null) {
               return true;
            } else {
               menu.openMenu(player);
               return true;
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getMenuManager().getAvailableMenuNames(), 2);
      } else {
         return args.length == 2
            ? CollectionUtils.filterMatches(args[1], Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toList(), 2)
            : Collections.emptyList();
      }
   }

   @Override
   public String getPermissionNode() {
      return "";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "custom";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"open", "o"};
   }
}
