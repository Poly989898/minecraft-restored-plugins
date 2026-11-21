package com.ticxo.modelengine.api.utils.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.joml.Vector3d;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Axis {
   X(new Vector3d(1.0, 0.0, 0.0)),
   Y(new Vector3d(0.0, 1.0, 0.0)),
   Z(new Vector3d(0.0, 0.0, 1.0));

   private final Vector3d vector;

   public Vector3d getVector() {
      return this.vector.get(new Vector3d());
   }
}
