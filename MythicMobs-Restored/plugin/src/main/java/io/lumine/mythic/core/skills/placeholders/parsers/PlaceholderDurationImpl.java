package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;

public class PlaceholderDurationImpl extends PlaceholderNumber<Duration> implements PlaceholderDuration {
   private final DurationUnit defaultUnit;

   public static PlaceholderDurationImpl of(String data) {
      return of(data, DurationUnit.SECONDS);
   }

   public static PlaceholderDurationImpl of(String data, DurationUnit defaultUnit) {
      if (io.lumine.mythic.bukkit.utils.numbers.RandomDuration.matchesRange(data)) {
         return new RandomDuration(data, defaultUnit);
      } else {
         return (PlaceholderDurationImpl)(Duration.isDuration(data) ? new StaticDuration(data, defaultUnit) : new PlaceholderDurationImpl(data, defaultUnit));
      }
   }

   public PlaceholderDurationImpl(String data) {
      this(data, DurationUnit.SECONDS);
   }

   protected PlaceholderDurationImpl() {
      super(null);
      this.defaultUnit = DurationUnit.SECONDS;
   }

   public PlaceholderDurationImpl(String data, DurationUnit defaultUnit) {
      super(data);
      this.defaultUnit = defaultUnit == null ? DurationUnit.SECONDS : defaultUnit;
   }

   protected Duration convertNumberToSelfType(double number) {
      return new Duration(number, this.defaultUnit);
   }

   protected Duration convertStringToSelfType(String string) {
      try {
         return Duration.parse(string, this.defaultUnit);
      } catch (IllegalArgumentException e) {
         return this.getDefaultValue();
      }
   }

   protected Duration getDefaultValue() {
      return new Duration(0.0, this.defaultUnit);
   }

   public boolean isStaticallyEqualTo(Duration value) {
      return this.isStatic() && this.get().equals(value);
   }
}
