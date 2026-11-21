package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.AbstractArrow.PickupStatus;

@MythicMechanic(
   author = "Ashijin",
   name = "volley",
   aliases = {"shootvolley"},
   description = "Fires a volley of projectiles"
)
public class VolleyMechanic extends ShootMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "type",
      aliases = {"source", "s"},
      description = "The type of volley (regular, rain)"
   )
   protected VolleyMechanic.VolleyType type;
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number of projectiles to shoot"
   )
   protected PlaceholderInt amount;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the volley spread"
   )
   protected PlaceholderDouble radius;
   @MythicField(
      name = "yOffset",
      aliases = {"y"},
      description = "The vertical offset of the volley"
   )
   protected PlaceholderDouble yOffset;
   @MythicField(
      name = "spread",
      aliases = {"sp"},
      description = "How spread out the volley is",
      defValue = "0"
   )
   protected PlaceholderFloat spread;

   public VolleyMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strType = mlc.getString(new String[]{"source", "s"}, "REGULAR").toUpperCase();
      if (strType.equals("RAIN")) {
         this.type = VolleyMechanic.VolleyType.RAIN;
      } else {
         this.type = VolleyMechanic.VolleyType.REGULAR;
      }

      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 10);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 0.0);
      this.spread = mlc.getPlaceholderFloat(new String[]{"spread", "sp"}, 0.0F);
      this.yOffset = mlc.getPlaceholderDouble("yoffset", 1.0);
      this.yOffset = mlc.getPlaceholderDouble("y", this.yOffset);
      this.canPickup = mlc.getBoolean(new String[]{"pickup", "canPickup"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.shoot(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      return this.shoot(data, target);
   }

   @Override
   public SkillResult shoot(SkillMetadata data, AbstractLocation target) {
      SkillCaster caster = data.getCaster();
      AbstractLocation origin;
      if (this.fromOrigin) {
         origin = data.getOrigin().clone();
      } else {
         origin = caster.getLocation();
      }

      AbstractLocation primeSource = null;
      if (this.type.equals(VolleyMechanic.VolleyType.RAIN)) {
         primeSource = target.clone().add(0.0, this.yOffset.get(data), 0.0);
      }

      float startYOffset = this.startYOffset.get(data);
      if (startYOffset > 0.0F) {
         origin = origin.add(0.0, startYOffset, 0.0);
      }

      float startForwardOffset = this.startForwardOffset.get(data);
      float startSideOffset = this.startSideOffset.get(data);
      if (startForwardOffset != 0.0F || startSideOffset != 0.0F) {
         origin = MythicUtil.move(this.faulty, origin, -startForwardOffset, 0.0, startSideOffset);
      }

      float velocity = (float)this.projectileVelocity.get(data);
      if (this.powerAffectsVelocity) {
         velocity *= this.power;
      }

      AbstractVector direction;
      if (this.fromOrigin) {
         direction = target.toVector().rotate(0.001F).subtract(origin.toVector());
      } else {
         direction = target.toVector().rotate(0.001F).subtract(caster.getEntity().getLocation().toVector());
      }

      if (!direction.isFinite() || direction.lengthSquared() == 0.0) {
         direction = caster.getEntity().getLocation().getDirection();
      }

      if (direction.isFinite() && direction.lengthSquared() != 0.0) {
         direction.normalize();
      } else {
         direction = new AbstractVector(0, 0, 1);
      }

      int amount = this.amount.get(data);
      float spread = this.spread.get(data);
      double spreadHalf = spread / 2.0;
      double radius = this.radius.get(data);

      for (int i = 0; i < amount; i++) {
         AbstractVector vector = direction.clone();
         if (spread != 0.0F) {
            double yaw = Numbers.randomDouble() * spread - spreadHalf;
            double pitch = Numbers.randomDouble() * spread - spreadHalf;
            vector.rotate(yaw);
            vector.rotateAroundX(Math.toRadians(pitch));
         }

         if (this.projectileVelocityHorizOffset.get(data) != 0.0F || this.projectileVelocityHorizNoise > 0.0F) {
            float noise = 0.0F;
            if (this.projectileVelocityHorizNoise > 0.0F) {
               noise = (float)(this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * this.projectileVelocityHorizNoise);
            }

            vector.rotate(this.projectileVelocityHorizOffset.get(data) + noise);
         }

         if (this.projectileVelocityVertOffset.get(data) != 0.0F || this.projectileVelocityVertNoise > 0.0F) {
            float noise = 0.0F;
            if (this.projectileVelocityVertNoise > 0.0F) {
               noise = (float)(this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * this.projectileVelocityVertNoise);
            }

            vector.add(new AbstractVector(0.0F, this.projectileVelocityVertOffset.get(data) + noise, 0.0F)).normalize();
         }

         if (!vector.isFinite() || vector.lengthSquared() == 0.0) {
            vector = direction.clone();
         }

         if (vector.isFinite() && vector.lengthSquared() != 0.0) {
            vector.normalize();
         } else {
            vector = new AbstractVector(0, 0, 1);
         }

         vector = vector.multiply(velocity);
         AbstractEntity projectile;
         if (this.type.equals(VolleyMechanic.VolleyType.RAIN)) {
            AbstractLocation source = primeSource.clone();
            source.setX(source.getX() - radius + Numbers.randomDouble() * (radius * 2.0));
            source.setZ(source.getZ() - radius + Numbers.randomDouble() * (radius * 2.0));
            if (this.projectileType.equals(ShootMechanic.ProjectileType.ITEM)) {
               projectile = SkillAdapter.get().rainProjectile(caster, source, this.projectileClass, velocity);
            } else if (this.projectileType.equals(ShootMechanic.ProjectileType.BLOCK)) {
               projectile = SkillAdapter.get().rainProjectile(caster, source, this.projectileClass, velocity);
            } else {
               projectile = SkillAdapter.get().rainProjectile(caster, source, this.projectileClass, velocity);
            }
         } else if (this.projectileType.equals(ShootMechanic.ProjectileType.ITEM)) {
            projectile = SkillAdapter.get().shootProjectile(caster, target, origin, this.projectileClass, vector, this);
         } else if (this.projectileType.equals(ShootMechanic.ProjectileType.BLOCK)) {
            projectile = SkillAdapter.get().shootProjectile(caster, target, origin, this.projectileClass, vector, this);
         } else {
            projectile = SkillAdapter.get().shootProjectile(caster, target, origin, this.projectileClass, vector, this);
         }

         if (projectile.getBukkitEntity() instanceof Arrow arrow) {
            arrow.setPickupStatus(this.canPickup ? PickupStatus.ALLOWED : PickupStatus.DISALLOWED);
         }

         new ShootMechanic.ProjectileTracker(data, projectile, this.power);
      }

      return SkillResult.SUCCESS;
   }

   protected static enum VolleyType {
      REGULAR,
      RAIN;
   }
}
