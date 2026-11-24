package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;

public class ManaTrigger extends Trigger {
   private final RandomAmount amount;
   private final ManaTrigger.Operation operation;

   public ManaTrigger(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"amount"});
      this.amount = new RandomAmount(config.getString("amount"));
      this.operation = config.contains("operation") ? ManaTrigger.Operation.valueOf(config.getString("operation").toUpperCase()) : ManaTrigger.Operation.GIVE;
   }

   @Override
   public void apply(PlayerData player) {
      if (this.operation == ManaTrigger.Operation.GIVE) {
         player.giveMana(this.amount.calculate(), PlayerResourceUpdateEvent.UpdateReason.TRIGGER);
      } else if (this.operation == ManaTrigger.Operation.SET) {
         player.setMana(this.amount.calculate());
      } else {
         player.giveMana(-this.amount.calculate(), PlayerResourceUpdateEvent.UpdateReason.TRIGGER);
      }
   }

   public enum Operation {
      GIVE,
      SET,
      TAKE;
   }
}
