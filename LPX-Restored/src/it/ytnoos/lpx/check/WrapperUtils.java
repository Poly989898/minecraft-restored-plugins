package it.ytnoos.lpx.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import org.jetbrains.annotations.Nullable;

public class WrapperUtils {
   private WrapperUtils() {
   }

   public static PacketWrapper createWrapper(ProtocolPlayer protocolPlayer, Object object, boolean flag) {
      PacketWrapper packetWrapper = new PacketWrapper(
         protocolPlayer.getUser().getClientVersion(),
         flag ? protocolPlayer.getUser().getClientVersion().toServerVersion() : PacketEvents.getAPI().getServerManager().getVersion(),
         -2
      );
      packetWrapper.buffer = object;
      packetWrapper.markReceivedData();
      return packetWrapper;
   }

   public static void releaseWrapper(@Nullable PacketWrapper packetWrapper) {
      if (packetWrapper != null) {
         if (packetWrapper.buffer != null && ByteBufHelper.refCnt(packetWrapper.buffer) > 0) {
            ByteBufHelper.release(packetWrapper.buffer);
         }
      }
   }

}
