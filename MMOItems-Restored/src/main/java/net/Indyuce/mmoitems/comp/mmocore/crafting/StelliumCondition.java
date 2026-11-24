package net.Indyuce.mmoitems.comp.mmocore.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class StelliumCondition extends Condition {
   private final double amount;

   public StelliumCondition(MMOLineConfig mmoLineConfig) {
      super("mana");
      mmoLineConfig.validate(new String[]{"amount"});
      this.amount = mmoLineConfig.getDouble("amount");
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).getStellium() >= this.amount;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#stellium#", "" + this.amount);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).giveStellium(-this.amount);
   }
}
