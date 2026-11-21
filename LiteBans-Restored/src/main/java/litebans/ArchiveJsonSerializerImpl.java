package litebans;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

class ArchiveJsonSerializerImpl implements JsonSerializer {
   final ArchiveHelper4 archiveHelper;

   ArchiveJsonSerializerImpl(ArchiveHelper4 archiveHelper) {
      this.archiveHelper = archiveHelper;
   }

   public JsonElement computeJsonElement(ConfigSection configSection, Type type, JsonSerializationContext jsonSerializationContext) {
      return jsonSerializationContext.serialize(configSection.keys);
   }

   public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
      return this.computeJsonElement((ConfigSection)obj, type, jsonSerializationContext);
   }
}
