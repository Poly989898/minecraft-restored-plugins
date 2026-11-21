package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;

public interface PlaceholderColor extends IGenericPlaceholder<Chroma> {
   static PlaceholderColor of(String in) {
      return MythicProvider.get().getConfiguration().createPlaceholderColor(in);
   }
}
