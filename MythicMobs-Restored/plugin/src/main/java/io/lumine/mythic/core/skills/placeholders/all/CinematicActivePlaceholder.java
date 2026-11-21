package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "cinematic.active",
   description = "Whether the entity is in an active cinematic camera (true/false)."
)
public class CinematicActivePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public CinematicActivePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity != null && entity.isPlayer()) {
         CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
         if (mgr == null) {
            return "false";
         } else {
            return mgr.isInCinematic(entity.getUniqueId()) ? "true" : "false";
         }
      } else {
         return "false";
      }
   }
}
