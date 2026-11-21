package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.sodium.SodiumMeta;

public class SodiumMetaCodec implements MetadataPartCodec<SodiumMeta> {
   static final MetadataPartCodec<SodiumMeta> INSTANCE = new SodiumMetaCodec();

   private SodiumMetaCodec() {
   }

   public @NotNull Class<SodiumMeta> type() {
      return SodiumMeta.class;
   }

   public @NotNull String name() {
      return "sodium";
   }

   public @NotNull SodiumMeta read(@NotNull JsonObject node) {
      List<String> ignoredShaders = new ArrayList();
      if (node.has("ignored_shaders")) {
         JsonElement el = node.get("ignored_shaders");
         if (el.isJsonArray()) {
            el.getAsJsonArray().asList().forEach((element) -> ignoredShaders.add(element.getAsString()));
         }
      }

      return SodiumMeta.sodium(ignoredShaders);
   }

   public void write(@NotNull JsonWriter writer, @NotNull SodiumMeta sodium) throws IOException {
      writer.beginObject().name("ignored_shaders").beginArray();

      for(String shader : sodium.ignoredShaders()) {
         writer.value(shader);
      }

      writer.endArray().endObject();
   }
}
