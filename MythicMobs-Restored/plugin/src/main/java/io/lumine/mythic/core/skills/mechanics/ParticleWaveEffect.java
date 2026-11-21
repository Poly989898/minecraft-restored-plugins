package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.HashSet;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlewave",
   aliases = {"particlewave", "e:pw"}
)
public class ParticleWaveEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   private String strCloudParticle;
   public int particlesFront = 10;
   public int particlesBack = 10;
   public int rows = 20;
   public float lengthFront = 1.5F;
   public float lengthBack = 3.0F;
   public float depthFront = 1.0F;
   public float heightBack = 0.5F;
   public float height = 2.0F;
   public float width = 5.0F;
   private PlaceholderDuration interval;
   private PlaceholderDuration duration;
   public double distance = 0.375;

   public ParticleWaveEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.strParticle = mlc.getString(new String[]{"particle", "p"}, "dripWater");
      this.strCloudParticle = mlc.getString(new String[]{"cloudparticle", "cp"}, "cloud");
      this.yOffset = mlc.getPlaceholderFloat(new String[]{"yoffset", "yo"}, 0.8F);
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "i"}, DurationUnit.TICKS, "4");
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "200");
      this.particlesFront = mlc.getInteger(new String[]{"amountfront", "af"}, 10);
      this.particlesBack = mlc.getInteger(new String[]{"amountback", "ab"}, 10);
      this.rows = mlc.getInteger(new String[]{"rows", "r"}, 20);
      this.lengthFront = mlc.getFloat(new String[]{"lengthfront", "lf"}, 1.5F);
      this.lengthBack = mlc.getFloat(new String[]{"lengthback", "lb"}, 3.0F);
      this.depthFront = mlc.getFloat(new String[]{"depthfront", "df"}, 1.0F);
      this.height = mlc.getFloat(new String[]{"height", "h"}, 2.0F);
      this.heightBack = mlc.getFloat(new String[]{"heightback", "hb"}, 0.5F);
      this.width = mlc.getFloat(new String[]{"width", "w"}, 5.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new ParticleWaveEffect.Animator(target, Math.max(1, this.interval.get(data).getTicks()), this.duration.get(data).getTicks());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new ParticleWaveEffect.Animator(target, Math.max(1, this.interval.get(data, target).getTicks()), this.duration.get(data, target).getTicks());
      return SkillResult.SUCCESS;
   }

   private class Animator implements Runnable {
      private AbstractEntity entity;
      private AbstractLocation location;
      private int interval;
      private int duration;
      private int iteration;
      private Task task;
      HashSet<AbstractVector> waterCache = new HashSet<>();
      HashSet<AbstractVector> cloudCache = new HashSet<>();
      protected boolean firstStep = true;
      public AbstractVector velocity = new AbstractVector();

      public Animator(AbstractLocation location, int interval, int duration) {
         this(interval, duration);
         this.entity = null;
         this.location = location;
      }

      public Animator(AbstractEntity entity, int interval, int duration) {
         this(interval, duration);
         this.entity = entity;
      }

      protected Animator(int interval, int duration) {
         this.interval = interval;
         this.duration = duration;
         this.iteration = 0;
         this.task = Schedulers.async().runRepeating(this, 0L, (long)interval);
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Started ParticleWave effect");
      }

      protected AbstractLocation getLocation() {
         return this.entity != null ? this.entity.getLocation() : this.location.clone();
      }

      protected void setup(AbstractLocation location) {
      }

      @Override
      public void run() {
         AbstractLocation location = this.getLocation();
         if (this.firstStep) {
            this.velocity.copy(location.getDirection().setY(0).normalize().multiply(0.2));
            this.setup(location);
         }

         location.add(this.velocity);

         for (AbstractVector v : this.cloudCache) {
            location.add(v);
            location.subtract(v);
         }

         for (AbstractVector v : this.waterCache) {
            location.add(v);
            location.subtract(v);
         }
      }
   }
}
