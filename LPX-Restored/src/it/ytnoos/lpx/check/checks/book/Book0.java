package it.ytnoos.lpx.check.checks.book;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.ArrayList;

public class Book0 extends PlayerCheck<WrapperPlayClientEditBook> {
   public Book0(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientEditBook wrapperPlayClientEditBook) {
      ItemStack itemStack = wrapperPlayClientEditBook.getItemStack();
      NBTList nbtList = new NBTList(NBTType.STRING);
      NBTCompound nbtCompound;
      if (itemStack == null) {
         nbtCompound = new NBTCompound();

         for (String string : wrapperPlayClientEditBook.getPages()) {
            nbtList.addTag(new NBTString(string));
         }

         nbtCompound.setTag("pages", nbtList);
         itemStack = ItemStack.builder().type(ItemTypes.WRITABLE_BOOK).nbt(nbtCompound).amount(1).build();
      } else {
         nbtCompound = itemStack.getNBT();
         if (nbtCompound != null) {
            NBTList nbtList2 = nbtCompound.getStringListTagOrNull("pages");
            if (nbtList2 != null) {
               nbtList = nbtList2;
            }
         } else {
            nbtCompound = new NBTCompound();
            itemStack.setNBT(nbtCompound);
         }
      }

      protocolPlayer.setProcessingPayload(true);

      try {
         CheckResult checkResult = protocolPlayer.handleChecks(NBTDecodeEvent.class, new NBTDecodeEvent(wrapperPlayClientEditBook, null, nbtCompound), this.isPreVia()).result();
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         checkResult = checkResult.merge(protocolPlayer.handleChecks(ItemStackDecodeEvent.class, new ItemStackDecodeEvent(wrapperPlayClientEditBook, null, itemStack), this.isPreVia()).result());
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         if (checkResult == CheckResult.CHANGE) {
            ArrayList arrayList = new ArrayList();

            for (Object var14_o : nbtList.getTags()) { NBTString nbtString = (NBTString) var14_o;
               arrayList.add(nbtString.getValue());
            }

            wrapperPlayClientEditBook.setPages(arrayList);
         }

         return checkResult;
      } finally {
         protocolPlayer.setProcessingPayload(false);
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
