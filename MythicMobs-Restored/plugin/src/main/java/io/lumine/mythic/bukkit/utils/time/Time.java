package io.lumine.mythic.bukkit.utils.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Time {
   public static long nowMillis() {
      return System.currentTimeMillis();
   }

   public static long nowSeconds() {
      return nowMillis() / 1000L;
   }

   public static Instant now() {
      return Instant.ofEpochMilli(System.currentTimeMillis());
   }

   public static Duration diffToNow(Instant other) {
      return Duration.between(now(), other).abs();
   }

   public static Duration duration(TimeUnit unit, long amount) {
      Objects.requireNonNull(unit, "unit");
      switch (unit) {
         case NANOSECONDS:
            return Duration.ofNanos(amount);
         case MICROSECONDS:
            return Duration.ofNanos(TimeUnit.MICROSECONDS.toNanos(amount));
         case MILLISECONDS:
            return Duration.ofMillis(amount);
         case SECONDS:
            return Duration.ofSeconds(amount);
         case MINUTES:
            return Duration.ofMinutes(amount);
         case HOURS:
            return Duration.ofHours(amount);
         case DAYS:
            return Duration.ofDays(amount);
         default:
            throw new AssertionError("unknown time unit: " + unit);
      }
   }

   private Time() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
