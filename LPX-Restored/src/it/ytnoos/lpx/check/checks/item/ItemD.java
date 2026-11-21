package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class ItemD extends PlayerCheck<ItemStackDecodeEvent> {
   private int maxBytes;
   private int resolvedTolerance;
   private boolean resize;

   public ItemD(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxBytes = this.getInt("max-bytes", 255);
      this.resolvedTolerance = this.getInt("resolved-tolerance", 3);
      this.resize = this.getBoolean("resize", true);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      NBTCompound nbtCompound = itemStackDecodeEvent.getItemStack().getNBT();
      if (nbtCompound == null) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = nbtCompound.getStringListTagOrNull("pages");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      boolean flag = false;
      boolean flag2 = ItemA.isResolved(itemStackDecodeEvent.getItemStack());

      for (int value = 0; value < nbtList.size(); value++) {
         NBTString nbtString = (NBTString)nbtList.getTag(value);
         byte[] bs = nbtString.getValue().getBytes();
         int value2 = bs.length;
         if (value2 > this.maxBytes) {
            if (!flag2 || value2 > this.maxBytes * this.resolvedTolerance) {
               this.raiseViolation(
                  itemStackDecodeEvent,
                  new FlagInfo[]{
                     FlagInfo.of("pages", nbtList.size()), FlagInfo.of("page-index", value), FlagInfo.of("page-bytes", value2), FlagInfo.of("resolved", flag2)
                  }
               );
               return CheckResult.DENY;
            }

            if (this.resize) {
               nbtString.setValue(new String(bs, 0, this.maxBytes));
               flag = true;
            }
         }
      }

      return flag ? CheckResult.CHANGE : CheckResult.ALLOW;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
