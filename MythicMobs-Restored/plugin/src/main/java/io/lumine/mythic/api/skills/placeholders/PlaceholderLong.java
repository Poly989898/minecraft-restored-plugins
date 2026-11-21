package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;

public interface PlaceholderLong extends IGenericPlaceholder<Long> {
   static PlaceholderLong of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderLong(in);
   }
}
