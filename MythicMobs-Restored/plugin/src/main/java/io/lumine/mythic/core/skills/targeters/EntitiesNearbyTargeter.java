package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.shapes.ITargeterShape;
import io.lumine.mythic.core.skills.targeters.shapes.TargeterShapes;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.function.Predicate;

@MythicTargeter(
   author = "Ashijin",
   name = "EntitiesNearby",
   aliases = {"NearbyEntities", "entitiesInRadius", "livingEntitiesInRadius", "livingInRadius", "allInRadius", "EIR"},
   description = "Targets entities around the caster"
)
public class EntitiesNearbyTargeter extends IEntitySelector {
   protected ITargeterShape shape;
   protected Predicate<AbstractEntity> livingOnly;

   public EntitiesNearbyTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      String shapeName = mlc.getString(new String[]{"shape"}, "SPHERE");
      this.shape = TargeterShapes.from(shapeName).create(mlc);
      boolean livingOnly = mlc.getBoolean(new String[]{"livingonly", "living", "l"}, true);
      if (livingOnly) {
         this.livingOnly = AbstractEntity::isLiving;
      } else {
         this.livingOnly = ae -> true;
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      return this.getEntitiesNearPoint(data, this.livingOnly, data.getCaster().getLocation());
   }

   protected Collection<AbstractEntity> getEntitiesNearPoint(SkillMetadata data, AbstractLocation location) {
      return this.getEntitiesNearPoint(data, null, location);
   }

   protected Collection<AbstractEntity> getEntitiesNearPoint(SkillMetadata data, Predicate<AbstractEntity> filter, AbstractLocation location) {
      return this.shape.getEntities(data, location, this.livingOnly);
   }
}
