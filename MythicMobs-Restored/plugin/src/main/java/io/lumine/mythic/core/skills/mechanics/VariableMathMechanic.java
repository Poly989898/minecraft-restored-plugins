package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.interfaces.NumericVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "variableMath",
   aliases = {"varMath", "mathVariable", "mathVar"},
   description = "Sets a variable to the result of a math equation, where 'x' is the variable's current value"
)
public class VariableMathMechanic extends VariableMechanic implements ITargetedEntitySkill {
   protected PlaceholderString equation;

   public VariableMathMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.equation = mlc.getPlaceholderString(new String[]{"equation", "eq", "e"}, "x");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data, target);
      Variable var = variables.get(this.key.get(data, target));
      if (var == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         String eq = this.equation.get(data, target);
         if (var instanceof NumericVariable numericVariable) {
            numericVariable.executeMath(eq);
            this.handleDuration(var);
            return SkillResult.SUCCESS;
         } else {
            MythicLogger.errorMechanicConfig(this, this.config, "Could not perform math operation on non-numeric variable type");
            return SkillResult.INVALID_CONFIG;
         }
      }
   }
}
