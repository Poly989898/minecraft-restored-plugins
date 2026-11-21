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
   placeholder = "global.score",
   version = "5.2",
   usedPlaceholderArguments = 1
)
public class GlobalScorePlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> obj = this.getPlaceholderString(0, new String[0]);

   public GlobalScorePlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      String parsedPbj = this.obj.getOrDefault(placeholderContext, PlaceholderString::get, null);
      if (parsedPbj == null) {
         return null;
      } else {
         Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(parsedPbj);
         return obj != null ? obj.getScore("__GLOBAL__").getScore() : 0;
      }
   }
}
