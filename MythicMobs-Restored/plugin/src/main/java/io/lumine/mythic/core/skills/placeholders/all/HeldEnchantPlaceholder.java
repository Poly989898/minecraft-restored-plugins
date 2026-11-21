package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(
   placeholder = "heldenchantlevel",
   aliases = {},
   usedPlaceholderArguments = 1
)
public class HeldEnchantPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> enchantmentName = this.getPlaceholderString(0, new String[0]);

   public HeldEnchantPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   public Integer applyToScope(PlaceholderContext placeholderContext) {
      return this.getEntity
         .get(placeholderContext)
         .getEnchantmentLevelHeld(this.enchantmentName.getOrDefault(placeholderContext, PlaceholderString::get, null));
   }
}
