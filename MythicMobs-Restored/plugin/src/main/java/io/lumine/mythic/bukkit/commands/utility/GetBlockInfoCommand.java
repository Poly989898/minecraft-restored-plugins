package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.HashSet;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetBlockInfoCommand extends Command<MythicBukkit> {
   public GetBlockInfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      Location location = p.getTargetBlock((HashSet)null, 64).getLocation();
      if (location == null) {
         CommandHelper.sendError(sender, "You must target a valid block!");
         return true;
      } else {
         CommandHelper.sendSuccess(sender, "BlockData: " + ChatColor.AQUA + location.getBlock().getBlockData().getAsString());
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getblockcoordinates";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getblockinfo";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"gbi"};
   }
}
