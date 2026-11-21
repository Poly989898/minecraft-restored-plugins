package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobBullet extends ProjectileBullet {
   @MythicField(
      name = "mobType",
      aliases = {"mob", "mm"},
      description = "The mythicmob to spawn as the bullet type",
      defValue = "SkeletalKnight"
   )
   protected MythicMob bulletMob = null;
   @MythicField(
      name = "bulletSpin",
      aliases = {"bspin"},
      description = "Makes the bullet spin",
      defValue = "0"
   )
   protected float bulletSpin;
   @MythicField(
      name = "bulletMatchDirection",
      aliases = {"bmd"},
      description = "Make the bullet face where the projectile is facing",
      defValue = "false"
   )
   protected boolean bulletMatchDirection;
   @MythicField(
      name = "bulletKillable",
      aliases = {"bk"},
      description = "Allow other entities to damage the projectile bullet",
      defValue = "false"
   )
   protected boolean bulletKillable;

   public MobBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.bulletSpin = config.getFloat(new String[]{"bulletspin", "bspin"}, 0.0F);
      this.bulletMatchDirection = config.getBoolean(new String[]{"bulletmatchdirection", "bmd"}, false);
      this.bulletKillable = config.getBoolean(new String[]{"bulletkillable", "bk"}, false);
      this.bulletYOffset = config.getDouble(new String[]{"bulletyoffset", "byoffset", "byo"}, 1.35);
      projectile.getManager().queueSecondPass(() -> {
         String mobType = config.getString(new String[]{"mob", "mobtype", "mm"}, "SkeletalKnight");
         this.bulletMob = this.getPlugin().getMobManager().getMythicMob(mobType).orElseGet(() -> null);
         if (this.bulletMob == null) {
            MythicLogger.errorMechanicConfig(this.getProjectileMechanic(), config, "Invalid bullet mob type specified");
         }
      });
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      if (this.bulletMob == null) {
         MythicLogger.errorMechanicConfig(this.getProjectileMechanic(), this.getConfig(), "Invalid bullet mob type specified");
         return null;
      } else {
         return new MobBullet.MobBulletTracker(projectile, target);
      }
   }

   @Override
   public boolean isVirtual() {
      return false;
   }

   public class MobBulletTracker extends ProjectileBullet.BulletTracker {
      protected ActiveMob activeMob;
      protected AbstractEntity bullet;

      private void markBulletHitTermination(ProjectileBulletableTracker projectile) {
         if (projectile instanceof Projectile.ProjectileTracker projectileTracker) {
            projectileTracker.setHasHitEntity(true);
            if (!MobBullet.this.getConfig().getBoolean(new String[]{"doEndSkillOnHit", "esoh"}, true)) {
               projectileTracker.setSuppressEndSkill(true);
            }
         }
      }

      public MobBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
         Events.subscribe(PlayerInteractEntityEvent.class)
            .filter(event -> this.bullet != null)
            .filter(event -> event.getRightClicked().getUniqueId().equals(this.bullet.getUniqueId()))
            .handler(event -> event.setCancelled(true))
            .bindWith(projectile);
         if (MobBullet.this.bulletKillable) {
            Events.subscribe(EntityDeathEvent.class)
               .filter(event -> this.bullet != null)
               .filter(event -> event.getEntity().getUniqueId().equals(this.bullet.getUniqueId()))
               .handler(event -> {
                  this.markBulletHitTermination(projectile);
                  projectile.terminate();
               })
               .bindWith(projectile);
            Events.subscribe(MythicMobDespawnEvent.class)
               .filter(event -> this.bullet != null)
               .filter(event -> event.getEntity().getUniqueId().equals(this.bullet.getUniqueId()))
               .handler(event -> {
                  this.markBulletHitTermination(projectile);
                  projectile.terminate();
               })
               .bindWith(projectile);
         }
      }

      @Override
      public void spawn(AbstractLocation start) {
         AbstractLocation l = this.getProjectile().getPreviousLocation().clone().subtract(0.0, MobBullet.this.bulletYOffset, 0.0);
         Runnable spawnRunnable = () -> {
            ActiveMob am = MobBullet.this.bulletMob.spawn(l, Double.NaN, SpawnReason.OTHER, entityx -> {
               entityx.setPersistent(false);
               entityx.setTicksLived(Integer.MAX_VALUE);
               AbstractVector velocity = this.getProjectile().getCurrentVelocity();
               if (velocity.isFinite()) {
                  entityx.setVelocity(BukkitAdapter.adapt(this.getProjectile().getCurrentVelocity()));
               }

               if (!MobBullet.this.bulletKillable) {
                  entityx.setInvulnerable(true);
               }

               if (entityx.getType().equals(EntityType.ARMOR_STAND)) {
                  MobBullet.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setArmorStandNoGravity(BukkitAdapter.adapt(entityx));
                  ((ArmorStand)entityx).setAI(true);
                  ((ArmorStand)entityx).setRemoveWhenFarAway(true);
               } else {
                  entityx.setGravity(false);
                  if (entityx instanceof LivingEntity livingEntity) {
                     livingEntity.setRemoveWhenFarAway(true);
                  }
               }
            }, null, this.getProjectile().getData().getExecutionAudience().orElse(null));
            if (am != null) {
               this.activeMob = am;
               Entity entity = am.getEntity().getBukkitEntity();
               am.setParent(this.getProjectile().getData().getCaster());
               am.setOwnerUUID(this.getProjectile().getData().getCaster().getEntity().getUniqueId());
               this.bullet = BukkitAdapter.adapt(entity);
               Projectile.BULLET_ENTITIES.add(this.bullet);
               MobBullet.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setHitBox(this.bullet, 0.0, 0.0);
               if (this.getProjectile().hasTerminated()) {
                  this.activeMob.despawn();
               }
            }
         };
         if (ServerVersion.isFolia()) {
            Schedulers.of(BukkitAdapter.adapt(l)).run(spawnRunnable);
         } else {
            Schedulers.ensureSync(spawnRunnable);
         }
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (!MobBullet.this.bulletKillable || this.bullet == null || !this.bullet.isDead() && this.bullet.isValid()) {
            double forwardOffset;
            if (MobBullet.this.getProjectileMechanic() instanceof Projectile projectileMechanic) {
               forwardOffset = projectileMechanic.getTickInterpolation() == 0
                  ? MobBullet.this.bulletForwardOffset
                  : MobBullet.this.bulletForwardOffset * projectileMechanic.getTickInterpolation();
            } else {
               forwardOffset = MobBullet.this.bulletForwardOffset;
            }

            AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity().clone().multiply(forwardOffset);
            AbstractLocation ol = this.getProjectile().getPreviousLocation().clone().subtract(0.0, MobBullet.this.bulletYOffset, 0.0).add(currentVelocity);
            if (MobBullet.this.bulletSpin != 0.0F) {
               float newSpin = this.bullet.getLocation().getYaw() + MobBullet.this.bulletSpin;
               ol.setYaw(newSpin);
            }

            Runnable tickRunnable = () -> {
               if (this.bullet != null && (!MobBullet.this.bulletKillable || !this.bullet.isDead())) {
                  this.bullet.teleport(ol);
               }
            };
            if (ServerVersion.isFolia() && this.bullet != null) {
               Schedulers.of(BukkitAdapter.adapt(this.bullet)).run(tickRunnable);
            } else {
               Schedulers.ensureSync(tickRunnable);
            }
         } else {
            this.markBulletHitTermination(this.getProjectile());
            this.getProjectile().terminate();
         }
      }

      @Override
      public void despawn() {
         ActiveMob activeMob = this.activeMob;
         AbstractEntity bullet = this.bullet;
         Runnable despawnRunnable = () -> {
            if (activeMob != null) {
               activeMob.despawn();
            } else if (bullet != null) {
               bullet.remove();
            }
         };
         if (ServerVersion.isFolia() && this.bullet != null) {
            Schedulers.of(BukkitAdapter.adapt(this.bullet)).run(despawnRunnable);
         } else {
            Schedulers.ensureSync(despawnRunnable);
         }

         if (this.bullet != null) {
            Projectile.BULLET_ENTITIES.remove(this.bullet);
         }

         this.activeMob = null;
         this.bullet = null;
      }
   }
}
