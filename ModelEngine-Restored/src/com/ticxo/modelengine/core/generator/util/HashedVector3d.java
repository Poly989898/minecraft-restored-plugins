package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.math.Axis;
import java.util.Objects;
import org.joml.Vector3d;

public class HashedVector3d extends Vector3d {
   private static final int PRECISION = 10000;
   private final Axis axis;

   public HashedVector3d(Axis axis, double x, double y, double z) {
      super(x, y, z);
      this.axis = axis;
   }

   public HashedVector3d() {
      this.axis = Axis.X;
   }

   public int hashCode() {
      return Objects.hash(this.axis, (int)Math.round(this.x * 10000.0), (int)Math.round(this.y * 10000.0), (int)Math.round(this.z * 10000.0));
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         return this.getClass() != obj.getClass() ? false : this.hashCode() == obj.hashCode();
      }
   }
}
