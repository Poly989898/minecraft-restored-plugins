package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setTrackedLocation",
   aliases = {"tracklocation", "stl"},
   description = "Sets the mob's tracked location to the targeted location"
)
public class SetTrackedLocationMechanic extends SkillMechanic implements ITargetedLocationSkill {
   public SetTrackedLocationMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.EITHER;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (data.getCaster() instanceof ActiveMob) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Setting tracked location to {0}", target.toString());
         ActiveMob am = (ActiveMob)data.getCaster();
         am.setTrackedLocation(target);
      }

      return SkillResult.SUCCESS;
   }
}
