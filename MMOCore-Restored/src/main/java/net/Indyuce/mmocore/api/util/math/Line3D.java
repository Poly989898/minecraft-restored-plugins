package net.Indyuce.mmocore.api.util.math;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Line3D {
   private final Vector dir;
   private final Vector pt;

   public Line3D(Vector from, Vector to) {
      this.pt = from.clone();
      this.dir = from.clone().subtract(to);
   }

   public Line3D(Location pt, Vector dir) {
      this.dir = dir.clone();
      this.pt = pt.toVector();
   }

   public Line3D(double a, double b, double c, double e, double f, double g) {
      this.pt = new Vector(a, b, c);
      this.dir = new Vector(e, f, g);
   }

   public double distanceSquared(Entity entity) {
      return this.distanceSquared(entity.getLocation().add(0.0, entity.getHeight() / 2.0, 0.0).toVector());
   }

   public double distanceSquared(Vector loc) {
      double a = this.dir.lengthSquared();
      double b = 2.0
         * (this.dir.getX() * (this.pt.getX() - loc.getX()) + this.dir.getY() * (this.pt.getY() - loc.getY()) + this.dir.getZ() * (this.pt.getZ() - loc.getZ()));
      double min = -b / (2.0 * a);
      return loc.distanceSquared(this.getPoint(min));
   }

   public double distance(Vector loc) {
      return Math.sqrt(this.distanceSquared(loc));
   }

   public Vector getPoint(double t) {
      return this.pt.clone().add(this.dir.clone().multiply(t));
   }
}
