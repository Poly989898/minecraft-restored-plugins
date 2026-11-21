package litebans;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.VersionedComponentSerializer;

public class BungeeChatConversion {
   public static BaseComponent[] computeBaseComponentArray(CharSequence message, ChatVersion chatVersion) {
      return VersionedComponentSerializer.forVersion(chatVersion).parse(message.toString());
   }
}
