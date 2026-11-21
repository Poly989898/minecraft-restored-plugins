package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   since = "1.14.0"
)
record BedSpecialRenderImpl(Key texture) implements BedSpecialRender {
   BedSpecialRenderImpl(final @NotNull Key texture) {
      this.texture = (Key)Objects.requireNonNull(texture, "texture");
   }

   public @NotNull Key texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{texture=" + String.valueOf(this.texture) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         BedSpecialRenderImpl that = (BedSpecialRenderImpl)o;
         return this.texture.equals(that.texture);
      } else {
         return false;
      }
   }
}
