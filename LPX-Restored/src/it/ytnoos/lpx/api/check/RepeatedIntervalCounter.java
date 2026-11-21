package it.ytnoos.lpx.api.check;

import java.util.Arrays;

public class RepeatedIntervalCounter extends IntervalCounter {
   private final int maxWarnings;
   private final boolean[] periodWarnings;
   private int period;

   public RepeatedIntervalCounter(int value, int value2, int value3, int value4) {
      super(value, value2);
      this.maxWarnings = value4;
      this.periodWarnings = new boolean[value3];
   }

   public RepeatedIntervalCounter(int value, int value2, int value3) {
      this(value, 1000, value2, value3);
   }

   @Override
   public boolean increment(double number) {
      if (this.maxCounter < 0L) {
         return false;
      }

      boolean flag = this.advancePeriodsIfNeeded(System.currentTimeMillis());
      this.counter += number;
      return flag;
   }

   public boolean isOverLimit() {
      if (this.maxCounter < 0L) {
         return false;
      }

      this.advancePeriodsIfNeeded(System.currentTimeMillis());
      return this.counter > this.maxCounter;
   }

   @Override
   public double getValue() {
      this.advancePeriodsIfNeeded(System.currentTimeMillis());
      return this.counter;
   }

   private boolean advancePeriods(int value) {
      if (value >= this.periodWarnings.length) {
         this.resetWarnings();
         this.period = 0;
         this.counter = 0.0;
         return false;
      }

      for (int value2 = 0; value2 < value; value2++) {
         if (this.counter > this.maxCounter) {
            this.periodWarnings[this.period] = true;
         }

         this.period++;
         if (this.period > this.periodWarnings.length - 1) {
            this.period = 0;
         }

         this.periodWarnings[this.period] = false;
         this.counter = 0.0;
      }

      if (this.countWarnings() >= this.maxWarnings) {
         this.resetWarnings();
         return true;
      } else {
         return false;
      }
   }

   public int getPeriod() {
      return this.period;
   }

   public int getWarnings() {
      this.advancePeriodsIfNeeded(System.currentTimeMillis());
      return this.countWarnings();
   }

   public int getMaxPeriods() {
      return this.periodWarnings.length;
   }

   public int getMaxWarnings() {
      return this.maxWarnings;
   }

   public void resetWarnings() {
      Arrays.fill(this.periodWarnings, false);
   }

   private boolean advancePeriodsIfNeeded(long longValue) {
      long longValue2 = longValue - this.startTime;
      if (longValue2 < this.interval) {
         return false;
      }

      int value = (int)(longValue2 / this.interval);
      boolean flag = this.advancePeriods(value);
      this.startTime = this.startTime + value * this.interval;
      return flag;
   }

   private int countWarnings() {
      int value = 0;

      for (boolean flag : this.periodWarnings) {
         if (flag) {
            value++;
         }
      }

      return value;
   }

}
