package team.unnamed.creative.model;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

record ItemTransformImpl(Vector3Float rotation, Vector3Float translation, Vector3Float scale) implements ItemTransform {
   ItemTransformImpl(final @NotNull Vector3Float rotation, final @NotNull Vector3Float translation, final @NotNull Vector3Float scale) {
      this.rotation = (Vector3Float)Objects.requireNonNull(rotation, "rotation");
      this.translation = (Vector3Float)Objects.requireNonNull(translation, "translation");
      this.scale = (Vector3Float)Objects.requireNonNull(scale, "scale");
      this.validate();
   }

   private void validate() {
      if (!(this.translation.x() < -80.0F) && !(this.translation.x() > 80.0F) && !(this.translation.y() < -80.0F) && !(this.translation.y() > 80.0F) && !(this.translation.z() < -80.0F) && !(this.translation.z() > 80.0F)) {
         if (this.scale.x() < -4.0F || this.scale.x() > 4.0F || this.scale.y() < -4.0F || this.scale.y() > 4.0F || this.scale.z() < -4.0F || this.scale.z() > 4.0F) {
            throw new IllegalArgumentException("Invalid scale (" + String.valueOf(this.scale) + "), out of bounds (-4.0 to 4.0)");
         }
      } else {
         throw new IllegalArgumentException("Invalid translation (" + String.valueOf(this.translation) + "), out of bounds (-80.0 to 80.0)");
      }
   }

   public @NotNull Vector3Float rotation() {
      return this.rotation;
   }

   public @NotNull Vector3Float translation() {
      return this.translation;
   }

   public @NotNull Vector3Float scale() {
      return this.scale;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{rotation=" + String.valueOf(this.rotation) + ", translation=" + String.valueOf(this.translation) + ", scale=" + String.valueOf(this.scale) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ItemTransformImpl that = (ItemTransformImpl)o;
         return this.rotation.equals(that.rotation) && this.translation.equals(that.translation) && this.scale.equals(that.scale);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements ItemTransform.Builder {
      private Vector3Float rotation;
      private Vector3Float translation;
      private Vector3Float scale;

      BuilderImpl() {
         this.rotation = ItemTransform.DEFAULT_ROTATION;
         this.translation = ItemTransform.DEFAULT_TRANSLATION;
         this.scale = ItemTransform.DEFAULT_SCALE;
      }

      public ItemTransform.@NotNull Builder rotation(@NotNull Vector3Float rotation) {
         this.rotation = (Vector3Float)Objects.requireNonNull(rotation, "rotation");
         return this;
      }

      public ItemTransform.@NotNull Builder translation(@NotNull Vector3Float translation) {
         this.translation = (Vector3Float)Objects.requireNonNull(translation, "translation");
         return this;
      }

      public ItemTransform.@NotNull Builder scale(@NotNull Vector3Float scale) {
         this.scale = (Vector3Float)Objects.requireNonNull(scale, "scale");
         return this;
      }

      public @NotNull ItemTransform build() {
         return new ItemTransformImpl(this.rotation, this.translation, this.scale);
      }
   }
}
