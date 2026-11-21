package io.lumine.mythic.core.skills.targeters.shapes;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class SphereShape implements ITargeterShape {
   private final PlaceholderDouble radius;
   private final boolean useBoundingBox;

   public SphereShape(MythicLineConfig config) {
      this.radius = PlaceholderDouble.of(config.getString(new String[]{"radius", "r"}, "5"));
      this.useBoundingBox = config.getBoolean(new String[]{"bb", "useboundingbox"}, false);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin) {
      return this.getEntities(data, origin, null);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin, Predicate<AbstractEntity> extraFilter) {
      double r = this.radius.get(data);
      double rSq = r * r;
      Collection<AbstractEntity> candidates = this.useBoundingBox
         ? origin.getWorld().getEntitiesNearLocation(origin, r)
         : MythicBukkit.inst()
            .getVolatileCodeHandler()
            .getWorldHandler()
            .getEntitiesNearLocation(origin, r, origin.getY() - r - 1.0, origin.getY() + r + 1.0, null);
      Collection<AbstractEntity> result = new ArrayList<>();

      for (AbstractEntity e : candidates) {
         double distanceSquared = this.useBoundingBox ? origin.distanceSquared(e) : origin.distanceSquared(e.getLocation());
         if (!(distanceSquared > rSq) && (extraFilter == null || extraFilter.test(e))) {
            result.add(e);
         }
      }

      return result;
   }
}
