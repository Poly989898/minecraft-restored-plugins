package io.lumine.mythic.bukkit.commands.skills;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class SkillsCommand extends Command<MythicBukkit> {
   public SkillsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(new Command[]{new InfoCommand(this)});
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      String[] messages = new String[]{
         ColorString.get("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
         "",
         ColorString.get("&e/mm s &finfo &6[name] &7\u25ba &7&oShow info about a skill")
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
      return "mythicmobs.command.skills";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "skills";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"skill", "sk"};
   }
}
