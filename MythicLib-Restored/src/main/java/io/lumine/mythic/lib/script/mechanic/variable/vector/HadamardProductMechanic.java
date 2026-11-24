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
      Variable var1x = meta.getVariable(this.varName1);
      Validate.isTrue(var1x instanceof PositionVariable, "Variable '" + this.varName1 + "' is not a vector");
      Position pos1 = (Position)var1x.getStored();
      Variable var2 = meta.getVariable(this.varName2);
      Validate.isTrue(var2 instanceof PositionVariable, "Variable '" + this.varName2 + "' is not a vector");
      Position pos2 = (Position)var2.getStored();
      this.getTargetVariableList(meta).registerVariable(new PositionVariable(this.getVariableName(), this.hadamardProduct(pos1, pos2)));
   }

   private Position hadamardProduct(Position pos1, Position pos2) {
      return new Position(pos1.getWorld(), pos1.getX() * pos2.getX(), pos1.getY() * pos2.getY(), pos1.getZ() * pos2.getZ());
   }
}
