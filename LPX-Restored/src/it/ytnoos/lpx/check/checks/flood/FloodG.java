package it.ytnoos.lpx.check.checks.flood;

import com.github.retrooper.packetevents.event.simple.ReadSlotsEvent;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RepeatedIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class FloodG extends PlayerCheck<ReadSlotsEvent> {
   private RepeatedIntervalCounter counter;
   private boolean cancel;
   private int lowSlotsThreshold;
   private double lowSlotsMultiplier;

   public FloodG(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      int value = this.getConfig().getInt(this.getPath("max"), 70);
      int value2 = this.getConfig().getInt(this.getPath("interval"), 250);
      int value3 = this.getConfig().getInt(this.getPath("periods"), 5);
      int value4 = this.getConfig().getInt(this.getPath("warnings"), 2);
      this.counter = new RepeatedIntervalCounter(value, value2, value3, value4);
      this.cancel = this.getConfig().getBoolean(this.getPath("cancel"), true);
      this.lowSlotsThreshold = Math.max(0, this.getInt("low-slots-threshold", 3));
      this.lowSlotsMultiplier = Math.max(0.1, this.getDouble("low-slots-multiplier", 0.7));
   }

   protected CheckResult check(ProtocolPlayer protocolPlayer, ReadSlotsEvent readSlotsEvent) {
      double number = readSlotsEvent.getSlotsCount() * protocolPlayer.itemFloodMultiplier();
      if (readSlotsEvent.getSlotsCount() <= this.lowSlotsThreshold) {
         number *= this.lowSlotsMultiplier;
      }

      if (this.counter.increment(number)) {
         this.raiseViolation(
            readSlotsEvent,
            new FlagInfo[]{
               FlagInfo.of("max-counter", this.counter.getMaxCounter()),
               FlagInfo.of("interval", this.counter.getInterval() + "ms"),
               FlagInfo.of("slots-count", readSlotsEvent.getSlotsCount()),
               FlagInfo.of("amount", number)
            }
         );
         return this.cancel ? CheckResult.DENY : CheckResult.ALLOW;
      } else {
         return this.cancel && this.counter.isOverLimit() ? CheckResult.DENY : CheckResult.ALLOW;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
