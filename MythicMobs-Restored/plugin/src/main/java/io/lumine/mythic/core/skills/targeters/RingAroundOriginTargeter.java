package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.VectorUtils;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Phil",
   name = "ringAroundOrigin",
   aliases = {"ringOrigin", "RAO"},
   description = "Targets points in a ring around the skill origin"
)
public class RingAroundOriginTargeter extends ILocationSelector {
   private PlaceholderDouble radius;
   private PlaceholderInt pPoints;
   protected boolean rotate;
   protected boolean relative;
   protected PlaceholderDouble xRotation;
   protected PlaceholderDouble yRotation;
   protected PlaceholderDouble zRotation;
   protected PlaceholderDouble xOffset;
   protected PlaceholderDouble yOffset;
   protected PlaceholderDouble zOffset;

   public RingAroundOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = PlaceholderDouble.of(mlc.getString(new String[]{"radius", "r"}, "5"));
      this.pPoints = PlaceholderInt.of(mlc.getString(new String[]{"points", "p"}, "10"));
      this.xRotation = mlc.getPlaceholderDouble(new String[]{"rotationx", "rotx", "rx"}, 0.0);
      this.yRotation = mlc.getPlaceholderDouble(new String[]{"rotationy", "roty", "ry"}, 0.0);
      this.zRotation = mlc.getPlaceholderDouble(new String[]{"rotationz", "rotz", "rz"}, 0.0);
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"offsetx", "offx", "ox"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"offsety", "offy", "oy"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"offsetz", "offz", "oz"}, 0.0);
      this.relative = mlc.getBoolean(new String[]{"relative"}, false);
      this.rotate = !this.xRotation.isStaticallyEqualTo(0.0) || !this.yRotation.isStaticallyEqualTo(0.0) || !this.zRotation.isStaticallyEqualTo(0.0);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractLocation center = data.getOrigin();
      this.addRingPoints(data, center, targets);
      return targets;
   }

   protected Collection<AbstractLocation> addRingPoints(SkillMetadata data, AbstractLocation center, Collection<AbstractLocation> targets) {
      int points = this.pPoints.get(data);
      double radius = this.radius.get(data);
      if (points < 1) {
         return targets;
      } else {
         if (this.rotate) {
            double xRotation = this.xRotation.get(data);
            double yRotation = this.yRotation.get(data);
            double zRotation = this.zRotation.get(data);
            double xOffset = this.xOffset.get(data);
            double yOffset = this.yOffset.get(data);
            double zOffset = this.zOffset.get(data);
            AbstractLocation c = center.clone().add(xOffset, yOffset, zOffset);
            double inc = (Math.PI * 2) / points;

            for (int i = 0; i < points; i++) {
               double angle = i * inc;
               AbstractVector v = new AbstractVector(0, 0, 0);
               v.setX(Math.cos(angle) * radius);
               v.setZ(Math.sin(angle) * radius);
               v = VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
               if (this.relative) {
                  v = VectorUtils.rotateVector(v, data.getCaster().getEntity().getLocation().getYaw(), data.getCaster().getEntity().getLocation().getPitch());
               }

               targets.add(c.clone().add(v));
            }
         } else {
            double bx = center.getX();
            double y = center.getY();
            double bz = center.getZ();
            AbstractWorld w = center.getWorld();
            double inc = (Math.PI * 2) / points;

            for (int i = 0; i < points; i++) {
               double angle = i * inc;
               double x = bx + radius * Math.cos(angle);
               double z = bz + radius * Math.sin(angle);
               AbstractLocation l2 = MythicBukkit.inst().getBootstrap().newLocation(w, x, y, z);
               l2 = this.mutate(data, l2);
               targets.add(l2);
            }
         }

         return targets;
      }
   }
}
