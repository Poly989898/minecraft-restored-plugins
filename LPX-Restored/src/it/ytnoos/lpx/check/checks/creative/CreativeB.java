package it.ytnoos.lpx.check.checks.creative;

import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class CreativeB extends PlayerCheck<WrapperPlayClientCreativeInventoryAction> {
   private int max;
   private int min;

   public CreativeB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getInt("max", 127);
      this.min = this.getInt("min", 0);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientCreativeInventoryAction wrapperPlayClientCreativeInventoryAction) {
      try {
         for (Enchantment enchantment : wrapperPlayClientCreativeInventoryAction.getItemStack().getEnchantments(protocolPlayer.getUser().getClientVersion())) {
            int value = enchantment.getLevel();
            if (value > this.max || value < this.min) {
               this.raiseViolation(wrapperPlayClientCreativeInventoryAction, new FlagInfo[]{FlagInfo.of("enchantment", enchantment.getType().getName()), FlagInfo.of("enchantment-level", value)});
               return CheckResult.DENY;
            }
         }
      } catch (Exception exception) {
         return CheckResult.ALLOW;
      }

      return CheckResult.ALLOW;
   }
}
