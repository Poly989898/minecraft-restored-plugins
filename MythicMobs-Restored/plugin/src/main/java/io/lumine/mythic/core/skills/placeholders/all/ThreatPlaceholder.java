package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "threat"
)
public class ThreatPlaceholder extends NumericEntityScopedPlaceholder {
   public ThreatPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      if (placeholderContext.meta().getCaster() instanceof ActiveMob am) {
         return !am.hasThreatTable() ? null : am.getThreatTable().getThreat(this.getEntity.get(placeholderContext));
      } else {
         return null;
      }
   }
}
