package com.ticxo.modelengine.api.utils.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.joml.Vector3d;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Direction {
   NORTH(new Vector3d(0.0, 0.0, -1.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
   EAST(new Vector3d(1.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
   SOUTH(new Vector3d(0.0, 0.0, 1.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
   WEST(new Vector3d(-1.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
   UP(new Vector3d(0.0, 1.0, 0.0), new Vector3d(0.0, 0.0, -1.0).normalize()),
   DOWN(new Vector3d(0.0, -1.0, 0.0), new Vector3d(0.0, 0.0, 1.0).normalize());

   private final Vector3d normal;
   private final Vector3d uvUp;

   public Vector3d getNormal() {
      return new Vector3d(this.normal);
   }

   public Vector3d getUvUp() {
      return new Vector3d(this.uvUp);
   }

   public static Direction fromNormal(Vector3d normal) {
      for (Direction value : values()) {
         if (TMath.isSimilar(value.normal.dot(normal), 1.0)) {
            return value;
         }
      }

      return null;
   }
}
