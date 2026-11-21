package team.unnamed.creative.model;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;

public record ElementRotation(Vector3Float origin, Vector3Float rotation, boolean rescale) {
   public static final boolean DEFAULT_RESCALE = false;

   public ElementRotation(Vector3Float origin, Vector3Float rotation, boolean rescale) {
      this.origin = (Vector3Float)Objects.requireNonNull(origin, "origin");
      this.rotation = (Vector3Float)Objects.requireNonNull(rotation, "rotation");
      this.rescale = rescale;
   }

   public boolean containsLegacyRotation(boolean writeLegacy, int minPackFormat) {
      float x = this.rotation.x();
      float y = this.rotation.y();
      float z = this.rotation.z();
      float min = -45.0F;
      float max = 45.0F;
      float step = 22.5F;
      float epsilon = 1.0E-4F;
      if (Math.abs(x) < epsilon && Math.abs(y) < epsilon && Math.abs(z) < epsilon) {
         return writeLegacy;
      } else if (!(x < min) && !(x > max)) {
         if (!(y < min) && !(y > max)) {
            if (!(z < min) && !(z > max)) {
               int nonZeroCount = 0;
               if (Math.abs(x) > epsilon) {
                  ++nonZeroCount;
               }

               if (Math.abs(y) > epsilon) {
                  ++nonZeroCount;
               }

               if (Math.abs(z) > epsilon) {
                  ++nonZeroCount;
               }

               if (nonZeroCount > 1) {
                  return false;
               } else {
                  boolean isSemiLegacy = minPackFormat >= 63 && minPackFormat < 75;
                  if (!this.isMultipleOfStep(x, step, epsilon)) {
                     return isSemiLegacy;
                  } else if (!this.isMultipleOfStep(y, step, epsilon)) {
                     return isSemiLegacy;
                  } else {
                     return !this.isMultipleOfStep(z, step, epsilon) ? isSemiLegacy : writeLegacy;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isMultipleOfStep(float value, float step, float epsilon) {
      float quotient = value / step;
      return Math.abs(quotient - (float)Math.round(quotient)) < epsilon;
   }

   /** @deprecated */
   @Deprecated
   public Axis3D axis() {
      if (this.rotation.x() != 0.0F) {
         return Axis3D.X;
      } else if (this.rotation.y() != 0.0F) {
         return Axis3D.Y;
      } else {
         return this.rotation.z() != 0.0F ? Axis3D.Z : Axis3D.Y;
      }
   }

   /** @deprecated */
   @Deprecated
   public float angle() {
      if (this.rotation.x() != 0.0F) {
         return this.rotation.x();
      } else {
         return this.rotation.y() != 0.0F ? this.rotation.y() : this.rotation.z();
      }
   }

   public ElementRotation origin(Vector3Float origin) {
      return this.origin == origin ? this : new ElementRotation(origin, this.rotation, this.rescale);
   }

   public ElementRotation rescale(boolean rescale) {
      return this.rescale == rescale ? this : new ElementRotation(this.origin, this.rotation, rescale);
   }

   public ElementRotation rotation(Vector3Float rotation) {
      return this.rotation == rotation ? this : new ElementRotation(this.origin, rotation, this.rescale);
   }

   public @NotNull Builder toBuilder() {
      return builder().origin(this.origin).rotation(this.rotation).rescale(this.rescale);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{origin=" + String.valueOf(this.origin) + ", rotation=" + String.valueOf(this.rotation) + ", rescale=" + this.rescale + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ElementRotation that = (ElementRotation)o;
         return this.rescale == that.rescale && this.rotation.equals(that.rotation) && this.origin.equals(that.origin);
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public static ElementRotation of(Vector3Float origin, Axis3D axis, float angle, boolean rescale) {
      Vector3Float rotation = Vector3Float.ZERO;
      if (axis == Axis3D.Y) {
         rotation = new Vector3Float(0.0F, angle, 0.0F);
      } else if (axis == Axis3D.X) {
         rotation = new Vector3Float(angle, 0.0F, 0.0F);
      } else if (axis == Axis3D.Z) {
         rotation = new Vector3Float(0.0F, 0.0F, angle);
      }

      return new ElementRotation(origin, rotation, rescale);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Vector3Float origin;
      private Vector3Float rotation;
      private boolean rescale = false;

      private Builder() {
      }

      public Builder origin(Vector3Float origin) {
         this.origin = (Vector3Float)Objects.requireNonNull(origin, "origin");
         return this;
      }

      public Builder rotation(Vector3Float rotation) {
         this.rotation = (Vector3Float)Objects.requireNonNull(rotation, "rotation");
         return this;
      }

      public Builder rescale(boolean rescale) {
         this.rescale = rescale;
         return this;
      }

      public ElementRotation build() {
         return new ElementRotation(this.origin, this.rotation, this.rescale);
      }
   }
}
