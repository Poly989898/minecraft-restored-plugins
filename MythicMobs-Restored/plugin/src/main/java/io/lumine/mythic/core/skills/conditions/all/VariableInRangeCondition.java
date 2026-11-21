package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.VariableCondition;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "variableInRange",
   aliases = {"varinrange", "varrange"},
   version = "4.6",
   description = "Checks if the given numeric variable is within a certain range."
)
public class VariableInRangeCondition
   extends VariableCondition
   implements ISkillMetaComparisonCondition,
   IEntityCondition,
   ILocationCondition,
   ISkillMetaCondition {
   @MythicField(
      name = "value",
      aliases = {"val", "v"},
      description = "A number range to match"
   )
   private PlaceholderString value;

   public VariableInRangeCondition(String line, MythicLineConfig mlc) {
      super(line, mlc);

      try {
         this.value = mlc.getPlaceholderString(new String[]{"value", "val", "v", "range", "r"}, null);
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable range must be set.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);
      float compare = registry.getFloat(this.key.get(target));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "VariableInRange: checking {0} == {1}", compare, this.value.toString());

      try {
         return new RangedDouble(this.value.get(target)).equals(compare);
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableInRange condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, target);
      float compare = registry.getFloat(this.key.get(meta, target));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "VariableInRange: checking {0} == {1}", compare, this.value.toString());

      try {
         return new RangedDouble(this.value.get(meta, target)).equals(compare);
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableInRange condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }

   @Override
   public boolean check(AbstractLocation target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);
      float compare = registry.getFloat(this.key.get());
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "VariableInRange: checking {0} == {1}", compare, this.value.toString());

      try {
         return new RangedDouble(this.value.get()).equals(compare);
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableInRange condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, meta.getCaster().getEntity());
      float compare = registry.getFloat(this.key.get(meta));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "VariableInRange: checking {0} == {1}", compare, this.value.toString());

      try {
         return new RangedDouble(this.value.get(meta)).equals(compare);
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableInRange condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }
}
