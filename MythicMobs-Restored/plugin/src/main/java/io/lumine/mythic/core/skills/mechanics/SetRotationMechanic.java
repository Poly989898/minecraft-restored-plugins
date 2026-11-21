package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "jaylawl",
   name = "setrotation",
   aliases = {"setrot"},
   description = "Sets the target's yaw and/or pitch without teleporting it. Does not work on players!"
)
public class SetRotationMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "relative",
      aliases = {"rel", "r"},
      version = "4.9",
      description = "When set to true, adds to the target's yaw/pitch rather than setting it. Default=false"
   )
   private boolean relative;
   @MythicField(
      name = "yaw",
      aliases = {"y"},
      version = "4.9",
      description = "The yaw to be set/add - Either yaw or pitch is required"
   )
   protected PlaceholderDouble yaw;
   @MythicField(
      name = "pitch",
      aliases = {"p"},
      version = "4.9",
      description = "The pitch to be set/add - Either yaw or pitch is required"
   )
   protected PlaceholderDouble pitch;

   public SetRotationMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.relative = mlc.getBoolean(new String[]{"relative", "rel", "r"}, false);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw", "y"}, -1337.69);
      this.pitch = mlc.getPlaceholderDouble(new String[]{"pitch", "p"}, -1337.69);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         float yaw = (float)this.yaw.get(data, target);
         float pitch = (float)this.pitch.get(data, target);
         AbstractLocation loc = target.getLocation();
         if (yaw + 1337.69F <= 1.0E-5) {
            yaw = loc.getYaw();
         } else if (this.relative) {
            yaw += loc.getYaw();
         }

         if (pitch + 1337.69F <= 1.0E-5) {
            pitch = loc.getPitch();
         } else if (this.relative) {
            pitch += loc.getPitch();
         }

         this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(target, pitch, yaw);
         return SkillResult.SUCCESS;
      }
   }
}
