package litebans;

public class TextComponentDeserializer {
   public static ChatBaseComponent[] computeChatBaseComponentArray(String text) {
      return ChatComponentDeserializer.obtainChatComponentDeserializer().computeChatBaseComponentArray(text);
   }

   public static String computeText(ChatBaseComponent[] chatBaseComponents) {
      return ChatComponentDeserializer.obtainChatComponentDeserializer().computeText(chatBaseComponents);
   }
}
