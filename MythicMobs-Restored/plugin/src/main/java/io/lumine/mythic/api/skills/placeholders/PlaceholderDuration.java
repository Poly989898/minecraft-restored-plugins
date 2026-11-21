package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;

public interface PlaceholderDuration extends IGenericPlaceholder<Duration> {
   static PlaceholderDuration of(String in) {
      return of(in, DurationUnit.SECONDS);
   }

   static PlaceholderDuration of(String in, DurationUnit defaultUnit) {
      return MythicProvider.get().getConfiguration().createPlaceholderDuration(in, defaultUnit);
   }
}
