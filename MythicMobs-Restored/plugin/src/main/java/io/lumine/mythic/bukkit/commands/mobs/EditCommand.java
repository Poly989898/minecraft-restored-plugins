package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class EditCommand extends Command<MythicBukkit> {
   public EditCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires Mythic Premium");
         return true;
      } else {
         Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getMythicMob(args[0]);
         if (maybeMob.isEmpty()) {
            CommandHelper.sendError(sender, "Mob type '" + args[0] + "' not found.");
            return true;
         } else {
            this.getPlugin().getMenuManager().getMobEditorMenu().open((Player)sender, maybeMob.get());
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? (List)StringUtil.copyPartialMatches(
            args[0],
            this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(),
            new ArrayList()
         )
         : null;
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
      return "edit";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"e"};
   }
}
