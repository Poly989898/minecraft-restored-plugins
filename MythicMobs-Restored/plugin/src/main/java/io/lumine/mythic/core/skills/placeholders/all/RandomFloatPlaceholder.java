package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.utils.numbers.RandomFloat;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.FloatPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "random.float",
   version = "5.2",
   usedPlaceholderArguments = -1
)
public class RandomFloatPlaceholder extends GenericPlaceholder<Float> implements FloatPlaceholder {
   private final RandomFloat randomFloat = new RandomFloat(
      this.collapseArgs(this.getPlaceholderSegmentsManager().getExtraArgs().stream().map(GenericPlaceholderSegment::key).toArray(String[]::new))
   );

   public RandomFloatPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   private String collapseArgs(String[] args) {
      return String.join(".", args);
   }

   @Nullable
   public Float applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.randomFloat.get();
   }
}
