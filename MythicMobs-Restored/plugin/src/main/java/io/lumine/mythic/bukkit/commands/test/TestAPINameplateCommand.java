package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TestAPINameplateCommand extends Command<MythicBukkit> {
   public TestAPINameplateCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      LivingEntity l = MythicUtil.getTargetedEntity(p);
      if (l == null) {
         CommandHelper.sendError(sender, "You must target a valid entity!");
         return true;
      } else {
         AbstractEntity aEntity = BukkitAdapter.adapt(l);
         if (!this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
            CommandHelper.sendError(sender, "You must target a valid MythicMob!");
            return true;
         } else if (args.length < 1) {
            CommandHelper.sendError(sender, "Input Required");
            return true;
         } else {
            boolean optAsTarget = false;
            if (args != null && args.length > 1 && args[0].startsWith("-")) {
               if (args[0].contains("t")) {
                  optAsTarget = true;
               }

               args = Arrays.copyOfRange(args, 1, args.length);
            }

            String input = "";

            for (int i = 0; i < args.length; i++) {
               input = input.concat(args[i]) + " ";
            }

            PlaceholderString text = PlaceholderString.of(input);
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(l);
            activeMob.setDisplayName(text.get(activeMob));
            activeMob.setShowCustomNameplate(true);
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "setname";
   }
}
