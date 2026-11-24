package io.lumine.mythic.lib.script.variable;

import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class VariableList implements VariableContainer {
   private final VariableScope scope;
   private final Map<String, Variable> vars = new HashMap<>();

   public VariableList(@NotNull VariableScope scope) {
      this.scope = scope;
   }

   @NotNull
   public VariableScope getScope() {
      return this.scope;
   }

   @Nullable
   @Override
   public Variable getVariable(@NotNull String name) {
      return this.vars.get(name);
   }

   public void registerVariable(@NotNull Variable var) {
      Validate.isTrue(!SkillMetadata.RESERVED_VARIABLE_NAMES.contains(var.getName()), "Cannot use reserved variable name '" + var.getName() + "'");
      this.vars.put(var.getName(), var);
   }
}
