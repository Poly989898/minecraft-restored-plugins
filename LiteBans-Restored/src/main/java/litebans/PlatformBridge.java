package litebans;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface PlatformBridge {
   Platform getPlatform();

   Object getPlugin(@NotNull String text);

   List findPlugins(@NotNull String text);

   Collection getServerNames();

   String getServerVersion();

   void disallowLogin(@NotNull Object obj, @NotNull CharSequence message, @NotNull String text, boolean flag, @NotNull String text2);

   PlayerInfo getPlayerInfo(@NotNull Object obj, boolean flag);

   void handleObj(@NotNull Object obj, @NotNull CharSequence message);

   String getTargetServerName(@NotNull Object obj);

   boolean hasPlugin(@NotNull String text);
}
