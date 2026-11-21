package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlelinering",
   aliases = {"particlelinering", "particleringline"}
)
public class ParticleLineRingEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat distanceBetween;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat targetYOffset;
   protected boolean fromOrigin;
   protected int ringPoints;
   protected PlaceholderDouble ringRadius;
   protected PlaceholderDouble maxDistance;

   public ParticleLineRingEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo", "ystartoffset", "ys"}, 0.0F);
      this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo", "ytargetoffset", "yt"}, 0.0F);
      this.distanceBetween = mlc.getPlaceholderFloat(new String[]{"distancebetween", "db"}, 1.0F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.ringPoints = mlc.getInteger(new String[]{"ringpoints", "rp"}, 16);
      this.ringRadius = mlc.getPlaceholderDouble(new String[]{"ringradius", "rr"}, 0.5);
      this.maxDistance = mlc.getPlaceholderDouble(new String[]{"maxdistance", "md"}, 256.0);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      this.playParticleLineEffect(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playParticleLineEffect(data, target.getLocation(), audience);
      return SkillResult.SUCCESS;
   }

   protected void playParticleLineEffect(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      SkillCaster am = data.getCaster();
      AbstractLocation origin = data.getOrigin();
      AbstractLocation targetLocation = target.clone().add(0.0, this.targetYOffset.get(data), 0.0);
      AbstractLocation startLocation;
      if (this.fromOrigin) {
         startLocation = origin.add(0.0, this.startYOffset.get(data), 0.0);
      } else if (this.useEyeLocation) {
         startLocation = am.getEntity().getEyeLocation();
      } else {
         startLocation = am.getEntity().getLocation().add(0.0, this.startYOffset.get(data), 0.0);
      }

      if (this.setYaw) {
         startLocation.setYaw(this.yaw);
      }

      if (this.setPitch) {
         startLocation.setPitch(this.pitch);
      }

      if (this.startForwardOffset != 0.0F) {
         startLocation = MythicUtil.move(this.faulty, startLocation, this.startForwardOffset, 0.0, 0.0);
      }

      if (this.startSideOffset.get(data) != 0.0F) {
         startLocation = MythicUtil.move(this.faulty, startLocation, 0.0, 0.0, this.startSideOffset.get(data));
      }

      double maxDist = this.maxDistance.get(data);
      float distBetween = this.distanceBetween.get(data);
      double distance = startLocation.distance(targetLocation);
      if (distance > maxDist) {
         distance = maxDist;
      }

      int c = (int)Math.ceil(distance / distBetween) - 1;
      if (c > 0) {
         AbstractVector direction = targetLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(distBetween);
         AbstractLocation l2 = startLocation.clone().add(0.0, this.yOffset.get(data), 0.0);
         double radius = this.ringRadius.get(data);
         int step = 0;

         for (int i = 0; i < c; i++) {
            l2.add(direction);
            AbstractVector up;
            if (Math.abs(direction.getY()) < 0.99) {
               up = new AbstractVector(0, 1, 0);
            } else {
               up = new AbstractVector(1, 0, 0);
            }

            AbstractVector side = direction.crossProduct(up).normalize();
            up = direction.crossProduct(side).normalize();

            for (int j = 0; j < c; j++) {
               double angle = (Math.PI * 2) * j / c;
               double xOffset = radius * Math.cos(angle);
               double zOffset = radius * Math.sin(angle);
               AbstractVector offset = side.clone().multiply(xOffset).add(up.clone().multiply(zOffset));
               AbstractLocation particleLocation = l2.clone().add(offset);
               this.playParticleEffect(data, particleLocation, audience);
            }
         }
      }
   }
}
