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
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
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
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

@MythicMechanic(
   author = "Ashijin",
   name = "projectile",
   aliases = {"p"},
   description = "Launches a custom projectile at the target"
)
public class ProjectileMechanic extends Projectile implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "type",
      description = "The type of the projectile, NORMAL or METEOR",
      defValue = "NORMAL"
   )
   protected ProjectileMechanic.ProjectileType type;
   @MythicField(
      name = "gravity",
      aliases = {"g"},
      description = "The gravity modifier the projectile uses",
      defValue = "0"
   )
   protected PlaceholderFloat projectileGravity;
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

   public ProjectileMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String type = mlc.getString("type", "NORMAL");
      this.type = ProjectileMechanic.ProjectileType.valueOf(type.toUpperCase());
      this.projectileGravity = mlc.getPlaceholderFloat(new String[]{"gravity", "g"}, null);
      this.bounce = mlc.getBoolean(new String[]{"bounces", "bounce"}, false);
      this.bounceVelocityMod = mlc.getPlaceholderFloat(new String[]{"bouncevelocity", "bv"}, 0.9F);
      boolean hugSurface = mlc.getBoolean(new String[]{"hugsurface", "hs"}, false);
      this.heightFromSurface = mlc.getPlaceholderFloat(new String[]{"heightfromsurface", "hfs"}, 0.5F);
      this.maxClimbHeight = mlc.getPlaceholderFloat(new String[]{"maxclimbheight", "mch"}, 3.0F);
      this.maxDropHeight = mlc.getPlaceholderFloat(new String[]{"maxdropheight", "mdh"}, 10.0F);
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
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();

      try {
         new ProjectileMechanic.ProjectileMechanicTracker(context.toBuilder().location(target.clone().add(0.0, this.targetYOffset.get(context), 0.0)).build());
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Projectile Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation().add(0.0, target.getEyeHeight() / 2.0, 0.0));
   }

   public class ProjectileMechanicTracker extends Projectile.ProjectileTracker {
      private float gravity = 0.0F;
      private float bounciness = 0.0F;
      private boolean isArrowBullet;
      private AbstractLocation target;

      public ProjectileMechanicTracker(PlaceholderContext context) {
         super(context);
         this.target = context.location();
         if (ProjectileMechanic.this.bounce) {
            this.bounciness = ProjectileMechanic.this.projectileVelocity.get(context);
         }

         this.start();
      }

      @Override
      public void projectileStart() {
         if (ProjectileMechanic.this.type == ProjectileMechanic.ProjectileType.METEOR) {
            this.startLocation = this.target.clone();
            this.startLocation.add(0.0, ProjectileMechanic.this.heightFromSurface.get(this.context), 0.0);
            if (ProjectileMechanic.this.projectileGravity != null) {
               this.gravity = ProjectileMechanic.this.projectileGravity.get(this.context);
            } else {
               this.gravity = ProjectileMechanic.this.projectileVelocity.get(this.context);
            }

            this.gravity = this.gravity / this.ticksPerSecond;
            this.velocityMagnitude = 0.0;
         } else {
            if (ProjectileMechanic.this.sourceIsOrigin) {
               this.startLocation = this.context.skillMetadata().getOrigin().clone();
            } else {
               this.startLocation = this.context.meta().getCaster().getEntity().getLocation().clone();
            }

            this.velocityMagnitude = ProjectileMechanic.this.projectileVelocity.get(this.context) / this.ticksPerSecond;
            if (ProjectileMechanic.this.projectileGravity != null) {
               this.gravity = ProjectileMechanic.this.projectileGravity.get(this.context) / this.ticksPerSecond;
            } else {
               this.gravity = 0.0F;
            }

            if (ProjectileMechanic.this.powerAffectsVelocity) {
               this.velocityMagnitude = this.velocityMagnitude * this.power;
            }

            if (ProjectileMechanic.this.tickInterpolation > 0) {
               this.velocityMagnitude = this.velocityMagnitude / (ProjectileMechanic.this.tickInterpolation + 1);
               this.gravity = this.gravity / (ProjectileMechanic.this.tickInterpolation + 1);
            }

            double syo = ProjectileMechanic.this.startYOffset.get(this.context);
            if (syo != 0.0) {
               this.startLocation.setY(this.startLocation.getY() + syo);
            }

            double sfo = ProjectileMechanic.this.startForwardOffset.get(this.context) * -1.0F;
            if (sfo != 0.0) {
               this.startLocation = MythicUtil.move(ProjectileMechanic.this.faulty, this.startLocation, sfo, 0.0, 0.0);
            }

            double sso = ProjectileMechanic.this.startSideOffset.get(this.context);
            if (sso != 0.0) {
               this.startLocation = MythicUtil.move(ProjectileMechanic.this.faulty, this.startLocation, 0.0, 0.0, sso);
            }

            double eso = ProjectileMechanic.this.endSideOffset.get(this.context);
            if (eso != 0.0) {
               this.target.setDirection(this.startLocation.getDirection());
               this.target = MythicUtil.move(ProjectileMechanic.this.faulty, this.target, 0.0, 0.0, eso);
            }
         }

         this.previousLocation = this.startLocation.clone();
         this.currentLocation = this.startLocation.clone();
         if (this.currentLocation != null) {
            this.currentVelocity = this.target.toVector().subtract(this.currentLocation.toVector());
            if (this.currentVelocity.lengthSquared() > 0.0) {
               this.currentVelocity.normalize();
            } else {
               this.currentVelocity = new AbstractVector(0, -1, 0);
            }

            if (ProjectileMechanic.this.projectileVelocityHorizOffset != null || ProjectileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
               float noise = 0.0F;
               if (ProjectileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
                  noise = (float)(
                     ProjectileMechanic.this.projectileVelocityHorizNoiseBase + Numbers.randomDouble() * ProjectileMechanic.this.projectileVelocityHorizNoise
                  );
               }

               if (ProjectileMechanic.this.projectileVelocityHorizOffset != null) {
                  noise = (float)(noise + ProjectileMechanic.this.projectileVelocityHorizOffset.get(this.context).getDegrees());
               }

               this.currentVelocity.rotate(noise);
            }

            if (ProjectileMechanic.this.projectileVelocityVertOffset != null || ProjectileMechanic.this.projectileVelocityVertNoise > 0.0F) {
               float noisex = 0.0F;
               if (ProjectileMechanic.this.projectileVelocityVertNoise > 0.0F) {
                  noisex = (float)(
                     ProjectileMechanic.this.projectileVelocityVertNoiseBase + Numbers.randomDouble() * ProjectileMechanic.this.projectileVelocityVertNoise
                  );
               }

               if (ProjectileMechanic.this.projectileVelocityVertOffset != null) {
                  noisex = (float)(noisex + ProjectileMechanic.this.projectileVelocityVertOffset.get(this.context).getSlope());
               }

               this.currentVelocity.add(new AbstractVector(0.0F, noisex, 0.0F)).normalize();
            }

            if (ProjectileMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE) {
               this.currentLocation.setY((int)this.currentLocation.getY() + ProjectileMechanic.this.heightFromSurface.get(this.context));
               this.currentVelocity.setY(0).normalize();
            }

            if (ProjectileMechanic.this.powerAffectsVelocity) {
               this.currentVelocity.multiply(this.power);
            }

            this.currentVelocity.multiply(this.velocityMagnitude);
            if (this.gravity != 0.0F) {
               this.currentVelocity.setY(this.currentVelocity.getY() - this.gravity);
            }

            this.isArrowBullet = ProjectileMechanic.this.bulletType.isPresent() && ProjectileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW;
            if (ProjectileMechanic.this.bullet != null) {
               this.bullet = ProjectileMechanic.this.bullet.create(this, null);
            }
         }
      }

      public void setVelocity(float value) {
         this.velocityMagnitude = value / this.ticksPerSecond;
         if (ProjectileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (ProjectileMechanic.this.tickInterpolation + 1);
         }

         this.currentVelocity = this.currentVelocity.normalize().multiply(this.velocityMagnitude);
      }

      public void multiplyVelocity(float v) {
         this.velocityMagnitude *= v;
         this.currentVelocity = this.currentVelocity.multiply(v);
      }

      public void addVelocity(float v) {
         if (ProjectileMechanic.this.tickInterpolation > 0) {
            v /= ProjectileMechanic.this.tickInterpolation + 1;
         }

         this.velocityMagnitude = (this.velocityMagnitude * this.ticksPerSecond + v) / this.ticksPerSecond;
         if (this.currentVelocity.lengthSquared() != 0.0) {
            AbstractVector normalizedVelocity = this.currentVelocity.clone().normalize();
            AbstractVector flatAmountVector = normalizedVelocity.multiply(v);
            this.currentVelocity.add(flatAmountVector);
         }
      }

      public void setGravity(float p) {
         if (ProjectileMechanic.this.tickInterpolation > 0) {
            p /= ProjectileMechanic.this.tickInterpolation + 1;
         }

         this.gravity = p;
      }

      public void multiplyGravity(float p) {
         this.gravity *= p;
      }

      public void addGravity(float g) {
         if (ProjectileMechanic.this.tickInterpolation > 0) {
            g /= ProjectileMechanic.this.tickInterpolation + 1;
         }

         this.gravity += g;
      }

      private boolean isHighAccuracy() {
         return ProjectileMechanic.this.highAccuracyMode == Projectile.ProjectileTriOption.TRUE
            || ProjectileMechanic.this.highAccuracyMode == Projectile.ProjectileTriOption.PLAYERS_ONLY && this.data.getCaster().getEntity().isPlayer();
      }

      @Override
      public void projectileMove() {
         this.previousLocation = this.currentLocation.clone();
         this.currentLocation.add(this.currentVelocity);
         this.clampToMaxRange();
         if (this.isHighAccuracy()) {
            RayTraceResult traceResult = ProjectileMechanic.this.getPlugin()
               .getVolatileCodeHandler()
               .getWorldHandler()
               .rayTraceBlock(this.previousLocation, this.currentLocation, FluidCollisionMode.NEVER, true);
            if (traceResult != null && traceResult.getHitBlock() != null && !traceResult.getHitBlock().isEmpty()) {
               AbstractLocation hitLocation = BukkitAdapter.adapt(traceResult.getHitPosition()).toLocation(this.currentLocation.getWorld());
               if (this.previousLocation.distanceSquared(hitLocation) <= this.previousLocation.distanceSquared(this.currentLocation)) {
                  if (ProjectileMechanic.this.canStopAtLocation(this.data, hitLocation)) {
                     this.currentLocation = hitLocation;
                  } else if (!ProjectileMechanic.this.stopOnHitGround
                     && ProjectileMechanic.this.onHitBlockSkill.isPresent()
                     && ProjectileMechanic.this.onHitBlockSkill.get().isUsable(this.data)) {
                     SkillMetadata sData = this.data.deepClone();
                     AbstractLocation location;
                     if (this.isArrowBullet) {
                        location = this.previousLocation.clone();
                     } else {
                        location = this.currentLocation.clone();
                     }

                     ProjectileMechanic.this.onHitBlockSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
                  }
               }
            }
         }

         if (ProjectileMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE) {
            if (this.currentLocation.getBlockX() != this.previousLocation.getBlockX() || this.currentLocation.getBlockZ() != this.previousLocation.getBlockZ()) {
               Block b = BukkitAdapter.adapt(this.currentLocation).subtract(0.0, ProjectileMechanic.this.heightFromSurface.get(this.context), 0.0).getBlock();
               if (BlockUtil.isPathable(b, ProjectileMechanic.this.surfaceMode)) {
                  int attempts = 0;
                  boolean ok = false;

                  while (attempts++ < ProjectileMechanic.this.maxDropHeight.get(this.context)) {
                     b = b.getRelative(BlockFace.DOWN);
                     if (!BlockUtil.isPathable(b, ProjectileMechanic.this.surfaceMode)) {
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

                  while (attempts++ < ProjectileMechanic.this.maxClimbHeight.get(this.context)) {
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

               this.currentLocation.setY((int)this.currentLocation.getY() + ProjectileMechanic.this.heightFromSurface.get(this.context));
            }
         } else if (this.gravity != 0.0F) {
            this.currentVelocity.setY(this.currentVelocity.getY() - this.gravity);
         }

         if (ProjectileMechanic.this.bounce && MythicBukkit.isVolatile()) {
            try {
               if (this.handleBounce()) {
                  this.executeProjectileSkill(ProjectileMechanic.this.onBounceSkill, this.data, false);
               }
            } catch (IllegalArgumentException e) {
               MythicLogger.errorMechanicConfig(
                  ProjectileMechanic.this,
                  ProjectileMechanic.this.config,
                  "An error occurred while calculating projectile bounce (did you set the bounding box to zero?)"
               );
               e.printStackTrace();
            }
         } else if (ProjectileMechanic.this.stopOnHitGround && !BlockUtil.isPathable(BukkitAdapter.adapt(this.currentLocation).getBlock(), this)) {
            if (!ProjectileMechanic.this.canStopAtLocation(this.data, this.currentLocation)) {
               return;
            }

            if (ProjectileMechanic.this.onHitBlockSkill.isPresent() && ProjectileMechanic.this.onHitBlockSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               AbstractLocation location;
               if (this.isArrowBullet) {
                  location = this.previousLocation.clone();
               } else {
                  location = this.currentLocation.clone();
               }

               ProjectileMechanic.this.onHitBlockSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
            }

            this.currentLocation = this.previousLocation;
            this.terminate();
         }
      }

      @Override
      public void projectileTick() {
         if (ProjectileMechanic.this.onTickSkill.isPresent() && ProjectileMechanic.this.onTickSkill.get().isUsable(this.context.skillMetadata())) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            AbstractLocation location;
            if (this.isArrowBullet) {
               location = this.previousLocation.clone();
            } else {
               location = this.currentLocation.clone();
            }

            sData.setLocationTargets(Collections.singletonList(location));
            sData.setOrigin(location);
            ProjectileMechanic.this.onTickSkill.get().execute(sData);
         }

         this.evaluateTargetsInBB();
         if (!this.targets.isEmpty()) {
            this.doHit(this.targets);
            ObjectIterator objectIterator = this.targets.iterator();

            while (objectIterator.hasNext()) {
               AbstractEntity target = (AbstractEntity)objectIterator.next();
               if (ProjectileMechanic.this.canStopAtEntity(this.data, target)) {
                  this.terminate();
                  break;
               }
            }
         }

         this.targets.clear();
      }

      private void doHit(Collection<AbstractEntity> targets) {
         this.hasHitEntity = true;
         this.applyHitboxVariable(this.data, targets);
         if (ProjectileMechanic.this.onHitSkill.isPresent()) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            sData.setEntityTargets(new ArrayList<>(targets));
            sData.setOrigin(this.currentLocation.clone());
            this.executeOnHit(targets, () -> {
               if (ProjectileMechanic.this.onHitSkill.get().isUsable(sData)) {
                  ProjectileMechanic.this.onHitSkill.get().execute(sData);
               }
            });
         }
      }

      @Override
      public void setCancelled() {
         this.terminate();
      }

      @Override
      public boolean getCancelled() {
         return this.components.hasTerminated();
      }

      public boolean handleBounce() {
         float hr = ProjectileMechanic.this.hitRadius.get(this.context);
         float vhr = ProjectileMechanic.this.verticalHitRadius.get(this.context);
         BoundingBox bb = BoundingBox.of(BukkitAdapter.adapt(this.currentLocation), hr, vhr, hr);
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
                     if (!(this.currentVelocity.getY() <= this.gravity * -1.0F)) {
                        this.currentVelocity.setY(0);
                        return false;
                     }

                     this.currentVelocity.setY(this.currentVelocity.getY() * -0.8);
                  }
                  break;
               case DOWN:
                  if (this.currentVelocity.getY() > 0.0) {
                     this.currentVelocity.setY(this.currentVelocity.getY() * -0.95);
                  }
            }

            this.currentVelocity.multiply(ProjectileMechanic.this.bounceVelocityMod.get(this.context));
            return true;
         }
      }
   }

   protected static enum ProjectileType {
      NORMAL,
      METEOR;
   }
}
