package litebans;

public class LiteHelper7 {
   public static ChatBaseComponent[] computeChatBaseComponentArray(CharSequence message) {
      return ChatTextComponent.fromLegacyText(message.toString());
   }
}
