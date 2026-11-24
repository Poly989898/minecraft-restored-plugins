package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.api.Removable;
import net.Indyuce.mmocore.api.quest.trigger.api.Temporary;
import org.apache.commons.lang.Validate;

public class StatTrigger extends Trigger implements Removable, Temporary {
   private final StatModifier modifier;
   private final String stat;
   private final double amount;

   public StatTrigger(MMOLineConfig config) {
      super(config);
      config.validateKeys(new String[]{"amount"});
      config.validateKeys(new String[]{"stat"});
      config.validateKeys(new String[]{"type"});
      String type = config.getString("type").toUpperCase();
      Validate.isTrue(type.equals("FLAT") || type.equals("RELATIVE"));
      this.stat = config.getString("stat");
      this.amount = config.getDouble("amount");
      this.modifier = new StatModifier(Trigger.STAT_MODIFIER_KEY, this.stat, this.amount, ModifierType.valueOf(type));
   }

   @Override
   public void apply(PlayerData player) {
      StatModifier prevModifier = (StatModifier)player.getMMOPlayerData().getStatMap().getInstance(this.stat).getModifier(this.modifier.getUniqueId());
      if (prevModifier == null) {
         this.modifier.register(player.getMMOPlayerData());
      } else {
         prevModifier.add(this.amount).register(player.getMMOPlayerData());
      }
   }

   @Override
   public void remove(PlayerData playerData) {
      this.modifier.unregister(playerData.getMMOPlayerData());
   }
}
