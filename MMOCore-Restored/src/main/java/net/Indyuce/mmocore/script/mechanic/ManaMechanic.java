package net.Indyuce.mmocore.script.mechanic;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ManaMechanic extends TargetMechanic {
   private final DoubleFormula amount;
   private final Operation operation;
   private final PlayerResourceUpdateEvent.UpdateReason reason;

   public ManaMechanic(ConfigObject config) {
      super(config);
      config.validateKeys(new String[]{"amount"});
      this.amount = new DoubleFormula(config.getString("amount"));
      this.reason = PlayerResourceUpdateEvent.UpdateReason.valueOf(UtilityMethods.enumName(config.getString("reason", "CUSTOM")));
      this.operation = config.contains("operation") ? Operation.valueOf(config.getString("operation").toUpperCase()) : Operation.GIVE;
   }

   public void cast(SkillMetadata meta, Entity target) {
      Validate.isTrue(target instanceof Player, "Target is not a player");
      PlayerData targetData = PlayerData.get(target.getUniqueId());
      if (this.operation == Operation.GIVE) {
         targetData.giveMana(this.amount.evaluate(meta), this.reason);
      } else if (this.operation == Operation.SET) {
         targetData.setMana(this.amount.evaluate(meta));
      } else if (this.operation == Operation.TAKE) {
         targetData.giveMana(-this.amount.evaluate(meta), this.reason);
      }
   }
}
