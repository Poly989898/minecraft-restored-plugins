package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.commands.SignalCommand;
import io.lumine.mythic.bukkit.commands.eggs.EggsCommand;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MobsCommand extends Command<MythicBukkit> {
   public MobsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new InfoCommand(this),
            new KillAllCommand(this),
            new KillCommand(this),
            new ListActiveCommand(this),
            new ListCommand(this),
            new SpawnCommand(this),
            new StatsCommand(this),
            new EggsCommand(this),
            new SignalCommand(this),
            new BrowseCommand(this),
            new EditCommand(this)
         }
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Component[] messages = new Component[]{
         Text.parse("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
         Text.parse(""),
         Text.parse("&e/mm m &fbrowse &3<filter> &7\u25ba &7&oOpen mob browser (premium-only)"),
         Text.parse("&e/mm m &flist &3<filter> &7\u25ba &7&oList all loaded Mythic Mobs"),
         Text.parse("&e/mm m &finfo &6[type] &7\u25ba &7&oShow info about a mob type"),
         Text.parse("&e/mm m &fkill &6[type] &7\u25ba &7&oKill all mobs of type [type]"),
         Text.parse("&e/mm m &fkillall &3<radius> &7\u25ba &7&oRemove all active mobs"),
         Text.parse("&e/mm m &fspawn &3<-t> &6[type] &3<amount> &3<w,x,y,z> &7\u25ba &7&oSpawn mobs"),
         Text.parse("&e/mm m &fstats &7\u25ba &7&oShows stats about active mobs")
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
      return "mythicmobs.command.mobs";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "mobs";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"mob", "m"};
   }
}
