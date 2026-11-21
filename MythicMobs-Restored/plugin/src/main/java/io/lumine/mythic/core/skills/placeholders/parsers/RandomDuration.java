package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

public class RandomDuration extends PlaceholderDurationImpl {
   private io.lumine.mythic.bukkit.utils.numbers.RandomDuration value;

   public RandomDuration(String string, DurationUnit defaultUnit) {
      DurationUnit unit = defaultUnit == null ? DurationUnit.SECONDS : defaultUnit;
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomDuration(string, unit);
   }

   public RandomDuration(io.lumine.mythic.bukkit.utils.numbers.RandomDuration duration) {
      this.value = duration;
   }

   public Duration get(PlaceholderContext context) {
      return this.value.get();
   }

   @Override
   public String toString() {
      return this.value.toString();
   }

   @Override
   public String serialize() {
      return this.toString();
   }
}
