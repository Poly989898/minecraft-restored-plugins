package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.VariableCondition;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "variableIsSet",
   aliases = {"varisset", "varset"},
   version = "4.6",
   description = "Checks if the given variable is set."
)
public class VariableIsSetCondition extends VariableCondition implements IEntityCondition, ILocationCondition, ISkillMetaCondition {
   public VariableIsSetCondition(String line, MythicLineConfig mlc) {
      super(line, mlc);
   }

   @Override
   public boolean check(AbstractEntity target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);

      try {
         return registry.has(this.key.get(target));
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableIsSet condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }

   @Override
   public boolean check(AbstractLocation target) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);

      try {
         return registry.has(this.key.get());
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableIsSet condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, meta.getCaster().getEntity());

      try {
         return registry.has(this.key.get(meta));
      } catch (Exception e) {
         MythicLogger.error("Failed to process VariableIsSet condition.");
         MythicLogger.handleMinorError(e);
         return false;
      }
   }
}
