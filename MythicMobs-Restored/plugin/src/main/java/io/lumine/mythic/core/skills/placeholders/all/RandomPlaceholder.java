package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "random",
   version = "5.2",
   usedPlaceholderArguments = 1
)
public class RandomPlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   private final RandomInt rand = new RandomInt(this.getPlaceholderSegmentsManager().getExtraArg(0).key());

   public RandomPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.rand.get();
   }
}
