package team.unnamed.creative.item;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;

public record Transformation(Vector3Float translation, Vector3Float scale, QuaternionFloat leftRotation, QuaternionFloat rightRotation) {
   public static final Transformation DEFAULT;

   public Transformation(final @NotNull Vector3Float translation, final @NotNull Vector3Float scale, final @NotNull QuaternionFloat leftRotation, final @NotNull QuaternionFloat rightRotation) {
      this.translation = (Vector3Float)Objects.requireNonNull(translation, "translation");
      this.scale = (Vector3Float)Objects.requireNonNull(scale, "scale");
      this.leftRotation = (QuaternionFloat)Objects.requireNonNull(leftRotation, "leftRotation");
      this.rightRotation = (QuaternionFloat)Objects.requireNonNull(rightRotation, "rightRotation");
   }

   public @NotNull Vector3Float translation() {
      return this.translation;
   }

   public @NotNull Transformation translation(Vector3Float translation) {
      return this.translation == translation ? this : new Transformation(translation, this.scale, this.leftRotation, this.rightRotation);
   }

   public @NotNull Vector3Float scale() {
      return this.scale;
   }

   public @NotNull Transformation scale(Vector3Float scale) {
      return this.scale == scale ? this : new Transformation(this.translation, scale, this.leftRotation, this.rightRotation);
   }

   public @NotNull QuaternionFloat leftRotation() {
      return this.leftRotation;
   }

   public @NotNull Transformation leftRotation(QuaternionFloat leftRotation) {
      return this.leftRotation == leftRotation ? this : new Transformation(this.translation, this.scale, leftRotation, this.rightRotation);
   }

   public @NotNull QuaternionFloat rightRotation() {
      return this.rightRotation;
   }

   public @NotNull Transformation rightRotation(QuaternionFloat rightRotation) {
      return this.rightRotation == rightRotation ? this : new Transformation(this.translation, this.scale, this.leftRotation, rightRotation);
   }

   public @NotNull Builder toBuilder() {
      return builder().translation(this.translation).scale(this.scale).leftRotation(this.leftRotation).rightRotation(this.rightRotation);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{translation=" + String.valueOf(this.translation) + ", scale=" + String.valueOf(this.scale) + ", leftRotation=" + String.valueOf(this.leftRotation) + ", rightRotation=" + String.valueOf(this.rightRotation) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Transformation that = (Transformation)o;
         return this.translation.equals(that.translation) && this.scale.equals(that.scale) && this.leftRotation.equals(that.leftRotation) && this.rightRotation.equals(that.rightRotation);
      } else {
         return false;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      DEFAULT = new Transformation(Vector3Float.ZERO, Vector3Float.ONE, QuaternionFloat.DEFAULT, QuaternionFloat.DEFAULT);
   }

   public static class Builder {
      private Vector3Float translation;
      private Vector3Float scale;
      private QuaternionFloat leftRotation;
      private QuaternionFloat rightRotation;

      private Builder() {
         this.translation = Vector3Float.ZERO;
         this.scale = Vector3Float.ONE;
         this.leftRotation = QuaternionFloat.DEFAULT;
         this.rightRotation = QuaternionFloat.DEFAULT;
      }

      public Builder translation(Vector3Float translation) {
         this.translation = (Vector3Float)Objects.requireNonNull(translation, "translation");
         return this;
      }

      public Builder scale(Vector3Float scale) {
         this.scale = (Vector3Float)Objects.requireNonNull(scale, "scale");
         return this;
      }

      public Builder leftRotation(QuaternionFloat leftRotation) {
         this.leftRotation = (QuaternionFloat)Objects.requireNonNull(leftRotation, "leftRotation");
         return this;
      }

      public Builder rightRotation(QuaternionFloat rightRotation) {
         this.rightRotation = (QuaternionFloat)Objects.requireNonNull(rightRotation, "rightRotation");
         return this;
      }

      public Transformation build() {
         return new Transformation(this.translation, this.scale, this.leftRotation, this.rightRotation);
      }
   }
}
