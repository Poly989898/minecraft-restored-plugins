package litebans;

import java.util.concurrent.TimeUnit;

public interface ClockSource {
   ClockSource clockSource = OsUtil.obtainClockSource2();
   TimeUnit[] c_f = new TimeUnit[]{
      TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS
   };
   String[] texts = new String[]{"ns", "µs", "ms", "s", "m", "h", "d"};

   static long currentTime() {
      return clockSource.currentTime0();
   }

   long currentTime0();

   static long elapsedMillis(long a) {
      return clockSource.elapsedMillis0(a);
   }

   long elapsedMillis0(long value);

   static long elapsedMillis(long a, long b) {
      return clockSource.elapsedMillis0(a, b);
   }

   long elapsedMillis0(long value, long value2);

   static long elapsedNanos(long a) {
      return clockSource.elapsedNanos0(a);
   }

   long elapsedNanos0(long value);

   long elapsedNanos0(long value, long value2);

   static long plusMillis(long a, long b) {
      return clockSource.plusMillis0(a, b);
   }

   long plusMillis0(long value, long value2);

   static String elapsedDisplayString(long a, long b) {
      return clockSource.elapsedDisplayString0(a, b);
   }

   default String elapsedDisplayString0(long value, long value2) {
      long elapsedNanos02 = this.elapsedNanos0(value, value2);
      StringBuilder stringBuilder = new StringBuilder(elapsedNanos02 < 0L ? "-" : "");
      long value3 = Math.abs(elapsedNanos02);

      for (TimeUnit timeUnit : c_f) {
         long convert2 = timeUnit.convert(value3, TimeUnit.NANOSECONDS);
         if (convert2 > 0L) {
            stringBuilder.append(convert2).append(texts[timeUnit.ordinal()]);
            value3 -= TimeUnit.NANOSECONDS.convert(convert2, timeUnit);
         }
      }

      return stringBuilder.toString();
   }
}
