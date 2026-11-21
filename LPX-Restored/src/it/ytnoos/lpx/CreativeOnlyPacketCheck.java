package it.ytnoos.lpx;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public abstract class CreativeOnlyPacketCheck extends PlayerCheck<PacketReceiveEvent> {
   private final PacketTypeCommon packetTypeCommon;

   protected CreativeOnlyPacketCheck(ProtocolPlayer protocolPlayer, CheckOptions checkOptions, PacketTypeCommon packetTypeCommon2) {
      super(protocolPlayer, checkOptions);
      this.packetTypeCommon = packetTypeCommon2;
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PacketReceiveEvent packetReceiveEvent) {
      if (packetReceiveEvent.getPacketType() != this.packetTypeCommon) {
         return CheckResult.ALLOW;
      } else {
         Player player2 = protocolPlayer.getPlayer().orElse(null);
         if (player2 != null && player2.getGameMode() != GameMode.CREATIVE) {
            this.raiseViolation(packetReceiveEvent, new FlagInfo[0]);
            return CheckResult.DENY;
         } else {
            return CheckResult.ALLOW;
         }
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
