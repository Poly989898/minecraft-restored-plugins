package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "variableUnset",
   aliases = {"varUnset", "unsetVariable", "unsetVar"},
   description = "Unsets a variable"
)
public class VariableUnsetMechanic extends VariableMechanic implements ITargetedEntitySkill {
   public VariableUnsetMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data, target);
      if (variables == null) {
         return SkillResult.SUCCESS;
      } else {
         variables.remove(this.key.get(data, target));
         return SkillResult.SUCCESS;
      }
   }
}
