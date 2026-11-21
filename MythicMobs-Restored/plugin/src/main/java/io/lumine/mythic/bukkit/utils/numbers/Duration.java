package io.lumine.mythic.bukkit.utils.numbers;

public class Duration {
   private final long milliseconds;

   private Duration(long milliseconds) {
      if (milliseconds < 0L) {
         throw new IllegalArgumentException("Duration cannot be negative");
      } else {
         this.milliseconds = milliseconds;
      }
   }

   public Duration(double value, DurationUnit unit) {
      if (Double.isNaN(value) || Double.isInfinite(value)) {
         throw new IllegalArgumentException("Invalid duration");
      } else if (value < 0.0) {
         throw new IllegalArgumentException("Duration cannot be negative");
      } else {
         double milliseconds = unit == DurationUnit.TICKS ? Math.round(value) * 50.0 : unit.toMilliseconds(value);
         this.milliseconds = roundMilliseconds(milliseconds);
      }
   }

   public static Duration ofMilliseconds(long millis) {
      return new Duration(millis);
   }

   public static Duration ofTicks(int ticks) {
      return new Duration(ticks, DurationUnit.TICKS);
   }

   public static Duration ofSeconds(double seconds) {
      return new Duration(seconds, DurationUnit.SECONDS);
   }

   public static Duration ofMinutes(double minutes) {
      return new Duration(minutes, DurationUnit.MINUTES);
   }

   public static Duration ofHours(double hours) {
      return new Duration(hours, DurationUnit.HOURS);
   }

   public static Duration ofDays(double days) {
      return new Duration(days, DurationUnit.DAYS);
   }

   public static Duration ofWeeks(double weeks) {
      return new Duration(weeks, DurationUnit.WEEKS);
   }

   public static Duration ofJavaDuration(java.time.Duration duration) {
      return ofMilliseconds(duration.toMillis());
   }

   public long getMilliseconds() {
      return this.milliseconds;
   }

   public int getTicks() {
      return Math.toIntExact(this.milliseconds / 50L);
   }

   public double getSeconds() {
      return DurationUnit.SECONDS.fromMilliseconds(this.milliseconds);
   }

   public double getMinutes() {
      return DurationUnit.MINUTES.fromMilliseconds(this.milliseconds);
   }

   public double getHours() {
      return DurationUnit.HOURS.fromMilliseconds(this.milliseconds);
   }

   public double getDays() {
      return DurationUnit.DAYS.fromMilliseconds(this.milliseconds);
   }

   public double getWeeks() {
      return DurationUnit.WEEKS.fromMilliseconds(this.milliseconds);
   }

   public java.time.Duration toJavaDuration() {
      return java.time.Duration.ofMillis(this.milliseconds);
   }

   public static Duration parse(String input, DurationUnit defaultUnit) {
      if (input != null && !input.isBlank()) {
         String s = input.trim();
         int i = s.length() - 1;

         while (i >= 0 && Character.isLetter(s.charAt(i))) {
            i--;
         }

         String numPart = s.substring(0, i + 1).trim();
         String sufPart = s.substring(i + 1).trim();

         double value;
         try {
            value = Double.parseDouble(numPart);
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in duration: " + numPart, e);
         }

         if (sufPart.isEmpty()) {
            return new Duration(value, defaultUnit);
         } else {
            DurationUnit unit = DurationUnit.fromSuffix(sufPart);
            if (unit == null) {
               throw new IllegalArgumentException("Unknown duration suffix: " + sufPart);
            } else {
               return new Duration(value, unit);
            }
         }
      } else {
         throw new IllegalArgumentException("Empty duration string");
      }
   }

   public static Duration parse(String input) {
      return parse(input, DurationUnit.SECONDS);
   }

   public static boolean isDuration(String input) {
      try {
         parse(input);
         return true;
      } catch (IllegalArgumentException e) {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("Duration{milliseconds=%d,ticks=%d,seconds=%.4f}", this.getMilliseconds(), this.getTicks(), this.getSeconds());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Duration other) ? false : this.milliseconds == other.milliseconds;
      }
   }

   @Override
   public int hashCode() {
      return Long.hashCode(this.milliseconds);
   }

   private static long roundMilliseconds(double milliseconds) {
      if (Double.isNaN(milliseconds) || Double.isInfinite(milliseconds)) {
         throw new IllegalArgumentException("Invalid duration");
      } else if (milliseconds < 0.0) {
         throw new IllegalArgumentException("Duration cannot be negative");
      } else if (milliseconds > 9.223372E18F) {
         throw new IllegalArgumentException("Duration is too large");
      } else {
         return Math.round(milliseconds);
      }
   }
}
