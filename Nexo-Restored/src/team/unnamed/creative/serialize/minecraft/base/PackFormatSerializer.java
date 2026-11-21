package team.unnamed.creative.serialize.minecraft.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;

@Internal
public final class PackFormatSerializer {
   private PackFormatSerializer() {
      throw new UnsupportedOperationException("Can't instantiate utility class");
   }

   public static void serialize(PackFormat format, JsonWriter writer) throws IOException {
      int minMajor = format.min().major();
      int maxMajor = format.max().major();
      if (format.isSingle()) {
         writer.value((long)format.min().major());
      } else {
         writer.beginArray();
         writer.value((long)minMajor);
         writer.value((long)maxMajor);
         writer.endArray();
      }

   }

   public static PackFormat deserialize(JsonElement el, @Nullable Integer format) {
      PackFormat f = deserialize(el);
      if (format != null && format >= 0) {
         FormatVersion.of(format);
      } else {
         f.min();
      }

      return PackFormat.format(f.min(), f.max());
   }

   public static PackFormat deserialize(JsonElement el) {
      int min;
      int max;
      if (el.isJsonPrimitive()) {
         min = max = el.getAsInt();
      } else if (el.isJsonArray()) {
         JsonArray arr = el.getAsJsonArray();
         min = arr.get(0).getAsInt();
         max = arr.get(1).getAsInt();
      } else {
         if (!el.isJsonObject()) {
            throw new IllegalStateException("Unsupported supported_formats type: " + String.valueOf(el.getClass()));
         }

         JsonObject obj = el.getAsJsonObject();
         min = obj.get("min_inclusive").getAsInt();
         max = obj.get("max_inclusive").getAsInt();
      }

      return PackFormat.format(FormatVersion.of(min), FormatVersion.of(max));
   }

   public static FormatVersion deserializeFormat(JsonElement el) {
      int major;
      int minor;
      if (el.isJsonPrimitive()) {
         major = el.getAsInt();
         minor = 0;
      } else {
         if (!el.isJsonArray()) {
            throw new IllegalStateException("Unsupported supported_formats type: " + String.valueOf(el.getClass()));
         }

         JsonArray arr = el.getAsJsonArray();
         major = arr.get(0).getAsInt();
         minor = arr.size() > 1 ? arr.get(1).getAsInt() : 0;
      }

      return FormatVersion.of(major, minor);
   }
}
