package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "matchrotation",
   aliases = {"matchrot"},
   description = "Sets the target's yaw and/or pitch to align with a location"
)
public class MatchRotationMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "location",
      aliases = {"target", "to", "loc", "l", "coordinates", "c"},
      version = "?",
      description = "The coordinates to teleport to in format x,y,z."
   )
   private Optional<SkillTargeter> targeter = Optional.empty();

   public MatchRotationMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String t = mlc.getString(new String[]{"target", "to", "t", "location", "loc", "l", "of"}, null);
      this.targeter = Optional.ofNullable(this.getPlugin().getSkillManager().getTargeter(t));
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else if (this.targeter.isEmpty()) {
         return SkillResult.INVALID_CONFIG;
      } else {
         SkillTargeter targeter = this.targeter.get();
         AbstractLocation location = this.getPlugin().getSkillManager().getLocationTarget(targeter, data);
         if (location == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MatchRotation failed: targeter returned nothing");
            return SkillResult.INVALID_TARGET;
         } else {
            float yaw = location.getYaw();
            float pitch = location.getPitch();
            this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(target, pitch, yaw);
            return SkillResult.SUCCESS;
         }
      }
   }
}
