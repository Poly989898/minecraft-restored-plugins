package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record ChestSpecialRenderImpl(Key texture, float openness) implements ChestSpecialRender {
   ChestSpecialRenderImpl(final @NotNull Key texture, final float openness) {
      this.texture = (Key)Objects.requireNonNull(texture, "texture");
      this.openness = openness;
      if (openness > 1.0F || openness < 0.0F) {
         throw new IllegalArgumentException("Openness must be between 0 and 1, got: " + openness);
      }
   }

   public @NotNull Key texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{texture=" + String.valueOf(this.texture) + ", openness=" + this.openness + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ChestSpecialRenderImpl that = (ChestSpecialRenderImpl)o;
         return Float.compare(that.openness, this.openness) == 0 && this.texture.equals(that.texture);
      } else {
         return false;
      }
   }
}
