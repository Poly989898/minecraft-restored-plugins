package it.ytnoos.lpx.listeners;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageDecoder;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.ReceivedPacket;
import it.ytnoos.lpx.config.logger.LoggerConfig;
import java.util.List;

@Sharable
public class PacketDecoder extends MessageToMessageDecoder<Object> {
   private final LPXBukkit lpxBukkit;
   private final ProtocolPlayer player;

   public PacketDecoder(LPXBukkit lpxBukkit2, ProtocolPlayer protocolPlayer) {
      this.lpxBukkit = lpxBukkit2;
      this.player = protocolPlayer;
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, Object object, List<Object> list) {
      ReceivedPacket receivedPacket = this.player.getLastReceivedPacket();
      if (receivedPacket != null) {
         this.player.setLastReceivedPacket(null);
         if (receivedPacket.getPacket() == null) {
            receivedPacket.setPacket(object);
         }

         this.lpxBukkit
            .getPacketTickMonitor()
            .recordDelay(this.lpxBukkit.getPacketTickMonitor().getCurrentTick() - receivedPacket.getReceivedAtTick(), channelHandlerContext.channel().eventLoop());
         this.logPacket(receivedPacket);
      }

      list.add(object);
   }

   private void logPacket(ReceivedPacket receivedPacket) {
      LoggerConfig loggerConfig = this.lpxBukkit.getConfigManager().getLoggerConfig();
      String string = receivedPacket.getPacketType().getName();
      boolean flag = loggerConfig.isHeavyListed(string) ? loggerConfig.isHeavyOverThreshold(string, receivedPacket.getElapsedTime()) : loggerConfig.isHeavy(receivedPacket.getElapsedTime());
      flag = flag && !loggerConfig.isHeavyIgnored(string);
      if (loggerConfig.isEnabled() || flag) {
         String string2 = receivedPacket.getPlayerName();
         if (loggerConfig.isPlayerLogged(string2) || flag) {
            if (!flag || this.player.getPacketCount() > 5L) {
               if ((loggerConfig.isPacketLogged(string) || flag && this.lpxBukkit.canPacketLogHeavy()) && !this.player.isKicked()) {
                  this.lpxBukkit.getPacketLogger().logPacket(receivedPacket);
               }
            }
         }
      }
   }

}
