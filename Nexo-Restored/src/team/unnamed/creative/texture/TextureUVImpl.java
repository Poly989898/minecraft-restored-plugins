package team.unnamed.creative.texture;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector2Float;

record TextureUVImpl(Vector2Float from, Vector2Float to) implements TextureUV {
   TextureUVImpl(final @NotNull Vector2Float from, final @NotNull Vector2Float to) {
      this.from = (Vector2Float)Objects.requireNonNull(from, "from");
      this.to = (Vector2Float)Objects.requireNonNull(to, "to");
   }

   public @NotNull Vector2Float from() {
      return this.from;
   }

   public @NotNull Vector2Float to() {
      return this.to;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{from=" + String.valueOf(this.from) + ", to=" + String.valueOf(this.to) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TextureUVImpl that = (TextureUVImpl)o;
         return !this.from.equals(that.from) ? false : this.to.equals(that.to);
      } else {
         return false;
      }
   }
}
