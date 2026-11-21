package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "location.temperature",
   aliases = {"l.temp", "temp"},
   version = "5.13",
   description = "Returns the adjusted biome temperature at the scoped location"
)
public class LocationTemperaturePlaceholder extends NumericEntityScopedPlaceholder {
   public LocationTemperaturePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractLocation location = this.getLocation.get(placeholderContext);
      return location == null ? null : BukkitAdapter.adapt(location).getBlock().getTemperature();
   }
}
