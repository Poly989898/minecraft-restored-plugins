package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ScopedVariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "caster.var",
   version = "5.4"
)
public class CasterVariablePlaceholder extends ScopedVariablePlaceholder {
   public CasterVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Nullable
   @Override
   protected VariableRegistry getRegistry(PlaceholderContext context) {
      return this.manager.getPlugin().getVariableManager().getRegistry(VariableScope.CASTER, context.meta(), context.entity());
   }
}
