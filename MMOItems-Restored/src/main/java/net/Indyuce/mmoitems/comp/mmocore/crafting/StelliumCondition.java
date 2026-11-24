package net.Indyuce.mmoitems.comp.mmocore.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class StelliumCondition extends Condition {
   private final double amount;

   public StelliumCondition(MMOLineConfig var1) {
      super("mana");
      var1.validate(new String[]{"amount"});
      this.amount = var1.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).getStellium() >= this.amount;
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#stellium#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).giveStellium(-this.amount);
   }
}
