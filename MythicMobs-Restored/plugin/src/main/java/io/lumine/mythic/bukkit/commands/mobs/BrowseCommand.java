package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
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
         CommandHelper.sendError(sender, "This feature requires Mythic Premium");
         return true;
      } else {
         this.getPlugin().getMenuManager().getMobBrowseMenu().open((Player)sender);
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.EMPTY_LIST;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.browse";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "browse";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"b"};
   }
}
