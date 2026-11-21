package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.utils.ItemUtils;

public class CreativeC extends PlayerCheck<WrapperPlayClientCreativeInventoryAction> {
   private int max;

   public CreativeC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getInt("max", 3000);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientCreativeInventoryAction wrapperPlayClientCreativeInventoryAction) {
      ItemStack itemStack = wrapperPlayClientCreativeInventoryAction.getItemStack();
      String string = (String)ItemUtils.getDisplayName(itemStack).orElse(null);
      if (string == null) {
         return CheckResult.ALLOW;
      }

      int value = string.length();
      if (value <= this.max) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(wrapperPlayClientCreativeInventoryAction, new FlagInfo[]{FlagInfo.of("length", value), FlagInfo.of("display-name", string)});
      return CheckResult.DENY;
   }

}
