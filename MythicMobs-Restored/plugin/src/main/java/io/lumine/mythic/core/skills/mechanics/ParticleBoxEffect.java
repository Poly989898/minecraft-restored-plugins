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
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlebox",
   aliases = {"particlebox", "e:pb", "pb"}
)
public class ParticleBoxEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   PlaceholderFloat radius;
   private PlaceholderInt points;

   public ParticleBoxEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.points = this.amount;
      this.amount = PlaceholderInt.of("1");
      this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 5.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      this.playParticleBoxEffect(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playParticleBoxEffect(data, target.getLocation(), audience);
      return SkillResult.SUCCESS;
   }

   protected void playParticleBoxEffect(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      AbstractLocation location = target.clone();
      location.add(0.0, this.yOffset.get(data), 0.0);
      int points = this.points.get(data);
      float radius = this.radius.get(data);

      for (int i = 0; i < points; i++) {
         double u = RandomUtil.random.nextDouble() * 2.0 * radius - radius;
         double v = RandomUtil.random.nextDouble() * 2.0 * radius - radius;
         double edge = RandomUtil.random.nextBoolean() ? radius : -radius;

         AbstractVector vector = switch (RandomUtil.random.nextInt(3)) {
            case 0 -> new AbstractVector(edge, u, v);
            case 1 -> new AbstractVector(u, edge, v);
            default -> new AbstractVector(u, v, edge);
         };
         location.add(vector);
         this.playParticleEffect(data, location, audience);
         location.subtract(vector);
      }
   }
}
