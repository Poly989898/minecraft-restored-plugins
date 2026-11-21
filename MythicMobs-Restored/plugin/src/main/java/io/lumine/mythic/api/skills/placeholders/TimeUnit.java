package io.lumine.mythic.api.skills.placeholders;

import java.util.Locale;

public enum TimeUnit {
   TICKS(50L, "t", "tick", "ticks"),
   MILLISECONDS(1L, "ms", "millisecond", "milliseconds", "milli", "millis"),
   MILLIS(1L),
   SECONDS(1000L, "s", "sec", "secs", "second", "seconds"),
   MINUTES(60000L, "m", "min", "mins", "minute", "minutes"),
   HOURS(3600000L, "h", "hr", "hrs", "hour", "hours"),
   DAYS(86400000L, "d", "day", "days"),
   WEEKS(604800000L, "w", "week", "weeks");

   private final long millisMultiplier;
   private final String[] suffixes;

   private TimeUnit(long millisMultiplier, String... suffixes) {
      this.millisMultiplier = millisMultiplier;
      this.suffixes = suffixes;
   }

   public long toMillis(long value) {
      return value * this.millisMultiplier;
   }

   public long toMillis(double value) {
      return (long)(value * this.millisMultiplier);
   }

   public static TimeUnit.SuffixMatch matchSuffix(String text) {
      if (text != null && !text.isEmpty()) {
         String normalized = text.toLowerCase(Locale.ROOT);
         TimeUnit matchedUnit = null;
         int matchedLength = 0;

         for (TimeUnit unit : values()) {
            for (String suffix : unit.suffixes) {
               if (suffix.length() > matchedLength && normalized.endsWith(suffix)) {
                  matchedUnit = unit;
                  matchedLength = suffix.length();
               }
            }
         }

         return matchedUnit == null ? null : new TimeUnit.SuffixMatch(matchedUnit, matchedLength);
      } else {
         return null;
      }
   }

   public record SuffixMatch(TimeUnit unit, int suffixLength) {
   }
}
