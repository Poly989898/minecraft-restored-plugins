package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.ListPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "passengers",
   aliases = {"riders"},
   description = "Returns a list of UUIDs of the entities riding the scoped entity, or an empty list if it has no passengers"
)
public class PassengersPlaceholder extends EntityScopedPlaceholder<List> implements ListPlaceholder {
   public PassengersPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public List<String> applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else {
         Collection<AbstractEntity> passengers = entity.getPassengers();
         return passengers == null ? null : passengers.stream().map(passenger -> passenger.getUniqueId().toString()).toList();
      }
   }
}
