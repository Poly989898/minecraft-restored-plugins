package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setProjectileDirection",
   description = "Sets the projectile's current direction to the given target"
)
public class SetProjectileDirectionMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
   @MythicField(
      name = "magnitude",
      aliases = {"m"},
      description = "Interpolation factor toward the new direction (0-1)",
      defValue = "1.0"
   )
   private final PlaceholderDouble directionFactor;

   public SetProjectileDirectionMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.directionFactor = mlc.getPlaceholderDouble(new String[]{"magnitude", "m"}, 1.0);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (data.getCallingEvent() instanceof Projectile.ProjectileTracker projectileTracker) {
         AbstractLocation currentLocation = projectileTracker.getCurrentLocation();
         AbstractVector currentDirection = projectileTracker.getCurrentVelocity();
         AbstractVector newDirection = target.toVector().subtract(currentLocation.toVector()).normalize();
         double factor = this.directionFactor.get(data);
         AbstractVector interpolatedDirection = currentDirection.multiply(1.0 - factor).add(newDirection.multiply(factor)).normalize();
         projectileTracker.setDirection(interpolatedDirection);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_CONFIG;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }
}
