package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class ItemB extends PlayerCheck<NBTDecodeEvent> {
   private int max;
   private int maxModern;

   public ItemB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getInt("max", 50);
      this.maxModern = this.getInt("max-modern", 100);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      NBT nbt = nbtDecodeEvent.getNbt();
      if (!(nbt instanceof NBTCompound)) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = ((NBTCompound)nbt).getStringListTagOrNull("pages");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      boolean flag = protocolPlayer.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14);
      int value = flag ? this.maxModern : this.max;
      if (nbtList.size() <= value) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("pages", nbtList.size()), FlagInfo.of("max", value), FlagInfo.of("modern", flag)});
      return CheckResult.DENY;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
