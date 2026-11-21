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
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileSurfaceMode;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import lombok.Getter;

@MythicMechanic(
   author = "Ashijin",
   name = "totem",
   aliases = {"toteme", "t"},
   description = "Creates a static totem projectile at the target"
)
public class TotemMechanic extends Projectile implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderInt maxCharges;
   protected PlaceholderFloat YOffset;
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
      defValue = "0.025"
   )
   protected float heightFromSurface;
   protected boolean faceAwayFromCaster;

   public TotemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.maxCharges = mlc.getPlaceholderInteger(new String[]{"charges", "ch", "c"}, 0);
      this.YOffset = mlc.getPlaceholderFloat(new String[]{"yoffset", "yo"}, 1.0F);
      this.stopOnHitEntity = mlc.getBoolean(new String[]{"stopatentity", "se"}, false);
      boolean hugSurface = mlc.getBoolean(new String[]{"hugsurface", "hs"}, false);
      this.heightFromSurface = mlc.getFloat(new String[]{"heightfromsurface", "hfs"}, 0.5F);
      this.faceAwayFromCaster = mlc.getBoolean(new String[]{"faceAwayFromCaster", "fafc"}, false);
      if (hugSurface) {
         boolean hugWater = mlc.getBoolean(new String[]{"hugliquid", "hugwater", "huglava"}, false);
         if (hugWater) {
            this.surfaceMode = ProjectileSurfaceMode.WATER;
         } else {
            this.surfaceMode = ProjectileSurfaceMode.SURFACE;
         }
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      try {
         new TotemMechanic.TotemTracker(data, target.getLocation());
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Totem Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      try {
         new TotemMechanic.TotemTracker(data, target);
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Totem Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   public class TotemTracker extends Projectile.ProjectileTracker {
      private int charges;

      public TotemTracker(SkillMetadata data, AbstractLocation target) {
         super(data, target);
         this.startLocation = target;
         this.previousLocation = target;
         this.currentLocation = target;
         this.currentVelocity = new AbstractVector(0, 0, 0);
         if (data.getCallingEvent() instanceof Projectile.ProjectileTracker projectileTracker) {
            this.currentVelocity = projectileTracker.getCurrentVelocity();
         }

         this.charges = TotemMechanic.this.maxCharges.get(data);
         this.start();
      }

      @Override
      public void projectileStart() {
         if (TotemMechanic.this.surfaceMode != ProjectileSurfaceMode.NONE) {
            this.currentLocation.setY((int)this.currentLocation.getY() + TotemMechanic.this.heightFromSurface);
            this.currentLocation.setPitch(0.0F);
         } else {
            float yOffset = TotemMechanic.this.YOffset.get(this.data);
            if (yOffset != 0.0F) {
               this.currentLocation.setY(this.currentLocation.getY() + yOffset);
            }
         }

         if (TotemMechanic.this.faceAwayFromCaster) {
            AbstractLocation casterLoc = this.data.getCaster().getLocation();
            double dx = this.currentLocation.getX() - casterLoc.getX();
            double dz = this.currentLocation.getZ() - casterLoc.getZ();
            float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
            this.currentLocation.setYaw(yaw);
         }

         if (TotemMechanic.this.bullet != null) {
            this.bullet = TotemMechanic.this.bullet.create(this, null);
         }
      }

      @Override
      public void projectileMove() {
      }

      @Override
      public void projectileTick() {
         if (TotemMechanic.this.onTickSkill.isPresent() && TotemMechanic.this.onTickSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            AbstractLocation location;
            if (TotemMechanic.this.bulletType.isPresent() && TotemMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
               location = this.previousLocation.clone();
            } else {
               location = this.currentLocation.clone();
            }

            HashSet<AbstractLocation> targets = new HashSet<>();
            targets.add(location);
            sData.setLocationTargets(targets);
            sData.setOrigin(location);
            TotemMechanic.this.onTickSkill.get().execute(sData);
         }

         this.evaluateTargetsInBB();
         if (!this.targets.isEmpty()) {
            this.doHit(this.targets.clone());
            ObjectIterator objectIterator = this.targets.iterator();

            while (objectIterator.hasNext()) {
               AbstractEntity target = (AbstractEntity)objectIterator.next();
               if (TotemMechanic.this.canStopAtEntity(this.data, target)) {
                  this.terminate();
                  break;
               }
            }

            this.charges--;
            if (TotemMechanic.this.maxCharges.get(this.data) > 0 && this.charges <= 0) {
               this.terminate();
            }
         }

         this.targets.clear();
      }

      public void doHit(Collection<AbstractEntity> targets) {
         this.hasHitEntity = true;
         this.applyHitboxVariable(this.data, targets);
         if (TotemMechanic.this.onHitSkill.isPresent() && TotemMechanic.this.onHitSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            sData.setEntityTargets(targets);
            sData.setOrigin(this.currentLocation.clone());
            this.executeOnHit(targets, () -> TotemMechanic.this.onHitSkill.get().execute(sData));
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
   }
}
