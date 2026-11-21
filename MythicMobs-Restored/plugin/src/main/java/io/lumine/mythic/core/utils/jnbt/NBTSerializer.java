package io.lumine.mythic.core.utils.jnbt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class NBTSerializer implements JsonSerializer<Tag>, JsonDeserializer<Tag> {
   public Tag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      int type = obj.get("type").getAsInt();
      Class<? extends Tag> clazz = NBTUtils.getTypeClass(type);
      return (Tag)context.deserialize(json, clazz);
   }

   public JsonElement serialize(Tag src, Type typeOfSrc, JsonSerializationContext context) {
      JsonElement element = context.serialize(src);
      int typeCode = NBTUtils.getTypeCode((Class<? extends Tag>)src.getClass());
      element.getAsJsonObject().addProperty("type", typeCode);
      return element;
   }
}
