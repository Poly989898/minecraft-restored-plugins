package io.lumine.mythic.bukkit.utils.numbers;

import javax.annotation.Nonnull;
import org.bukkit.util.Vector;

public class Planes {
   @Nonnull
   final Vector normal;
   @Nonnull
   final Vector point;
   final double distFromOrigin;

   private Planes() {
      this.normal = new Vector(0, 0, 0);
      this.point = new Vector(0, 0, 0);
      this.distFromOrigin = 0.0;
   }

   Planes(Vector a, Vector b, Vector c) {
      Vector ab = b.clone().subtract(a);
      Vector bc = c.clone().subtract(b);
      this.normal = ab.getCrossProduct(bc);
      this.point = a;
      this.distFromOrigin = this.normal.dot(this.point);
   }

   private boolean betweenPoints(Vector testPoint, Vector a, Vector b) {
      return a.distance(testPoint) + b.distance(testPoint) == a.distance(b);
   }

   public Vector getIntersectionPoint(Vector a, Vector b) {
      Vector ba = b.clone().subtract(a);
      double dotBA = this.normal.dot(ba);
      if (dotBA == 0.0) {
         return null;
      } else {
         double scalingFactor = (this.distFromOrigin - this.normal.dot(a)) / this.normal.dot(ba.normalize());
         ba.multiply(scalingFactor);
         Vector result = a.clone().add(ba);
         return this.betweenPoints(result, a, b) ? result : null;
      }
   }

   public static boolean isWithinArea(Vector point, Vector bottomLeft, Vector topRight) {
      return point.isInAABB(bottomLeft, topRight);
   }
}
