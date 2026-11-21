package team.unnamed.creative.serialize.minecraft.waypoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;
import team.unnamed.creative.waypoint.WaypointStyle;

public class WaypointStyleSerializer implements JsonResourceSerializer<WaypointStyle>, JsonResourceDeserializer<WaypointStyle> {
   public static final WaypointStyleSerializer INSTANCE = new WaypointStyleSerializer();
   public static final ResourceCategoryImpl<WaypointStyle> CATEGORY;

   public WaypointStyle deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      WaypointStyle.Builder builder = WaypointStyle.waypointStyle().key(key);
      JsonObject objectNode = node.getAsJsonObject();
      if (objectNode.has("near_distance")) {
         builder.nearDistance(objectNode.get("near_distance").getAsInt());
      }

      if (objectNode.has("far_distance")) {
         builder.farDistance(objectNode.get("far_distance").getAsInt());
      }

      if (objectNode.has("sprites")) {
         for(JsonElement textureKeyElement : objectNode.getAsJsonArray("sprites")) {
            String asString = textureKeyElement.getAsString();
            Key textureKey = Key.key(asString);
            builder.sprite(textureKey);
         }
      }

      return builder.build();
   }

   public void serializeToJson(WaypointStyle object, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();
      writer.name("near_distance");
      writer.value((long)object.nearDistance());
      writer.name("far_distance");
      writer.value((long)object.farDistance());
      writer.name("sprites");
      writer.beginArray();

      for(Key sprite : object.sprites()) {
         writer.value(sprite.asString());
      }

      writer.endArray();
      writer.endObject();
   }

   static {
      CATEGORY = new ResourceCategoryImpl<WaypointStyle>("waypoint_style", ".json", ResourceContainer::waypointStyles, INSTANCE);
   }
}
