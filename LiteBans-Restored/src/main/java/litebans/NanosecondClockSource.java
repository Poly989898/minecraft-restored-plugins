package litebans;

import java.util.concurrent.TimeUnit;

public class NanosecondClockSource implements ClockSource {
   @Override
   public long currentTime0() {
      return System.nanoTime();
   }

   @Override
   public long elapsedMillis0(long value) {
      return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - value);
   }

   @Override
   public long elapsedMillis0(long value, long value2) {
      return TimeUnit.NANOSECONDS.toMillis(value2 - value);
   }

   @Override
   public long elapsedNanos0(long value) {
      return System.nanoTime() - value;
   }

   @Override
   public long elapsedNanos0(long value, long value2) {
      return value2 - value;
   }

   @Override
   public long plusMillis0(long value, long value2) {
      return value + TimeUnit.MILLISECONDS.toNanos(value2);
   }
}
