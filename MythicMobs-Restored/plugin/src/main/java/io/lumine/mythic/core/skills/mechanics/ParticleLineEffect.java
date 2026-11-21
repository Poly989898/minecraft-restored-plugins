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
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particleline",
   aliases = {"e:pl", "pl", "particleline"}
)
public class ParticleLineEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat distanceBetween;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat targetYOffset;
   protected boolean fromOrigin;
   protected boolean zigZag;
   protected PlaceholderInt zigZags;
   protected PlaceholderDouble zigZagOffset;
   protected PlaceholderDouble maxDistance;

   public ParticleLineEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo", "ystartoffset", "ys"}, 0.0F);
      this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo", "ytargetoffset", "yt"}, 0.0F);
      this.distanceBetween = mlc.getPlaceholderFloat(new String[]{"distancebetween", "db"}, 0.25F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.zigZag = mlc.getBoolean(new String[]{"zigzag", "zz"}, false);
      this.zigZags = mlc.getPlaceholderInteger(new String[]{"zigzags", "zzs"}, 10);
      this.zigZagOffset = mlc.getPlaceholderDouble(new String[]{"zigzagoffset", "zzo"}, 0.2);
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

      double maxDistance = this.maxDistance.get(data);
      float distanceBetween = this.distanceBetween.get(data);
      double distance = startLocation.distance(targetLocation);
      if (distance > maxDistance) {
         distance = maxDistance;
      }

      int c = (int)Math.ceil(distance / distanceBetween) - 1;
      if (c > 0) {
         AbstractVector v = targetLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(distanceBetween);
         AbstractLocation l2 = startLocation.clone().add(0.0, this.yOffset.get(data), 0.0);
         boolean zag = false;
         AbstractVector zigZagOffset = null;
         if (this.zigZag) {
            double zzo = this.zigZagOffset.get(data);
            double oX = -(zzo / 2.0) + zzo * Numbers.randomDouble();
            double oY = -(zzo / 2.0) + zzo * Numbers.randomDouble();
            double oZ = -(zzo / 2.0) + zzo * Numbers.randomDouble();
            zigZagOffset = new AbstractVector(oX, oY, oZ);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drawing ParticleLine from {0} to {1}", startLocation, targetLocation);
         int step = 0;
         int amount = c / this.zigZags.get(data);

         for (int i = 0; i < c; i++) {
            if (this.zigZag) {
               if (zag) {
                  l2.add(zigZagOffset);
               } else {
                  l2.subtract(zigZagOffset);
               }

               if (step >= amount) {
                  if (zag) {
                     zag = false;
                  } else {
                     zag = true;
                  }

                  step = 0;
               }

               step++;
            }

            l2.add(v);
            if (this.directional) {
               this.playDirectionalParticleEffect(data, startLocation, targetLocation, l2, audience);
            } else {
               this.playParticleEffect(data, l2, audience);
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
   }
}
