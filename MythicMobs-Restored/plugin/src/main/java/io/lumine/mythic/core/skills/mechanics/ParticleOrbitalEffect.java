package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.VectorUtils;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particleorbital",
   aliases = {"e:particleorbital", "particleorbital", "effect:particlecircle", "particlecircle", "e:particlecricle"}
)
public class ParticleOrbitalEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat radius;
   protected PlaceholderInt points;
   protected PlaceholderDuration interval;
   protected PlaceholderDuration iterations;
   protected double velocity = 1.0;
   protected boolean rotate = false;
   protected boolean reversed = false;
   protected PlaceholderDouble xRotation;
   protected PlaceholderDouble yRotation;
   protected PlaceholderDouble zRotation;
   protected double angularVelocityX = 0.015707963267948967;
   protected double angularVelocityY = 0.018479956785822312;
   protected double angularVelocityZ = 0.02026833970057931;
   protected PlaceholderDouble xOffset;
   protected PlaceholderDouble yOffset;
   protected PlaceholderDouble zOffset;

   public ParticleOrbitalEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 4.0F);
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 20);
      this.iterations = mlc.getPlaceholderDuration(new String[]{"ticks", "t"}, DurationUnit.TICKS, "100");
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "in", "i"}, DurationUnit.TICKS, "10");
      this.xRotation = mlc.getPlaceholderDouble(new String[]{"rotationx", "rotx", "rx"}, 0.0);
      this.yRotation = mlc.getPlaceholderDouble(new String[]{"rotationy", "roty", "ry"}, 0.0);
      this.zRotation = mlc.getPlaceholderDouble(new String[]{"rotationz", "rotz", "rz"}, 0.0);
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"offsetx", "offx", "ox"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"offsety", "offy", "oy"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"offsetz", "offz", "oz"}, 0.0);
      this.angularVelocityX = mlc.getDouble(new String[]{"angularvelocityx", "avx", "vx"}, 0.0);
      this.angularVelocityY = mlc.getDouble(new String[]{"angularvelocityy", "avy", "vy"}, 0.0);
      this.angularVelocityZ = mlc.getDouble(new String[]{"angularvelocityz", "avz", "vz"}, 0.0);
      this.angularVelocityX = this.angularVelocityX == 0.0 ? 0.0 : Math.PI / this.angularVelocityX;
      this.angularVelocityY = this.angularVelocityY == 0.0 ? 0.0 : Math.PI / this.angularVelocityY;
      this.angularVelocityZ = this.angularVelocityZ == 0.0 ? 0.0 : Math.PI / this.angularVelocityZ;
      this.rotate = mlc.getBoolean(new String[]{"rotate"}, this.angularVelocityX > 0.0 || this.angularVelocityY > 0.0 || this.angularVelocityZ > 0.0);
      this.reversed = mlc.getBoolean(new String[]{"reversed", "reverse"}, false);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new ParticleOrbitalEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new ParticleOrbitalEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   private class Animator implements Runnable {
      private SkillMetadata data;
      private Optional<AbstractEntity> entity = Optional.empty();
      private Optional<AbstractLocation> location = Optional.empty();
      private int iteration = 0;
      private final int maxIterations;
      private final int interval;
      private int step = 0;
      private Task task;

      public Animator(SkillMetadata data, AbstractEntity entity) {
         this.data = data;
         this.entity = Optional.of(entity);
         this.interval = Math.max(1, ParticleOrbitalEffect.this.interval.get(data, entity).getTicks());
         this.maxIterations = ParticleOrbitalEffect.this.iterations.get(data, entity).getTicks();
         this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
      }

      public Animator(SkillMetadata data, AbstractLocation location) {
         this.data = data;
         this.location = Optional.of(location);
         this.interval = Math.max(1, ParticleOrbitalEffect.this.interval.get(data).getTicks());
         this.maxIterations = ParticleOrbitalEffect.this.iterations.get(data).getTicks();
         this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
      }

      public AbstractLocation getLocation() {
         return this.entity.isPresent()
            ? this.entity
               .get()
               .getLocation()
               .add(
                  ParticleOrbitalEffect.this.xOffset.get(this.data),
                  ParticleOrbitalEffect.this.yOffset.get(this.data),
                  ParticleOrbitalEffect.this.zOffset.get(this.data)
               )
            : this.location
               .get()
               .clone()
               .add(
                  ParticleOrbitalEffect.this.xOffset.get(this.data),
                  ParticleOrbitalEffect.this.yOffset.get(this.data),
                  ParticleOrbitalEffect.this.zOffset.get(this.data)
               );
      }

      @Override
      public void run() {
         if (this.data.getCaster() != null && this.data.getCaster().getEntity().isDead()) {
            this.task.terminate();
         } else {
            float radius = ParticleOrbitalEffect.this.radius.get(this.data);
            AbstractLocation location = this.getLocation();
            double inc = (Math.PI * 2) / ParticleOrbitalEffect.this.points.get(this.data);
            double angle = this.step * inc;
            AbstractVector v = new AbstractVector();
            v.setX(Math.cos(angle) * radius);
            v.setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(
               v,
               ParticleOrbitalEffect.this.xRotation.get(this.data),
               ParticleOrbitalEffect.this.yRotation.get(this.data),
               ParticleOrbitalEffect.this.zRotation.get(this.data)
            );
            if (ParticleOrbitalEffect.this.rotate) {
               VectorUtils.rotateVector(
                  v,
                  ParticleOrbitalEffect.this.angularVelocityX * this.step,
                  ParticleOrbitalEffect.this.angularVelocityY * this.step,
                  ParticleOrbitalEffect.this.angularVelocityZ * this.step
               );
            }

            AbstractLocation loc = location.add(v);
            Collection<AbstractPlayer> audience = SkillAudience.getEffective(ParticleOrbitalEffect.this.audience, this.data, this.data.getCaster().getEntity());
            ParticleOrbitalEffect.this.playParticleEffect(this.data, loc, audience);
            if (ParticleOrbitalEffect.this.reversed) {
               this.step--;
            } else {
               this.step++;
            }

            if (++this.iteration * this.interval >= this.maxIterations) {
               this.task.terminate();
            }
         }
      }
   }
}
