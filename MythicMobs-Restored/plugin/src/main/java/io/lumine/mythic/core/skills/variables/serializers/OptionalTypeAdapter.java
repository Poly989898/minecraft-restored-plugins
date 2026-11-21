package io.lumine.mythic.core.skills.variables.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapter implements JsonSerializer<Optional<?>>, JsonDeserializer<Optional<?>> {
   public Optional<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
      if (jsonElement != null && !jsonElement.isJsonNull()) {
         Type innerType;
         if (type instanceof ParameterizedType) {
            innerType = ((ParameterizedType)type).getActualTypeArguments()[0];
         } else {
            innerType = Object.class;
         }

         Object value = context.deserialize(jsonElement, innerType);
         return Optional.ofNullable(value);
      } else {
         return Optional.empty();
      }
   }

   public JsonElement serialize(Optional<?> optional, Type type, JsonSerializationContext context) {
      return optional.<JsonElement>map(context::serialize).orElse(JsonNull.INSTANCE);
   }
}
