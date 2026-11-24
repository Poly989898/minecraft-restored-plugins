package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;

public class StaminaTrigger extends Trigger {
   private final RandomAmount amount;
   private final ManaTrigger.Operation operation;

   public StaminaTrigger(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"amount"});
      this.amount = new RandomAmount(config.getString("amount"));
      this.operation = config.contains("operation") ? ManaTrigger.Operation.valueOf(config.getString("operation").toUpperCase()) : ManaTrigger.Operation.GIVE;
   }

   @Override
   public void apply(PlayerData player) {
      if (this.operation == ManaTrigger.Operation.GIVE) {
         player.giveStamina(this.amount.calculate(), PlayerResourceUpdateEvent.UpdateReason.TRIGGER);
      } else if (this.operation == ManaTrigger.Operation.SET) {
         player.setStamina(this.amount.calculate());
      } else {
         player.giveStamina(-this.amount.calculate(), PlayerResourceUpdateEvent.UpdateReason.TRIGGER);
      }
   }
}
