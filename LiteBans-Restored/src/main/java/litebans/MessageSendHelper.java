package litebans;

import net.md_5.bungee.api.event.LoginEvent;
import org.jetbrains.annotations.NotNull;

public final class MessageSendHelper {
   private MessageSendHelper() {
   }

   public final void handleObj(@NotNull Object obj, @NotNull CharSequence message) {
      if (obj instanceof LoginEvent) {
         ((LoginEvent)obj).setCancelReason(message.toString());
      }
   }

   public MessageSendHelper(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
