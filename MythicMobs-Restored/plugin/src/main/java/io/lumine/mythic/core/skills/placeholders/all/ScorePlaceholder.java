package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "score",
   version = "5.2",
   usedPlaceholderArguments = 2
)
public class ScorePlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> objective = this.getPlaceholderString(0, new String[0]);
   private final ResolvedPlaceholderSegment<PlaceholderString> entry = this.getPlaceholderString(1, new String[0]);

   public ScorePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      if (this.objective.value() == null) {
         return null;
      } else {
         String parsedObj = this.objective.getOrDefault(placeholderContext, PlaceholderString::get, null);
         if (parsedObj == null) {
            return null;
         } else {
            Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(parsedObj);
            String parsedEntry = this.entry.getOrDefault(placeholderContext, PlaceholderString::get, null);
            if (parsedEntry == null) {
               return null;
            } else {
               return obj != null ? obj.getScore(parsedEntry).getScore() : 0;
            }
         }
      }
   }
}
