package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "pushblock",
   description = "Pushes a block at the target location using piston logic"
)
public class PushBlockMechanic extends SkillMechanic implements ITargetedLocationSkill {
   protected AbstractDirection direction;
   protected Optional<SkillTargeter> relativeDirection = Optional.empty();
   protected Optional<Skill> onPushSkill = Optional.empty();
   protected Optional<Skill> onFailSkill = Optional.empty();

   public PushBlockMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String direction = mlc.getString(new String[]{"direction", "dir", "d"}, "NORTH").toUpperCase();
      if (direction.startsWith("@")) {
         SkillTargeter targeter = this.getManager().getTargeter(direction);
         if (targeter == null || !(targeter instanceof ILocationTargeter)) {
            MythicLogger.errorMechanic(this, "Invalid targeter supplied for direction");
            return;
         }

         this.relativeDirection = Optional.ofNullable(targeter);
      } else {
         try {
            this.direction = AbstractDirection.valueOf(direction);
         } catch (Throwable e) {
            MythicLogger.errorMechanic(this, "Invalid value provided for direction '" + direction + "'");
         }
      }

      String strPushSkill = mlc.getString(new String[]{"onpush", "then"}, null);
      String strFailSkill = mlc.getString(new String[]{"onfail"}, null);
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (strPushSkill != null) {
            this.onPushSkill = this.getPlugin().getSkillManager().getSkill(file, this, strPushSkill);
            if (this.onPushSkill.isEmpty()) {
               MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + strPushSkill);
            }
         }

         if (strFailSkill != null) {
            this.onFailSkill = this.getPlugin().getSkillManager().getSkill(file, this, strFailSkill);
            if (this.onFailSkill.isEmpty()) {
               MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + strFailSkill);
            }
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      AbstractDirection direction;
      if (this.relativeDirection.isPresent()) {
         SkillTargeter targeter = this.relativeDirection.get();
         AbstractLocation destination = this.getManager().getLocationTarget(targeter, data);
         if (target.equals(destination)) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "PushBlock already at relative destination");
            return SkillResult.INVALID_TARGET;
         }

         direction = AbstractDirection.getRelative(target, destination);
      } else {
         direction = this.direction;
      }

      if (this.getPlugin().getVolatileCodeHandler().getBlockHandler().pushBlock(target, direction)) {
         this.onPushSkill.ifPresent(skill -> {
            SkillMetadata newData = data.deepCloneNoTargets();
            AbstractLocation targetLocation = target.getRelative(direction).center();
            newData.setLocationTarget(targetLocation);
            Schedulers.sync().runLater(() -> {
               if (skill.isUsable(newData)) {
                  skill.execute(newData);
               }
            }, 4L);
         });
      } else {
         this.onFailSkill.ifPresent(skill -> {
            SkillMetadata newData = data.deepCloneNoTargets();
            newData.setLocationTarget(target.center());
            if (skill.isUsable(newData)) {
               skill.execute(newData);
            }
         });
      }

      return SkillResult.SUCCESS;
   }
}
