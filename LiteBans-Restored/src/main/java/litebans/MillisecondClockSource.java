package litebans;

import java.util.concurrent.TimeUnit;

public final class MillisecondClockSource implements ClockSource {
   @Override
   public long currentTime0() {
      return System.currentTimeMillis();
   }

   @Override
   public long elapsedMillis0(long value) {
      return System.currentTimeMillis() - value;
   }

   @Override
   public long elapsedMillis0(long value, long value2) {
      return value2 - value;
   }

   @Override
   public long elapsedNanos0(long value) {
      return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - value);
   }

   @Override
   public long elapsedNanos0(long value, long value2) {
      return TimeUnit.MILLISECONDS.toNanos(value2 - value);
   }

   @Override
   public long plusMillis0(long value, long value2) {
      return value + value2;
   }
}
