package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CreativeA extends PlayerCheck<PacketReceiveEvent> {
   public CreativeA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PacketReceiveEvent packetReceiveEvent) {
      if (packetReceiveEvent.getPacketType() != Client.CREATIVE_INVENTORY_ACTION) {
         return CheckResult.ALLOW;
      } else {
         Player player2 = protocolPlayer.getPlayer().orElse(null);
         if (player2 == null) {
            return CheckResult.ALLOW;
         } else if (!protocolPlayer.getPrinterData().isUsingPrinter() && player2.getGameMode() != GameMode.CREATIVE) {
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
