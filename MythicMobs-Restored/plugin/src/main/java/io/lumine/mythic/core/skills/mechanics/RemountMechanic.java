package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
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
   name = "remount",
   description = "Causes the caster to remount their mount"
)
public class RemountMechanic extends SkillMechanic implements INoTargetSkill {
   public RemountMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!(data.getCaster() instanceof ActiveMob am)) {
         return SkillResult.INVALID_TARGET;
      } else {
         if (data.getCaster().getEntity().getVehicle() == null && am.getMount().isPresent() && !am.getMount().get().isDead()) {
            AbstractEntity entity = am.getEntity();
            AbstractEntity mount = am.getMount().get().getEntity();
            mount.setPassenger(entity);
         }

         return SkillResult.SUCCESS;
      }
   }
}
