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
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.VectorUtils;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:atom",
   aliases = {"e:atom", "atom"}
)
public class ParticleAtomEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected String particleNucleus;
   protected String particleOrbital;
   protected BukkitParticle pNucleus;
   protected Function<PlaceholderMeta, Object> pNucleusData = null;
   protected PlaceholderFloat radiusN;
   protected PlaceholderInt radius;
   protected PlaceholderInt amountN;
   protected PlaceholderInt orbitals;
   protected PlaceholderInt rotation;
   protected int amountO;
   protected PlaceholderDuration interval;
   protected PlaceholderDuration iterations;
   protected double velocity = 1.0;
   protected double angularVelocity = 0.039269908169872414;

   public ParticleAtomEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.particleOrbital = mlc.getString("particleorbital", this.strParticle);
      this.particleOrbital = mlc.getString("particleo", this.particleOrbital);
      this.particleOrbital = mlc.getString("po", this.particleOrbital);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
         this.particle = BukkitParticle.get(this.particleOrbital);
         if (this.particle.requiresData()) {
            this.particleData = this.particle.parseDataOptions(mlc);
         }
      }

      this.particleNucleus = mlc.getString("particlenucleus", "reddust");
      this.particleNucleus = mlc.getString("particlen", this.particleNucleus);
      this.particleNucleus = mlc.getString("pn", this.particleNucleus);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
         this.pNucleus = BukkitParticle.get(this.particleNucleus);
         if (this.pNucleus.requiresData()) {
            this.pNucleusData = this.pNucleus.parseDataOptions(mlc);
         }
      }

      this.amountN = mlc.getPlaceholderInteger(new String[]{"amountnucleus", "amountn", "apn", "an"}, 50);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amountorbital", "amounto", "apo", "ao"}, 1);
      this.orbitals = mlc.getPlaceholderInteger(new String[]{"orbitals", "o"}, 2);
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 4);
      this.radiusN = mlc.getPlaceholderFloat(new String[]{"radiusnucleus", "rn"}, 1.0F);
      this.rotation = mlc.getPlaceholderInteger(new String[]{"rotation", "ro"}, 0);
      this.iterations = mlc.getPlaceholderDuration(new String[]{"ticks", "t"}, DurationUnit.TICKS, "1");
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "in"}, DurationUnit.TICKS, "10");
      this.velocity = mlc.getDouble(new String[]{"velocity", "v"}, 80.0);
      this.angularVelocity = Math.PI / this.velocity;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new ParticleAtomEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new ParticleAtomEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   protected void playNucleusParticleEffect(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      int amountN = this.amountN.get(data);
      if (this.pNucleusData == null) {
         if (this.color != null) {
            Chroma color = this.staticColor == null ? Chroma.of(this.color.get(data)) : this.staticColor;
            this.pNucleus
               .sendLegacyColored(
                  audience, target, this.pSpeed.get(data), amountN, this.xSpread.get(data), this.vSpread.get(data), this.zSpread.get(data), color
               );
         } else {
            this.pNucleus.send(audience, target, this.pSpeed.get(data), amountN, this.xSpread.get(data), this.vSpread.get(data), this.zSpread.get(data));
         }
      } else {
         this.pNucleus
            .send(
               audience,
               target,
               this.pSpeed.get(data),
               amountN,
               this.xSpread.get(data),
               this.vSpread.get(data),
               this.zSpread.get(data),
               this.pNucleusData.apply(data),
               this.exactOffsets
            );
      }
   }

   private class Animator implements Runnable {
      private SkillMetadata data;
      private Optional<AbstractEntity> entity = Optional.empty();
      private Optional<AbstractLocation> location = Optional.empty();
      private int iteration = 0;
      private int step = 0;
      private final int interval;
      private final int iterations;
      private Task task;

      public Animator(SkillMetadata data, AbstractEntity entity) {
         this.data = data;
         this.entity = Optional.of(entity);
         this.interval = Math.max(1, ParticleAtomEffect.this.interval.get(data, entity).getTicks());
         this.iterations = ParticleAtomEffect.this.iterations.get(data, entity).getTicks();
         this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
      }

      public Animator(SkillMetadata data, AbstractLocation location) {
         this.data = data;
         this.location = Optional.of(location);
         this.interval = Math.max(1, ParticleAtomEffect.this.interval.get(data).getTicks());
         this.iterations = ParticleAtomEffect.this.iterations.get(data).getTicks();
         this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
      }

      public AbstractLocation getLocation() {
         return this.entity.isPresent() ? this.entity.get().getLocation() : this.location.get();
      }

      @Override
      public void run() {
         AbstractLocation location = this.getLocation();
         Collection<AbstractPlayer> audience = SkillAudience.getEffective(ParticleAtomEffect.this.audience, this.data, this.entity.orElseGet(() -> null));
         int amountN = ParticleAtomEffect.this.amountN.get(this.data);
         int radius = ParticleAtomEffect.this.radius.get(this.data);
         float radiusN = ParticleAtomEffect.this.radiusN.get(this.data);
         int orbitals = ParticleAtomEffect.this.orbitals.get(this.data);
         int rotation = ParticleAtomEffect.this.rotation.get(this.data);

         for (int i = 0; i < amountN; i++) {
            AbstractVector v = RandomUtil.getRandomVector().multiply(radius * radiusN);
            location.add(v);
            ParticleAtomEffect.this.playNucleusParticleEffect(this.data, location, audience);
            location.subtract(v);
         }

         for (int i = 0; i < ParticleAtomEffect.this.amountO; i++) {
            double angle = this.step * ParticleAtomEffect.this.angularVelocity;

            for (int j = 0; j < orbitals; j++) {
               double xRotation = Math.PI / orbitals * j;
               AbstractVector v = new AbstractVector(Math.cos(angle), Math.sin(angle), 0.0).multiply(radius);
               VectorUtils.rotateAroundAxisX(v, xRotation);
               VectorUtils.rotateAroundAxisY(v, (double)rotation);
               location.add(v);
               ParticleAtomEffect.this.playParticleEffect(this.data, location, audience);
               location.subtract(v);
            }

            this.step++;
         }

         if (++this.iteration * this.interval >= this.iterations) {
            this.task.terminate();
         }
      }
   }
}
