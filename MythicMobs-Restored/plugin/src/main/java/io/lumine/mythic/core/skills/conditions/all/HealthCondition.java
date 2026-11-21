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
   name = "health",
   aliases = {"hp"},
   description = "Matches the target's health"
)
public class HealthCondition extends SkillCondition implements IEntityCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The health range to check for"
   )
   private PlaceholderString amount;
   private boolean includeAbsorption = false;

   public HealthCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = mlc.getPlaceholderString(new String[]{"amount", "a", "health", "h"}, "0", this.conditionVar);
      this.includeAbsorption = mlc.getBoolean(new String[]{"includeabsorption", "ia"}, false);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      double health = this.includeAbsorption ? entity.getHealth() + entity.getAbsorption() : entity.getHealth();
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking Health: {0} vs {1}", health, this.amount.get(entity));
      return new RangedDouble(this.amount.get(entity)).equals(health);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      double health = this.includeAbsorption ? target.getHealth() + target.getAbsorption() : target.getHealth();
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking Health: {0} vs {1}", health, this.amount.get(meta, target));
      return new RangedDouble(this.amount.get(meta, target)).equals(health);
   }
}
