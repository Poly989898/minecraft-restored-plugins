package it.ytnoos.lpx.check.checks.flood;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RepeatedIntervalCounter;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.utils.ItemUtils;

public class FloodC extends PlayerCheck<ItemStackDecodeEvent> {
   private RepeatedIntervalCounter repeatedIntervalCounter;
   private RollingIntervalCounter sustained;
   private long bigThreshold;
   private long harmlessThreshold;
   private double harmlessMultiplier;

   public FloodC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      int value = this.getInt("reset-interval", 100);
      int value2 = this.getInt("max-periods", 6);
      int value3 = this.getInt("max-warnings", 3);
      int value4 = this.getInt("sustained-max", 32);
      int value5 = this.getInt("sustained-interval", 1000);
      this.bigThreshold = this.getLong("big-threshold", 6000L);
      this.harmlessThreshold = this.getLong("harmless-threshold", 512L);
      this.harmlessMultiplier = Math.max(0.01, this.getDouble("harmless-multiplier", 0.05));
      this.repeatedIntervalCounter = new RepeatedIntervalCounter(this.getInt("max", 20), value, value2, value3);
      this.sustained = value4 >= 0 ? new RollingIntervalCounter(value4, value5) : null;
   }

   protected CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (protocolPlayer.getPrinterData().isUsingPrinter()) {
         return CheckResult.ALLOW;
      }

      int value = itemStackDecodeEvent.getItemStack().getDecodedSizeBytes();
      if (value <= 0) {
         return CheckResult.ALLOW;
      }

      double number = (int)(value / this.bigThreshold / 4L);
      if (number == 0.0) {
         number = 1.0;
      }

      if (value < this.bigThreshold) {
         number = 0.25;
      }

      if (value <= this.harmlessThreshold) {
         number *= this.harmlessMultiplier;
      }

      if (ItemUtils.isEmpty(itemStackDecodeEvent.getItemStack())) {
         number *= 0.15;
      } else {
         number *= protocolPlayer.itemFloodMultiplier();
      }

      if (this.repeatedIntervalCounter.increment(number)) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("max-counter", this.repeatedIntervalCounter.getMaxCounter()), FlagInfo.of("size", value)});
         return CheckResult.DENY;
      }

      if (this.sustained != null) {
         if (this.sustained.increment(number)) {
            this.raiseViolation(
               itemStackDecodeEvent,
               new FlagInfo[]{
                  FlagInfo.of("mode", "rolling"),
                  FlagInfo.of("max-counter", this.sustained.getMaxCounter()),
                  FlagInfo.of("interval", this.sustained.getInterval() + "ms"),
                  FlagInfo.of("size", value)
               }
            );
            return CheckResult.DENY;
         }

         if (this.sustained.isOverLimit()) {
            return CheckResult.DENY;
         }
      }

      return CheckResult.ALLOW;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
