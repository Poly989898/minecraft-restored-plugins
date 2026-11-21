package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "globalcooldown",
   aliases = {"gcd"}
)
public class GlobalCooldownPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   public GlobalCooldownPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      ActiveMob am = this.getActiveMob(placeholderContext);
      return am == null ? 0 : am.getGlobalCooldown();
   }
}
