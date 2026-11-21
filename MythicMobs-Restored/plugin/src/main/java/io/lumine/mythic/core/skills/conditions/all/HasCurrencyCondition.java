package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "hasCurrency",
   aliases = {"hasmoney"},
   description = "If the target has the given amount of vault currency"
)
public class HasCurrencyCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of currency"
   )
   protected double amount;

   public HasCurrencyCondition(String line, MythicLineConfig mlc) {
      super(line);
      String amount = mlc.getString(new String[]{"amount", "a"}, String.valueOf(Double.MAX_VALUE), this.conditionVar);
      this.amount = Double.valueOf(amount);
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (!target.isPlayer()) {
         return false;
      } else {
         return MythicBukkit.inst().getCompatibility().getVault().isPresent()
            ? MythicBukkit.inst().getCompatibility().getVault().get().hasMoney(target.asPlayer(), this.amount)
            : false;
      }
   }
}
