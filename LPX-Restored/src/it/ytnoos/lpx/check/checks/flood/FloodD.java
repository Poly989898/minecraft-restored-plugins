package it.ytnoos.lpx.check.checks.flood;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RollingIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class FloodD extends PlayerCheck<ItemStackDecodeEvent> {
   private RollingIntervalCounter counter;
   private int bigThreshold;
   private RollingIntervalCounter bigBookCounter;

   public FloodD(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.counter = new RollingIntervalCounter(this.getConfig().getInt(this.getPath("max"), 13));
      this.bigThreshold = this.getConfig().getInt(this.getPath("big-threshold"), 4000);
      this.bigBookCounter = new RollingIntervalCounter(this.getConfig().getInt(this.getPath("max-big"), 6));
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemStack itemStack = itemStackDecodeEvent.getItemStack();
      if (itemStack.getType() != ItemTypes.WRITABLE_BOOK) {
         return CheckResult.ALLOW;
      }

      if (this.counter.increment()) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("books-ps", this.counter.getMaxCounter())});
         return CheckResult.DENY;
      }

      if (this.counter.isOverLimit()) {
         return CheckResult.DENY;
      }

      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound != null && nbtCompound.getTags().containsKey("pages")) {
         int value = itemStack.getDecodedSizeBytes();
         if (value <= 0) {
            return CheckResult.ALLOW;
         }

         if (value > this.bigThreshold) {
            if (this.bigBookCounter.increment()) {
               this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("big-books-ps", this.bigBookCounter.getMaxCounter()), FlagInfo.of("size", value)});
               return CheckResult.DENY;
            }

            if (this.bigBookCounter.isOverLimit()) {
               return CheckResult.DENY;
            }
         }

         return CheckResult.ALLOW;
      } else {
         return CheckResult.ALLOW;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
