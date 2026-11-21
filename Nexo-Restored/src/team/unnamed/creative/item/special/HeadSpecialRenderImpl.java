package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.HeadType;

record HeadSpecialRenderImpl(HeadType kind, Key texture, float animation) implements HeadSpecialRender {
   HeadSpecialRenderImpl(final @NotNull HeadType kind, final @Nullable Key texture, final float animation) {
      this.kind = (HeadType)Objects.requireNonNull(kind, "kind");
      this.texture = texture;
      this.animation = animation;
   }

   public @NotNull HeadType kind() {
      return this.kind;
   }

   public @Nullable Key texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{kind=" + String.valueOf(this.kind) + ", texture=" + String.valueOf(this.texture) + ", animation=" + this.animation + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         HeadSpecialRenderImpl that = (HeadSpecialRenderImpl)o;
         return Float.compare(that.animation, this.animation) == 0 && this.kind == that.kind && Objects.equals(this.texture, that.texture);
      } else {
         return false;
      }
   }
}
