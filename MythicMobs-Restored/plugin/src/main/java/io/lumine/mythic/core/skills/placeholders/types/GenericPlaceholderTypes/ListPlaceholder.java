package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializationRegistry;
import java.util.List;

public interface ListPlaceholder extends GenericPlaceholderTyping<List> {
   default String convertType(List parsedPlaceholder) {
      return PlaceholderClassSerializationRegistry.format(List.class, parsedPlaceholder);
   }

   @Override
   default Class<List> getParsedValueClass() {
      return List.class;
   }
}
