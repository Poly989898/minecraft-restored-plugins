package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "lastsignal"
)
public class LastSignalPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public LastSignalPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      ActiveMob am = this.getActiveMob(placeholderContext);
      return am == null ? null : am.getLastSignal();
   }
}
