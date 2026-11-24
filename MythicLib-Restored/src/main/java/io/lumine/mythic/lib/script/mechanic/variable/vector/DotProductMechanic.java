package io.lumine.mythic.lib.script.mechanic.variable.vector;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.variable.VariableMechanic;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.DoubleVariable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.Position;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;

@MechanicMetadata
public class DotProductMechanic extends VariableMechanic {
   private final String varName1;
   private final String varName2;

   public DotProductMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("vec1", "vec2");
      this.varName1 = config.getString("vec1");
      this.varName2 = config.getString("vec2");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Variable var1x = meta.getVariable(this.varName1);
      Validate.isTrue(var1x instanceof PositionVariable, "Variable '" + this.varName1 + "' is not a vector");
      Position vec1 = (Position)var1x.getStored();
      Variable variable = meta.getVariable(this.varName2);
      Validate.isTrue(variable instanceof PositionVariable, "Variable '" + this.varName2 + "' is not a vector");
      Position vec2 = (Position)variable.getStored();
      this.getTargetVariableList(meta).registerVariable(new DoubleVariable(this.getVariableName(), vec1.dot(vec2)));
   }
}
