package io.lumine.mythic.bukkit.commands.menus;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand extends Command<MythicBukkit> {
   public MenuCommand(MythicBukkit parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{new ItemBrowserMenuCommand(this), new MobBrowserMenuCommand(this), new SpawnerBrowserMenuCommand(this), new OpenCommand(this)}
      );
   }

   public MenuCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{new ItemBrowserMenuCommand(this), new MobBrowserMenuCommand(this), new SpawnerBrowserMenuCommand(this), new OpenCommand(this)}
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires MythicMobs Premium");
         return true;
      } else {
         Player player = (Player)sender;
         this.getPlugin().getMenuManager().getMainMenu().open(player, player);
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.menu";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "menu";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"menus"};
   }
}
