package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import java.util.concurrent.TimeUnit;

public interface ClockSource {
   ClockSource INSTANCE = ClockSource.Factory.create();
   TimeUnit[] TIMEUNITS_DESCENDING = new TimeUnit[]{
      TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS
   };
   String[] TIMEUNIT_DISPLAY_VALUES = new String[]{"ns", "\u03bcs", "ms", "s", "m", "h", "d"};

   long currentTime();

   long toMillis(long value);

   long toNanos(long value);

   long elapsedMillis(long value);

   long elapsedMillis(long value, long value2);

   long elapsedNanos(long value);

   long elapsedNanos(long value, long value2);

   long plusMillis(long value, long value2);

   TimeUnit getSourceTimeUnit();

   String elapsedDisplayString(long value, long value2);

   public static class Factory {
      private static ClockSource create() {
         String os = System.getProperty("os.name");
         return (ClockSource)("Mac OS X".equals(os) ? new ClockSource.MillisecondClockSource() : new ClockSource.NanosecondClockSource());
      }
   }

   public static final class MillisecondClockSource extends ClockSource.NanosecondClockSource {
      @Override
      public long currentTime() {
         return System.currentTimeMillis();
      }

      @Override
      public long elapsedMillis(long startTime) {
         return System.currentTimeMillis() - startTime;
      }

      @Override
      public long elapsedMillis(long startTime, long endTime) {
         return endTime - startTime;
      }

      @Override
      public long elapsedNanos(long startTime) {
         return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
      }

      @Override
      public long elapsedNanos(long startTime, long endTime) {
         return TimeUnit.MILLISECONDS.toNanos(endTime - startTime);
      }

      @Override
      public long toMillis(long time) {
         return time;
      }

      @Override
      public long toNanos(long time) {
         return TimeUnit.MILLISECONDS.toNanos(time);
      }

      @Override
      public long plusMillis(long time, long millis) {
         return time + millis;
      }

      @Override
      public TimeUnit getSourceTimeUnit() {
         return TimeUnit.MILLISECONDS;
      }
   }

   public static class NanosecondClockSource implements ClockSource {
      @Override
      public long currentTime() {
         return System.nanoTime();
      }

      @Override
      public long toMillis(long time) {
         return TimeUnit.NANOSECONDS.toMillis(time);
      }

      @Override
      public long toNanos(long time) {
         return time;
      }

      @Override
      public long elapsedMillis(long startTime) {
         return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      }

      @Override
      public long elapsedMillis(long startTime, long endTime) {
         return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
      }

      @Override
      public long elapsedNanos(long startTime) {
         return System.nanoTime() - startTime;
      }

      @Override
      public long elapsedNanos(long startTime, long endTime) {
         return endTime - startTime;
      }

      @Override
      public long plusMillis(long time, long millis) {
         return time + TimeUnit.MILLISECONDS.toNanos(millis);
      }

      @Override
      public TimeUnit getSourceTimeUnit() {
         return TimeUnit.NANOSECONDS;
      }

      @Override
      public String elapsedDisplayString(long startTime, long endTime) {
         long elapsedNanos = this.elapsedNanos(startTime, endTime);
         StringBuilder sb = new StringBuilder(elapsedNanos < 0L ? "-" : "");
         elapsedNanos = Math.abs(elapsedNanos);

         for (TimeUnit unit : TIMEUNITS_DESCENDING) {
            long converted = unit.convert(elapsedNanos, TimeUnit.NANOSECONDS);
            if (converted > 0L) {
               sb.append(converted).append(TIMEUNIT_DISPLAY_VALUES[unit.ordinal()]);
               elapsedNanos -= TimeUnit.NANOSECONDS.convert(converted, unit);
            }
         }

         return sb.toString();
      }
   }
}
