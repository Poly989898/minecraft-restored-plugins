package net.Indyuce.mmoitems.comp.eco;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class MoneyCondition extends Condition {
   private final double amount;

   public MoneyCondition(MMOLineConfig mmoLineConfig) {
      super("money");
      Validate.isTrue(MMOItems.plugin.hasEconomy(), "No economy plugin found");
      mmoLineConfig.validateKeys(new String[]{"amount"});
      this.amount = mmoLineConfig.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return MMOItems.plugin.getVault().getEconomy().has(playerData.getPlayer(), this.amount);
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#money#", String.valueOf(this.amount));
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      MMOItems.plugin.getVault().getEconomy().withdrawPlayer(playerData.getPlayer(), this.amount);
   }

   public double getAmount() {
      return this.amount;
   }
}
