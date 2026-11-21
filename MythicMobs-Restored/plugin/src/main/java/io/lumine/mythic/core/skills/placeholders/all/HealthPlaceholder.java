package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "health",
   aliases = {"hp", "thp", "h"}
)
public class HealthPlaceholder extends NumericEntityScopedPlaceholder {
   public HealthPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      return this.getEntity.get(placeholderContext).getHealth();
   }
}
