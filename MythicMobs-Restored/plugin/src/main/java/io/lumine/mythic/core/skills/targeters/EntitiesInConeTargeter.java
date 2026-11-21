package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.function.Predicate;

@MythicTargeter(
   author = "Ashijin",
   name = "livingInCone",
   aliases = {"entitiesInCone", "livingEntitiesInCone", "LEIC", "EIC"},
   description = "Targets random points in a cone shape"
)
public class EntitiesInConeTargeter extends IEntitySelector {
   protected PlaceholderAngle angle;
   protected PlaceholderDouble range;
   protected PlaceholderDouble rotation;
   protected PlaceholderDouble yOffset;
   private boolean livingOnly = true;
   private boolean usePitch = false;

   public EntitiesInConeTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.angle = mlc.getPlaceholderAngle(new String[]{"angle", "a"}, AngleUnit.DEGREES, "90");
      this.range = mlc.getPlaceholderDouble(new String[]{"range", "r"}, 16.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "yo"}, 0.0);
      this.rotation = mlc.getPlaceholderDouble(new String[]{"rotation", "rot"}, 0.0);
      this.usePitch = mlc.getBoolean(new String[]{"usepitch", "pitch", "p"}, false);
      this.livingOnly = mlc.getBoolean(new String[]{"livingonly", "lo"}, true);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      return this.usePitch ? this.getPitchBased(data) : this.getFaceForward(data);
   }

   private Collection<AbstractEntity> getPitchBased(SkillMetadata data) {
      double angleHalf = this.angle.get(data).getDegrees() / 2.0;
      double rotationOffset = this.rotation.get(data);
      double maxRange = this.range.get(data);
      double maxRangeSq = maxRange * maxRange;
      double cosAngleThreshold = Math.cos(Math.toRadians(angleHalf));
      SkillCaster caster = data.getCaster();
      AbstractLocation casterLocation = caster.getLocation().clone();
      casterLocation.add(0.0, this.yOffset.get(data), 0.0);
      AbstractVector direction = this.getDirectionVector(casterLocation, rotationOffset);
      direction.normalize();
      Predicate<AbstractEntity> entityFilter = entity -> {
         if (entity.getUniqueId().equals(caster.getEntity().getUniqueId())) {
            return false;
         } else if (this.livingOnly && !entity.isLiving()) {
            return false;
         } else {
            double distanceSq = this.distanceSquared(casterLocation, entity);
            if (distanceSq > maxRangeSq) {
               return false;
            } else {
               AbstractVector toEntity = this.closestPoint(casterLocation, entity);
               toEntity.normalize();
               double dotProduct = direction.dot(toEntity);
               return dotProduct >= cosAngleThreshold;
            }
         }
      };
      return casterLocation.getWorld().getEntitiesNearLocation(casterLocation, maxRange, entityFilter);
   }

   private Collection<AbstractEntity> getFaceForward(SkillMetadata data) {
      double angle = this.angle.get(data).getDegrees() / 2.0;
      double rotation = this.rotation.get(data);
      double radius = this.range.get(data);
      double radiusSq = Math.pow(radius, 2.0);
      AbstractLocation casterLocation = data.getCaster().getLocation();
      AbstractVector dir = data.getCaster().getLocation().getDirection();
      dir.rotate((float)rotation);
      dir.setY(0);
      dir.normalize();
      double cos = Math.cos(angle * Math.PI / 180.0);
      double cosSq = cos * cos;
      Predicate<AbstractEntity> entityFilter = ae -> {
         if (ae.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())) {
            return false;
         } else if (!ae.isLiving()) {
            return false;
         } else {
            double dist = this.distanceSquared(casterLocation, ae);
            if (dist > radiusSq) {
               return false;
            } else {
               AbstractVector relative = ae.getLocation().subtract(data.getCaster().getLocation()).toVector();
               relative.setY(0);
               double dot = relative.getX() * dir.getX() + relative.getY() * dir.getY() + relative.getZ() * dir.getZ();
               double value = dot * dot / relative.lengthSquared();
               return angle < 180.0 && dot > 0.0 && value >= cosSq ? true : angle >= 180.0 && (dot > 0.0 || dot <= cosSq);
            }
         }
      };
      return casterLocation.getWorld().getEntitiesNearLocation(casterLocation, radius, entityFilter);
   }

   private AbstractVector getDirectionVector(AbstractLocation casterLocation, double rotationOffset) {
      float yaw = (float)Math.toRadians(casterLocation.getYaw() + rotationOffset);
      float pitch = this.usePitch ? (float)Math.toRadians(casterLocation.getPitch()) : 0.0F;
      double x = -Math.sin(yaw) * Math.cos(pitch);
      double y = -Math.sin(pitch);
      double z = Math.cos(yaw) * Math.cos(pitch);
      return new AbstractVector(x, y, z);
   }
}
