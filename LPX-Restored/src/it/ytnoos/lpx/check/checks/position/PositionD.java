package it.ytnoos.lpx.check.checks.position;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class PositionD extends PlayerCheck<WrapperPlayClientPlayerFlying> {
   private int max;

   public PositionD(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getConfig().getInt(this.getPath("max"), 1000000);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying) {
      if (!wrapperPlayClientPlayerFlying.hasRotationChanged()) {
         return CheckResult.ALLOW;
      } else {
         float floatValue = wrapperPlayClientPlayerFlying.getLocation().getYaw();
         if (Math.abs(floatValue) > this.max) {
            this.raiseViolation(wrapperPlayClientPlayerFlying, new FlagInfo[]{FlagInfo.of("yaw", floatValue)});
            return CheckResult.DENY;
         } else {
            return CheckResult.ALLOW;
         }
      }
   }

}
