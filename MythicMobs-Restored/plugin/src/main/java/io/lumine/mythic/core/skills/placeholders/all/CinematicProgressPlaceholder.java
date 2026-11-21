package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.skills.cinematic.CinematicState;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "cinematic.progress",
   description = "Progress 0..1 of the active cinematic camera, or 0 if none."
)
public class CinematicProgressPlaceholder extends NumericEntityScopedPlaceholder {
   public CinematicProgressPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context, 3);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity != null && entity.isPlayer()) {
         CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
         if (mgr == null) {
            return 0.0;
         } else {
            Optional<CinematicState> state = mgr.getState(entity.getUniqueId());
            return state.<Number>map(s -> s.getProgress()).orElse(0.0);
         }
      } else {
         return 0.0;
      }
   }
}
