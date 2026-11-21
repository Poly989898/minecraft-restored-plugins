package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setStance",
   aliases = {"stance"},
   description = "Sets the target mob's stance"
)
public class SetStanceMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString stance;

   public SetStanceMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.stance = PlaceholderString.of(mlc.getString(new String[]{"stance", "s"}, "default"));
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.getPlugin().getMobManager().isActiveMob(target.getUniqueId())) {
         ActiveMob at = this.getPlugin().getMobManager().getActiveMob(target.getUniqueId()).get();
         String stance = this.stance.get(data, target);
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Set stance to {0}", stance);
         at.setStance(stance);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.SUCCESS;
      }
   }
}
