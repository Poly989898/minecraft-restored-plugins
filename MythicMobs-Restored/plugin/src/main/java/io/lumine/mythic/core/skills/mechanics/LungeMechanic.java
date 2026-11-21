package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "lunge",
   description = "Causes the caster to lunge forward at the target"
)
public class LungeMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "velocity",
      aliases = {"v", "magnitude"},
      description = "The velocity of the lunge",
      defValue = "1"
   )
   protected PlaceholderDouble velocity;
   @MythicField(
      name = "velocityY",
      aliases = {"yvelocity", "vy", "yv"},
      description = "The y-velocity of the lunge",
      defValue = "11"
   )
   protected PlaceholderDouble velocityY;
   @MythicField(
      name = "oldMath",
      aliases = {"old", "o"},
      description = "Whether it should use the old velocity math",
      defValue = "false"
   )
   protected boolean old;

   public LungeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v", "magnitude"}, 1.0);
      this.velocityY = mlc.getPlaceholderDouble(new String[]{"velocityy", "yvelocity", "vy", "yv"}, 0.0);
      this.old = mlc.getBoolean(new String[]{"oldMath", "old", "o"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      AbstractVector moveVector = this.old ? this.doOldVelocity(context) : this.doNewVelocity(context);
      if (data.getCause() == SkillTrigger.get("JUMP")) {
         Schedulers.sync().runLater(() -> data.getCaster().getEntity().setVelocity(moveVector), 1L);
      } else {
         data.getCaster().getEntity().setVelocity(moveVector);
      }

      return SkillResult.SUCCESS;
   }

   AbstractVector doNewVelocity(PlaceholderContext context) {
      double velocity = Math.abs(this.velocity.get(context));
      double velocityY = this.velocityY.get(context);
      AbstractLocation casterLocation = context.meta().getCaster().getEntity().getLocation();
      AbstractLocation location = context.location();
      if (casterLocation.distance2DSquared(location) == 0.0) {
         location = location.add(casterLocation.getDirection().normalize().multiply(2));
      }

      AbstractVector moveVector = location.toVector().subtract(casterLocation.toVector()).normalize();
      moveVector.setY(0);
      moveVector = moveVector.multiply(velocity);
      if (velocityY != 0.0) {
         moveVector.setY(velocityY);
      }

      if (moveVector.length() > 4.0) {
         moveVector = moveVector.normalize().multiply(4);
      }

      return moveVector;
   }

   AbstractVector doOldVelocity(PlaceholderContext context) {
      double velocity = Math.abs(this.velocity.get(context));
      double velocityY = this.velocityY.get(context);
      AbstractLocation cL = context.meta().getCaster().getEntity().getLocation();
      AbstractLocation location = context.location();
      if (cL.distance2DSquared(location) == 0.0) {
         location = location.add(cL.getDirection().normalize().multiply(2));
      }

      AbstractVector v = location.toVector().subtract(cL.toVector()).normalize();
      if (velocityY != 0.0) {
         v.setY(velocityY);
      }

      v = v.multiply(velocity);
      if (v.length() > 4.0) {
         v = v.normalize().multiply(4);
      }

      return v;
   }
}
