package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileSurfaceMode;
import io.lumine.mythic.core.utils.BlockUtil;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.physics.CollisionHelper;
import io.lumine.mythic.core.utils.physics.PhysicsCollision;
import java.io.File;
import java.util.Collection;
import lombok.Getter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

@MythicMechanic(
   author = "Ashijin",
   name = "missile",
   aliases = {"mi"},
   description = "Shoots a homing missile at the target"
)
public class MissileMechanic extends Projectile implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "inertia",
      aliases = {"intertia", "in"},
      description = "The inertia of the projectile",
      defValue = "1.5"
   )
   protected PlaceholderFloat projectileInertia;
   @Getter
   @MythicFields({@MythicField(
         name = "hugSurface",
         aliases = {"hs"},
         description = "Whether the projectile will hug the surface",
         defValue = "false"
      ), @MythicField(
         name = "hugLiquid",
         aliases = {"hugWater", "hugLava"},
         description = "If hugSurface is set, determines whether the projectile will hug liquid",
         defValue = "false"
      )})
   protected ProjectileSurfaceMode surfaceMode = ProjectileSurfaceMode.NONE;
   @MythicField(
      name = "heightFromSurface",
      aliases = {"hfs"},
      description = "    The offset depends on the type of the projectile and if hugSurface is set.\n    NORMAL - how high above the surface the projectile will glide.\n    METEOR - how high above the surface the projectile starts above the target\n",
      defValue = "0.5"
   )
   protected PlaceholderFloat heightFromSurface;
   @MythicField(
      name = "maxClimbHeight",
      aliases = {"mch"},
      description = "The number of attempts the projectile will try to increase its y-location before terminating the projectile",
      defValue = "3"
   )
   protected PlaceholderFloat maxClimbHeight;
   @MythicField(
      name = "maxDropHeight",
      aliases = {"mdh"},
      description = "The number of attempts the projectile will try to decrease its y-location before terminating the projectile",
      defValue = "10"
   )
   protected PlaceholderFloat maxDropHeight;
   @MythicField(
      name = "bounces",
      aliases = {"bounce"},
      description = "The projectile can bounce",
      defValue = "false",
      premium = true
   )
   protected boolean bounce;
   @MythicField(
      name = "bounceVelocity",
      aliases = {"bv"},
      description = "The velocity modifier of the bounce",
      defValue = "0.9"
   )
   protected PlaceholderFloat bounceVelocityMod;
   @MythicField(
      name = "highaccuracymode",
      aliases = {"ham"},
      description = "Whether to use high-accuracy mode, which raytraces every tick to ensure the projectile cannot ever go anything",
      defValue = "PLAYERS_ONLY"
   )
   protected Projectile.ProjectileTriOption highAccuracyMode;
   @MythicField(
      name = "startWithParentVelocity",
      aliases = {"swpv"},
      description = "If the missile is called by another projectile, should it start with the same velocity as the parent projectile?",
      defValue = "false"
   )
   protected boolean startWithParentVelocity;

   public MissileMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.hitNonPlayers = mlc.getBoolean(new String[]{"hitnonplayers", "hnp"}, true);
      this.projectileInertia = mlc.getPlaceholderFloat(new String[]{"intertia", "inertia", "in"}, 1.5F);
      this.bounce = mlc.getBoolean(new String[]{"bounces", "bounce"}, false);
      this.bounceVelocityMod = mlc.getPlaceholderFloat(new String[]{"bouncevelocity", "bv"}, 0.9F);
      boolean hugSurface = mlc.getBoolean(new String[]{"hugsurface", "hs"}, false);
      this.heightFromSurface = mlc.getPlaceholderFloat(new String[]{"heightfromsurface", "hfs"}, 0.5F);
      this.maxClimbHeight = mlc.getPlaceholderFloat(new String[]{"maxclimbheight", "mch"}, 3.0F);
      this.maxDropHeight = mlc.getPlaceholderFloat(new String[]{"maxdropheight", "mdh"}, 10.0F);
      this.startWithParentVelocity = mlc.getBoolean(new String[]{"startWithParentVelocity", "swpv", "spv"}, false);
      String highAccuracyDefault = "PLAYERS_ONLY";
      if (hugSurface) {
         highAccuracyDefault = "FALSE";
         boolean hugWater = mlc.getBoolean(new String[]{"hugliquid", "hugwater", "huglava"}, false);
         if (hugWater) {
            this.surfaceMode = ProjectileSurfaceMode.WATER;
         } else {
            this.surfaceMode = ProjectileSurfaceMode.SURFACE;
         }
      }

      String highAccuracyMode = mlc.getString(new String[]{"highaccuracymode", "ham"}, highAccuracyDefault);

      try {
         this.highAccuracyMode = Projectile.ProjectileTriOption.valueOf(highAccuracyMode.toUpperCase());
      } catch (Throwable e) {
         MythicLogger.errorMechanic(this, "Invalid input for highAccuracyMode option '" + highAccuracyMode + "'");
         this.highAccuracyMode = Projectile.ProjectileTriOption.PLAYERS_ONLY;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      try {
         new MissileMechanic.MissileTracker(data, target).start();
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Missile Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      try {
         new MissileMechanic.MissileTracker(data, target).start();
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Missile Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   public class MissileTracker extends Projectile.ProjectileTracker {
      protected float inertia;
      protected AbstractEntity target = null;
      protected AbstractLocation targetLocation = null;
      protected double targetYOffset;

      public MissileTracker(SkillMetadata data, AbstractEntity target) {
         super(data, target.getLocation());
         this.target = target;
         this.targetLocation = target.getLocation();
         this.inertia = MissileMechanic.this.projectileInertia.get(data, target);
         this.velocityMagnitude = MissileMechanic.this.projectileVelocity.get(data) / this.ticksPerSecond;
         if (MissileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (MissileMechanic.this.tickInterpolation + 1);
         }
      }

      public MissileTracker(SkillMetadata data, AbstractLocation target) {
         super(data, target);
         this.target = null;
         this.targetLocation = target;
         this.inertia = MissileMechanic.this.projectileInertia.get(data);
         this.velocityMagnitude = MissileMechanic.this.projectileVelocity.get(data) / this.ticksPerSecond;
         if (MissileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (MissileMechanic.this.tickInterpolation + 1);
         }
      }

      public AbstractLocation getTarget() {
         return this.target != null ? this.target.getLocation() : this.targetLocation.clone();
      }

      @Override
      public void projectileStart() {
         this.velocityMagnitude = MissileMechanic.this.projectileVelocity.get(this.data) / this.ticksPerSecond;
         if (MissileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (MissileMechanic.this.tickInterpolation + 1);
         }

         if (MissileMechanic.this.fromOrigin) {
            this.startLocation = this.data.getOrigin().clone();
         } else {
            this.startLocation = this.data.getCaster().getEntity().getLocation().clone();
         }

         double tyo = MissileMechanic.this.getTargetYOffset().get(this.data);
         if (this.target != null) {
            this.targetYOffset = tyo == 0.0 ? this.target.getEyeLocation().getY() - this.target.getLocation().getY() : tyo;
         } else {
            this.targetYOffset = tyo;
         }

         double syo = MissileMechanic.this.startYOffset.get(this.data);
         if (syo != 0.0) {
            this.startLocation.setY(this.startLocation.getY() + syo);
         }

         if (MissileMechanic.this.startForwardOffset.get(this.data) * -1.0F != 0.0F) {
            this.startLocation = MythicUtil.move(
               MissileMechanic.this.faulty, this.startLocation, MissileMechanic.this.startForwardOffset.get(this.data) * -1.0F, 0.0, 0.0
            );
         }

         double sso = MissileMechanic.this.startSideOffset.get(this.data);
         if (sso != 0.0) {
            this.startLocation = MythicUtil.move(MissileMechanic.this.faulty, this.startLocation, 0.0, 0.0, sso);
         }

         this.previousLocation = this.startLocation.clone();
         this.currentLocation = this.startLocation.clone();
         if (MissileMechanic.this.projectileStartDirection.isPresent()) {
            AbstractLocation startVectorTarget = MissileMechanic.this.getPlugin()
               .getSkillManager()
               .getLocationTarget(MissileMechanic.this.projectileStartDirection.get(), this.data);
            this.currentVelocity = startVectorTarget.toVector().rotate(0.001F).subtract(this.currentLocation.toVector()).normalize();
         } else if (MissileMechanic.this.startWithParentVelocity && this.data.getCallingEvent() instanceof Projectile.ProjectileTracker projectileTracker) {
            this.currentVelocity = projectileTracker.getCurrentVelocity().clone();
         } else {
            this.currentVelocity = this.getTarget().toVector().rotate(0.001F).subtract(this.currentLocation.toVector()).normalize();
         }

         this.currentLocation.setDirection(this.currentVelocity);
         if (MissileMechanic.this.projectileVelocityHorizOffset != null || MissileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
            float noise = 0.0F;
            if (MissileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
               noise = (float)(
                  MissileMechanic.this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * MissileMechanic.this.projectileVelocityHorizNoise
               );
            }

            if (MissileMechanic.this.projectileVelocityHorizOffset != null) {
               noise = (float)(noise + MissileMechanic.this.projectileVelocityHorizOffset.get(this.data).getDegrees());
            }

            this.currentVelocity.rotate(noise);
         }

         if (MissileMechanic.this.projectileVelocityVertOffset != null || MissileMechanic.this.projectileVelocityVertNoise > 0.0F) {
            float noisex = 0.0F;
            if (MissileMechanic.this.projectileVelocityVertNoise > 0.0F) {
               noisex = (float)(
                  MissileMechanic.this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * MissileMechanic.this.projectileVelocityVertNoise
               );
            }

            if (MissileMechanic.this.projectileVelocityVertOffset != null) {
               noisex = (float)(noisex + MissileMechanic.this.projectileVelocityVertOffset.get(this.data).getSlope());
            }

            this.currentVelocity.add(new AbstractVector(0.0F, noisex, 0.0F)).normalize();
         }

         if (MissileMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE) {
            this.currentLocation.setY((int)this.currentLocation.getY() + MissileMechanic.this.heightFromSurface.get(this.data));
            this.currentVelocity.setY(0).normalize();
         }

         if (MissileMechanic.this.powerAffectsVelocity) {
            this.velocityMagnitude = this.velocityMagnitude * this.power;
         }

         if (!MissileMechanic.this.startWithParentVelocity) {
            this.currentVelocity.multiply(this.velocityMagnitude);
         }

         if (MissileMechanic.this.bullet != null) {
            this.bullet = MissileMechanic.this.bullet.create(this, this.target);
         }
      }

      private boolean isHighAccuracy() {
         return MissileMechanic.this.highAccuracyMode == Projectile.ProjectileTriOption.TRUE
            || MissileMechanic.this.highAccuracyMode == Projectile.ProjectileTriOption.PLAYERS_ONLY && this.data.getCaster().getEntity().isPlayer();
      }

      @Override
      public void projectileMove() {
         this.previousLocation = this.currentLocation.clone();
         this.currentLocation.add(this.currentVelocity);
         this.clampToMaxRange();
         this.currentVelocity.multiply(this.inertia);
         this.currentVelocity
            .add(
               this.getTarget()
                  .add(0.0, this.targetYOffset, 0.0)
                  .subtract(this.currentLocation.getX(), this.currentLocation.getY(), this.currentLocation.getZ())
                  .toVector()
                  .normalize()
            );
         if (MissileMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE) {
            this.currentVelocity.setY(0);
         }

         this.currentVelocity.normalize().multiply(this.velocityMagnitude);
         if (this.isHighAccuracy()) {
            RayTraceResult traceResult = MissileMechanic.this.getPlugin()
               .getVolatileCodeHandler()
               .getWorldHandler()
               .rayTraceBlock(this.previousLocation, this.currentLocation, FluidCollisionMode.NEVER, true);
            if (traceResult != null && traceResult.getHitBlock() != null && !traceResult.getHitBlock().isEmpty()) {
               AbstractLocation hitLocation = BukkitAdapter.adapt(traceResult.getHitPosition()).toLocation(this.currentLocation.getWorld());
               Location from = BukkitAdapter.adapt(this.previousLocation);
               Location to = BukkitAdapter.adapt(hitLocation);
               if (this.previousLocation.distanceSquared(hitLocation) <= this.previousLocation.distanceSquared(this.currentLocation)) {
                  if (MissileMechanic.this.canStopAtLocation(this.data, hitLocation)) {
                     this.currentLocation = hitLocation;
                  } else if (!MissileMechanic.this.stopOnHitGround
                     && MissileMechanic.this.onHitBlockSkill.isPresent()
                     && MissileMechanic.this.onHitBlockSkill.get().isUsable(this.data)) {
                     SkillMetadata sData = this.data.deepClone();
                     AbstractLocation location;
                     if (MissileMechanic.this.bulletType.isPresent() && MissileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
                        location = this.previousLocation.clone();
                     } else {
                        location = this.currentLocation.clone();
                     }

                     MissileMechanic.this.onHitBlockSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
                  }
               }
            }
         }

         if (MissileMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE
            && (this.currentLocation.getBlockX() != this.previousLocation.getBlockX() || this.currentLocation.getBlockZ() != this.previousLocation.getBlockZ())
            )
          {
            float hfs = MissileMechanic.this.heightFromSurface.get(this.data);
            float mdh = MissileMechanic.this.maxDropHeight.get(this.data);
            float mch = MissileMechanic.this.maxClimbHeight.get(this.data);
            Block b = BukkitAdapter.adapt(this.currentLocation).subtract(0.0, hfs, 0.0).getBlock();
            if (BlockUtil.isPathable(b, MissileMechanic.this.surfaceMode)) {
               int attempts = 0;
               boolean ok = false;

               while (attempts++ < mdh) {
                  b = b.getRelative(BlockFace.DOWN);
                  if (!BlockUtil.isPathable(b, MissileMechanic.this.surfaceMode)) {
                     ok = true;
                     break;
                  }

                  this.currentLocation.subtract(0.0, 1.0, 0.0);
               }

               if (!ok) {
                  this.terminate();
                  return;
               }
            } else {
               int attempts = 0;
               boolean ok = false;

               while (attempts++ < mch) {
                  b = b.getRelative(BlockFace.UP);
                  this.currentLocation.add(0.0, 1.0, 0.0);
                  if (BlockUtil.isPathable(b)) {
                     ok = true;
                     break;
                  }
               }

               if (!ok) {
                  this.terminate();
                  return;
               }
            }

            this.currentLocation.setY((int)this.currentLocation.getY() + hfs);
         }

         if (MissileMechanic.this.bounce && MythicBukkit.isVolatile()) {
            try {
               if (this.handleBounce()) {
                  this.executeProjectileSkill(MissileMechanic.this.onBounceSkill, this.data, false);
               }
            } catch (IllegalArgumentException e) {
               MythicLogger.errorMechanicConfig(
                  MissileMechanic.this,
                  MissileMechanic.this.config,
                  "An error occurred while calculating projectile bounce (did you set the bounding box to zero?)"
               );
               e.printStackTrace();
            }
         } else if (MissileMechanic.this.stopOnHitGround && !BlockUtil.isPathable(BukkitAdapter.adapt(this.currentLocation).getBlock(), this)) {
            if (!MissileMechanic.this.canStopAtLocation(this.data, this.currentLocation)) {
               return;
            }

            if (MissileMechanic.this.onHitBlockSkill.isPresent() && MissileMechanic.this.onHitBlockSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               AbstractLocation location;
               if (MissileMechanic.this.bulletType.isPresent() && MissileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
                  location = this.previousLocation.clone();
               } else {
                  location = this.currentLocation.clone();
               }

               MissileMechanic.this.onHitBlockSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
            }

            this.terminate();
            return;
         }

         if (this.target != null && (this.target.isDead() || !this.target.isValid())) {
            this.terminate();
         }
      }

      @Override
      public void projectileTick() {
         if (MissileMechanic.this.onTickSkill.isPresent() && MissileMechanic.this.onTickSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            AbstractLocation location;
            if (MissileMechanic.this.bulletType.isPresent() && MissileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
               location = this.previousLocation.clone();
            } else {
               location = this.currentLocation.clone();
            }

            MissileMechanic.this.onTickSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
         }

         this.evaluateTargetsInBB();
         if (this.targets.size() > 0) {
            this.doHit(this.targets.clone());
         }

         this.targets.clear();
      }

      public void setVelocity(double value) {
         this.velocityMagnitude = value / this.ticksPerSecond;
         if (MissileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (MissileMechanic.this.tickInterpolation + 1);
         }
      }

      public void multiplyVelocity(double v) {
         this.velocityMagnitude *= v;
      }

      public void addVelocity(double v) {
         if (MissileMechanic.this.tickInterpolation > 0) {
            v /= MissileMechanic.this.tickInterpolation + 1;
         }

         this.velocityMagnitude = (this.velocityMagnitude * this.ticksPerSecond + v) / this.ticksPerSecond;
      }

      public void setInertia(float p) {
         this.inertia = p;
      }

      public void multiplyInertia(float p) {
         this.inertia *= p;
      }

      public void addInertia(float p) {
         this.inertia += p;
      }

      public void doHit(Collection<AbstractEntity> targets) {
         this.hasHitEntity = true;
         this.applyHitboxVariable(this.data, targets);
         if (MissileMechanic.this.onHitSkill.isPresent()) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            sData.setEntityTargets(targets);
            sData.setOrigin(this.currentLocation.clone());
            this.executeOnHit(targets, () -> {
               if (MissileMechanic.this.onHitSkill.get().isUsable(sData)) {
                  MissileMechanic.this.onHitSkill.get().execute(sData);
               }
            });
         }

         for (AbstractEntity target : targets) {
            if (MissileMechanic.this.canStopAtEntity(this.data, target)) {
               this.terminate();
               break;
            }
         }
      }

      public boolean handleBounce() {
         BoundingBox bb = BoundingBox.of(
            BukkitAdapter.adapt(this.currentLocation),
            MissileMechanic.this.hitRadius.get(this.data),
            MissileMechanic.this.verticalHitRadius.get(this.data),
            MissileMechanic.this.hitRadius.get(this.data)
         );
         BlockFace collided = null;
         double bV = 0.0;

         for (PhysicsCollision collision : CollisionHelper.getCollisions(this.currentVelocity, bb, this.previousLocation)) {
            switch (collision.getBlockFace()) {
               case NORTH:
                  double magnitudexxxxx = Math.abs(this.currentVelocity.getZ());
                  if (magnitudexxxxx > bV) {
                     bV = magnitudexxxxx;
                     collided = BlockFace.NORTH;
                  }
                  break;
               case EAST:
                  double magnitudexxxx = Math.abs(this.currentVelocity.getX());
                  if (magnitudexxxx > bV) {
                     bV = magnitudexxxx;
                     collided = BlockFace.EAST;
                  }
                  break;
               case SOUTH:
                  double magnitudexxx = Math.abs(this.currentVelocity.getZ());
                  if (magnitudexxx > bV) {
                     bV = magnitudexxx;
                     collided = BlockFace.SOUTH;
                  }
                  break;
               case WEST:
                  double magnitudexx = Math.abs(this.currentVelocity.getX());
                  if (magnitudexx > bV) {
                     bV = magnitudexx;
                     collided = BlockFace.WEST;
                  }
                  break;
               case UP:
                  double magnitudex = Math.abs(this.currentVelocity.getY());
                  if (magnitudex > bV) {
                     bV = magnitudex;
                     collided = BlockFace.UP;
                  }
                  break;
               case DOWN:
                  double magnitude = Math.abs(this.currentVelocity.getY());
                  if (magnitude > bV) {
                     bV = magnitude;
                     collided = BlockFace.DOWN;
                  }
            }
         }

         if (collided == null) {
            return false;
         } else {
            switch (collided) {
               case NORTH:
                  if (this.currentVelocity.getZ() > 0.0) {
                     this.currentVelocity.setZ(this.currentVelocity.getZ() * -0.995);
                  }
                  break;
               case EAST:
                  if (this.currentVelocity.getX() < 0.0) {
                     this.currentVelocity.setX(this.currentVelocity.getX() * -0.995);
                  }
                  break;
               case SOUTH:
                  if (this.currentVelocity.getZ() < 0.0) {
                     this.currentVelocity.setZ(this.currentVelocity.getZ() * -0.995);
                  }
                  break;
               case WEST:
                  if (this.currentVelocity.getX() > 0.0) {
                     this.currentVelocity.setX(this.currentVelocity.getX() * -0.995);
                  }
                  break;
               case UP:
                  if (this.currentVelocity.getY() < 0.0) {
                     this.currentVelocity.setY(this.currentVelocity.getY() * -0.995);
                  }
                  break;
               case DOWN:
                  if (this.currentVelocity.getY() > 0.0) {
                     this.currentVelocity.setY(this.currentVelocity.getY() * -0.995);
                  }
            }

            this.currentVelocity.multiply(MissileMechanic.this.bounceVelocityMod.get(this.data));
            return true;
         }
      }
   }
}
