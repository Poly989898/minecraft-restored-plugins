package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "distancesquared",
   aliases = {"distancesq"}
)
public class DistanceSqPlaceholder extends NumericEntityScopedPlaceholder {
   public DistanceSqPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractLocation caster = placeholderContext.meta().getCaster().getEntity().getLocation();
      AbstractLocation target = this.getLocation.get(placeholderContext);
      return caster.getWorld().equals(target.getWorld()) ? caster.distanceSquared(target) : null;
   }
}
