package litebans;

import java.util.UUID;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface LiteSender {
   LiteSender[] liteSenderArray = new LiteSender[0];
   UUID ZERO_UUID = new UUID(0L, 0L);

   void dispatchCommand(@NonNull String text);

   @NonNull String getDisplayName();

   @Nullable Object getHandle();

   @Nullable String getIpAddress();

   @NonNull String getName();

   @Nullable String getServerName();

   @NonNull UUID getUuid();

   @NonNull String getUuidString();

   boolean hasPermission(@Nullable String text);

   boolean isConsole();

   boolean isOnline();

   boolean isPlayer();

   void disconnect(@NonNull String text);

   void sendMessage(String text);

   void sendRichMessage(String text);

   void sendPluginMessage(String text, byte[] bytes);
}
