package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "tt.top",
   aliases = {"threattable.top"}
)
public class ThreatTableTopPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public ThreatTableTopPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   public String applyToScope(PlaceholderContext placeholderContext) {
      ActiveMob activeMob = this.getActiveMob(placeholderContext);
      return activeMob != null && activeMob.hasThreatTable() && activeMob.getThreatTable().inCombat()
         ? activeMob.getThreatTable().getTopThreatHolder().getName()
         : null;
   }
}
