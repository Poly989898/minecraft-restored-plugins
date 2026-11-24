package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.EntityVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;

public class VariableEntityTargeter implements EntityTargeter {
   private final String[] args;

   public VariableEntityTargeter(ConfigObject config) {
      config.validateKeys("name");
      this.args = config.getString("name").split("\\.");
   }

   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      Variable var = meta.getVariable(this.args[0]);

      for (int i = 1; i < this.args.length; i++) {
         var = var.getVariable(this.args[i]);
      }

      Validate.isTrue(var instanceof EntityVariable, "Variable '" + var.getName() + "' is not an entity");
      return Arrays.asList(((EntityVariable)var).getStored());
   }
}
