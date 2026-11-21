package io.lumine.mythic.bukkit.utils.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public enum DurationFormatter {
   LONG,
   CONCISE {
      @Override
      protected String formatUnitPlural(ChronoUnit unit) {
         return String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
      }

      @Override
      protected String formatUnitSingular(ChronoUnit unit) {
         return this.formatUnitPlural(unit);
      }
   },
   CONCISE_LOW_ACCURACY(3) {
      @Override
      protected String formatUnitPlural(ChronoUnit unit) {
         return String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
      }

      @Override
      protected String formatUnitSingular(ChronoUnit unit) {
         return this.formatUnitPlural(unit);
      }
   };

   private final DurationFormatter.Unit[] units = new DurationFormatter.Unit[]{
      new DurationFormatter.Unit(ChronoUnit.YEARS),
      new DurationFormatter.Unit(ChronoUnit.MONTHS),
      new DurationFormatter.Unit(ChronoUnit.WEEKS),
      new DurationFormatter.Unit(ChronoUnit.DAYS),
      new DurationFormatter.Unit(ChronoUnit.HOURS),
      new DurationFormatter.Unit(ChronoUnit.MINUTES),
      new DurationFormatter.Unit(ChronoUnit.SECONDS)
   };
   private final int accuracy;

   private DurationFormatter() {
      this(Integer.MAX_VALUE);
   }

   private DurationFormatter(int accuracy) {
      this.accuracy = accuracy;
   }

   public String format(Duration duration) {
      long seconds = duration.getSeconds();
      StringBuilder output = new StringBuilder();
      int outputSize = 0;

      for (DurationFormatter.Unit unit : this.units) {
         long n = seconds / unit.duration;
         if (n > 0L) {
            seconds -= unit.duration * n;
            output.append(' ').append(n).append(unit.toString(n));
            outputSize++;
         }

         if (seconds <= 0L || outputSize >= this.accuracy) {
            break;
         }
      }

      return output.length() == 0 ? "0" + this.units[this.units.length - 1].stringPlural : output.substring(1);
   }

   protected String formatUnitPlural(ChronoUnit unit) {
      return " " + unit.name().toLowerCase();
   }

   protected String formatUnitSingular(ChronoUnit unit) {
      String s = unit.name().toLowerCase();
      return " " + s.substring(0, s.length() - 1);
   }

   private final class Unit {
      private final long duration;
      private final String stringPlural;
      private final String stringSingular;

      Unit(ChronoUnit unit) {
         this.duration = unit.getDuration().getSeconds();
         this.stringPlural = DurationFormatter.this.formatUnitPlural(unit);
         this.stringSingular = DurationFormatter.this.formatUnitSingular(unit);
      }

      public String toString(long n) {
         return n == 1L ? this.stringSingular : this.stringPlural;
      }
   }
}
