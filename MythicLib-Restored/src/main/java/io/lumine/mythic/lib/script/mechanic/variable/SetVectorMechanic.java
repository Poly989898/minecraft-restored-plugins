package io.lumine.mythic.lib.script.mechanic.variable;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.Position;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

@MechanicMetadata
public class SetVectorMechanic extends VariableMechanic {
   private final DoubleFormula x;
   private final DoubleFormula y;
   private final DoubleFormula z;

   public SetVectorMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("x", "y", "z");
      this.x = new DoubleFormula(config.getString("x"));
      this.y = new DoubleFormula(config.getString("y"));
      this.z = new DoubleFormula(config.getString("z"));
   }

   @Override
   public void cast(SkillMetadata meta) {
      this.getTargetVariableList(meta)
         .registerVariable(
            new PositionVariable(
               this.getVariableName(), new Position(meta.getSourceLocation().getWorld(), this.x.evaluate(meta), this.y.evaluate(meta), this.z.evaluate(meta))
            )
         );
   }
}
