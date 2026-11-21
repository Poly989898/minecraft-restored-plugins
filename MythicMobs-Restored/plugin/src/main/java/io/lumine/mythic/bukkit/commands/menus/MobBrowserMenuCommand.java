package io.lumine.mythic.bukkit.commands.menus;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.menus.mobs.MobMenuContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobBrowserMenuCommand extends Command<MythicBukkit> {
   public MobBrowserMenuCommand(Command<MythicBukkit> parent) {
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
            Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getMythicMob(args[0]);
            if (maybeMob.isEmpty()) {
               CommandHelper.sendError(sender, "Mob type '" + args[0] + "' not found.");
               return true;
            }

            this.getPlugin().getMenuManager().getMobEditorMenu().open(player, maybeMob.get());
         } else {
            this.getPlugin().getMenuManager().getMobBrowseMenu().open(player, new MobMenuContext());
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(), 2
         )
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.browse";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "mobs";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"m"};
   }
}
