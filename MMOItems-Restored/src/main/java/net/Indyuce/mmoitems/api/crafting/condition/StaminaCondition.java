package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class StaminaCondition extends Condition {
   private final double amount;

   public StaminaCondition(MMOLineConfig mmoLineConfig) {
      super("stamina");
      mmoLineConfig.validate(new String[]{"amount"});
      this.amount = mmoLineConfig.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return playerData.getRPG().getStamina() >= this.amount;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#stamina#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      playerData.getRPG().giveStamina(-this.amount);
   }
}
