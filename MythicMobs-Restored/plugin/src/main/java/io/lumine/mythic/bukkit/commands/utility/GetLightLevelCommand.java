package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetLightLevelCommand extends Command<MythicBukkit> {
   public GetLightLevelCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      p.sendMessage(Text.colorizeLegacy("<gold>Light Level at Location:"));
      p.sendMessage(Text.colorizeLegacy("<gray>- <yellow>Light Level<white>: " + p.getLocation().getBlock().getLightLevel()));
      p.sendMessage(Text.colorizeLegacy("<gray>- <yellow>from Sky<white>: " + p.getLocation().getBlock().getLightFromSky()));
      p.sendMessage(Text.colorizeLegacy("<gray>- <yellow>from Blocks<white>:" + p.getLocation().getBlock().getLightFromBlocks()));
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getlightlevel";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getlightlevel";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"gll"};
   }
}
