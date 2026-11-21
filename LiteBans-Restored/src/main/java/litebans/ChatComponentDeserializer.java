package litebans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;

public class ChatComponentDeserializer implements JsonDeserializer {
   private final Gson gson2;
   private final GameVersion gameVersion;
   private static final ChatComponentDeserializer return4 = new ChatComponentDeserializer(GameVersion.V1_16);
   private static final ChatComponentDeserializer return3 = new ChatComponentDeserializer(GameVersion.V1_21_5);
   public static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

   public ChatComponentDeserializer(GameVersion gameVersion2) {
      this.gameVersion = gameVersion2;
      this.gson2 = new GsonBuilder()
         .registerTypeAdapter(ChatBaseComponent.class, this)
         .registerTypeAdapter(ChatTextComponent.class, new LiteJsonSerializerImpl(this))
         .registerTypeAdapter(ChatStyle.class, new ChatStyleSerializer())
         .registerTypeAdapter(ChatText.class, new ChatComponentSerializer())
         .create();
   }

   public static ChatComponentDeserializer computeChatComponentDeserializer(GameVersion gameVersion2) {
      switch (ChatComponentDeserializerSwitchMap.INTS[gameVersion2.ordinal()]) {
         case 1:
            return return4;
         case 2:
            return return3;
         default:
            throw new IllegalArgumentException("Unknown version " + gameVersion2);
      }
   }

   @Deprecated
   public static ChatComponentDeserializer obtainChatComponentDeserializer() {
      return return4;
   }

   public ChatBaseComponent[] computeChatBaseComponentArray(String text) {
      JsonElement string = JsonParser.parseString(text);
      return string.isJsonArray() ? (ChatBaseComponent[])this.gson2.fromJson(string, ChatBaseComponent[].class) : new ChatBaseComponent[]{(ChatBaseComponent)this.gson2.fromJson(string, ChatBaseComponent.class)};
   }

   public String computeText(ChatBaseComponent[] chatBaseComponents) {
      return chatBaseComponents.length == 1 ? this.gson2.toJson(chatBaseComponents[0]) : this.gson2.toJson(new ChatTextComponent(chatBaseComponents));
   }

   public ChatBaseComponent computeChatBaseComponent(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      return jsonElement.isJsonPrimitive() ? new ChatTextComponent(jsonElement.getAsString()) : (ChatBaseComponent)jsonDeserializationContext.deserialize(jsonElement, ChatTextComponent.class);
   }

   public GameVersion getGameVersion() {
      return this.gameVersion;
   }

   public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      return this.computeChatBaseComponent(jsonElement, type, jsonDeserializationContext);
   }
}
