package it.ytnoos.lpx;

import com.github.retrooper.packetevents.exception.PacketLimitException;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class PacketLimitCheck extends PlayerCheck<PacketLimitException> {
   private final boolean preVia;

   public PacketLimitCheck(ProtocolPlayer protocolPlayer, CheckOptions checkOptions, boolean flag) {
      super(protocolPlayer, checkOptions);
      this.preVia = flag;
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PacketLimitException packetLimitException) {
      this.raiseViolation(
         packetLimitException, new FlagInfo[]{FlagInfo.of("limit", packetLimitException.getLimit().getDescription()), FlagInfo.of("value", packetLimitException.getValue()), FlagInfo.of("max", packetLimitException.getMax())}
      );
      return CheckResult.DENY;
   }

   @Override
   public boolean isPreVia() {
      return this.preVia;
   }
}
