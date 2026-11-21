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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.adapters.BukkitParticle;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particletornado",
   aliases = {"particletornado", "e:pt"}
)
public class ParticleTornadoEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   PlaceholderFloat cloudHSpread;
   PlaceholderFloat cloudVSpread;
   PlaceholderFloat cloudPSpeed;
   PlaceholderFloat cloudYOffset;
   public String strCloudParticle = "cloud";
   protected BukkitParticle cloudParticle;
   protected Function<PlaceholderMeta, Object> cloudParticleData = null;
   public PlaceholderFloat cloudSize;
   public float particleSpeedRamp = 0.0F;
   public PlaceholderInt cloudAmount;
   public PlaceholderFloat yOffset;
   public PlaceholderFloat tornadoHeight;
   public PlaceholderFloat maxTornadoRadius;
   private int sliceHeight;
   private PlaceholderDuration interval;
   private PlaceholderDuration duration;
   private PlaceholderFloat rotationSpeed;
   public boolean showCloud = true;
   public boolean showTornado = true;
   private boolean stopOnCasterDeath = true;
   private boolean stopOnEntityDeath = true;
   public double distance = 0.375;

   public ParticleTornadoEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.maxTornadoRadius = mlc.getPlaceholderFloat(new String[]{"maxradius", "mr"}, 3.0F);
      this.yOffset = mlc.getPlaceholderFloat(new String[]{"yoffset", "yo"}, 0.8F);
      this.tornadoHeight = mlc.getPlaceholderFloat(new String[]{"height", "h"}, 4.0F);
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "i"}, DurationUnit.TICKS, "4");
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "200");
      this.rotationSpeed = mlc.getPlaceholderFloat(new String[]{"rotationspeed", "rs"}, 0.04F);
      this.sliceHeight = mlc.getInteger(new String[]{"sliceheight", "sh"}, 64);
      this.particleSpeedRamp = mlc.getFloat(new String[]{"particlespeedramp", "psr"}, 0.0F);
      this.stopOnCasterDeath = mlc.getBoolean(new String[]{"stoponcasterdeath", "scd"}, true);
      this.stopOnEntityDeath = mlc.getBoolean(new String[]{"stoponentitydeath", "sed"}, true);
      this.strCloudParticle = mlc.getString(new String[]{"cloudparticle", "cp"}, "largeexplode");
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
         this.cloudParticle = BukkitParticle.get(this.strCloudParticle);
         if (this.cloudParticle.requiresData()) {
            this.cloudParticleData = this.cloudParticle.parseDataOptions(mlc);
         }
      }

      this.cloudSize = mlc.getPlaceholderFloat(new String[]{"cloudsize", "cs"}, 5.0F);
      this.cloudAmount = mlc.getPlaceholderInteger(new String[]{"cloudamount", "ca"}, 1);
      this.cloudHSpread = mlc.getPlaceholderFloat(new String[]{"cloudhspread", "chs"}, 1.0F);
      this.cloudVSpread = mlc.getPlaceholderFloat(new String[]{"cloudvspread", "cvs"}, 1.8F);
      this.cloudPSpeed = mlc.getPlaceholderFloat(new String[]{"cloudpspeed", "cps", "cs"}, 2.0F);
      this.cloudYOffset = mlc.getPlaceholderFloat(new String[]{"cloudyoffset", "cyo"}, 1.8F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new ParticleTornadoEffect.Animator(data, target, Math.max(1, this.interval.get(data).getTicks()), this.duration.get(data).getTicks());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new ParticleTornadoEffect.Animator(data, target, Math.max(1, this.interval.get(data, target).getTicks()), this.duration.get(data, target).getTicks());
      return SkillResult.SUCCESS;
   }

   public ArrayList<AbstractVector> createCircle(double y, double radius, double rotation) {
      double amount = radius * this.sliceHeight;
      double inc = (Math.PI * 2) / amount;
      double r = rotation % 360.0;
      ArrayList<AbstractVector> vecs = new ArrayList<>();

      for (int i = 0; i < amount; i++) {
         double angle = i * inc + r;
         double x = radius * Math.cos(angle);
         double z = radius * Math.sin(angle);
         AbstractVector v = new AbstractVector(x, y, z);
         vecs.add(v);
      }

      return vecs;
   }

   private class Animator implements Runnable {
      private SkillMetadata data;
      private AbstractEntity entity;
      private AbstractLocation location;
      private int interval;
      private int duration;
      private int iteration;
      private Task task;

      public Animator(SkillMetadata data, AbstractLocation location, int interval, int duration) {
         this(interval, duration);
         this.data = data;
         this.entity = null;
         this.location = location;
      }

      public Animator(SkillMetadata data, AbstractEntity entity, int interval, int duration) {
         this(interval, duration);
         this.data = data;
         this.entity = entity;
      }

      protected Animator(int interval, int duration) {
         this.interval = interval;
         this.duration = duration;
         this.iteration = 0;
         this.task = Schedulers.async().runRepeating(this, 0L, (long)interval);
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Started ParticleTornado effect");
      }

      protected AbstractLocation getLocation() {
         return this.entity != null ? this.entity.getLocation() : this.location.clone();
      }

      @Override
      public void run() {
         if (ParticleTornadoEffect.this.stopOnEntityDeath && this.entity != null && this.entity.isDead()) {
            this.task.terminate();
         } else if (ParticleTornadoEffect.this.stopOnCasterDeath && this.data.getCaster().getEntity().isDead()) {
            this.task.terminate();
         } else if (this.iteration * this.interval > this.duration) {
            this.task.terminate();
         } else {
            this.iteration++;
            float cloudYOffset = ParticleTornadoEffect.this.cloudYOffset.get(this.data);
            float cloudSize = ParticleTornadoEffect.this.cloudSize.get(this.data);
            float cloudPSpeed = ParticleTornadoEffect.this.cloudPSpeed.get(this.data);
            int cloudAmount = ParticleTornadoEffect.this.cloudAmount.get(this.data);
            float cloudHSpread = ParticleTornadoEffect.this.cloudHSpread.get(this.data);
            float cloudVSpread = ParticleTornadoEffect.this.cloudVSpread.get(this.data);
            float yOffset = ParticleTornadoEffect.this.yOffset.get(this.data);
            float maxTornadoRadius = ParticleTornadoEffect.this.maxTornadoRadius.get(this.data);
            float tornadoHeight = ParticleTornadoEffect.this.tornadoHeight.get(this.data);
            float rotationSpeed = ParticleTornadoEffect.this.rotationSpeed.get(this.data);
            AbstractLocation l = this.getLocation().add(0.0, cloudYOffset, 0.0);

            for (int i = 0; i < cloudSize; i++) {
               AbstractVector v = RandomUtil.getRandomCircleVector().multiply(RandomUtil.random.nextDouble() * cloudSize);
               if (ParticleTornadoEffect.this.showCloud) {
                  l.add(v);
                  Collection<AbstractPlayer> audience = SkillAudience.getEffective(ParticleTornadoEffect.this.audience, this.data, this.entity);
                  if (ParticleTornadoEffect.this.cloudParticleData == null) {
                     if (ParticleTornadoEffect.this.color != null) {
                        Chroma color = ParticleTornadoEffect.this.staticColor == null
                           ? Chroma.of(ParticleTornadoEffect.this.color.get(this.data))
                           : ParticleTornadoEffect.this.staticColor;
                        ParticleTornadoEffect.this.cloudParticle
                           .sendLegacyColored(audience, l, cloudPSpeed, cloudAmount, cloudHSpread, cloudVSpread, cloudHSpread, color);
                     } else {
                        ParticleTornadoEffect.this.cloudParticle.send(audience, l, cloudPSpeed, cloudAmount, cloudHSpread, cloudVSpread, cloudHSpread);
                     }
                  } else {
                     ParticleTornadoEffect.this.cloudParticle
                        .send(
                           audience,
                           l,
                           cloudPSpeed,
                           cloudAmount,
                           cloudHSpread,
                           cloudVSpread,
                           cloudHSpread,
                           ParticleTornadoEffect.this.cloudParticleData.apply(this.data),
                           ParticleTornadoEffect.this.exactOffsets
                        );
                  }

                  l.subtract(v);
               }
            }

            AbstractLocation t = l.clone().add(0.0, 0.2 + yOffset, 0.0);
            double r = 0.45 * (maxTornadoRadius * (2.35 / tornadoHeight));

            for (double y = 0.0; y < tornadoHeight; y += ParticleTornadoEffect.this.distance) {
               double fr = r * y;
               if (fr > maxTornadoRadius) {
                  fr = maxTornadoRadius;
               }

               for (AbstractVector v : ParticleTornadoEffect.this.createCircle(y, fr, (double)this.iteration * this.interval * rotationSpeed)) {
                  if (ParticleTornadoEffect.this.showTornado) {
                     l.add(v);
                     Collection<AbstractPlayer> audience = SkillAudience.getEffective(ParticleTornadoEffect.this.audience, this.data, this.entity);
                     if (ParticleTornadoEffect.this.particleData == null) {
                        if (ParticleTornadoEffect.this.color != null) {
                           Chroma color = ParticleTornadoEffect.this.staticColor == null
                              ? Chroma.of(ParticleTornadoEffect.this.color.get(this.data))
                              : ParticleTornadoEffect.this.staticColor;
                           ParticleTornadoEffect.this.particle
                              .sendLegacyColored(
                                 audience,
                                 l,
                                 ParticleTornadoEffect.this.pSpeed.get(this.data),
                                 ParticleTornadoEffect.this.amount.get(this.data),
                                 ParticleTornadoEffect.this.xSpread.get(this.data),
                                 ParticleTornadoEffect.this.vSpread.get(this.data),
                                 ParticleTornadoEffect.this.zSpread.get(this.data),
                                 color
                              );
                        } else {
                           ParticleTornadoEffect.this.particle
                              .send(
                                 audience,
                                 l,
                                 ParticleTornadoEffect.this.pSpeed.get(this.data),
                                 ParticleTornadoEffect.this.amount.get(this.data),
                                 ParticleTornadoEffect.this.xSpread.get(this.data),
                                 ParticleTornadoEffect.this.vSpread.get(this.data),
                                 ParticleTornadoEffect.this.zSpread.get(this.data)
                              );
                        }
                     } else {
                        ParticleTornadoEffect.this.particle
                           .send(
                              audience,
                              l,
                              ParticleTornadoEffect.this.pSpeed.get(this.data),
                              ParticleTornadoEffect.this.amount.get(this.data),
                              ParticleTornadoEffect.this.xSpread.get(this.data),
                              ParticleTornadoEffect.this.vSpread.get(this.data),
                              ParticleTornadoEffect.this.zSpread.get(this.data),
                              ParticleTornadoEffect.this.particleData.apply(this.data),
                              ParticleTornadoEffect.this.exactOffsets
                           );
                     }

                     t.subtract(v);
                  }
               }
            }

            l.subtract(0.0, yOffset, 0.0);
         }
      }
   }
}
