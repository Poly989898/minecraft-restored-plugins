package it.ytnoos.lpx.api.check;

public final class RollingIntervalCounter {
   private static final int INITIAL_CAPACITY = 8;
   private static final long NANOS_PER_MILLISECOND = 1000000L;
   private final double maxCounter;
   private final long interval;
   private final long intervalNanos;
   private long[] sampleTimes;
   private double[] sampleValues;
   private int headIndex;
   private int tailIndex;
   private double counter;
   private long lastFlag;
   private boolean overLimit;

   public RollingIntervalCounter(long longValue) {
      this(longValue, 1000L);
   }

   public RollingIntervalCounter(long longValue, long longValue2) {
      if (longValue2 <= 0L) {
         throw new IllegalArgumentException("interval must be positive");
      }

      this.maxCounter = longValue;
      this.interval = longValue2;
      this.intervalNanos = longValue2 * 1000000L;
      this.sampleTimes = new long[8];
      this.sampleValues = new double[8];
   }

   public boolean increment() {
      return this.increment(1.0);
   }

   public boolean increment(double number) {
      if (this.maxCounter < 0.0) {
         return false;
      }

      long longValue = System.nanoTime();
      this.evictExpired(longValue);
      this.addSample(longValue, number);
      boolean flag = this.counter > this.maxCounter;
      boolean flag2 = flag && !this.overLimit;
      this.overLimit = flag;
      if (flag2) {
         this.lastFlag = System.currentTimeMillis();
      }

      return flag2;
   }

   public boolean isOverLimit() {
      if (this.maxCounter < 0.0) {
         return false;
      }

      this.evictExpired(System.nanoTime());
      this.overLimit = this.counter > this.maxCounter;
      return this.overLimit;
   }

   public long getInterval() {
      return this.interval;
   }

   public double getValue() {
      this.evictExpired(System.nanoTime());
      return this.counter;
   }

   public double getMaxCounter() {
      return this.maxCounter;
   }

   public boolean hasFlaggedRecently() {
      return this.hasFlaggedRecently(this.interval);
   }

   public boolean hasFlaggedRecently(long longValue) {
      return System.currentTimeMillis() - this.lastFlag < longValue;
   }

   private void evictExpired(long longValue) {
      long longValue2 = longValue - this.intervalNanos;

      while (this.headIndex != this.tailIndex && this.sampleTimes[this.headIndex] < longValue2) {
         this.counter = this.counter - this.sampleValues[this.headIndex];
         this.sampleValues[this.headIndex] = 0.0;
         this.headIndex++;
         if (this.headIndex >= this.sampleTimes.length) {
            this.headIndex = 0;
         }
      }

      if (this.counter <= 0.0) {
         this.counter = 0.0;
      }
   }

   private void addSample(long longValue, double number) {
      if (!(number <= 0.0)) {
         int value = incrementIndex(this.tailIndex, this.sampleTimes.length);
         if (value == this.headIndex) {
            this.resize();
            value = incrementIndex(this.tailIndex, this.sampleTimes.length);
         }

         this.sampleTimes[this.tailIndex] = longValue;
         this.sampleValues[this.tailIndex] = number;
         this.tailIndex = value;
         this.counter += number;
      }
   }

   private void resize() {
      int value = this.size();
      int value2 = this.sampleTimes.length * 2;
      long[] longValues = new long[value2];
      double[] numbers = new double[value2];

      for (int value3 = 0; value3 < value; value3++) {
         int value4 = (this.headIndex + value3) % this.sampleTimes.length;
         longValues[value3] = this.sampleTimes[value4];
         numbers[value3] = this.sampleValues[value4];
      }

      this.sampleTimes = longValues;
      this.sampleValues = numbers;
      this.headIndex = 0;
      this.tailIndex = value;
   }

   private int size() {
      return this.tailIndex >= this.headIndex ? this.tailIndex - this.headIndex : this.tailIndex + (this.sampleTimes.length - this.headIndex);
   }

   private static int incrementIndex(int value, int value2) {
      return value + 1 >= value2 ? 0 : value + 1;
   }
}
