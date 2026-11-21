package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class ItemA extends PlayerCheck<ItemStackDecodeEvent> {
   private static final String RESOLVED = "resolved";

   public ItemA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public static boolean isResolved(ItemStack itemStack) {
      ItemType itemType = itemStack.getType();
      boolean flag = itemType == ItemTypes.WRITTEN_BOOK;
      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound != null && nbtCompound.getTags().containsKey("resolved")) {
         flag = nbtCompound.getBoolean("resolved");
      }

      return flag;
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemStack itemStack = itemStackDecodeEvent.getItemStack();
      ItemType itemType = itemStack.getType();
      if (isResolved(itemStack) && itemType != ItemTypes.WRITTEN_BOOK) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("resolved", true), FlagInfo.of("item", itemType.getName())});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
