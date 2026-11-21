package team.unnamed.creative.item.special;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.DyeColor;

record BannerSpecialRenderImpl(DyeColor color) implements BannerSpecialRender {
   BannerSpecialRenderImpl(final @NotNull DyeColor color) {
      this.color = (DyeColor)Objects.requireNonNull(color, "color");
   }

   public @NotNull DyeColor color() {
      return this.color;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{color=" + String.valueOf(this.color) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         BannerSpecialRenderImpl that = (BannerSpecialRenderImpl)o;
         return this.color == that.color;
      } else {
         return false;
      }
   }
}
