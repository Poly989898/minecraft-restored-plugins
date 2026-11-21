package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.UUID;
import javax.annotation.Nullable;

@MythicPlaceholder(
   placeholder = "random.uuid",
   version = "5.2"
)
public class RandomUUIDPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   public RandomUUIDPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return UUID.randomUUID().toString();
   }
}
