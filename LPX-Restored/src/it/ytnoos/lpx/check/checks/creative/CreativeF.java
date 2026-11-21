package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class CreativeF extends PlayerCheck<WrapperPlayClientCreativeInventoryAction> {
   private int max;

   public CreativeF(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getInt("max", 64);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientCreativeInventoryAction wrapperPlayClientCreativeInventoryAction) {
      ItemStack itemStack = wrapperPlayClientCreativeInventoryAction.getItemStack();
      int value = itemStack.getAmount();
      if (value <= this.max) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(wrapperPlayClientCreativeInventoryAction, new FlagInfo[]{FlagInfo.of("amount", value)});
      return CheckResult.DENY;
   }

}
