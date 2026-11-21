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
   name = "effect:particlelinehelix",
   aliases = {"particlelinehelix", "particlehelixline"}
)
public class ParticleLineHelixEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat distanceBetween;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat targetYOffset;
   protected boolean fromOrigin;
   protected PlaceholderDouble helixLength;
   protected PlaceholderDouble helixRadius;
   protected PlaceholderDouble helixRotation;
   protected PlaceholderDouble maxDistance;

   public ParticleLineHelixEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo", "ystartoffset", "ys"}, 0.0F);
      this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo", "ytargetoffset", "yt"}, 0.0F);
      this.distanceBetween = mlc.getPlaceholderFloat(new String[]{"distancebetween", "db"}, 1.0F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.helixLength = mlc.getPlaceholderDouble(new String[]{"helixlength", "hl"}, 2.0);
      this.helixRadius = mlc.getPlaceholderDouble(new String[]{"helixradius", "hr"}, 1.0);
      this.helixRotation = mlc.getPlaceholderDouble(new String[]{"helixrotation", "rot"}, 0.0);
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
      float startYOffset = this.startYOffset.get(data);
      float targetYOffset = this.targetYOffset.get(data);
      float distanceBetween = this.distanceBetween.get(data);
      double helixLength = this.helixLength.get(data);
      double helixRadius = this.helixRadius.get(data);
      double helixRotation = this.helixRotation.get(data);
      double maxDistance = this.maxDistance.get(data);
      AbstractLocation targetLocation = target.clone().add(0.0, targetYOffset, 0.0);
      AbstractLocation startLocation;
      if (this.fromOrigin) {
         startLocation = origin.add(0.0, startYOffset, 0.0);
      } else if (this.useEyeLocation) {
         startLocation = am.getEntity().getEyeLocation();
      } else {
         startLocation = am.getEntity().getLocation().add(0.0, startYOffset, 0.0);
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

      double distance = startLocation.distance(targetLocation);
      if (distance > maxDistance) {
         distance = maxDistance;
      }

      AbstractVector direction = targetLocation.toVector().subtract(startLocation.toVector()).normalize();
      double totalRevolutions = distance / helixLength;
      double particlesPerRevolution = helixLength / distanceBetween;
      int totalParticles = (int)Math.round(particlesPerRevolution * totalRevolutions);
      double step = distance / totalParticles;
      AbstractVector up;
      if (Math.abs(direction.getY()) < 0.99) {
         up = new AbstractVector(0, 1, 0);
      } else {
         up = new AbstractVector(1, 0, 0);
      }

      AbstractVector side = direction.crossProduct(up).normalize();
      double helixRotationRadians = Math.toRadians(helixRotation);

      for (int i = 0; i < totalParticles; i++) {
         double angle = (Math.PI * 2) * i / particlesPerRevolution + helixRotationRadians;
         double xOffset = helixRadius * Math.cos(angle);
         double zOffset = helixRadius * Math.sin(angle);
         AbstractVector offset = side.clone().multiply(xOffset).add(up.clone().multiply(zOffset));
         AbstractLocation particleLocation = startLocation.clone().add(direction.clone().multiply(step * i)).add(offset);
         this.playParticleEffect(data, particleLocation, audience);
      }
   }
}
