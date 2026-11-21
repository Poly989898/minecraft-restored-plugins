package litebans;

public class LiteHelper6 {
   public static String computeText(String text, GameVersion gameVersion) {
      return ChatComponentDeserializer.computeChatComponentDeserializer(gameVersion).computeText(ChatTextComponent.fromLegacyText(text));
   }
}
