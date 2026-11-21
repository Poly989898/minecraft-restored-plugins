package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.CubeFace;

record ShulkerBoxSpecialRenderImpl(Key texture, float openness, CubeFace orientation) implements ShulkerBoxSpecialRender {
   ShulkerBoxSpecialRenderImpl(final @NotNull Key texture, final float openness, final @NotNull CubeFace orientation) {
      this.texture = (Key)Objects.requireNonNull(texture, "texture");
      this.openness = openness;
      this.orientation = (CubeFace)Objects.requireNonNull(orientation, "orientation");
      if (openness > 1.0F || openness < 0.0F) {
         throw new IllegalArgumentException("Openness must be between 0 and 1, got: " + openness);
      }
   }

   public @NotNull Key texture() {
      return this.texture;
   }

   public @NotNull CubeFace orientation() {
      return this.orientation;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{texture=" + String.valueOf(this.texture) + ", openness=" + this.openness + ", orientation=" + String.valueOf(this.orientation) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ShulkerBoxSpecialRenderImpl that = (ShulkerBoxSpecialRenderImpl)o;
         return Float.compare(that.openness, this.openness) == 0 && this.texture.equals(that.texture) && this.orientation == that.orientation;
      } else {
         return false;
      }
   }
}
