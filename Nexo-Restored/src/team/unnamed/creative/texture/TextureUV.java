package team.unnamed.creative.texture;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector2Float;

public interface TextureUV {
   @Contract("_, _ -> new")
   static @NotNull TextureUV uv(@NotNull Vector2Float from, @NotNull Vector2Float to) {
      return new TextureUVImpl(from, to);
   }

   @Contract("_, _, _, _ -> new")
   static @NotNull TextureUV uv(float fromX, float fromY, float toX, float toY) {
      return uv(new Vector2Float(fromX, fromY), new Vector2Float(toX, toY));
   }

   @NotNull Vector2Float from();

   @NotNull Vector2Float to();
}
