package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record TileGuiScalingImpl(int width, int height) implements TileGuiScaling {
   TileGuiScalingImpl(final int width, final int height) {
      this.width = width;
      this.height = height;
      this.validate();
   }

   private void validate() {
      if (this.width <= 0) {
         throw new IllegalArgumentException("Width must be positive! Got " + this.width);
      } else if (this.height <= 0) {
         throw new IllegalArgumentException("Height must be positive! Got " + this.height);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{width=" + this.width + ", height=" + this.height + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TileGuiScalingImpl that = (TileGuiScalingImpl)o;
         if (this.width != that.width) {
            return false;
         } else {
            return this.height == that.height;
         }
      } else {
         return false;
      }
   }
}
