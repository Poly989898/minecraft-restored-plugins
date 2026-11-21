package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "aura.*.stacks",
   aliases = {"aura.*"}
)
public class AuraStacksPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> auraName = this.getPlaceholderString(0, SegmentSource.WILDCARD_ARGS, new String[0]);

   public AuraStacksPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity != null && entity.isLiving()) {
         String aura = this.auraName.getOrDefault(placeholderContext, PlaceholderString::get, null);
         if (aura == null) {
            return null;
         } else {
            AuraRegistry registry = this.manager.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(entity);
            return registry == null ? 0 : registry.getStacks(aura);
         }
      } else {
         return 0;
      }
   }
}
