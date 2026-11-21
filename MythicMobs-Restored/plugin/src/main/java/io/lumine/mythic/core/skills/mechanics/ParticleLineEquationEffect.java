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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.math.MathExpressions;
import java.io.File;
import java.util.Collection;
import net.objecthunter.exp4j.Expression;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particlelineequation",
   aliases = {"particlelineequation", "particlelineeq"}
)
public class ParticleLineEquationEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat distanceBetween;
   protected PlaceholderFloat startYOffset;
   protected PlaceholderFloat targetYOffset;
   protected boolean fromOrigin;
   protected PlaceholderString equationX;
   protected PlaceholderString equationY;
   protected PlaceholderString equationZ;
   protected PlaceholderDouble maxDistance;
   protected final MathExpressions.TemplateMemo mathMemoX = new MathExpressions.TemplateMemo();
   protected final MathExpressions.TemplateMemo mathMemoY = new MathExpressions.TemplateMemo();
   protected final MathExpressions.TemplateMemo mathMemoZ = new MathExpressions.TemplateMemo();

   public ParticleLineEquationEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.startYOffset = mlc.getPlaceholderFloat(new String[]{"startyoffset", "syo", "ystartoffset", "ys"}, 0.0F);
      this.targetYOffset = mlc.getPlaceholderFloat(new String[]{"targetyoffset", "tyo", "ytargetoffset", "yt"}, 0.0F);
      this.distanceBetween = mlc.getPlaceholderFloat(new String[]{"distancebetween", "db"}, 1.0F);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.equationX = mlc.getPlaceholderString(new String[]{"equationx", "eqx"}, "0");
      this.equationY = mlc.getPlaceholderString(new String[]{"equationy", "eqy"}, "0");
      this.equationZ = mlc.getPlaceholderString(new String[]{"equationz", "eqz"}, "0");
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
      double distance = startLocation.distance(targetLocation);
      if (distance > maxDistance) {
         distance = maxDistance;
      }

      AbstractVector direction = targetLocation.toVector().subtract(startLocation.toVector()).normalize();
      int totalParticles = (int)Math.round(distance / this.distanceBetween.get(data));
      double step = distance / totalParticles;
      String equationX = this.equationX.get(data);
      Expression expressionX = this.mathMemoX.instantiate(equationX, "t", "distance").setVariable("distance", distance);
      String equationY = this.equationY.get(data);
      Expression expressionY = this.mathMemoY.instantiate(equationY, "t", "distance").setVariable("distance", distance);
      String equationZ = this.equationZ.get(data);
      Expression expressionZ = this.mathMemoZ.instantiate(equationZ, "t", "distance").setVariable("distance", distance);

      for (int i = 0; i < totalParticles; i++) {
         double t = step * i / distance;
         expressionX.setVariable("t", t);
         expressionY.setVariable("t", t);
         expressionZ.setVariable("t", t);
         double offsetX = expressionX.evaluate();
         double offsetY = expressionY.evaluate();
         double offsetZ = expressionZ.evaluate();
         AbstractLocation particleLocation = startLocation.clone().add(direction.clone().multiply(step * i)).add(offsetX, offsetY, offsetZ);
         this.playParticleEffect(data, particleLocation, audience);
      }
   }
}
