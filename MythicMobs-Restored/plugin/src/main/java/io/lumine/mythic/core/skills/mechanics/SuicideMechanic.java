package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "suicide",
   description = "Kills the caster"
)
public class SuicideMechanic extends SkillMechanic implements INoTargetSkill {
   protected String search;

   public SuicideMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!data.getCaster().getEntity().isLiving()) {
         data.getCaster().getEntity().remove();
      } else {
         if (data.getCaster() instanceof ActiveMob activeMob && activeMob.getType().getPassthroughDamage()) {
            activeMob.setParent(null);
         }

         data.getCaster().getEntity().setHealth(1.0);
         data.getCaster().getEntity().damage(999.0F, data.getCaster().getEntity());
      }

      return SkillResult.SUCCESS;
   }
}
