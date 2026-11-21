package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "healthpercent",
   aliases = {"hppercent"},
   description = "Matches the target's health percentage or multiplier"
)
public class HealthPercentCondition extends SkillCondition implements IEntityCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "percent",
      aliases = {"p"},
      description = "The health percentage or multiplier to check for (e.g., 50% or 0.5)"
   )
   private PlaceholderString percent;
   private boolean includeAbsorption = false;

   public HealthPercentCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.percent = mlc.getPlaceholderString(new String[]{"percent", "p", "healthpercent", "hp"}, "0", this.conditionVar);
      this.includeAbsorption = mlc.getBoolean(new String[]{"includeabsorption", "ia"}, false);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      double maxHealth = entity.getMaxHealth();
      double currentHealth = this.includeAbsorption ? entity.getHealth() + entity.getAbsorption() : entity.getHealth();
      double healthPercent = currentHealth / maxHealth;
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking Health Percent: {0}% vs {1}", healthPercent, this.percent.get(entity));
      return this.checkHealthRange(healthPercent, this.percent.get(entity));
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      double maxHealth = target.getMaxHealth();
      double currentHealth = this.includeAbsorption ? target.getHealth() + target.getAbsorption() : target.getHealth();
      double healthPercent = currentHealth / maxHealth;
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking Health Percent: {0}% vs {1}", healthPercent, this.percent.get(meta, target));
      return this.checkHealthRange(healthPercent, this.percent.get(meta, target));
   }

   private boolean checkHealthRange(double healthPercent, String input) {
      try {
         if (input.endsWith("%")) {
            String percentageString = input.substring(0, input.length() - 1).trim();
            return new RangedDouble(percentageString).equals(healthPercent * 100.0);
         } else {
            return new RangedDouble(input).equals(healthPercent);
         }
      } catch (NumberFormatException e) {
         MythicLogger.error("Invalid health percent input: {0}. Must be a valid number or percentage.", input);
         return false;
      }
   }
}
