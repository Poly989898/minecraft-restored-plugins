package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "ydiff"
)
public class YDiffPlaceholder extends NumericEntityScopedPlaceholder {
   public YDiffPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      double tY = this.getLocation.get(placeholderContext).getY();
      double cY = placeholderContext.meta().getCaster().getEntity().getLocation().getY();
      return tY - cY;
   }
}
