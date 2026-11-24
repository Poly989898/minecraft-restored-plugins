package net.Indyuce.mmoitems.comp.eco;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class MoneyCondition extends Condition {
   private final double amount;

   public MoneyCondition(MMOLineConfig var1) {
      super("money");
      Validate.isTrue(MMOItems.plugin.hasEconomy(), "No economy plugin found");
      var1.validateKeys(new String[]{"amount"});
      this.amount = var1.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return MMOItems.plugin.getVault().getEconomy().has(var1.getPlayer(), this.amount);
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#money#", String.valueOf(this.amount));
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      MMOItems.plugin.getVault().getEconomy().withdrawPlayer(var1.getPlayer(), this.amount);
   }

   public double getAmount() {
      return this.amount;
   }
}
