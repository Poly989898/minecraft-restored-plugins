package team.unnamed.creative.serialize.minecraft.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public class Vector3FloatSerializer implements JsonResourceSerializer<Vector3Float>, JsonResourceDeserializer<Vector3Float> {
   public static final Vector3FloatSerializer INSTANCE = new Vector3FloatSerializer();

   public Vector3Float deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) throws IOException {
      JsonArray array = node.getAsJsonArray();
      return new Vector3Float(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
   }

   public void serializeToJson(Vector3Float object, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginArray();
      writer.value(object.x());
      writer.value(object.y());
      writer.value(object.z());
      writer.endArray();
   }
}
