package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "projectilevelocity",
   aliases = {"pvelocity"},
   description = "Sets the velocity on the calling projectile"
)
public class ProjectileVelocityMechanic extends SkillMechanic implements INoTargetSkill {
   protected PlaceholderFloat velocityX;
   protected PlaceholderFloat velocityY;
   protected PlaceholderFloat velocityZ;
   protected ProjectileVelocityMechanic.VelocityMode mode;
   protected boolean relative;

   public ProjectileVelocityMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocityX = mlc.getPlaceholderFloat(new String[]{"velocityx", "vx", "x"}, 1.0F);
      this.velocityY = mlc.getPlaceholderFloat(new String[]{"velocityy", "vy", "y"}, 1.0F);
      this.velocityZ = mlc.getPlaceholderFloat(new String[]{"velocityz", "vz", "z"}, 1.0F);
      this.relative = mlc.getBoolean(new String[]{"relative", "r"}, true);
      String strMode = mlc.getString(new String[]{"mode", "m"}, "SET");

      try {
         this.mode = ProjectileVelocityMechanic.VelocityMode.valueOf(strMode);
      } catch (IllegalArgumentException e) {
         this.mode = ProjectileVelocityMechanic.VelocityMode.SET;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCallingEvent() instanceof ProjectileMechanic.ProjectileMechanicTracker projectileTracker) {
         this.applyVelocityChange(data, projectileTracker);
      } else if (data.getCallingEvent() instanceof MissileMechanic.MissileTracker missileTracker) {
         this.applyVelocityChange(data, missileTracker);
      }

      return SkillResult.SUCCESS;
   }

   public void applyVelocityChange(SkillMetadata data, Projectile.ProjectileTracker projectile) {
      AbstractVector currentVelocity = projectile.getCurrentVelocity().clone();
      AbstractVector velocityChange = new AbstractVector(this.velocityX.get(data), this.velocityY.get(data), this.velocityZ.get(data));
      if (this.relative) {
         AbstractVector forward = currentVelocity.clone().normalize();
         AbstractVector up = new AbstractVector(0, 1, 0);
         AbstractVector right = forward.crossProduct(up).normalize();
         up = right.crossProduct(forward).normalize();
         double localVx = currentVelocity.dot(right);
         double localVy = currentVelocity.dot(up);
         double localVz = currentVelocity.dot(forward);
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

         currentVelocity = right.multiply(localV.getX()).add(up.multiply(localV.getY())).add(forward.multiply(localV.getZ()));
         if (Double.isNaN(currentVelocity.getX())) {
            currentVelocity.setX(0);
         }

         if (Double.isNaN(currentVelocity.getY())) {
            currentVelocity.setY(0);
         }

         if (Double.isNaN(currentVelocity.getZ())) {
            currentVelocity.setZ(0);
         }

         projectile.setCurrentVelocity(currentVelocity);
      } else {
         switch (this.mode) {
            case SET:
               currentVelocity = velocityChange;
               break;
            case ADD:
               currentVelocity = currentVelocity.add(velocityChange);
               break;
            case MULTIPLY:
               currentVelocity = currentVelocity.multiply(velocityChange);
               break;
            case REMOVE:
               currentVelocity = currentVelocity.subtract(velocityChange);
               break;
            case DIVIDE:
               currentVelocity = currentVelocity.divide(velocityChange);
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

         projectile.setCurrentVelocity(currentVelocity);
      }
   }

   static enum VelocityMode {
      SET,
      ADD,
      MULTIPLY,
      REMOVE,
      DIVIDE;
   }
}
