package litebans;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BungeeChatSupport {
   private static boolean return2 = ClassUtil.classExists("net.md_5.bungee.chat.ChatVersion");

   public static boolean obtainFlag() {
      return return2;
   }

   public static BaseComponent[] computeBaseComponentArray(CharSequence message) {
      return TextComponent.fromLegacyText(message.toString());
   }

   public static BaseComponent[] computeBaseComponentArray2(CharSequence message) {
      return ComponentSerializer.parse(message.toString());
   }
}
