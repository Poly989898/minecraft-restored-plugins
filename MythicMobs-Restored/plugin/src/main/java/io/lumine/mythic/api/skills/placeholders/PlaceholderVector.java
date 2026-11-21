package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractVector;

public interface PlaceholderVector extends IGenericPlaceholder<AbstractVector> {
   static PlaceholderVector of(String input) {
      return MythicProvider.get().getConfiguration().createPlaceholderVector(input);
   }
}
