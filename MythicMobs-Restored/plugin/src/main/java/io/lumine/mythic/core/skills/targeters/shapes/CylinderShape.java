package io.lumine.mythic.core.skills.targeters.shapes;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class CylinderShape implements ITargeterShape {
   private final PlaceholderDouble radius;
   private final PlaceholderDouble height;
   private final PlaceholderDouble yOffset;

   public CylinderShape(MythicLineConfig config) {
      this.radius = PlaceholderDouble.of(config.getString(new String[]{"radius", "r"}, "5"));
      this.height = PlaceholderDouble.of(config.getString(new String[]{"height", "h"}, "5"));
      this.yOffset = PlaceholderDouble.of(config.getString(new String[]{"yOffset", "offsetY", "yo"}, "0"));
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin) {
      return this.getEntities(data, origin, null);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data, AbstractLocation origin, Predicate<AbstractEntity> extraFilter) {
      double r = this.radius.get(data);
      double h = this.height.get(data);
      double yOff = this.yOffset.get(data);
      double rSq = r * r;
      Collection<AbstractEntity> candidates = origin.getWorld().getEntitiesNearLocation(origin, r);
      Collection<AbstractEntity> result = new ArrayList<>();
      double ox = origin.getX();
      double oz = origin.getZ();
      double minY = origin.getY() + yOff;
      double maxY = minY + h;

      for (AbstractEntity e : candidates) {
         AbstractLocation loc = e.getLocation();
         double dx = loc.getX() - ox;
         double dz = loc.getZ() - oz;
         if (!(dx * dx + dz * dz > rSq)) {
            double y = loc.getY();
            if (!(y < minY) && !(y > maxY) && (extraFilter == null || extraFilter.test(e))) {
               result.add(e);
            }
         }
      }

      return result;
   }
}
