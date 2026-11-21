package io.lumine.mythic.bukkit.utils.numbers;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDuration {
   private final boolean isStatic;
   private final Duration min;
   private final Duration max;

   public RandomDuration(Duration duration) {
      this.min = Objects.requireNonNull(duration, "duration");
      this.max = duration;
      this.isStatic = true;
   }

   public RandomDuration(Duration min, Duration max) {
      this.min = Objects.requireNonNull(min, "min");
      this.max = Objects.requireNonNull(max, "max");
      validateRange(this.min, this.max);
      this.isStatic = false;
   }

   public RandomDuration(String value) {
      this(value, DurationUnit.SECONDS);
   }

   public RandomDuration(String value, DurationUnit defaultUnit) {
      Objects.requireNonNull(defaultUnit, "defaultUnit");
      if (value != null && !value.isBlank()) {
         String v = value.trim();
         String[] range = splitRange(v);
         if (range == null) {
            this.min = Duration.parse(v, defaultUnit);
            this.max = this.min;
            this.isStatic = true;
         } else {
            DurationUnit inheritedUnit = getInheritedUnit(range[0], range[1], defaultUnit);
            this.min = Duration.parse(range[0], inheritedUnit);
            this.max = Duration.parse(range[1], inheritedUnit);
            validateRange(this.min, this.max);
            this.isStatic = false;
         }
      } else {
         throw new IllegalArgumentException("Empty duration string");
      }
   }

   public Duration get() {
      if (!this.isStatic && !this.min.equals(this.max)) {
         long minMillis = this.min.getMilliseconds();
         long maxMillis = this.max.getMilliseconds();
         long millis = randomLongInclusive(minMillis, maxMillis);
         return Duration.ofMilliseconds(millis);
      } else {
         return this.min;
      }
   }

   @Override
   public String toString() {
      return this.isStatic ? this.min.getMilliseconds() + "ms" : this.min.getMilliseconds() + "msto" + this.max.getMilliseconds() + "ms";
   }

   public boolean isStatic() {
      return this.isStatic;
   }

   public static boolean matches(String value) {
      try {
         new RandomDuration(value);
         return true;
      } catch (NullPointerException | IllegalArgumentException e) {
         return false;
      }
   }

   public static boolean matchesRange(String value) {
      try {
         return value != null && splitRange(value.trim()) != null && matches(value);
      } catch (IllegalArgumentException e) {
         return false;
      }
   }

   private static String[] splitRange(String value) {
      if (value.contains("to")) {
         return splitRange(value, "to");
      } else {
         return !value.startsWith("-") && value.contains("-") ? splitRange(value, "-") : null;
      }
   }

   private static String[] splitRange(String value, String separator) {
      String[] parts = value.split(separator, 2);
      if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
         return parts;
      } else {
         throw new IllegalArgumentException("Invalid duration range: " + value);
      }
   }

   private static DurationUnit getInheritedUnit(String min, String max, DurationUnit defaultUnit) {
      DurationUnit minUnit = getExplicitUnit(min);
      DurationUnit maxUnit = getExplicitUnit(max);
      if (minUnit != null) {
         return minUnit;
      } else {
         return maxUnit != null ? maxUnit : defaultUnit;
      }
   }

   private static DurationUnit getExplicitUnit(String value) {
      String s = value.trim();
      int i = s.length() - 1;

      while (i >= 0 && Character.isLetter(s.charAt(i))) {
         i--;
      }

      String suffix = s.substring(i + 1).trim();
      if (suffix.isEmpty()) {
         return null;
      } else {
         DurationUnit unit = DurationUnit.fromSuffix(suffix);
         if (unit == null) {
            throw new IllegalArgumentException("Unknown duration suffix: " + suffix);
         } else {
            return unit;
         }
      }
   }

   private static void validateRange(Duration min, Duration max) {
      if (min.getMilliseconds() > max.getMilliseconds()) {
         throw new IllegalArgumentException("Minimum duration cannot be greater than maximum duration");
      }
   }

   private static long randomLongInclusive(long min, long max) {
      return min == 0L && max == Long.MAX_VALUE
         ? ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE
         : min + ThreadLocalRandom.current().nextLong(max - min + 1L);
   }

   public Duration getMin() {
      return this.min;
   }

   public Duration getMax() {
      return this.max;
   }
}
