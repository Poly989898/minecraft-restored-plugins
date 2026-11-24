package io.lumine.mythic.lib.script.mechanic.variable;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.IntegerVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;

@MechanicMetadata
public class IncrementMechanic extends VariableMechanic {
   public IncrementMechanic(ConfigObject config) {
      super(config);
   }

   @Override
   public void cast(SkillMetadata meta) {
      Variable var = this.getTargetVariableList(meta).getVariable(this.getVariableName());
      Validate.notNull(var, "Could not find int variable '" + this.getVariableName() + "'");
      Validate.isTrue(var instanceof IntegerVariable, "Variable '" + var + "' is not an int");
      var.setStored(((IntegerVariable)var).getStored() + 1);
   }
}
