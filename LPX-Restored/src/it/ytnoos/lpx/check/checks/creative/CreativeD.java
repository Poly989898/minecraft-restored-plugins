package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.utils.ItemUtils;

public class CreativeD extends PlayerCheck<WrapperPlayClientCreativeInventoryAction> {
   private int max;

   public CreativeD(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getInt("max", 16384);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientCreativeInventoryAction wrapperPlayClientCreativeInventoryAction) {
      ItemStack itemStack = wrapperPlayClientCreativeInventoryAction.getItemStack();
      NBTList nbtList = (NBTList)ItemUtils.getLore(itemStack).orElse(null);
      if (nbtList != null && !nbtList.isEmpty()) {
         int value = 0;

         for (Object var15_o : nbtList.getTags()) { NBTString nbtString = (NBTString) var15_o;
            value += nbtString.getValue().length();
         }

         if (value <= this.max) {
            return CheckResult.ALLOW;
         }

         this.raiseViolation(wrapperPlayClientCreativeInventoryAction, new FlagInfo[]{FlagInfo.of("lore-length", value)});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

}
