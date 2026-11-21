package team.unnamed.creative.serialize.minecraft.atlas;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

@Internal
public final class AtlasSerializer implements JsonResourceSerializer<Atlas>, JsonResourceDeserializer<Atlas> {
   public static final AtlasSerializer INSTANCE = new AtlasSerializer();
   public static final ResourceCategoryImpl<Atlas> CATEGORY;
   private static final String SOURCES_FIELD = "sources";

   private AtlasSerializer() {
   }

   public void serializeToJson(Atlas object, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject().name("sources").beginArray();

      for(AtlasSource source : object.sources()) {
         AtlasSourceSerializer.serialize(source, writer);
      }

      writer.endArray().endObject();
   }

   public Atlas deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      List<AtlasSource> sources = new ArrayList();

      for(JsonElement sourceElement : node.getAsJsonObject().getAsJsonArray("sources")) {
         sources.add(AtlasSourceSerializer.deserialize(sourceElement.getAsJsonObject()));
      }

      return Atlas.atlas().key(key).sources(sources).build();
   }

   static {
      CATEGORY = new ResourceCategoryImpl<Atlas>("atlases", ".json", ResourceContainer::atlases, INSTANCE);
   }
}
