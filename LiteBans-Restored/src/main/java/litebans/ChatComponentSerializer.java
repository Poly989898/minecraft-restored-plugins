package litebans;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class ChatComponentSerializer implements JsonSerializer, JsonDeserializer {
   public ChatText computeChatText(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      if (jsonElement.isJsonArray()) {
         return new ChatText((ChatBaseComponent[])jsonDeserializationContext.deserialize(jsonElement, ChatBaseComponent[].class));
      } else {
         return jsonElement.isJsonPrimitive() ? new ChatText(jsonElement.getAsJsonPrimitive().getAsString()) : new ChatText(new ChatBaseComponent[]{(ChatBaseComponent)jsonDeserializationContext.deserialize(jsonElement, ChatBaseComponent.class)});
      }
   }

   public JsonElement computeJsonElement(ChatText chatText, Type type, JsonSerializationContext jsonSerializationContext) {
      return jsonSerializationContext.serialize(chatText.getChatBaseComponents());
   }

   public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
      return this.computeJsonElement((ChatText)obj, type, jsonSerializationContext);
   }

   public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      return this.computeChatText(jsonElement, type, jsonDeserializationContext);
   }
}
