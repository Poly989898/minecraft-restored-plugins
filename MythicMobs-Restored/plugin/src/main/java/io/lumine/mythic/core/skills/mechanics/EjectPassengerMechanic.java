package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "ejectpassenger",
   aliases = {"eject_passenger"},
   description = "Kicks off any entities using the caster as a vehicle"
)
public class EjectPassengerMechanic extends SkillMechanic implements INoTargetSkill {
   public EjectPassengerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster().getEntity().getBukkitEntity().getPassenger() != null) {
         data.getCaster().getEntity().getBukkitEntity().eject();
      }

      return SkillResult.SUCCESS;
   }
}
