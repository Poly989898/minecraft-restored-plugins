package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenColorPickerCommand extends Command<MythicBukkit> {
   public OpenColorPickerCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      Chroma in;
      if (args.length > 0) {
         in = Chroma.of(args[0]);
      } else {
         in = null;
      }

      this.getPlugin()
         .getMenuManager()
         .getColorPickerMenu()
         .openMenu(player, in)
         .thenAcceptSync(out -> CommandHelper.send(player, "Output Color " + out.serializeShortForm()));
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getoptimaldyes";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "opencolorpicker";
   }
}
