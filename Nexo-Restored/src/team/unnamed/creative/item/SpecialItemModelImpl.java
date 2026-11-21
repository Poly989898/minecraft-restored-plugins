package team.unnamed.creative.item;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.special.SpecialRender;

record SpecialItemModelImpl(SpecialRender render, Key base, Transformation transformation) implements SpecialItemModel {
   SpecialItemModelImpl(final @NotNull SpecialRender render, final @NotNull Key base, final @Nullable Transformation transformation) {
      this.render = (SpecialRender)Objects.requireNonNull(render, "render");
      this.base = (Key)Objects.requireNonNull(base, "base");
      this.transformation = transformation;
   }

   public @NotNull SpecialRender render() {
      return this.render;
   }

   public @NotNull Key base() {
      return this.base;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{base=" + String.valueOf(this.base) + ", render=" + String.valueOf(this.render) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         SpecialItemModelImpl that = (SpecialItemModelImpl)o;
         return this.render.equals(that.render) && this.base.equals(that.base) && Objects.equals(this.transformation, that.transformation);
      } else {
         return false;
      }
   }

   public @Nullable Transformation transformation() {
      return this.transformation;
   }
}
