package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class StaminaCondition extends Condition {
   private final double amount;

   public StaminaCondition(MMOLineConfig var1) {
      super("stamina");
      var1.validate(new String[]{"amount"});
      this.amount = var1.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return var1.getRPG().getStamina() >= this.amount;
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#stamina#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      var1.getRPG().giveStamina(-this.amount);
   }
}
