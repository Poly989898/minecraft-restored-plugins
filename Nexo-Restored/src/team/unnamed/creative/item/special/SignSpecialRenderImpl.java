package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.WoodType;

/** @deprecated */
@Deprecated(
   since = "1.14.0"
)
record SignSpecialRenderImpl(boolean hanging, WoodType woodType, Key texture) implements SignSpecialRender {
   SignSpecialRenderImpl(final boolean hanging, final @NotNull WoodType woodType, final @Nullable Key texture) {
      this.hanging = hanging;
      this.woodType = (WoodType)Objects.requireNonNull(woodType, "woodType");
      this.texture = texture;
   }

   public @NotNull WoodType woodType() {
      return this.woodType;
   }

   public @Nullable Key texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{hanging=" + this.hanging + ", woodType=" + String.valueOf(this.woodType) + ", texture=" + String.valueOf(this.texture) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         SignSpecialRenderImpl that = (SignSpecialRenderImpl)o;
         return this.hanging == that.hanging && this.woodType == that.woodType && Objects.equals(this.texture, that.texture);
      } else {
         return false;
      }
   }
}
