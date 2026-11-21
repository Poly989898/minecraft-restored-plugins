package it.ytnoos.lpx.check.checks.book;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.common.utils.TimeUtils;

public class BookB extends PlayerCheck<WrapperPlayClientEditBook> {
   private long last;
   private int delay;
   private RollingIntervalCounter rollingIntervalCounter;

   public BookB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.delay = this.getInt("delay", 1000);
      this.rollingIntervalCounter = new RollingIntervalCounter(this.getInt("max", 15));
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientEditBook wrapperPlayClientEditBook) {
      if (this.rollingIntervalCounter.increment()) {
         this.raiseViolation(wrapperPlayClientEditBook, new FlagInfo[]{FlagInfo.of("pps", this.rollingIntervalCounter.getMaxCounter())});
         return CheckResult.DENY;
      } else if (this.rollingIntervalCounter.isOverLimit()) {
         return CheckResult.DENY;
      } else {
         long longValue = TimeUtils.difference(this.last);
         this.last = TimeUtils.current();
         if (longValue < this.delay) {
            this.raiseViolation(wrapperPlayClientEditBook, new FlagInfo[]{FlagInfo.of("delay", longValue)});
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
