package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.menus.items.ItemMenuContext;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrowseCommand extends Command<MythicBukkit> {
   public BrowseCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires MythicMobs Premium");
         return true;
      } else {
         Player player = (Player)sender;
         this.getPlugin().getMenuManager().getItemBrowseMenu().open(player, new ItemMenuContext());
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.browse";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "browse";
   }
}
