package io.lumine.mythic.core.skills.targeters.shapes;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class SquareShape implements ITargeterShape {
   private final PlaceholderDouble radius;

   public SquareShape(MythicLineConfig config) {
      this.radius = PlaceholderDouble.of(config.getString(new String[]{"radius", "r"}, "5"));
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin) {
      return this.getEntities(data, origin, null);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin, Predicate<AbstractEntity> extraFilter) {
      double r = this.radius.get(data);
      Collection<AbstractEntity> candidates = origin.getWorld().getEntitiesNearLocation(origin, r);
      Collection<AbstractEntity> result = new ArrayList<>();
      double ox = origin.getX();
      double oz = origin.getZ();

      for (AbstractEntity e : candidates) {
         AbstractLocation loc = e.getLocation();
         double dx = Math.abs(loc.getX() - ox);
         double dz = Math.abs(loc.getZ() - oz);
         if (!(dx > r) && !(dz > r) && (extraFilter == null || extraFilter.test(e))) {
            result.add(e);
         }
      }

      return result;
   }
}
