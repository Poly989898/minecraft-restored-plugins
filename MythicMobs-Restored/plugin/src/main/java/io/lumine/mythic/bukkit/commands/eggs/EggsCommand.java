package io.lumine.mythic.bukkit.commands.eggs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class EggsCommand extends Command<MythicBukkit> {
   public EggsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(new Command[]{new GetCommand(this), new GiveCommand(this)});
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      String[] messages = new String[]{
         ColorString.get("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
         "",
         ColorString.get("/mm egg get &6[mob] &3<amount> &7\u25ba &7&oGives you mob eggs"),
         ColorString.get("/mm egg give &6[player] &6[mob] &3<amount> &7\u25ba &7&oGives player some mob eggs")
      };
      CommandHelper.sendCommandMessage(sender, messages);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.eggs";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "egg";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"eggs", "e"};
   }
}
