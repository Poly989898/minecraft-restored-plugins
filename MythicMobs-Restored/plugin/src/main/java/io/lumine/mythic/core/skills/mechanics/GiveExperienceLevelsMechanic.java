package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "giveexperiencelevels",
   aliases = {"giveexplevels"},
   description = "Gives experience levels from the target player"
)
public class GiveExperienceLevelsMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of levels to give"
   )
   protected PlaceholderInt amount;

   public GiveExperienceLevelsMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, "0");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Player player = (Player)target.getBukkitEntity();
         int amount = this.amount.get(data, target);
         Schedulers.runOnRegion(player, () -> player.giveExpLevels(amount));
         return SkillResult.SUCCESS;
      }
   }
}
