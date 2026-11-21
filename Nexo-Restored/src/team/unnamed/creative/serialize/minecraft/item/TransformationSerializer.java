package team.unnamed.creative.serialize.minecraft.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.item.Transformation;

public class TransformationSerializer {
   public static void writeToWriter(@NotNull JsonWriter writer, @NotNull Transformation transformation) throws IOException {
      writer.name("transformation");
      writer.beginObject();
      writer.name("translation").jsonValue(Vector3FloatSerializer.INSTANCE.serializeToJsonString(transformation.translation()));
      writer.name("scale").jsonValue(Vector3FloatSerializer.INSTANCE.serializeToJsonString(transformation.scale()));
      writer.name("left_rotation").jsonValue(QuaternionSerializer.INSTANCE.serializeToJsonString(transformation.leftRotation()));
      writer.name("right_rotation").jsonValue(QuaternionSerializer.INSTANCE.serializeToJsonString(transformation.rightRotation()));
      writer.endObject();
   }

   public static Transformation readFromJson(@NotNull JsonElement node) {
      Transformation transformation = Transformation.DEFAULT;
      if (node instanceof JsonObject object) {
         if (!object.isEmpty()) {
            JsonArray array = object.getAsJsonArray("translation");
            float x = array.get(0).getAsNumber().floatValue();
            float y = array.get(1).getAsNumber().floatValue();
            float z = array.get(2).getAsNumber().floatValue();
            transformation = transformation.translation(new Vector3Float(x, y, z));
            array = object.getAsJsonArray("scale");
            x = array.get(0).getAsNumber().floatValue();
            y = array.get(1).getAsNumber().floatValue();
            z = array.get(2).getAsNumber().floatValue();
            transformation = transformation.scale(new Vector3Float(x, y, z));
            array = object.getAsJsonArray("left_rotation");
            x = array.get(0).getAsNumber().floatValue();
            y = array.get(1).getAsNumber().floatValue();
            z = array.get(2).getAsNumber().floatValue();
            float w = array.get(3).getAsNumber().floatValue();
            transformation = transformation.leftRotation(new QuaternionFloat(x, y, z, w));
            array = object.getAsJsonArray("right_rotation");
            x = array.get(0).getAsNumber().floatValue();
            y = array.get(1).getAsNumber().floatValue();
            z = array.get(2).getAsNumber().floatValue();
            w = array.get(3).getAsNumber().floatValue();
            transformation = transformation.rightRotation(new QuaternionFloat(x, y, z, w));
            return transformation;
         }
      }

      return transformation;
   }
}
