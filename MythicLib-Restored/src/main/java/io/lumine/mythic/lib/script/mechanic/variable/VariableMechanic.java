package io.lumine.mythic.lib.script.mechanic.variable;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.variable.VariableList;
import io.lumine.mythic.lib.script.variable.VariableScope;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public abstract class VariableMechanic extends Mechanic {
   private final String varName;
   private final VariableScope scope;

   public VariableMechanic(ConfigObject config) {
      config.validateKeys("variable");
      this.varName = config.getString("variable");
      this.scope = config.contains("scope") ? VariableScope.valueOf(config.getString("scope").toUpperCase()) : VariableScope.SKILL;
   }

   public String getVariableName() {
      return this.varName;
   }

   public VariableList getTargetVariableList(SkillMetadata meta) {
      return this.scope == VariableScope.SKILL ? meta.getVariableList() : meta.getCaster().getData().getVariableList();
   }
}
