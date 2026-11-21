package litebans;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProtocolLibHelper {
   public static final void handlePluginModule(@NotNull PluginModule pluginModule, @Nullable Object obj) {
      if (obj != null) {
         ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)obj);
      }
   }

   public static final void handlePluginModule2(@NotNull PluginModule pluginModule, @Nullable Object obj) {
      if (obj != null) {
         ProtocolLibrary.getProtocolManager().removePacketListener((PacketListener)obj);
      }
   }

   public static final boolean access$getClass(@NotNull Player player) {
      return player.getClass().getSimpleName().length() > 8 && StringsKt.checkText(player.getClass().getSimpleName(), "TemporaryPlayer", false, 2, null);
   }

   public static final boolean checkPlayer(@Nullable Player player) {
      return player == null || access$getClass(player);
   }

   public static final int computeIntValue(@NotNull Player player) {
      int currentVersion = MinecraftProtocolVersion.getCurrentVersion();
      int protocolVersion = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
      return protocolVersion <= 0 ? currentVersion : protocolVersion;
   }
}
