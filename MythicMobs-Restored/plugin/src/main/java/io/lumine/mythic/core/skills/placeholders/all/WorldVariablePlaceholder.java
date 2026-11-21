package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ScopedVariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "world.var",
   version = "5.6"
)
public class WorldVariablePlaceholder extends ScopedVariablePlaceholder {
   public WorldVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Nullable
   @Override
   protected VariableRegistry getRegistry(PlaceholderContext context) {
      return this.manager.getPlugin().getVariableManager().getRegistry(VariableScope.WORLD, context.meta(), context.meta().getTrigger());
   }
}
