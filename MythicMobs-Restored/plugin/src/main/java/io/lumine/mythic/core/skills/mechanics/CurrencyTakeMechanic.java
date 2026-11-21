package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "currencyTake",
   aliases = {"takeCurrency"},
   description = "Removes an amount of vault currency"
)
public class CurrencyTakeMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount to remove",
      defValue = "0"
   )
   private final PlaceholderDouble amount;

   public CurrencyTakeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 0.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         if (this.getPlugin().getCompatibility().getVault().isPresent()) {
            this.getPlugin().getCompatibility().getVault().get().takeMoney(target.asPlayer(), this.amount.get(data, target));
         } else {
            MythicLogger.errorMechanicConfig(this, this.config, "Could not run TakeCurrency mechanic: Vault not found.");
         }

         return SkillResult.SUCCESS;
      }
   }
}
