package team.unnamed.creative.serialize.minecraft.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public class QuaternionSerializer implements JsonResourceSerializer<QuaternionFloat>, JsonResourceDeserializer<QuaternionFloat> {
   public static final QuaternionSerializer INSTANCE = new QuaternionSerializer();

   public QuaternionFloat deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) throws IOException {
      JsonArray array = node.getAsJsonArray();
      return new QuaternionFloat(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
   }

   public void serializeToJson(QuaternionFloat object, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginArray();
      writer.value(object.x());
      writer.value(object.y());
      writer.value(object.z());
      writer.value(object.w());
      writer.endArray();
   }
}
