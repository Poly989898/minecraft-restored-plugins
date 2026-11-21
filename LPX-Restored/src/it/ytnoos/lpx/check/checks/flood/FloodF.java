package it.ytnoos.lpx.check.checks.flood;

import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FloodF extends PlayerCheck<PlayerCommandPreprocessEvent> {
   private RollingIntervalCounter rollingIntervalCounter;

   public FloodF(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.rollingIntervalCounter = new RollingIntervalCounter(this.getInt("max", 8));
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
      if (this.rollingIntervalCounter.increment()) {
         this.raiseViolation(playerCommandPreprocessEvent, new FlagInfo[]{FlagInfo.of("command", playerCommandPreprocessEvent.getMessage())});
         return CheckResult.DENY;
      } else {
         return this.rollingIntervalCounter.isOverLimit() ? CheckResult.DENY : CheckResult.ALLOW;
      }
   }

}
