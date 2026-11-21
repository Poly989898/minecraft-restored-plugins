package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import java.io.File;

public abstract class MetaVariableMechanic extends SkillMechanic {
   protected final PlaceholderString variableName;
   protected final VariableScope variableScope;

   public MetaVariableMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      String strKey = mlc.getString(new String[]{"variable", "var"}, null);
      if (strKey != null) {
         VariableScopedName variableNameContext = VariableScopedName.of(strKey);
         this.variableName = variableNameContext.name();
         this.variableScope = variableNameContext.scope();
      } else {
         this.variableName = null;
         this.variableScope = null;
      }
   }

   protected Variable getVariable(PlaceholderContext context) {
      if (this.variableName == null) {
         return null;
      } else {
         VariableRegistry registry = this.getPlugin().getVariableManager().getRegistry(this.variableScope, context);
         return registry != null ? registry.get(this.variableName.get(context)) : null;
      }
   }
}
