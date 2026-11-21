package litebans;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 0)
public final class LockdownState {
   public static final LockdownState LOCKDOWN_STATE = new LockdownState();

   private LockdownState() {
   }

   public final void handleObj(@Nullable Object obj, @NotNull String text) {
      PacketContainer packetContainer = new PacketContainer(Server.KICK_DISCONNECT);
      packetContainer.getChatComponents().write(0, WrappedChatComponent.fromJson(text));
      ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
      Intrinsics.checkNotNullExpressionValue(obj, "");
      protocolManager.sendServerPacket((Player)obj, packetContainer);
   }

   public final boolean obtainFlag() {
      return ProtocolLibrary.getProtocolManager() == null;
   }
}
