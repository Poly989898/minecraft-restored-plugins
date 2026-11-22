package litebans;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class LiteJsonSerializerImpl extends LiteHelper10 implements JsonSerializer, JsonDeserializer {
   public LiteJsonSerializerImpl(ChatComponentDeserializer chatComponentDeserializer) {
      super(chatComponentDeserializer);
   }

   public ChatTextComponent computeChatTextComponent(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      ChatTextComponent chatTextComponent = new ChatTextComponent();
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      JsonElement jsonElement2 = asJsonObject.get("text");
      if (jsonElement2 != null) {
         chatTextComponent.setContent(jsonElement2.getAsString());
      }

      this.handleJsonObject(asJsonObject, chatTextComponent, jsonDeserializationContext);
      return chatTextComponent;
   }

   public JsonElement computeJsonElement(ChatTextComponent chatTextComponent, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      this.handleJsonObject(jsonObject, chatTextComponent, jsonSerializationContext);
      jsonObject.addProperty("text", chatTextComponent.getContent());
      return jsonObject;
   }

   public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
      return this.computeJsonElement((ChatTextComponent)obj, type, jsonSerializationContext);
   }

   public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      return this.computeChatTextComponent(jsonElement, type, jsonDeserializationContext);
   }
}
