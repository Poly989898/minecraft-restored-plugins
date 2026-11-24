package io.lumine.mythic.lib.script.mechanic.variable.vector;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.variable.VariableMechanic;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.Position;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;

@MechanicMetadata
public class HadamardProductMechanic extends VariableMechanic {
   private final String varName1;
   private final String varName2;

   public HadamardProductMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("vec1", "vec2");
      this.varName1 = config.getString("vec1");
      this.varName2 = config.getString("vec2");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Variable firstVariable = meta.getVariable(this.varName1);
      Validate.isTrue(firstVariable instanceof PositionVariable, "Variable '" + this.varName1 + "' is not a vector");
      Position first = (Position)firstVariable.getStored();
      Variable variable = meta.getVariable(this.varName2);
      Validate.isTrue(variable instanceof PositionVariable, "Variable '" + this.varName2 + "' is not a vector");
      Position second = (Position)variable.getStored();
      this.getTargetVariableList(meta).registerVariable(new PositionVariable(this.getVariableName(), this.hadamardProduct(first, second)));
   }

   private Position hadamardProduct(Position first, Position second) {
      return new Position(first.getWorld(), first.getX() * second.getX(), first.getY() * second.getY(), first.getZ() * second.getZ());
   }
}
