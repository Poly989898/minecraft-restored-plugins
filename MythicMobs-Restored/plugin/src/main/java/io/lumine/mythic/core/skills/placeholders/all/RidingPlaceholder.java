package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "riding",
   aliases = {"vehicle", "mount"},
   description = "Returns the UUID of the entity the scoped entity is riding, or nothing if unmounted"
)
public class RidingPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public RidingPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else {
         AbstractEntity vehicle = entity.getVehicle();
         return vehicle != null ? vehicle.getUniqueId().toString() : null;
      }
   }
}
