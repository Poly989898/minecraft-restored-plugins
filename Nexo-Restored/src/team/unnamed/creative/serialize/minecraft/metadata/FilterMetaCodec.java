package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.KeyPattern;
import team.unnamed.creative.metadata.filter.FilterMeta;
import team.unnamed.creative.serialize.minecraft.base.KeyPatternSerializer;

final class FilterMetaCodec implements MetadataPartCodec<FilterMeta> {
   public @NotNull Class<FilterMeta> type() {
      return FilterMeta.class;
   }

   public @NotNull String name() {
      return "filter";
   }

   public @NotNull FilterMeta read(@NotNull JsonObject node) {
      List<KeyPattern> patterns = new ArrayList();

      for(JsonElement filterNode : node.getAsJsonArray("block")) {
         patterns.add(KeyPatternSerializer.deserialize(filterNode.getAsJsonObject()));
      }

      return FilterMeta.filter(patterns);
   }

   public void write(@NotNull JsonWriter writer, @NotNull FilterMeta filter) throws IOException {
      writer.beginObject().name("block").beginArray();

      for(KeyPattern pattern : filter.patterns()) {
         KeyPatternSerializer.serialize(pattern, writer);
      }

      writer.endArray().endObject();
   }
}
