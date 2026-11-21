package it.ytnoos.lpx.check.checks.payload;

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.entity.Player;

public class PayloadF extends PlayerCheck<WrapperPlayClientPluginMessage> {
   public PayloadF(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      }

      if (!wrapperPlayClientPluginMessage.getChannelName().equalsIgnoreCase("purpur:beehive_c2s")) {
         return CheckResult.ALLOW;
      }

      ByteBuf byteBuf = null;

      try {
         byteBuf = Unpooled.copiedBuffer(wrapperPlayClientPluginMessage.getData());
         Vector3i vector3i = new Vector3i(ByteBufHelper.readLong(byteBuf));
         if (!player2.getWorld().isChunkLoaded(vector3i.getX() >> 4, vector3i.getZ() >> 4)) {
            this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[]{FlagInfo.of("blockpos", vector3i)});
            return CheckResult.DENY;
         }
      } catch (Exception exception) {
         this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[]{FlagInfo.of("exception", exception.getMessage())});
         return CheckResult.DENY;
      } finally {
         if (byteBuf != null && byteBuf.refCnt() > 0) {
            byteBuf.release();
         }
      }

      return CheckResult.ALLOW;
   }
}
