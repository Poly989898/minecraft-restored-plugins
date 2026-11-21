package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class StaticPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private final String replacement;

   public StaticPlaceholder(String replacement, PlaceholderExecutor manager) {
      super(GenericPlaceholder.GenericPlaceholderArguments.builder().usedPlaceholderArguments((short)-1).manager(manager).build());
      this.replacement = replacement;
   }

   @Override
   public String apply(PlaceholderContext placeholderContext) {
      return this.replacement;
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.replacement;
   }
}
