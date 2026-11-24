package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class ManaCondition extends Condition {
   private final double amount;

   public ManaCondition(MMOLineConfig mmoLineConfig) {
      super("mana");
      mmoLineConfig.validate(new String[]{"amount"});
      this.amount = mmoLineConfig.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return playerData.getRPG().getMana() >= this.amount;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#mana#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      playerData.getRPG().giveMana(-this.amount);
   }
}
