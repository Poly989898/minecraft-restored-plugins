package team.unnamed.creative.base;

import java.util.Arrays;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

/** @deprecated */
@Deprecated
@ScheduledForRemoval(
   inVersion = "2.0.0"
)
public record Vector4Float(float x, float y, float x2, float y2) implements Iterable<Float> {
   public static final Vector4Float ZERO = new Vector4Float(0.0F, 0.0F, 0.0F, 0.0F);

   public float[] toArray() {
      return new float[]{this.x, this.y, this.x2, this.y2};
   }

   public Vector4Float multiply(float scalar) {
      return new Vector4Float(this.x * scalar, this.y * scalar, this.x2 * scalar, this.y2 * scalar);
   }

   public @NotNull Iterator<Float> iterator() {
      return Arrays.asList(this.x, this.y, this.x2, this.y2).iterator();
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{x=" + this.x + ", y=" + this.y + ", x2=" + this.x2 + ", y2=" + this.y2 + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Vector4Float that = (Vector4Float)o;
         return this.x == that.x && this.y == that.y && this.x2 == that.x2 && this.y2 == that.y2;
      } else {
         return false;
      }
   }
}
