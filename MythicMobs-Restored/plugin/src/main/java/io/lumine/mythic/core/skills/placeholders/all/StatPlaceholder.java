package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.DoublePlaceholder;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "stat",
   version = "5.4",
   usedPlaceholderArguments = 1
)
public class StatPlaceholder extends EntityScopedPlaceholder<Double> implements DoublePlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> statName = this.getPlaceholderString(0, new String[0]);
   private final boolean isCaster;

   public StatPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      this.isCaster = context.source == EntityScopedPlaceholder.TargetSource.CASTER;
   }

   @Nullable
   public Double applyToScope(PlaceholderContext placeholderContext) {
      String maybeName = this.statName.getOrDefault(placeholderContext, PlaceholderString::get, null);
      if (maybeName == null) {
         return null;
      } else {
         Optional<StatType> maybeStat = this.manager.getPlugin().getStatManager().getStat(maybeName);
         if (maybeStat.isEmpty()) {
            return 0.0;
         } else {
            Optional<? extends StatSnapshot> maybeRegistry;
            if (this.isCaster && placeholderContext.meta() instanceof SkillMetadata skillMetadata) {
               maybeRegistry = skillMetadata.getCasterStats();
            } else {
               maybeRegistry = this.manager.getPlugin().getStatManager().getStatRegistry(this.getEntity.get(placeholderContext));
            }

            if (maybeRegistry.isEmpty()) {
               return 0.0;
            } else {
               StatSnapshot statRegistry = maybeRegistry.get();
               return statRegistry.get(maybeStat.get());
            }
         }
      }
   }
}
