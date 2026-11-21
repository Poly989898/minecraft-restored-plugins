package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class CastCommand extends Command<MythicBukkit> {
   public CastCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optSilent = false;
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         if (args[0].startsWith("-s")) {
            optSilent = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      List<Entity> targets = new ArrayList<>();
      Entity casterEntity = null;
      Location origin = null;
      Player player2;
      if (sender instanceof Player player) {
         player2 = player;
         origin = player.getLocation();
         LivingEntity target = MythicUtil.getTargetedEntity(player);
         if (target != null) {
            targets.add(target);
         }
      } else {
         player2 = null;
         origin = null;
      }

      String spell = args[0];
      MythicBukkit.inst().getAPIHelper().castSkill(player2, spell, player2, origin, targets, null, 1.0F);
      if (!optSilent) {
         CommandHelper.sendSuccess(sender, "Trying to cast spell " + spell);
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return (List<String>)StringUtil.copyPartialMatches(args[0], this.visibleSkillNames(), new ArrayList());
      } else {
         return args.length == 2 && args[0].startsWith("-") && args[0].startsWith("-s")
            ? (List)StringUtil.copyPartialMatches(args[1], this.visibleSkillNames(), new ArrayList())
            : null;
      }
   }

   private List<String> visibleSkillNames() {
      return this.getPlugin().getSkillManager().getSkills().stream().filter(skill -> !skill.isHidden()).map(Skill::getInternalName).toList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.cast";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "cast";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"c"};
   }
}
