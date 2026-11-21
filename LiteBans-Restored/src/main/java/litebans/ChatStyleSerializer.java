package litebans;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.Map.Entry;

public class ChatStyleSerializer implements JsonSerializer, JsonDeserializer {
   private static final boolean checkJsonElement(JsonElement jsonElement) {
      if (jsonElement.isJsonPrimitive()) {
         JsonPrimitive jsonPrimitive = (JsonPrimitive)jsonElement;
         if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
         }

         if (jsonPrimitive.isNumber()) {
            Number asNumber = jsonPrimitive.getAsNumber();
            if (asNumber instanceof Byte) {
               return asNumber.byteValue() != 0;
            }
         }
      }

      return false;
   }

   static void handleChatStyle(ChatStyle chatStyle, JsonObject jsonObject) {
      if (chatStyle.getBold() != null) {
         jsonObject.addProperty("bold", chatStyle.getBold());
      }

      if (chatStyle.getItalic() != null) {
         jsonObject.addProperty("italic", chatStyle.getItalic());
      }

      if (chatStyle.getUnderlined() != null) {
         jsonObject.addProperty("underlined", chatStyle.getUnderlined());
      }

      if (chatStyle.getStrikethrough() != null) {
         jsonObject.addProperty("strikethrough", chatStyle.getStrikethrough());
      }

      if (chatStyle.getObfuscated() != null) {
         jsonObject.addProperty("obfuscated", chatStyle.getObfuscated());
      }

      if (chatStyle.hasColor() && chatStyle.getColor().getColor() != null) {
         jsonObject.addProperty("color", chatStyle.getColor().getText4());
      }

      if (chatStyle.hasRgbColor()) {
         jsonObject.addProperty("shadow_color", chatStyle.getRgbColor().getRGB());
      }

      if (chatStyle.hasFont()) {
         jsonObject.addProperty("font", chatStyle.getFont());
      }
   }

   public ChatStyle computeChatStyle(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      KotlinHelper3 kotlinHelper = ChatStyle.obtainKotlinHelper();
      JsonObject asJsonObject = jsonElement.getAsJsonObject();

      for (Entry entry : asJsonObject.entrySet()) {
         String key = (String)entry.getKey();
         JsonElement value = (JsonElement)entry.getValue();
         switch (key) {
            case "bold":
               kotlinHelper.bold(checkJsonElement(value));
               break;
            case "italic":
               kotlinHelper.italic(checkJsonElement(value));
               break;
            case "underlined":
               kotlinHelper.underlined(checkJsonElement(value));
               break;
            case "strikethrough":
               kotlinHelper.strikethrough(checkJsonElement(value));
               break;
            case "obfuscated":
               kotlinHelper.obfuscated(checkJsonElement(value));
               break;
            case "color":
               kotlinHelper.color(ColorTranslator.computeColorTranslator(value.getAsString()));
               break;
            case "shadow_color":
               if (value.isJsonArray()) {
                  JsonArray asJsonArray = value.getAsJsonArray();
                  kotlinHelper.rgb(new Color(asJsonArray.get(0).getAsFloat(), asJsonArray.get(1).getAsFloat(), asJsonArray.get(2).getAsFloat(), asJsonArray.get(3).getAsFloat()));
               } else if (value.isJsonPrimitive()) {
                  kotlinHelper.rgb(new Color(value.getAsNumber().intValue(), true));
               }
               break;
            case "font":
               kotlinHelper.font(value.getAsString());
         }
      }

      return kotlinHelper.build();
   }

   public JsonElement computeJsonElement(ChatStyle chatStyle, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      handleChatStyle(chatStyle, jsonObject);
      return jsonObject;
   }

   public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
      return this.computeJsonElement((ChatStyle)obj, type, jsonSerializationContext);
   }

   public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      return this.computeChatStyle(jsonElement, type, jsonDeserializationContext);
   }
}
