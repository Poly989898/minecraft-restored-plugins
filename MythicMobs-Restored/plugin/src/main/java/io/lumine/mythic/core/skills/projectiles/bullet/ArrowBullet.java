package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Trident;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArrowBullet extends ProjectileBullet {
   @MythicField(
      name = "arrowType",
      description = "    The type of arrow to use if the bullet type is ARROW\n    Arrow Types:\n    - NORMAL\n    - SPECTRAL\n    - TRIDENT\n",
      defValue = "NORMAL"
   )
   protected ArrowBullet.ArrowType arrowType;

   public ArrowBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      String strArrowType = config.getString(new String[]{"bulletarrowtype", "arrowtype"}, "NORMAL");

      try {
         this.arrowType = ArrowBullet.ArrowType.valueOf(strArrowType);
      } catch (Throwable e) {
         MythicLogger.errorMechanicConfig(projectile, config, "Invalid bullet arrow type specified");
      }
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new ArrowBullet.ArrowBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return false;
   }

   public class ArrowBulletTracker extends ProjectileBullet.BulletTracker {
      protected AbstractEntity bullet;

      public ArrowBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
         Events.subscribe(EntityDamageByEntityEvent.class)
            .filter(event -> this.bullet != null)
            .filter(event -> event.getDamager() != null)
            .filter(event -> event.getDamager().getUniqueId().equals(this.bullet.getUniqueId()))
            .handler(event -> event.setCancelled(true))
            .bindWith(projectile);
      }

      @Override
      public void spawn(AbstractLocation start) {
         Location loc = BukkitAdapter.adapt(start);
         AbstractVector velocity = this.getProjectile().getCurrentVelocity();
         if (velocity.lengthSquared() != 0.0) {
            AbstractVector delta = velocity.clone().normalize().multiply(this.getProjectile().getVelocityMagnitude() / 7.6);
            double yawRad = Math.atan2(delta.getX(), delta.getZ());
            float yawDeg = (float)Math.toDegrees(yawRad);
            double horizontal = Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ());
            double pitchRad = Math.atan2(delta.getY(), horizontal);
            float pitchDeg = (float)Math.toDegrees(pitchRad);
            loc.setYaw(yawDeg);
            loc.setPitch(pitchDeg);

            AbstractArrow arrow = (AbstractArrow)(switch (ArrowBullet.this.arrowType) {
               case NORMAL -> (Arrow)loc.getWorld().spawn(loc, Arrow.class, projectile -> {
                  projectile.setVelocity(BukkitAdapter.adapt(velocity));
                  projectile.setPickupStatus(PickupStatus.DISALLOWED);
                  projectile.setPierceLevel(125);
                  projectile.setTicksLived(Integer.MAX_VALUE);
                  projectile.setInvulnerable(true);
                  projectile.setGravity(false);
                  projectile.setRotation(yawDeg, pitchDeg);
                  projectile.setVelocity(loc.getDirection().multiply(1));
               });
               case SPECTRAL -> (SpectralArrow)loc.getWorld().spawn(loc, SpectralArrow.class);
               case TRIDENT -> (Trident)loc.getWorld().spawn(loc, Trident.class);
            });
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
            arrow.setPierceLevel(125);
            arrow.setTicksLived(Integer.MAX_VALUE);
            arrow.setInvulnerable(true);
            arrow.setGravity(false);
            arrow.setDamage(0.0);
            this.bullet = BukkitAdapter.adapt(arrow);
            this.bullet.setVelocity(velocity);
            Projectile.BULLET_ENTITIES.add(this.bullet);
            ArrowBullet.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(this.bullet, pitchDeg, yawDeg);
            ArrowBullet.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setHitBox(this.bullet, 0.0, 0.0);
            if (this.getProjectile().hasTerminated()) {
               this.bullet.remove();
            }
         }
      }

      @Override
      public void tick(AbstractLocation origin) {
         AbstractVector o = this.bullet.getLocation().toVector();
         AbstractVector n = this.getProjectile().getCurrentLocation().toVector();
         AbstractVector velocity = n.subtract(o).normalize().multiply(this.getProjectile().getVelocityMagnitude());
         if (velocity.isFinite()) {
            this.bullet.setVelocity(velocity);
         } else {
            this.bullet.setVelocity(this.getProjectile().getCurrentVelocity());
         }
      }

      @Override
      public void despawn() {
         this.bullet.remove();
         Projectile.BULLET_ENTITIES.remove(this.bullet);
      }
   }

   protected static enum ArrowType {
      NORMAL,
      SPECTRAL,
      TRIDENT;
   }
}
