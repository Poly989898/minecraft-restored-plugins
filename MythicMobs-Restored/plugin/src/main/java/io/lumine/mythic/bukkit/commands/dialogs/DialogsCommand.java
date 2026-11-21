package io.lumine.mythic.bukkit.commands.dialogs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class DialogsCommand extends Command<MythicBukkit> {
   public DialogsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(new Command[]{new ListDialogsCommand(this), new ShowDialogCommand(this)});
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.dialogs";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "dialogs";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"dialog", "dl"};
   }
}
