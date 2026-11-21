package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class ItemF extends PlayerCheck<NBTDecodeEvent> {
   private boolean replace;

   public ItemF(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.replace = this.getBoolean("replace", true);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      if (!(nbtDecodeEvent.getNbt() instanceof NBTCompound)) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = ((NBTCompound)nbtDecodeEvent.getNbt()).getStringListTagOrNull("pages");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      if (!this.replace) {
         return CheckResult.ALLOW;
      }

      for (int value = 0; value < nbtList.size(); value++) {
         NBTString nbtString = (NBTString)nbtList.getTag(value);
         nbtString.setValue(nbtString.getValue().replace('.', '․'));
      }

      return CheckResult.CHANGE;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
