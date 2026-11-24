package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class FoodCondition extends Condition {
   private final int amount;

   public FoodCondition(MMOLineConfig mmoLineConfig) {
      super("food");
      mmoLineConfig.validate(new String[]{"amount"});
      this.amount = mmoLineConfig.getInt("amount");
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return !playerData.isOnline() ? false : playerData.getPlayer().getFoodLevel() >= this.amount;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#food#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         playerData.getPlayer().setFoodLevel(Math.max(0, playerData.getPlayer().getFoodLevel() - this.amount));
      }
   }
}
