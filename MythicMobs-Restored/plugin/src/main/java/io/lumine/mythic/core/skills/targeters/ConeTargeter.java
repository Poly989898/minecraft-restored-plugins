package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.RandomUtil;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "cone",
   aliases = {},
   description = "Targets random points in a cone in front of the caster"
)
public class ConeTargeter extends ILocationSelector {
   protected PlaceholderAngle angle;
   protected double range;
   protected int points;
   protected int minPoints;
   protected int slices;
   protected double rotation;
   protected double yOffset;
   protected boolean exact;
   protected int lines = 4;
   protected double cos;
   protected double cosSq;

   public ConeTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.angle = mlc.getPlaceholderAngle(new String[]{"angle", "a"}, AngleUnit.DEGREES, "90d");
      this.range = mlc.getDouble(new String[]{"range", "r"}, 16.0);
      this.points = mlc.getInteger(new String[]{"points", "p"}, 144);
      this.minPoints = mlc.getInteger(new String[]{"minpoints", "mp"}, 1);
      this.slices = mlc.getInteger(new String[]{"slices", "s"}, Numbers.floor(this.range));
      this.rotation = mlc.getDouble(new String[]{"rotation", "rot"}, 0.0);
      this.yOffset = mlc.getDouble(new String[]{"yoffset", "yo", "y"}, 0.0);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = new ArrayList<>();
      AbstractLocation sourceLocation = data.getCaster().getLocation().add(0.0, this.yOffset, 0.0);
      AbstractVector dir = sourceLocation.getDirection();
      if (this.rotation > 0.0) {
         dir.rotate((float)this.rotation);
      }

      dir.setY(0);
      double angle = this.angle.get(data).getDegrees();
      double cosSq = Math.pow(Math.cos(angle * Math.PI / 180.0), 2.0);
      if (this.exact) {
         targets.addAll(this.generateExactConeCircles(sourceLocation, dir, angle));
      } else {
         for (int i = 0; i < this.points; i++) {
            AbstractVector vector = RandomUtil.getRandomVector().multiply(Numbers.randomDouble() * this.range);
            AbstractLocation location = sourceLocation.clone().add(vector);
            AbstractVector relative = location.clone().subtract(sourceLocation).toVector().setY(0);
            double dot = relative.getX() * dir.getX() + relative.getY() * dir.getY() + relative.getZ() * dir.getZ();
            double value = dot * dot / relative.lengthSquared();
            if (angle < 180.0 && dot > 0.0 && value >= cosSq) {
               targets.add(location);
            } else if (angle >= 180.0 && (dot > 0.0 || dot <= cosSq)) {
               targets.add(location);
            }
         }
      }

      return targets;
   }

   private Collection<AbstractLocation> generateExactConeCircles(AbstractLocation center, AbstractVector direction, double angle) {
      HashSet<AbstractLocation> outlinePoints = new HashSet<>();

      for (int i = 0; i < this.slices; i++) {
         double fraction = (double)i / (this.slices - 1);
         double currentRange = this.range * fraction;
         double currentAngle = Math.toRadians(angle) * fraction;
         int currentPoints = this.minPoints + (int)((this.points - this.minPoints) * fraction);

         for (int j = 0; j < currentPoints; j++) {
            double angleIncrement = (Math.PI * 2) / currentPoints;
            double x = currentRange * Math.cos(j * angleIncrement) * Math.sin(currentAngle);
            double z = currentRange * Math.sin(j * angleIncrement) * Math.sin(currentAngle);
            double y = currentRange * Math.cos(currentAngle);
            AbstractVector circlePoint = new AbstractVector(x, y, z);
            circlePoint.rotate(direction);
            outlinePoints.add(center.clone().add(circlePoint));
         }
      }

      return outlinePoints;
   }
}
