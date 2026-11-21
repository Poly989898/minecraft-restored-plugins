package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Pineapple",
   name = "entitiesInRing",
   aliases = {"EIRR"},
   description = "Targets all entities in a ring"
)
public class EntitiesInRingTargeter extends IEntitySelector {
   protected PlaceholderDouble min;
   protected PlaceholderDouble max;

   public EntitiesInRingTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.min = mlc.getPlaceholderDouble(new String[]{"min", "minrange"}, 5.0);
      this.max = mlc.getPlaceholderDouble(new String[]{"max", "maxrange"}, 10.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      return this.getEntitiesInRing(data, data.getCaster().getLocation());
   }

   protected Collection<AbstractEntity> getEntitiesInRing(SkillMetadata data, AbstractLocation origin) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      double max = Math.pow(this.max.get(data), 2.0);
      double min = Math.pow(this.min.get(data), 2.0);

      for (AbstractEntity e : origin.getWorld().getLivingEntities()) {
         if (origin.getWorld().equals(e.getWorld())) {
            double dist = this.distanceSquared(origin, e);
            if (dist < max && dist > min) {
               targets.add(e);
            }
         }
      }

      return targets;
   }
}
