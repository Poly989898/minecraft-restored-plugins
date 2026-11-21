package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@MythicMechanic(
   author = "Ashijin",
   version = "5.11",
   name = "fear",
   description = "Applies an aura that makes the target run around in fear",
   deprecated = true
)
public class FearMechanic extends Aura implements ITargetedEntitySkill {
   private final PlaceholderFloat speed;
   private final PlaceholderDuration changeDirInterval;
   private final PlaceholderFloat fearDistance;
   private final PlaceholderDuration rotationTicks;

   public FearMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.auraGroup = Optional.ofNullable(mlc.getPlaceholderString(new String[]{"auragroup", "auratype", "group", "type", "g"}, "#fear"));
      this.speed = mlc.getPlaceholderFloat(new String[]{"speed", "s"}, 0.35F);
      this.changeDirInterval = mlc.getPlaceholderDuration(new String[]{"directionChangeInterval", "dirchange", "dci"}, DurationUnit.TICKS, "20");
      this.fearDistance = mlc.getPlaceholderFloat(new String[]{"feardistance", "fearrange", "fdist", "fd"}, 10.0F);
      this.rotationTicks = mlc.getPlaceholderDuration(new String[]{"rotationticks", "rotateticks", "rt"}, DurationUnit.TICKS, "5");
      this.interval = PlaceholderDuration.of("1", DurationUnit.TICKS);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isLiving()) {
         return SkillResult.INVALID_TARGET;
      } else {
         new FearMechanic.FearMechanicTracker(target, data);
         return SkillResult.SUCCESS;
      }
   }

   protected class FearMechanicTracker extends Aura.AuraTracker {
      private AbstractVector currentDirection;
      private int ticksUntilChange;
      private float startYaw;
      private float endYaw;
      private int rotationTicksRemaining;
      private int rotationTicksTotal;

      public FearMechanicTracker(AbstractEntity target, SkillMetadata data) {
         super(target, data);
         this.pickNewDirection();
         this.start();
      }

      @Override
      public void auraStart() {
         this.executeAuraSkill(FearMechanic.this.onStartSkill, this.skillMetadata);
      }

      @Override
      public void auraTick() {
         if (this.entity.isPresent() && this.entity.get().isLiving()) {
            AbstractEntity abstractEntity = this.entity.get();
            if (this.ticksUntilChange-- <= 0 || this.currentDirection == null) {
               this.pickNewDirection();
            }

            float spd = FearMechanic.this.speed.get(this.skillMetadata, abstractEntity);
            if (!(spd <= 0.0F)) {
               AbstractVector vel = this.currentDirection.clone().multiply(spd);
               vel.setY(abstractEntity.getVelocity().getY());
               abstractEntity.setVelocity(vel);
               if (this.currentDirection != null) {
                  AbstractVector dirVec = this.currentDirection.clone();
                  if (dirVec.lengthSquared() > 0.0) {
                     dirVec.setY(0);
                     if (dirVec.lengthSquared() > 0.0) {
                        dirVec.normalize();
                        float targetYaw = (float)Math.toDegrees(Math.atan2(-dirVec.getX(), dirVec.getZ()));
                        float pitch = 0.0F;
                        if (abstractEntity.isPlayer()) {
                           this.updatePlayerRotation(abstractEntity, targetYaw, pitch);
                        } else {
                           FearMechanic.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(abstractEntity, pitch, targetYaw);
                        }
                     }
                  }
               }
            }
         } else {
            this.terminate();
         }
      }

      @Override
      public void auraStop() {
         this.executeAuraSkill(FearMechanic.this.onEndSkill, this.skillMetadata);
      }

      private void pickNewDirection() {
         if (this.entity.isPresent()) {
            AbstractEntity target = this.entity.get();
            AbstractLocation targetLoc = target.getLocation();
            AbstractVector dir = this.randomHorizontalDirection();
            if (this.skillMetadata != null && this.skillMetadata.getCaster() != null && this.skillMetadata.getCaster().getEntity() != null) {
               AbstractLocation casterLoc = this.skillMetadata.getCaster().getEntity().getLocation();
               AbstractVector away = targetLoc.toVector().subtract(casterLoc.toVector());
               away.setY(0);
               float fearRange = FearMechanic.this.fearDistance.get(this.skillMetadata, target);
               if (fearRange > 0.0F) {
                  double distSq = away.lengthSquared();
                  double fearRangeSq = (double)fearRange * fearRange;
                  if (distSq <= fearRangeSq && distSq > 1.0E-4) {
                     dir = away.normalize();
                     double baseAngle = Math.atan2(dir.getZ(), dir.getX());
                     double wobble = ThreadLocalRandom.current().nextDouble(-Math.PI / 4, Math.PI / 4);
                     double newAngle = baseAngle + wobble;
                     dir.setX(Math.cos(newAngle));
                     dir.setZ(Math.sin(newAngle));
                     dir.normalize();
                  }
               }
            }

            this.currentDirection = dir;
            this.ticksUntilChange = Math.max(1, FearMechanic.this.changeDirInterval.get(this.skillMetadata, this.entity.get()).getTicks());
            if (this.entity.isPresent() && this.entity.get().isPlayer()) {
               float currentYaw = this.entity.get().getLocation().getYaw();
               if (dir.lengthSquared() > 0.0) {
                  dir.setY(0);
                  if (dir.lengthSquared() > 0.0) {
                     dir.normalize();
                     float newYaw = (float)Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
                     this.startYaw = this.normalizeYaw(currentYaw);
                     this.endYaw = this.normalizeYaw(newYaw);
                     this.rotationTicksTotal = Math.max(1, FearMechanic.this.rotationTicks.get(this.skillMetadata, this.entity.get()).getTicks());
                     this.rotationTicksRemaining = this.rotationTicksTotal;
                  }
               }
            }
         }
      }

      private AbstractVector randomHorizontalDirection() {
         double angle = ThreadLocalRandom.current().nextDouble(0.0, Math.PI * 2);
         return new AbstractVector(Math.cos(angle), 0.0, Math.sin(angle)).normalize();
      }

      private void updatePlayerRotation(AbstractEntity abstractEntity, float targetYaw, float pitch) {
         if (this.rotationTicksTotal <= 0) {
            FearMechanic.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), targetYaw, pitch, false);
         } else {
            if (this.rotationTicksRemaining > 0) {
               float t = 1.0F - (float)this.rotationTicksRemaining / this.rotationTicksTotal;
               float lerpedYaw = this.lerpAngle(this.startYaw, this.endYaw, t);
               FearMechanic.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), lerpedYaw, pitch, false);
               this.rotationTicksRemaining--;
            } else {
               FearMechanic.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), targetYaw, pitch, false);
            }
         }
      }

      private float normalizeYaw(float yaw) {
         yaw %= 360.0F;
         if (yaw <= -180.0F) {
            yaw += 360.0F;
         }

         if (yaw > 180.0F) {
            yaw -= 360.0F;
         }

         return yaw;
      }

      private float lerpAngle(float a, float b, float t) {
         a = this.normalizeYaw(a);
         b = this.normalizeYaw(b);
         float diff = this.normalizeYaw(b - a);
         return this.normalizeYaw(a + diff * t);
      }
   }
}
