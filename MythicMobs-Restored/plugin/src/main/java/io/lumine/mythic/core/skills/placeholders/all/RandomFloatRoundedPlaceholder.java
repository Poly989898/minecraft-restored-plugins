package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.utils.numbers.RandomFloat;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "random.float.rounded",
   version = "5.2",
   usedPlaceholderArguments = -1
)
public class RandomFloatRoundedPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private static final DecimalFormat df = new DecimalFormat("0.00");
   private final RandomFloat random = new RandomFloat(
      this.getPlaceholderSegmentsManager().getExtraArgs().stream().map(GenericPlaceholderSegment::key).collect(Collectors.joining("."))
   );

   public RandomFloatRoundedPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
      this.metaKeywordParser = (x$0, x$1) -> this.applyDirectly(x$0, x$1);
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return df.format(this.random.get());
   }
}
