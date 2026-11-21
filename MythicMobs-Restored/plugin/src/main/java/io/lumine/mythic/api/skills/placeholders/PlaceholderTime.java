package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import java.time.Instant;

public interface PlaceholderTime extends IGenericPlaceholder<Instant> {
   static PlaceholderTime of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderTime(in);
   }
}
