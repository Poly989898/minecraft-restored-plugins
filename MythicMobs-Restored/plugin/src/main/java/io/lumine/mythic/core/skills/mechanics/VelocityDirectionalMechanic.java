package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "directionalvelocity",
   aliases = {"dvelocity"},
   description = "Sets the velocity on the target entity"
)
public class VelocityDirectionalMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderFloat velocityYaw;
   protected PlaceholderFloat velocityPitch;
   protected PlaceholderFloat magnitude;
   protected VelocityDirectionalMechanic.VelocityMode mode;

   public VelocityDirectionalMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocityYaw = mlc.getPlaceholderFloat(new String[]{"yaw"}, null);
      this.velocityPitch = mlc.getPlaceholderFloat(new String[]{"pitch"}, null);
      this.magnitude = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, null);
      String strMode = mlc.getString(new String[]{"mode", "m"}, "SET");

      try {
         this.mode = VelocityDirectionalMechanic.VelocityMode.valueOf(strMode.toUpperCase());
      } catch (IllegalArgumentException e) {
         this.mode = VelocityDirectionalMechanic.VelocityMode.SET;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      double velocityX = 0.0;
      double velocityY = 0.0;
      double velocityZ = 0.0;
      if (this.velocityYaw != null) {
         float velocityYaw = this.velocityYaw.get(data, target);
         double yawRad = Math.toRadians(velocityYaw);
         velocityX = -Math.sin(yawRad);
         velocityZ = Math.cos(yawRad);
      }

      if (this.velocityPitch != null) {
         float velocityPitch = this.velocityPitch.get(data, target);
         double pitchRad = Math.toRadians(velocityPitch);
         velocityY = -Math.sin(pitchRad);
         double horizontalFactor = Math.cos(pitchRad);
         velocityX *= horizontalFactor;
         velocityZ *= horizontalFactor;
      }

      double velocityMagnitude = this.magnitude != null ? this.magnitude.get(data, target) : 1.0;
      AbstractVector velocityChange = new AbstractVector(velocityX, velocityY, velocityZ).normalize().multiply(velocityMagnitude);
      AbstractVector currentVelocity = target.getVelocity();
      switch (this.mode) {
         case SET:
            currentVelocity = velocityChange;
            break;
         case ADD:
            currentVelocity.add(velocityChange);
            break;
         case MULTIPLY:
            currentVelocity.multiply(velocityChange);
            break;
         case REMOVE:
            currentVelocity.subtract(velocityChange);
            break;
         case DIVIDE:
            currentVelocity.divide(velocityChange);
            break;
         case MINIMUM:
            currentVelocity.setX(velocityChange.getX());
            if (this.velocityPitch != null) {
               currentVelocity.setY(Math.max(currentVelocity.getY(), velocityChange.getY()));
            }

            currentVelocity.setZ(velocityChange.getZ());
            break;
         default:
            return SkillResult.INVALID_CONFIG;
      }

      if (Double.isNaN(currentVelocity.getX())) {
         currentVelocity.setX(0);
      }

      if (Double.isNaN(currentVelocity.getY())) {
         currentVelocity.setY(0);
      }

      if (Double.isNaN(currentVelocity.getZ())) {
         currentVelocity.setZ(0);
      }

      target.setVelocity(currentVelocity);
      return SkillResult.SUCCESS;
   }

   static enum VelocityMode {
      SET,
      ADD,
      MULTIPLY,
      REMOVE,
      DIVIDE,
      MINIMUM;
   }
}
