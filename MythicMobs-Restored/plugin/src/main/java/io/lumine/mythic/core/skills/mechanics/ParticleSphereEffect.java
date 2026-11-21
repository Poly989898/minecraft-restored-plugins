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
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.ParticleMaker;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlesphere",
   aliases = {"particlesphere", "e:ps", "ps"}
)
public class ParticleSphereEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   private PlaceholderInt points;
   private PlaceholderFloat radius;

   public ParticleSphereEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.points = this.amount;
      this.amount = PlaceholderInt.of("1");
      this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 0.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      this.playParticleSphereEffect(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playParticleSphereEffect(data, target.getLocation(), audience);
      return SkillResult.SUCCESS;
   }

   protected void playParticleSphereEffect(SkillMetadata data, AbstractLocation t, Collection<AbstractPlayer> audience) {
      AbstractLocation target = t;
      AbstractLocation location = t.clone();
      if (this.setYaw) {
         location.setYaw(this.yaw);
      }

      if (this.setPitch) {
         location.setPitch(this.pitch);
      }

      int points = this.points.get(data);

      for (int i = 0; i < points; i++) {
         AbstractVector vector = RandomUtil.getRandomVector().multiply(this.radius.get(data));
         location.add(vector);
         if (this.directional) {
            this.playDirectionalParticleEffect(data, target, target, location, audience);
         } else {
            this.playParticleEffect(data, location, audience);
         }

         location.subtract(vector);
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
               .sendDirectional(audience, origin, this.pSpeed.get(data), 1, this.xSpread.get(data), this.vSpread.get(data), this.zSpread.get(data), direction);
         }
      } else {
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
