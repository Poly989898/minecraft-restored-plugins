package io.lumine.mythic.lib.script.mechanic.variable;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.variable.def.StringVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

@MechanicMetadata
public class SetStringMechanic extends VariableMechanic {
   private final String value;

   public SetStringMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("value");
      this.value = config.getString("value");
   }

   @Override
   public void cast(SkillMetadata meta) {
      this.getTargetVariableList(meta).registerVariable(new StringVariable(this.getVariableName(), meta.parseString(this.value)));
   }
}
