package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ScopedVariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "skill.var",
   version = "5.4"
)
public class SkillVariablePlaceholder extends ScopedVariablePlaceholder {
   public SkillVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Override
   protected VariableRegistry getRegistry(PlaceholderContext context) {
      return context.meta() instanceof SkillMetadata skillMetadata ? skillMetadata.getVariables() : null;
   }
}
