package io.lumine.mythic.bukkit.commands.menus;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.menus.spawners.SpawnerMenuContext;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnerBrowserMenuCommand extends Command<MythicBukkit> {
   public SpawnerBrowserMenuCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires Mythic Premium");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length > 0) {
            MythicSpawner spawner = this.getPlugin().getSpawnerManager().getSpawnerByName(args[0]);
            if (spawner == null) {
               CommandHelper.sendError(sender, "Spawner '" + args[0] + "' not found.");
               return true;
            }

            this.getPlugin().getMenuManager().getSpawnerEditorMenu().open(player, spawner);
         } else {
            this.getPlugin().getMenuManager().getSpawnerBrowseMenu().open(player, new SpawnerMenuContext());
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(spawner -> spawner.getInternalName()).toList(), 2
         )
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.browse";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "spawners";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"s"};
   }
}
