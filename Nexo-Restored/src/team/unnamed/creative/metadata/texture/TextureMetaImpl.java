package team.unnamed.creative.metadata.texture;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;

record TextureMetaImpl(boolean blur, boolean clamp) implements TextureMeta {
   public @NotNull Class<? extends MetadataPart> type() {
      return TextureMeta.class;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{blur=" + this.blur + ", clamp=" + this.clamp + "}";
   }
}
