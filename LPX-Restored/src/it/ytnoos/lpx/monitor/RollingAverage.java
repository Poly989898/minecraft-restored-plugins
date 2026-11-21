package it.ytnoos.lpx.monitor;

import it.ytnoos.lpx.ClassKeyDerivation;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class RollingAverage {
   private final BigDecimal[] samples;
   private final int capacity;
   private int index = 0;
   private int count = 0;
   private static final long a = ClassKeyDerivation.a(8686105358667700620L, -6518805881484361373L, MethodHandles.lookup().lookupClass()).a(209533644865090L);

   public RollingAverage(int value) {
      if (value <= 0) {
         throw new IllegalArgumentException("size must be > 0");
      }

      this.capacity = value;
      this.samples = new BigDecimal[value];
   }

   public void add(BigDecimal bigDecimal) {
      this.samples[this.index] = bigDecimal;
      this.index = (this.index + 1) % this.capacity;
      if (this.count < this.capacity) {
         this.count++;
      }
   }

   public double getAverage(long longValue) {
      long longValue2 = longValue;
      longValue2 = a ^ longValue2;
      if (this.count == 0) {
         return 0.0;
      }

      BigDecimal bigDecimal = BigDecimal.ZERO;
      int value = 0;

      while (true) {
         BigDecimal bigDecimal2;
         if (value < this.count) {
            bigDecimal2 = bigDecimal;
         } else {
            bigDecimal2 = bigDecimal;
            if (longValue2 > 0L) {
               return bigDecimal.divide(new BigDecimal(this.count), 30, RoundingMode.HALF_UP).doubleValue();
            }
         }

         if (6585810834011133426L > longValue2) {
            bigDecimal2 = bigDecimal2.add(this.samples[value]);
         }

         bigDecimal = bigDecimal2;
         value++;
      }
   }

   public BigDecimal getMin() {
      boolean flag = false;
      if (this.count == 0) {
         return BigDecimal.ZERO;
      }

      BigDecimal bigDecimal = this.samples[0];
      int value = 1;

      while (value < this.count) {
         if (this.samples[value].compareTo(bigDecimal) < 0) {
            bigDecimal = this.samples[value];
         }

         value++;
         if (flag) {
            break;
         }
      }

      return bigDecimal;
   }

   public BigDecimal getMax() {
      boolean flag = false;
      if (this.count == 0) {
         return BigDecimal.ZERO;
      }

      BigDecimal bigDecimal = this.samples[0];
      int value = 1;

      while (value < this.count) {
         if (this.samples[value].compareTo(bigDecimal) > 0) {
            bigDecimal = this.samples[value];
         }

         value++;
         if (flag) {
            break;
         }
      }

      return bigDecimal;
   }

   public BigDecimal getPercentile(double number) {
      if (this.count == 0) {
         return BigDecimal.ZERO;
      }

      BigDecimal[] bigDecimals = new BigDecimal[this.count];
      System.arraycopy(this.samples, 0, bigDecimals, 0, this.count);
      Arrays.sort(bigDecimals);
      int value = (int)Math.ceil(number / 100.0 * this.count);
      return bigDecimals[Math.max(0, Math.min(value - 1, this.count - 1))];
   }

   public int getCount() {
      return this.count;
   }
}
