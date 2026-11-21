package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ScopedVariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "target.var",
   version = "5.6"
)
public class TargetVariablePlaceholder extends ScopedVariablePlaceholder {
   public TargetVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Override
   protected VariableRegistry getRegistry(PlaceholderContext context) {
      return this.manager.getPlugin().getVariableManager().getRegistry(VariableScope.TARGET, context);
   }
}
