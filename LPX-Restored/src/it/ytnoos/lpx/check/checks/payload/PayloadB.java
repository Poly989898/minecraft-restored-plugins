package it.ytnoos.lpx.check.checks.payload;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.check.BukkitCheckManager;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.common.utils.TimeUtils;

public class PayloadB extends PlayerCheck<WrapperPlayClientPluginMessage> {
   private long last;
   private int delay;
   private RollingIntervalCounter rollingIntervalCounter;

   public PayloadB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.delay = this.getInt("delay", 1000);
      this.rollingIntervalCounter = new RollingIntervalCounter(this.getInt("max", 15));
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage) {
      String string = wrapperPlayClientPluginMessage.getChannelName();
      if (string.equals("BungeeCord")) {
         return CheckResult.ALLOW;
      } else if (!BukkitCheckManager.BOOK_CHANNELS.contains(string)) {
         return CheckResult.ALLOW;
      } else if (this.rollingIntervalCounter.increment()) {
         this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[]{FlagInfo.of("pps", this.rollingIntervalCounter.getMaxCounter())});
         return CheckResult.DENY;
      } else if (this.rollingIntervalCounter.isOverLimit()) {
         return CheckResult.DENY;
      } else {
         long longValue = TimeUtils.difference(this.last);
         this.last = TimeUtils.current();
         if (longValue < this.delay) {
            this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[]{FlagInfo.of("delay", longValue)});
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
