package it.ytnoos.lpx.check.checks.netty;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import io.netty.buffer.ByteBuf;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class NettyA extends PlayerCheck<ByteBuf> {
   private int maxSize;
   private final boolean preVia;

   public NettyA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions, boolean flag) {
      super(protocolPlayer, checkOptions);
      this.preVia = flag;
   }

   @Override
   public void loadOptions() {
      this.maxSize = this.getBoolean("automatic", true) ? this.getMaxSize() : this.getInt("max", 98304);
   }

   private int getMaxSize() {
      ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
      if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
         return 8448;
      } else {
         return serverVersion.isOlderThanOrEquals(ServerVersion.V_1_12_2) ? 24576 : 98304;
      }
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ByteBuf byteBuf) {
      long longValue = protocolPlayer.getLastReceivedPacket().getSize();
      long longValue2 = this.maxSize;
      if (!this.preVia) {
         longValue2 *= 2L;
      }

      if (longValue >= 0L && longValue < longValue2) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(byteBuf, new FlagInfo[]{FlagInfo.of("size", longValue), FlagInfo.of("temp-max-size", longValue2), FlagInfo.of("max-size", this.maxSize)});
      return CheckResult.DENY;
   }

   @Override
   public boolean isPreVia() {
      return this.preVia;
   }

}
