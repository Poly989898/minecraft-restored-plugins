package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ScopedVariablePlaceholder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "dialog",
   version = "5.10"
)
public class DialogVariablePlaceholder extends ScopedVariablePlaceholder {
   public DialogVariablePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) throws IllegalArgumentException {
      super(metaContext);
   }

   @Override
   protected void initializeMetaKeywords() {
      super.initializeMetaKeywords();
      if (this.variable != null) {
         this.variable = PlaceholderString.of("dialog." + this.variable.get());
      }
   }

   @Nullable
   @Override
   protected VariableRegistry getRegistry(PlaceholderContext context) {
      return context.meta() instanceof SkillMetadata skillMetadata ? skillMetadata.getVariables() : null;
   }
}
