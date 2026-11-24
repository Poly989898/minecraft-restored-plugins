package io.lumine.mythic.lib.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Line3D {
   private final Vector direction;
   private final Vector point;

   public Line3D(Vector loc1, Vector loc2) {
      this.point = loc1.clone();
      this.direction = loc1.clone().subtract(loc2);
   }

   public Line3D(double a, double b, double c, double e, double f, double g) {
      this.point = new Vector(a, b, c);
      this.direction = new Vector(e, f, g);
   }

   public Line3D(Location point, Vector direction) {
      this.direction = direction.clone();
      this.point = point.toVector();
   }

   public double distanceSquared(Entity entity) {
      return this.distanceSquared(entity.getLocation().add(0.0, entity.getHeight() / 2.0, 0.0).toVector());
   }

   public double distanceSquared(Vector loc) {
      double a = this.direction.lengthSquared();
      double b = 2.0
         * (
            this.direction.getX() * (this.point.getX() - loc.getX())
               + this.direction.getY() * (this.point.getY() - loc.getY())
               + this.direction.getZ() * (this.point.getZ() - loc.getZ())
         );
      double min = -b / (2.0 * a);
      return loc.distanceSquared(this.getPoint(min));
   }

   public double distance(Vector loc) {
      return Math.sqrt(this.distanceSquared(loc));
   }

   public Vector getPoint(double t) {
      return this.point.clone().add(this.direction.clone().multiply(t));
   }
}
