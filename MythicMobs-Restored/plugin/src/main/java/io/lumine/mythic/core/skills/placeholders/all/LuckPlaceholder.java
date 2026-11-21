package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "luck"
)
public class LuckPlaceholder extends NumericEntityScopedPlaceholder {
   public LuckPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      return this.getEntity.get(placeholderContext).getLuck();
   }
}
