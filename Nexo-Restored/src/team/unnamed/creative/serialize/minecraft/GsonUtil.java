package team.unnamed.creative.serialize.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;

@Internal
public final class GsonUtil {
   private static final JsonParser JSON_PARSER = new JsonParser();

   public static @NotNull JsonElement parseString(@NotNull String string) {
      return JSON_PARSER.parse(string);
   }

   public static @NotNull JsonElement parseReader(@NotNull Reader reader) {
      return JSON_PARSER.parse(reader);
   }

   public static @NotNull JsonElement parseReader(@NotNull JsonReader reader) {
      return JSON_PARSER.parse(reader);
   }

   public static boolean isNullOrAbsent(JsonObject object, String name) {
      return !object.has(name) || object.get(name).isJsonNull();
   }

   public static boolean getBoolean(JsonObject object, String key, boolean def) {
      if (!object.has(key)) {
         return def;
      } else {
         JsonElement element = object.get(key);
         if (!element.isJsonPrimitive() || !((JsonPrimitive)element).isBoolean() && !((JsonPrimitive)element).isString()) {
            throw new IllegalStateException("Field '" + key + "' must be a boolean");
         } else {
            return element.getAsBoolean();
         }
      }
   }

   public static boolean isInt(JsonObject object, String key) {
      if (!object.has(key)) {
         return false;
      } else {
         JsonElement element = object.get(key);
         return element.isJsonPrimitive() && ((JsonPrimitive)element).isNumber();
      }
   }

   public static int getInt(JsonObject object, String key, int def) {
      if (!object.has(key)) {
         return def;
      } else {
         JsonElement element = object.get(key);
         if (element.isJsonPrimitive() && ((JsonPrimitive)element).isNumber()) {
            return element.getAsInt();
         } else {
            throw new IllegalStateException("Field '" + key + "' must be an integer");
         }
      }
   }

   public static float getFloat(JsonObject object, String key, float def) {
      if (!object.has(key)) {
         return def;
      } else {
         JsonElement element = object.get(key);
         if (element.isJsonPrimitive() && ((JsonPrimitive)element).isNumber()) {
            return (float)element.getAsDouble();
         } else {
            throw new IllegalStateException("Field '" + key + "' must be a float");
         }
      }
   }

   public static void writeVector3Float(JsonWriter writer, Vector3Float vector) throws IOException {
      writer.beginArray();
      writer.value(vector.x());
      writer.value(vector.y());
      writer.value(vector.z());
      writer.endArray();
   }

   public static void writeVector2Float(JsonWriter writer, Vector2Float vector) throws IOException {
      writer.beginArray();
      writer.value(vector.x());
      writer.value(vector.y());
      writer.endArray();
   }

   public static Vector3Float readVector3Float(JsonElement element) {
      JsonArray array = element.getAsJsonArray();
      return new Vector3Float((float)array.get(0).getAsDouble(), (float)array.get(1).getAsDouble(), (float)array.get(2).getAsDouble());
   }

   public static int parseColor(JsonElement element) {
      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         return element.getAsInt();
      } else if (element.isJsonArray()) {
         JsonArray array = element.getAsJsonArray();
         if (array.size() != 3) {
            throw new IllegalArgumentException("RGB array must have exactly 3 elements");
         } else {
            int r = (int)(clamp(array.get(0).getAsFloat()) * 255.0F);
            int g = (int)(clamp(array.get(1).getAsFloat()) * 255.0F);
            int b = (int)(clamp(array.get(2).getAsFloat()) * 255.0F);
            return -16777216 | r << 16 | g << 8 | b;
         }
      } else {
         throw new IllegalArgumentException("Invalid color format");
      }
   }

   private static float clamp(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
