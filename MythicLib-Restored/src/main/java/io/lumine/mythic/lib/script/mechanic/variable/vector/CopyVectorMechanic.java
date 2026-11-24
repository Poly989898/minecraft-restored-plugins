package io.lumine.mythic.lib.script.mechanic.variable.vector;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.variable.VariableMechanic;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;

@MechanicMetadata
public class CopyVectorMechanic extends VariableMechanic {
   private final String varName;

   public CopyVectorMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("value");
      this.varName = config.getString("value");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Variable var = meta.getVariable(this.varName);
      Validate.isTrue(var instanceof PositionVariable, "Variable '" + var.getName() + "' is not a vector");
      this.getTargetVariableList(meta).registerVariable(new PositionVariable(this.getVariableName(), ((PositionVariable)var).getStored().clone()));
   }
}
