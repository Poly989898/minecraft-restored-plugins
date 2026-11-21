package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "propel",
   description = "Propels the caster towards the target"
)
public class PropelMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderDouble velocity;

   public PropelMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v", "magnitude"}, 1.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.cast(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      double velocity = Math.abs(this.velocity.get(context));
      AbstractLocation cL = context.meta().getCaster().getEntity().getLocation();
      AbstractLocation target = context.location();
      if (cL.distance2DSquared(target) == 0.0) {
         target = target.add(cL.getDirection().normalize().multiply(2));
      }

      AbstractVector v = target.toVector().subtract(cL.toVector()).normalize();
      v = v.multiply(velocity);
      if (v.length() > 4.0) {
         v = v.normalize().multiply(4);
      }

      context.meta().getCaster().getEntity().setVelocity(v);
      return SkillResult.SUCCESS;
   }
}
