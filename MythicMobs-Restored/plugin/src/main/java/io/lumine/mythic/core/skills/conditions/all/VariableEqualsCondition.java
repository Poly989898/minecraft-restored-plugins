package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableCondition;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "variableEquals",
   aliases = {"variableeq", "varequals", "vareq"},
   version = "4.6",
   description = "Checks if the given variable has a particular value."
)
public class VariableEqualsCondition
   extends VariableCondition
   implements IEntityCondition,
   ILocationCondition,
   ISkillMetaCondition,
   ISkillMetaComparisonCondition {
   @MythicField(
      name = "value",
      aliases = {"val", "v"},
      description = "The value to match"
   )
   private PolymorphicPlaceholder value;

   public VariableEqualsCondition(String line, MythicLineConfig mlc) {
      super(line, mlc);

      try {
         this.value = mlc.getPolymorphicPlaceholder(new String[]{"value", "val", "v"}, null);
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable name must be set.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);
      if (registry == null) {
         return false;
      } else {
         try {
            String keyName = this.key.get(target);
            Variable var = registry.get(keyName);
            if (var == null) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.CONDITION,
                  "Variable {1} not found in registry {2}. VariableEquals condition could not be normally parsed, and it's returning false",
                  keyName,
                  this.scope.toString()
               );
               return false;
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "VariableEquals: checking {0} == {1}", this.value, var.toString());
               return var.checkEquality(this.value, PlaceholderContext.builder().entity(target).build());
            }
         } catch (Exception e) {
            MythicLogger.errorCondition(this, "Failed to process VariableEquals condition", e);
            return false;
         }
      }
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, target);
      if (registry == null) {
         return false;
      } else {
         try {
            String keyName = this.key.get(meta, target);
            Variable var = registry.get(keyName);
            if (var == null) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.CONDITION,
                  "Variable {1} not found in registry {2}. VariableEquals condition could not be normally parsed, and it's returning false",
                  keyName,
                  this.scope.toString()
               );
               return false;
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "VariableEquals: checking {0} == {1}", this.value, var.toString());
               return var.checkEquality(this.value, PlaceholderContext.of(meta, target, null, null, null));
            }
         } catch (Exception e) {
            MythicLogger.errorCondition(this, "Failed to process VariableEquals condition", e);
            return false;
         }
      }
   }

   @Override
   public boolean check(AbstractLocation location) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, location);
      if (registry == null) {
         return false;
      } else {
         try {
            String keyName = this.key.get();
            Variable var = registry.get(keyName);
            if (var == null) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.CONDITION,
                  "Variable {1} not found in registry {2}. VariableEquals condition could not be normally parsed, and it's returning false",
                  keyName,
                  this.scope.toString()
               );
               return false;
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "VariableEquals: checking {0} == {1}", this.value, var.toString());
               return var.checkEquality(this.value, PlaceholderContext.builder().location(location).build());
            }
         } catch (Exception e) {
            MythicLogger.errorCondition(this, "Failed to process VariableEquals condition", e);
            return false;
         }
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, meta.getCaster().getEntity());
      if (registry == null) {
         return false;
      } else {
         try {
            String keyName = this.key.get(meta);
            Variable var = registry.get(keyName);
            if (var == null) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.CONDITION,
                  "Variable {1} not found in registry {2}. VariableEquals condition could not be normally parsed, and it's returning false",
                  keyName,
                  this.scope.toString()
               );
               return false;
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "VariableEquals: checking {0} == {1}", this.value, var.toString());
               return var.checkEquality(this.value, PlaceholderContext.of(meta, meta.getCaster().getEntity(), null, null, null));
            }
         } catch (Exception e) {
            MythicLogger.errorCondition(this, "Failed to process VariableEquals condition", e);
            return false;
         }
      }
   }
}
