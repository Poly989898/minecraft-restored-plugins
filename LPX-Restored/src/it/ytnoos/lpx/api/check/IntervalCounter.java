package it.ytnoos.lpx.api.check;


public class IntervalCounter {
   protected final long maxCounter;
   protected final long interval;
   protected double counter;
   protected long startTime = System.currentTimeMillis();
   protected long lastFlag = 0L;

   public IntervalCounter(long longValue) {
      this.maxCounter = longValue;
      this.interval = 1000L;
   }

   public IntervalCounter(long longValue, long longValue2) {
      this.maxCounter = longValue;
      this.interval = longValue2;
   }

   public boolean increment() {
      return this.increment(1.0);
   }

   public boolean increment(double number) {
      if (this.maxCounter < 0L) {
         return false;
      }

      if (System.currentTimeMillis() - this.startTime >= this.interval) {
         this.reset();
      }

      this.counter += number;
      if (this.counter > this.maxCounter) {
         this.lastFlag = System.currentTimeMillis();
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   protected void reset() {
      this.counter = 0.0;
      this.startTime = System.currentTimeMillis();
   }

   public long getInterval() {
      return this.interval;
   }

   public double getValue() {
      return this.counter;
   }

   public long getMaxCounter() {
      return this.maxCounter;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public boolean hasFlaggedRecently() {
      return this.hasFlaggedRecently(this.interval);
   }

   public boolean hasFlaggedRecently(long longValue) {
      return System.currentTimeMillis() - this.lastFlag < longValue;
   }

}
