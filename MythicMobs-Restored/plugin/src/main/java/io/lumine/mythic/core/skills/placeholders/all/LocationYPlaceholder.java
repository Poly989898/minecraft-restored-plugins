package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "location.y",
   aliases = {"l.y"},
   usedPlaceholderArguments = -1
)
public class LocationYPlaceholder extends LocationPlaceholder {
   public LocationYPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      return (Number)(this.centered ? this.getLocation.get(placeholderContext).getBlockY() : this.getLocation.get(placeholderContext).getY());
   }
}
