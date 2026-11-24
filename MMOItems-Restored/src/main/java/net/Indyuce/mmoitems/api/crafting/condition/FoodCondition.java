package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class FoodCondition extends Condition {
   private final int amount;

   public FoodCondition(MMOLineConfig var1) {
      super("food");
      var1.validate(new String[]{"amount"});
      this.amount = var1.getInt("amount");
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return !var1.isOnline() ? false : var1.getPlayer().getFoodLevel() >= this.amount;
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#food#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         var1.getPlayer().setFoodLevel(Math.max(0, var1.getPlayer().getFoodLevel() - this.amount));
      }
   }
}
