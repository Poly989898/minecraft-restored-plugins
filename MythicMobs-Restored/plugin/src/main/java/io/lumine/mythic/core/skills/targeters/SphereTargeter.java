package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "sphere",
   aliases = {},
   description = "Targets points in a sphere around the caster"
)
public class SphereTargeter extends ILocationSelector {
   private PlaceholderDouble radius;
   private PlaceholderDouble yOffset;
   private PlaceholderInt points;
   private boolean exact;

   public SphereTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 2.0);
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 32);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "y"}, 0.0);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractLocation target = am.getLocation();
      AbstractLocation location = target.clone();
      location.add(0.0, this.yOffset.get(data), 0.0);
      if (this.exact) {
         targets = this.generateExactSphere(location, this.radius.get(data), this.points.get(data));
      } else {
         for (int i = 0; i < this.points.get(data); i++) {
            AbstractVector vector = RandomUtil.getRandomVector().multiply(this.radius.get(data));
            targets.add(location.clone().add(vector));
         }
      }

      return targets;
   }

   private Collection<AbstractLocation> generateExactSphere(AbstractLocation center, double radius, int points) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      double increment = Math.PI * (3.0 - Math.sqrt(5.0));

      for (int i = 0; i < points; i++) {
         double y = 1.0 - (double)i / (points - 1) * 2.0;
         double radiusAtY = Math.sqrt(1.0 - y * y);
         double theta = i * increment;
         double x = Math.cos(theta) * radiusAtY;
         double z = Math.sin(theta) * radiusAtY;
         targets.add(center.clone().add(x * radius, y * radius, z * radius));
      }

      return targets;
   }
}
