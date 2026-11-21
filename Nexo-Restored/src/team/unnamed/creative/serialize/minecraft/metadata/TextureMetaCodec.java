package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.texture.TextureMeta;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

final class TextureMetaCodec implements MetadataPartCodec<TextureMeta> {
   public @NotNull Class<TextureMeta> type() {
      return TextureMeta.class;
   }

   public @NotNull String name() {
      return "texture";
   }

   public @NotNull TextureMeta read(@NotNull JsonObject node) {
      boolean blur = GsonUtil.getBoolean(node, "blur", false);
      boolean clamp = GsonUtil.getBoolean(node, "clamp", false);
      return TextureMeta.texture(blur, clamp);
   }

   public void write(@NotNull JsonWriter writer, @NotNull TextureMeta texture) throws IOException {
      writer.beginObject();
      boolean blur = texture.blur();
      if (blur) {
         writer.name("blur").value(blur);
      }

      boolean clamp = texture.clamp();
      if (clamp) {
         writer.name("clamp").value(clamp);
      }

      writer.endObject();
   }
}
