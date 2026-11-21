package it.ytnoos.lpx.check.checks.position;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.PrinterStatus;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.common.utils.TimeUtils;
import it.ytnoos.lpx.player.BukkitProtocolPlayer;

public class PositionA extends PlayerCheck<WrapperPlayClientPlayerFlying> {
   private static final double O = 3.0E7;
   private static final double i = 2.0E7;

   public PositionA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying) {
      if (!wrapperPlayClientPlayerFlying.hasPositionChanged()) {
         return CheckResult.ALLOW;
      }

      Location location = wrapperPlayClientPlayerFlying.getLocation();
      double number = location.getX();
      double number2 = location.getY();
      double number3 = location.getZ();
      if (this.isValid(number, 3.0E7) && this.isValid(number2, 2.0E7) && this.isValid(number3, 3.0E7)) {
         if (protocolPlayer.getPrinterData().getPrinterStatus() == PrinterStatus.AUTOMATIC
            && TimeUtils.difference(protocolPlayer.getPrinterData().getLastPlace())
               > ((BukkitProtocolPlayer)protocolPlayer).getLpxBukkit().getConfigManager().getOptionsConfig().getPrinterConfig().getDisableDelay()) {
            protocolPlayer.getPrinterData().setPrinterMode(false);
         }

         return CheckResult.ALLOW;
      } else {
         this.raiseViolation(wrapperPlayClientPlayerFlying, new FlagInfo[]{FlagInfo.of("x", number), FlagInfo.of("y", number2), FlagInfo.of("z", number3)});
         return CheckResult.DENY;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

   private boolean isValid(double number, double number2) {
      return !Double.isNaN(number) && !Double.isInfinite(number) && !(Math.abs(number) > number2);
   }

}
