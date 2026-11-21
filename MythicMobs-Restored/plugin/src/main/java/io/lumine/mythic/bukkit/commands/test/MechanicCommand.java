package io.lumine.mythic.bukkit.commands.test;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.adapters.VirtualEntity;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MechanicCommand extends Command<MythicBukkit> {
   public MechanicCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optSilent = false;
      boolean optDelayed = false;
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         if (args[0].contains("s")) {
            optSilent = true;
         }

         if (args[0].startsWith("d")) {
            optDelayed = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      String line = "[ - " + StringUtils.join(args, " ") + " ]";
      AbstractEntity entity;
      if (sender instanceof Player player) {
         entity = BukkitAdapter.adapt(player);
      } else {
         entity = new VirtualEntity();
      }

      SkillCaster caster = this.getPlugin().getSkillManager().getCaster(entity);
      Skill skill = this.getPlugin().getSkillManager().getSkill(line).orElse(null);
      SkillMetadataImpl meta = new SkillMetadataImpl(
         SkillTriggers.API, caster, entity, entity.getLocation(), Lists.newArrayList(new AbstractEntity[]{entity}), null, 1.0F
      );
      if (skill == null) {
         CommandHelper.sendError(sender, "Invalid line supplied");
         return true;
      } else {
         skill.execute(meta);
         if (!optSilent) {
            CommandHelper.sendSuccess(sender, "<gold>Running Mechanic<gray>: <white>" + line);
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.mechanic";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "mechanic";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"m"};
   }
}
