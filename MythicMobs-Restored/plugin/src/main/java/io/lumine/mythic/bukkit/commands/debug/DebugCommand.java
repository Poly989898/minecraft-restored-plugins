package io.lumine.mythic.bukkit.commands.debug;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DebugCommand extends Command<MythicBukkit> {
   public DebugCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(new Command[]{new ToggleClockCommand(this), new TimingsCommand(this)});
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length == 0) {
         String[] messages = new String[]{
            ColorString.get("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
            "",
            ColorString.get("&e/mm d &3[level] &7\u25ba &7&oSets debug reporting. Can be between 0 and 4 (0=off,4=too much info)."),
            ColorString.get("&e/mm d &fmode &6[true/false] &7\u25ba &7&oSets debug mode on or off. See plugin manual for details)."),
            ColorString.get("&e/mm d &ftimings &7\u25ba &7&oShow basic timing information (timings must be on first)."),
            ColorString.get("&e/mm d &ftimings &3[on/off] &7\u25ba &7&oTurns timings on/off."),
            ColorString.get("&e/mm d &ftimings skills &7\u25ba &7&oShows skill-specific timings in the console."),
            ColorString.get("&e/mm d &ftimings spawners &7\u25ba &7&oShows spawner-specific timings in the console.")
         };
         CommandHelper.sendCommandMessage(sender, messages);
         return true;
      } else {
         try {
            this.getPlugin().getConfiguration().setDebugLevel(Integer.parseInt(args[0]));
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Debug level has been set to " + args[0]);
         } catch (Exception e) {
            CommandHelper.sendError(sender, "Debug Level must be an integer (1-5).");
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.debug";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "debug";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"d"};
   }
}
