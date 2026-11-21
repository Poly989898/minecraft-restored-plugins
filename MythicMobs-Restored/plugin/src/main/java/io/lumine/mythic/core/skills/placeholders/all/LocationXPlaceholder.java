package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "location.x",
   aliases = {"l.x"},
   usedPlaceholderArguments = -1
)
public class LocationXPlaceholder extends LocationPlaceholder {
   public LocationXPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      return (Number)(this.centered ? this.getLocation.get(placeholderContext).getBlockX() : this.getLocation.get(placeholderContext).getX());
   }
}
