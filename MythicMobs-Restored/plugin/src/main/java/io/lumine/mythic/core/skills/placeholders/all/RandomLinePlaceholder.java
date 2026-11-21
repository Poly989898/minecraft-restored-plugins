package io.lumine.mythic.core.skills.placeholders.all;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class RandomLinePlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private List<String> values = Lists.newArrayList();

   public RandomLinePlaceholder(PlaceholderExecutor manager, List<String> values) {
      super(GenericPlaceholder.GenericPlaceholderArguments.builder().usedPlaceholderArguments((short)0).manager(manager).build());
      this.values = values;
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.values.get(Numbers.randomInt(this.values.size()));
   }
}
