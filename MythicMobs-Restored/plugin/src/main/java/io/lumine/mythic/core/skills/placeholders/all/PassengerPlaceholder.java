package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "passenger",
   aliases = {"rider"},
   description = "Returns the UUID of the primary entity riding the scoped entity, matching the @Passenger targeter, or nothing if it has no passengers"
)
public class PassengerPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public PassengerPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else {
         AbstractEntity passenger = entity.getPassenger();
         return passenger != null ? passenger.getUniqueId().toString() : null;
      }
   }
}
