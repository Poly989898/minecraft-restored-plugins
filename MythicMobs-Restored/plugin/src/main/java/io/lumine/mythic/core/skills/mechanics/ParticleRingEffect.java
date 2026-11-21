package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.ParticleMaker;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlering",
   aliases = {"particlering", "e:pr", "pr"}
)
public class ParticleRingEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   private PlaceholderFloat radius;
   private PlaceholderInt points;

   public ParticleRingEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "pts"}, 8);
      this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 10.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      if (this.fromOrigin) {
         this.playParticleRingEffect(data, data.getOrigin(), audience);
      } else {
         this.playParticleRingEffect(data, target, audience);
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playParticleRingEffect(data, target.getLocation(), audience);
      return SkillResult.SUCCESS;
   }

   protected void playParticleRingEffect(SkillMetadata data, AbstractLocation t, Collection<AbstractPlayer> audience) {
      AbstractLocation target = t;
      if (t != null) {
         double bx = t.getX();
         double y = t.getY() + this.yOffset.get(data);
         double bz = t.getZ();
         AbstractWorld w = t.getWorld();
         float inc = 360.0F / this.points.get(data);

         for (double i = 0.0; i < 360.0; i += inc) {
            double angle = i * Math.PI / 180.0;
            double x = bx + this.radius.get(data) * Math.cos(angle);
            double z = bz + this.radius.get(data) * Math.sin(angle);
            AbstractLocation l = new AbstractLocation(w, x, y, z);
            if (this.directional) {
               this.playDirectionalParticleEffect(data, l, target, l, audience);
            } else {
               this.playParticleEffect(data, l, audience);
            }
         }
      }
   }

   protected void playDirectionalParticleEffect(
      SkillMetadata data, AbstractLocation origin, AbstractLocation target, AbstractLocation spawn, Collection<AbstractPlayer> audience
   ) {
      AbstractVector direction;
      if (this.directionReversed) {
         direction = origin.toVector().subtract(target.clone().toVector()).normalize();
      } else {
         direction = target.toVector().subtract(origin.clone().toVector()).normalize();
      }

      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
         if (this.particleData == null) {
            this.particle
               .sendDirectional(
                  audience,
                  origin,
                  this.pSpeed.get(data),
                  this.amount.get(data),
                  this.xSpread.get(data),
                  this.vSpread.get(data),
                  this.zSpread.get(data),
                  direction
               );
         }
      } else {
         int amount = this.amount.get(data);

         for (int i = 0; i < amount; i++) {
            AbstractLocation ln = spawn.clone()
               .add(
                  0.0F - this.xSpread.get(data) + Numbers.randomDouble() * this.xSpread.get(data) * 2.0,
                  this.vSpread.get(data) + Numbers.randomDouble() * this.vSpread.get(data) * 2.0,
                  0.0F - this.zSpread.get(data) + Numbers.randomDouble() * this.zSpread.get(data) * 2.0
               );
            new ParticleMaker.ParticlePacket(this.strParticle, direction, this.pSpeed.get(data), 1, true).send(ln, this.viewDistance);
         }
      }
   }
}
