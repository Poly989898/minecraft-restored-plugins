package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "tt.size",
   aliases = {"threattable.size"}
)
public class ThreatTableSizePlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   public ThreatTableSizePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   public Integer applyToScope(PlaceholderContext placeholderContext) {
      ActiveMob activeMob = this.getActiveMob(placeholderContext);
      return activeMob != null && activeMob.hasThreatTable() && activeMob.getThreatTable().inCombat() ? activeMob.getThreatTable().size() : 0;
   }
}
