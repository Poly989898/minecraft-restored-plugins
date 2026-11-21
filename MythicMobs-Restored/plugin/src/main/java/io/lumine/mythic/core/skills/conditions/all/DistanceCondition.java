package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "distance",
   aliases = {},
   description = "Whether the distance between the caster and target is within the given range"
)
public class DistanceCondition
   extends SkillCondition
   implements ISkillMetaComparisonCondition,
   ISkillMetaLocationComparisonCondition,
   IEntityComparisonCondition,
   ILocationComparisonCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The distance to match"
   )
   protected RangedDouble distance;
   private PlaceholderString strDistance;

   public DistanceCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.strDistance = mlc.getPlaceholderString(new String[]{"distance", "d"}, this.conditionVar);
      if (this.strDistance.isStatic()) {
         this.distance = new RangedDouble(this.strDistance.get(), true);
      } else {
         this.distance = null;
      }
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      return this.check(meta, target.getLocation());
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractLocation target) {
      AbstractEntity entity = meta.getCaster().getEntity();
      if (!entity.getWorld().equals(target.getWorld())) {
         return false;
      } else {
         double diffSq = (float)entity.getLocation().distanceSquared(target);
         if (this.distance == null) {
            RangedDouble distance = new RangedDouble(this.strDistance.get(meta, target), true);
            return distance.equals(diffSq);
         } else {
            return this.distance.equals(diffSq);
         }
      }
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (!entity.getWorld().equals(target.getWorld())) {
         return false;
      } else {
         double diffSq = (float)entity.getLocation().distanceSquared(target.getLocation());
         if (this.distance == null) {
            RangedDouble distance = new RangedDouble(this.strDistance.get(), true);
            return distance.equals(diffSq);
         } else {
            return this.distance.equals(diffSq);
         }
      }
   }

   @Override
   public boolean check(AbstractLocation location, AbstractLocation target) {
      if (target == null) {
         return false;
      } else if (!location.getWorld().equals(target.getWorld())) {
         return false;
      } else {
         double diffSq = (float)location.distanceSquared(target);
         if (this.distance == null) {
            RangedDouble distance = new RangedDouble(this.strDistance.get(), true);
            return distance.equals(diffSq);
         } else {
            return this.distance.equals(diffSq);
         }
      }
   }
}
