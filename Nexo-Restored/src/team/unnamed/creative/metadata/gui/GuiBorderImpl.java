package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record GuiBorderImpl(int top, int bottom, int left, int right) implements GuiBorder {
   GuiBorderImpl(final int top, final int bottom, final int left, final int right) {
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
      this.validate();
   }

   private void validate() {
      if (this.top < 0) {
         throw new IllegalArgumentException("top border size must be greater than or equal to 0, got " + this.top);
      } else if (this.bottom < 0) {
         throw new IllegalArgumentException("bottom border size must be greater than or equal to 0, got " + this.bottom);
      } else if (this.left < 0) {
         throw new IllegalArgumentException("left border size must be greater than or equal to 0, got " + this.left);
      } else if (this.right < 0) {
         throw new IllegalArgumentException("right border size must be greater than or equal to 0, got " + this.right);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{top=" + this.top + ", bottom=" + this.bottom + ", left=" + this.left + ", right=" + this.right + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof GuiBorderImpl)) {
         return false;
      } else {
         GuiBorderImpl guiBorder = (GuiBorderImpl)o;
         return this.top == guiBorder.top && this.bottom == guiBorder.bottom && this.left == guiBorder.left && this.right == guiBorder.right;
      }
   }
}
