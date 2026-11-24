package net.Indyuce.mmocore.comp.vault;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;

public class MoneyTrigger extends Trigger {
   private final RandomAmount amount;
   private final MoneyTrigger.Operation operation;

   public MoneyTrigger(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"amount"});
      this.amount = new RandomAmount(config.getString("amount"));
      this.operation = config.contains("operation") ? MoneyTrigger.Operation.valueOf(config.getString("operation").toUpperCase()) : MoneyTrigger.Operation.GIVE;
   }

   @Override
   public void apply(PlayerData player) {
      if (MMOCore.plugin.economy.isValid() && player.isOnline()) {
         if (this.operation == MoneyTrigger.Operation.GIVE) {
            MMOCore.plugin.economy.getEconomy().depositPlayer(player.getPlayer(), this.amount.calculate());
         } else {
            if (this.operation == MoneyTrigger.Operation.SET) {
               throw new IllegalArgumentException("Operation SET is not available for the money trigger.");
            }

            MMOCore.plugin.economy.getEconomy().withdrawPlayer(player.getPlayer(), this.amount.calculate());
         }
      }
   }

   public enum Operation {
      GIVE,
      SET,
      TAKE;
   }
}
