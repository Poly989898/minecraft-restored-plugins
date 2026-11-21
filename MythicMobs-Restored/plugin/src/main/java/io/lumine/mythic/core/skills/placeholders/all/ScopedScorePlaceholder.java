package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "score",
   usedPlaceholderArguments = 1
)
public class ScopedScorePlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> objective = this.getPlaceholderString(0, new String[0]);

   public ScopedScorePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      if (this.objective == null) {
         return null;
      } else {
         String objectiveString = this.objective.getOrDefault(placeholderContext, PlaceholderString::get, null);
         if (objectiveString == null) {
            return null;
         } else {
            Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objectiveString);
            if (obj != null) {
               AbstractEntity entity = this.getEntity.get(placeholderContext);
               return entity.isPlayer() ? obj.getScore(entity.asPlayer().getName()).getScore() : obj.getScore(entity.getUniqueId().toString()).getScore();
            } else {
               return 0;
            }
         }
      }
   }
}
