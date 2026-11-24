package io.lumine.mythic.lib.script.mechanic.variable;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.variable.def.BooleanVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import io.lumine.mythic.lib.util.formula.BooleanExpression;

@MechanicMetadata
public class SetBooleanMechanic extends VariableMechanic {
   private final String expression;

   public SetBooleanMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("value");
      this.expression = config.getString("value");
   }

   @Override
   public void cast(SkillMetadata meta) {
      boolean value = BooleanExpression.eval(meta.parseString(this.expression));
      this.getTargetVariableList(meta).registerVariable(new BooleanVariable(this.getVariableName(), value));
   }
}
