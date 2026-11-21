package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "currencyGive",
   aliases = {"giveCurrency"},
   description = "Gives an amount of vault currency. Requires Vault."
)
public class CurrencyGiveMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount to give",
      defValue = "0"
   )
   private final PlaceholderDouble amount;

   public CurrencyGiveMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 0.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         if (this.getPlugin().getCompatibility().getVault().isPresent()) {
            this.getPlugin().getCompatibility().getVault().get().giveMoney(target.asPlayer(), this.amount.get(data, target));
         }

         return SkillResult.SUCCESS;
      }
   }
}
