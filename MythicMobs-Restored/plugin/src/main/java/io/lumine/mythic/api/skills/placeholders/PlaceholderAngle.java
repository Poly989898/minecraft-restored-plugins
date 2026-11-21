package io.lumine.mythic.api.skills.placeholders;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.utils.numbers.Angle;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;

public interface PlaceholderAngle extends IGenericPlaceholder<Angle> {
   static PlaceholderAngle of(String in, AngleUnit defaultUnit) {
      return MythicProvider.get().getConfiguration().createPlaceholderAngle(in, defaultUnit);
   }
}
