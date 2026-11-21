package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SpawnersCommand extends Command<MythicBukkit> {
   public SpawnersCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new ActivateCommand(this),
            new AddConditionCommand(this),
            new AddConditionSpawnCommand(this),
            new CopyCommand(this),
            new CreateCommand(this),
            new CutCommand(this),
            new InfoCommand(this),
            new FindCommand(this),
            new MoveCommand(this),
            new PasteCommand(this),
            new RemoveCommand(this),
            new RemoveConditionCommand(this),
            new RemoveConditionSpawnCommand(this),
            new ResetTimersCommand(this),
            new SetCommand(this),
            new UndoCommand(this)
         }
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      int page = 1;
      if (args.length > 0 && args[0].equals("2")) {
         page = 2;
      }

      if (page == 1) {
         String[] messages = new String[]{
            ColorString.get("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
            "",
            ColorString.get("&e/mm s &fcreate &6[name] &6[mob] &3<w,x,y,z> &7\u25ba &7&oCreate a mob spawner."),
            ColorString.get("&e/mm s &fdelete &6[name] &7\u25ba &7&oRemove a mob spawner."),
            ColorString.get("&e/mm s &finfo &6[name] &7\u25ba &7&oShow info about a spawner."),
            ColorString.get("&e/mm s &ffind &3<radius> &7\u25ba &7&oList nearby spawners"),
            ColorString.get("&e/mm s &factivate &6[name] &7\u25ba &7&o Force-ticks a spawner."),
            ColorString.get("&e/mm s &fset &6[name] &6[attribute] &6[value] &7\u25ba &7&oModify spawner attributes."),
            ColorString.get("&e/mm s &faddcondition &6[name] &6[condition] &6[value] &7\u25ba &7&oAdds a condition to a spawner."),
            ColorString.get("&e/mm s &fremovecondition &6[name] &6[condition] &7\u25ba &7&oRemoves a condition from a spawner."),
            "",
            ColorString.get("&6&L ** Page 1 of 2")
         };
         CommandHelper.sendCommandMessage(sender, messages);
      } else if (page == 2) {
         String[] messages = new String[]{
            ColorString.get("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
            "",
            ColorString.get("&e/mm s &fmove &6[name] &7\u25ba &7&oMoves the spawner to the location you are looking at."),
            ColorString.get("&e/mm s &fcopy &6[name] &6[copyName] &7\u25ba &7&oTurns the block you're looking at into a copy of a mob spawner."),
            ColorString.get("&e/mm s &fcut &6[name] &7\u25ba &7&oCuts the given spawners and saves them to the clipboard."),
            ColorString.get("&e/mm s &fpaste &6[name] &7\u25ba &7&oPastes spawners in your clipboard based on your relative location."),
            ColorString.get(
               "&e/mm s &fundo &7\u25ba &7&oUndo the last clipboard-related change you've done (clipboard must still have spawners in it for this to work)"
            ),
            "",
            ColorString.get("&6&L ** Page 2 of 2")
         };
         CommandHelper.sendCommandMessage(sender, messages);
      } else {
         String[] messages = new String[]{ChatColor.RED + "Page not found."};
         CommandHelper.sendCommandMessage(sender, messages);
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "spawners";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"s", "sp"};
   }
}
