package team.unnamed.creative.serialize.minecraft.base;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.base.KeyPattern;

@Internal
public final class KeyPatternSerializer {
   private KeyPatternSerializer() {
      throw new UnsupportedOperationException("Can't instantiate utility class");
   }

   public static void serialize(KeyPattern pattern, JsonWriter writer) throws IOException {
      Pattern namespace = pattern.namespace();
      Pattern value = pattern.value();
      writer.beginObject();
      if (namespace != null) {
         writer.name("namespace").value(namespace.pattern());
      }

      if (value != null) {
         writer.name("path").value(value.pattern());
      }

      writer.endObject();
   }

   public static KeyPattern deserialize(JsonObject node) {
      String namespace = null;
      String path = null;
      if (node.has("namespace")) {
         namespace = node.get("namespace").getAsString();
      }

      if (node.has("path")) {
         path = node.get("path").getAsString();
      }

      return KeyPattern.of(namespace, path);
   }
}
