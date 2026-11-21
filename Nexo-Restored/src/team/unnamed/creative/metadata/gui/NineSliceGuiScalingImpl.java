package team.unnamed.creative.metadata.gui;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record NineSliceGuiScalingImpl(int width, int height, GuiBorder border, boolean stretchInner) implements NineSliceGuiScaling {
   NineSliceGuiScalingImpl(final int width, final int height, final @NotNull GuiBorder border, final boolean stretchInner) {
      this.width = width;
      this.height = height;
      this.border = (GuiBorder)Objects.requireNonNull(border, "border");
      this.stretchInner = stretchInner;
      this.validate();
   }

   private void validate() {
      if (this.width <= 0) {
         throw new IllegalArgumentException("Width must be positive! Got " + this.width);
      } else if (this.height <= 0) {
         throw new IllegalArgumentException("Height must be positive! Got " + this.height);
      } else if (this.border.left() + this.border.right() >= this.width) {
         int var1 = this.border.left();
         throw new IllegalArgumentException("Horizontal borders too big: " + var1 + " + " + this.border.right() + " >= " + this.width);
      } else if (this.border.top() + this.border.bottom() >= this.height) {
         int var10002 = this.border.top();
         throw new IllegalArgumentException("Vertical borders too big: " + var10002 + " + " + this.border.bottom() + " >= " + this.height);
      }
   }

   public @NotNull GuiBorder border() {
      return this.border;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{width=" + this.width + ", height=" + this.height + ", border=" + String.valueOf(this.border) + ", stretch_inner=" + this.stretchInner + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         NineSliceGuiScalingImpl that = (NineSliceGuiScalingImpl)o;
         if (this.width != that.width) {
            return false;
         } else if (this.height != that.height) {
            return false;
         } else if (!this.border.equals(that.border)) {
            return false;
         } else {
            return this.stretchInner == that.stretchInner;
         }
      } else {
         return false;
      }
   }
}
