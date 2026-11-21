package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.concurrent.ThreadLocalRandom;

public class FearAuraComponent implements AuraComponent {
   private final AuraMechanic mechanic;
   private final Aura.AuraTracker tracker;
   private final PlaceholderFloat speed;
   private final PlaceholderDuration changeDirInterval;
   private final PlaceholderFloat fearDistance;
   private final PlaceholderDuration rotationTicks;
   private AbstractVector currentDirection;
   private int ticksUntilChange;
   private float startYaw;
   private float endYaw;
   private int rotationTicksRemaining;
   private int rotationTicksTotal;
   private Task task;

   public FearAuraComponent(
      AuraMechanic mechanic,
      Aura.AuraTracker tracker,
      PlaceholderFloat speed,
      PlaceholderDuration changeDirInterval,
      PlaceholderFloat fearDistance,
      PlaceholderDuration rotationTicks
   ) {
      this.mechanic = mechanic;
      this.tracker = tracker;
      this.speed = speed;
      this.changeDirInterval = changeDirInterval;
      this.fearDistance = fearDistance;
      this.rotationTicks = rotationTicks;
   }

   @Override
   public void auraStart() {
      this.pickNewDirection();
      if (this.tracker.getEntity().isPresent()) {
         this.task = Schedulers.of(this.tracker.getEntity().get().getBukkitEntity()).runRepeating(this::tickFear, 0L, 1L);
         this.tracker.registerAuraComponent(this.task);
      }
   }

   @Override
   public void auraTick() {
   }

   private void tickFear() {
      if (!this.tracker.getEntity().isEmpty() && this.tracker.getEntity().get().isLiving()) {
         AbstractEntity abstractEntity = this.tracker.getEntity().get();
         if (this.ticksUntilChange-- <= 0 || this.currentDirection == null) {
            this.pickNewDirection();
         }

         float spd = this.speed.get(this.tracker.getSkillMetadata(), abstractEntity);
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
                        this.mechanic.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(abstractEntity, pitch, targetYaw);
                     }
                  }
               }
            }
         }
      } else {
         this.tracker.terminate();
      }
   }

   private void pickNewDirection() {
      if (!this.tracker.getEntity().isEmpty()) {
         AbstractEntity target = this.tracker.getEntity().get();
         AbstractLocation targetLoc = target.getLocation();
         AbstractVector dir = this.randomHorizontalDirection();
         if (this.tracker.getSkillMetadata() != null
            && this.tracker.getSkillMetadata().getCaster() != null
            && this.tracker.getSkillMetadata().getCaster().getEntity() != null) {
            AbstractLocation casterLoc = this.tracker.getSkillMetadata().getCaster().getEntity().getLocation();
            AbstractVector away = targetLoc.toVector().subtract(casterLoc.toVector());
            away.setY(0);
            float fearRange = this.fearDistance.get(this.tracker.getSkillMetadata(), target);
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
         this.ticksUntilChange = Math.max(1, this.changeDirInterval.get(this.tracker.getSkillMetadata(), target).getTicks());
         if (this.tracker.getEntity().isPresent() && this.tracker.getEntity().get().isPlayer()) {
            float currentYaw = this.tracker.getEntity().get().getLocation().getYaw();
            if (dir.lengthSquared() > 0.0) {
               dir.setY(0);
               if (dir.lengthSquared() > 0.0) {
                  dir.normalize();
                  float newYaw = (float)Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
                  this.startYaw = this.normalizeYaw(currentYaw);
                  this.endYaw = this.normalizeYaw(newYaw);
                  this.rotationTicksTotal = Math.max(1, this.rotationTicks.get(this.tracker.getSkillMetadata(), target).getTicks());
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
         this.mechanic.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), targetYaw, pitch, false);
      } else {
         if (this.rotationTicksRemaining > 0) {
            float t = 1.0F - (float)this.rotationTicksRemaining / this.rotationTicksTotal;
            float lerpedYaw = this.lerpAngle(this.startYaw, this.endYaw, t);
            this.mechanic.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), lerpedYaw, pitch, false);
            this.rotationTicksRemaining--;
         } else {
            this.mechanic.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(abstractEntity.asPlayer(), targetYaw, pitch, false);
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

   @Override
   public void auraStop() {
      if (this.task != null) {
         this.task.terminate();
         this.task = null;
      }
   }

   @MythicAuraComponent(
      name = "fear",
      aliases = {"feared"},
      description = "Makes the target run around in fear"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final PlaceholderFloat speed;
      private final PlaceholderDuration changeDirInterval;
      private final PlaceholderFloat fearDistance;
      private final PlaceholderDuration rotationTicks;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.mechanic.setThreadSafetyLevel(ThreadSafetyLevel.SYNC_ONLY);
         this.mechanic.addAuraTag("fear");
         this.speed = mlc.getPlaceholderFloat(new String[]{"speed", "s"}, 0.35F);
         this.changeDirInterval = mlc.getPlaceholderDuration(new String[]{"directionChangeInterval", "dirchange", "dci"}, DurationUnit.TICKS, "20");
         this.fearDistance = mlc.getPlaceholderFloat(new String[]{"feardistance", "fearrange", "fdist", "fd"}, 10.0F);
         this.rotationTicks = mlc.getPlaceholderDuration(new String[]{"rotationticks", "rotateticks", "rt"}, DurationUnit.TICKS, "5");
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new FearAuraComponent(this.mechanic, tracker, this.speed, this.changeDirInterval, this.fearDistance, this.rotationTicks);
      }
   }
}
