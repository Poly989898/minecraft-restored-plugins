package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Pose;

record CopperGolemStatueSpecialRenderImpl(Pose pose, Key texture) implements CopperGolemStatueSpecialRender {
   public @NotNull Pose pose() {
      return this.pose;
   }

   public @NotNull Key texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{pose=" + String.valueOf(this.pose) + ", texture=" + String.valueOf(this.texture) + "}";
   }

   public boolean equals(Object o) {
      if (!(o instanceof CopperGolemStatueSpecialRender that)) {
         return false;
      } else {
         return this.pose == that.pose() && Objects.equals(this.texture, that.texture());
      }
   }
}
