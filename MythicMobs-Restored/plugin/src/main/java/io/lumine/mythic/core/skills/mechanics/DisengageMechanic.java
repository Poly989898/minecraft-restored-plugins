package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "disengage",
   version = "4.9",
   description = "Causes the caster to leap backwards away from the target entity"
)
public class DisengageMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "velocity",
      aliases = {"v", "magnitude"},
      description = "The velocity to set",
      defValue = "1"
   )
   protected PlaceholderFloat velocity;
   @MythicField(
      name = "velocityY",
      aliases = {"yvelocity", "vy", "yv"},
      description = "The y-velocity to set",
      defValue = "0.01337"
   )
   protected PlaceholderFloat velocityY;

   public DisengageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v", "magnitude"}, 1.0F);
      this.velocityY = mlc.getPlaceholderFloat(new String[]{"velocityy", "yvelocity", "vy", "yv"}, 0.01337F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.doEffect(data, target, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      return this.doEffect(data, null, target);
   }

   private SkillResult doEffect(SkillMetadata data, AbstractEntity target, AbstractLocation location) {
      AbstractLocation cL = data.getCaster().getEntity().getLocation();
      if (cL.distance2DSquared(location) == 0.0) {
         location = location.add(cL.getDirection().normalize().multiply(2));
      }

      AbstractVector v = cL.toVector().subtract(location.toVector()).normalize();
      float velocity = target == null ? this.velocity.get(data) : this.velocity.get(data, target);
      float velocityY = target == null ? this.velocityY.get(data) : this.velocityY.get(data, target);
      if (velocityY != 0.01337) {
         v.setY(velocityY);
      }

      v = v.multiply(velocity);
      if (v.length() > 4.0) {
         v = v.normalize().multiply(4);
      }

      data.getCaster().getEntity().setVelocity(v);
      return SkillResult.SUCCESS;
   }
}
