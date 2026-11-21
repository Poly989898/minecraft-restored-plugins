package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholder(
   placeholder = "pity",
   aliases = {},
   usedPlaceholderArguments = 1
)
public class PityPlaceholder extends NumericEntityScopedPlaceholder {
   @NotNull
   private final ResolvedPlaceholderSegment<PlaceholderString> category = this.getPlaceholderString(0, new String[0]);

   public PityPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      return (Number)(!entity.isPlayer()
         ? 0
         : this.manager
            .getPlugin()
            .getDropManager()
            .getPityManager()
            .getPity(entity.getUniqueId(), this.category.getOrDefault(placeholderContext, PlaceholderString::get, "DEFAULT")));
   }
}
