package io.lumine.mythic.core.skills.variables;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class VariableSerializer implements JsonSerializer<Variable>, JsonDeserializer<Variable> {
   public Variable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      String type = obj.get("type").getAsString();
      Class<? extends Variable> clazz = VariableUtils.getTypeClass(type);
      return (Variable)context.deserialize(json, clazz);
   }

   public JsonElement serialize(Variable src, Type typeOfSrc, JsonSerializationContext context) {
      JsonElement element = context.serialize(src);
      String typeCode = src.getTypeName();
      element.getAsJsonObject().addProperty("type", typeCode);
      return element;
   }
}
