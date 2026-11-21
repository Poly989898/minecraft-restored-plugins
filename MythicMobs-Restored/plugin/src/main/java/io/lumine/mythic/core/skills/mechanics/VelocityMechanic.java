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
   name = "velocity",
   description = "Sets the velocity on the target entity"
)
public class VelocityMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderFloat velocityX;
   protected PlaceholderFloat velocityY;
   protected PlaceholderFloat velocityZ;
   protected VelocityMechanic.VelocityMode mode;
   protected boolean relative;

   public VelocityMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocityX = mlc.getPlaceholderFloat(new String[]{"velocityx", "vx", "x"}, null);
      this.velocityY = mlc.getPlaceholderFloat(new String[]{"velocityy", "vy", "y"}, null);
      this.velocityZ = mlc.getPlaceholderFloat(new String[]{"velocityz", "vz", "z"}, null);
      this.relative = mlc.getBoolean(new String[]{"relative", "r"}, false);
      String strMode = mlc.getString(new String[]{"mode", "m"}, "SET");

      try {
         this.mode = VelocityMechanic.VelocityMode.valueOf(strMode.toUpperCase());
      } catch (IllegalArgumentException e) {
         this.mode = VelocityMechanic.VelocityMode.SET;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractVector v = target.getVelocity();
      double velocityX = this.velocityX != null ? this.velocityX.get(data, target) : v.getX();
      double velocityY = this.velocityY != null ? this.velocityY.get(data, target) : v.getY();
      double velocityZ = this.velocityZ != null ? this.velocityZ.get(data, target) : v.getZ();
      AbstractVector velocityChange = new AbstractVector(velocityX, velocityY, velocityZ);
      if (this.relative) {
         AbstractVector forward = target.getLocation().getDirection().clone().normalize();
         AbstractVector up = new AbstractVector(0, 1, 0);
         AbstractVector right = forward.crossProduct(up).normalize();
         up = right.crossProduct(forward).normalize();
         double localVx = v.dot(right);
         double localVy = v.dot(up);
         double localVz = v.dot(forward);
         AbstractVector localV = new AbstractVector(localVx, localVy, localVz);
         switch (this.mode) {
            case SET:
               localV = velocityChange;
               break;
            case ADD:
               localV = localV.add(velocityChange);
               break;
            case MULTIPLY:
               localV = localV.multiply(velocityChange);
               break;
            case REMOVE:
               localV = localV.subtract(velocityChange);
               break;
            case DIVIDE:
               localV = localV.divide(velocityChange);
         }

         v = right.multiply(localV.getX()).add(up.multiply(localV.getY())).add(forward.multiply(localV.getZ()));
         if (Double.isNaN(v.getX())) {
            v.setX(0);
         }

         if (Double.isNaN(v.getY())) {
            v.setY(0);
         }

         if (Double.isNaN(v.getZ())) {
            v.setZ(0);
         }

         target.setVelocity(v);
      } else {
         switch (this.mode) {
            case SET:
               v = velocityChange;
               break;
            case ADD:
               v.add(velocityChange);
               break;
            case MULTIPLY:
               v.multiply(velocityChange);
               break;
            case REMOVE:
               v.subtract(velocityChange);
               break;
            case DIVIDE:
               v.divide(velocityChange);
               break;
            default:
               return SkillResult.INVALID_CONFIG;
         }

         if (Double.isNaN(v.getX())) {
            v.setX(0);
         }

         if (Double.isNaN(v.getY())) {
            v.setY(0);
         }

         if (Double.isNaN(v.getZ())) {
            v.setZ(0);
         }

         target.setVelocity(v);
      }

      return SkillResult.SUCCESS;
   }

   static enum VelocityMode {
      SET,
      ADD,
      MULTIPLY,
      REMOVE,
      DIVIDE;
   }
}
