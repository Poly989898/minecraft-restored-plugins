package io.lumine.mythic.lib.script.mechanic.variable.vector;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.variable.VariableMechanic;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.Position;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;

@MechanicMetadata
public class SubtractVectorMechanic extends VariableMechanic {
   private final DoubleFormula x;
   private final DoubleFormula y;
   private final DoubleFormula z;
   private final String varToSubstract;

   public SubtractVectorMechanic(ConfigObject config) {
      super(config);
      this.x = config.contains("x") ? new DoubleFormula(config.getString("x")) : DoubleFormula.ZERO;
      this.y = config.contains("y") ? new DoubleFormula(config.getString("y")) : DoubleFormula.ZERO;
      this.z = config.contains("z") ? new DoubleFormula(config.getString("z")) : DoubleFormula.ZERO;
      this.varToSubstract = config.getString("subtracted", null);
   }

   @Override
   public void cast(SkillMetadata meta) {
      Variable targetVar = meta.getVariable(this.getVariableName());
      Validate.isTrue(targetVar instanceof PositionVariable, "Variable '" + this.getVariableName() + "' is not a vector");
      Position target = (Position)targetVar.getStored();
      if (this.varToSubstract != null) {
         Variable var = meta.getVariable(this.varToSubstract);
         Validate.isTrue(var instanceof PositionVariable, "Variable '" + this.varToSubstract + "' is not a vector");
         target.add(((PositionVariable)var).getStored().clone().multiply(-1.0));
      }

      double x = this.x.evaluate(meta);
      double y = this.y.evaluate(meta);
      double z = this.z.evaluate(meta);
      target.add(-x, -y, -z);
   }
}
