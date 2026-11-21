package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.IEntityLocationComparisonCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;

@MythicCondition(
   author = "Ashijin",
   name = "fieldOfView",
   aliases = {"infieldofview", "fov"},
   version = "4.3",
   description = "Tests if the target is within the given angle from where the caster is looking"
)
public class FieldOfViewCondition extends SkillCondition implements IEntityComparisonCondition, IEntityLocationComparisonCondition {
   @MythicField(
      name = "angle",
      aliases = {"a"},
      description = "The angle of the FOV to check in"
   )
   private double angle;
   @MythicField(
      name = "rotation",
      aliases = {"r"},
      description = "Rotates the FOV to check in"
   )
   private final double rotation;

   public FieldOfViewCondition(String line, MythicLineConfig mlc) {
      super(line);
      double a = mlc.getDouble(new String[]{"angle", "a"}, this.conditionVar == null ? 90.0 : Double.valueOf(this.conditionVar));
      this.angle = a / 2.0;
      this.rotation = mlc.getDouble(new String[]{"rotation", "r"}, 0.0);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      return !ServerVersion.isFolia()
            || entity.getBukkitEntity() == null
            || target.getBukkitEntity() == null
            || Bukkit.isPrimaryThread() && FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())
         ? this.checkLogic(entity, target.getEyeLocation().toVector())
         : Schedulers.of(entity.getBukkitEntity()).supply(() -> this.checkLogic(entity, target.getEyeLocation().toVector())).join();
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractLocation target) {
      return !ServerVersion.isFolia()
            || entity.getBukkitEntity() == null
            || Bukkit.isPrimaryThread() && FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())
         ? this.checkLogic(entity, target.toVector())
         : Schedulers.of(entity.getBukkitEntity()).supply(() -> this.checkLogic(entity, target.toVector())).join();
   }

   private boolean checkLogic(AbstractEntity entity, AbstractVector targetVector) {
      AbstractLocation eyeLocation = entity.getEyeLocation();
      AbstractVector entityVector = eyeLocation.toVector();
      AbstractVector headDirection = eyeLocation.getDirection();
      headDirection = this.rotateAroundY(headDirection, this.rotation);
      AbstractVector targetDirection = targetVector.subtract(entityVector).normalize();
      double targetAngle = Math.toDegrees(targetDirection.angle(headDirection));
      return targetAngle <= this.angle;
   }

   private AbstractVector rotateAroundY(AbstractVector v, double degrees) {
      double radians = Math.toRadians(degrees);
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);
      double x = v.getX() * cos + v.getZ() * sin;
      double z = v.getX() * -sin + v.getZ() * cos;
      return new AbstractVector(x, v.getY(), z);
   }
}
