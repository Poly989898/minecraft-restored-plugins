package io.lumine.mythic.bukkit.commands.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.commands.items.edit.ItemEditCommand;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ItemsCommand extends Command<MythicBukkit> {
   public ItemsCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new ItemEditCommand(this),
            new ExportCommand(this),
            new GetCommand(this),
            new GiveCommand(this),
            new InfoCommand(this),
            new ListCommand(this),
            new EnchantCommand(this),
            new BrowseCommand(this)
         }
      );
      if (ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3)) {
         this.addSubCommands(new Command[]{new ImportCommand(this)});
      }
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Component[] messages = new Component[]{
         Text.parse("  &7Arguments: &6[arg] &f= Required&7, &3<arg> &f= Optional"),
         Text.parse(""),
         Text.parse("&e/mm i &fbrowse &7\u25ba &7&oOpen Item Browser"),
         Text.parse("&e/mm i &flist &3<filter> &7\u25ba &7&oList all loaded items"),
         Text.parse("&e/mm i &finfo &6[name] &7\u25ba &7&oShow info about an item"),
         Text.parse("&e/mm i &fget &6[name] &7\u25ba &7&oGive yourself an item"),
         Text.parse("&e/mm i &fgive &3<-s> &6[player] &6[name] &3<amount> &7\u25ba &7&oGive a player an item"),
         Text.parse("&e/mm i &fenchant &6[name] &6<level> &7\u25ba &7&oEnchant your held item"),
         Text.parse("&e/mm i &fimport &6[name] &3<fileName> &7\u25ba &7&oImport your held item")
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
      return "mythicmobs.command.items";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "items";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"i"};
   }
}
