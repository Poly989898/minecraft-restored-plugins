package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "variableSubtract",
   aliases = {"variableSub", "subtractVariable", "subVar", "reduceVariable"},
   description = "Reduces the value of the given variable on the target entity"
)
public class VariableSubtractMechanic extends VariableMechanic implements ITargetedEntitySkill {
   protected PolymorphicPlaceholder amount;

   public VariableSubtractMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.amount = mlc.getPolymorphicPlaceholder(new String[]{"value", "val", "v", "amount", "a"}, "0");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data, target);
      String key = this.key.get(data, target);
      Variable var = variables.get(key);
      if (var == null) {
         return SkillResult.INVALID_CONFIG;
      } else if (!var.executeSubtraction(this.amount, PlaceholderContext.of(data, target, null, null, null))) {
         MythicLogger.errorMechanicConfig(
            this, this.config, "Could not perform subtraction on variable" + this.scope.toString() + "." + key + " of type" + var.getTypeName()
         );
         return SkillResult.INVALID_CONFIG;
      } else {
         this.handleDuration(var);
         return SkillResult.SUCCESS;
      }
   }
}
