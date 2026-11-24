package net.Indyuce.mmoitems.api.util;

import java.util.Random;

public class RandomAmount {
   private final int min;
   private final int max;
   private static final Random RANDOM = new Random();

   public RandomAmount(int var1, int var2) {
      this.min = var1;
      this.max = var2;
   }

   public RandomAmount(String var1) {
      String[] var2 = var1.split("-");
      this.min = Integer.parseInt(var2[0]);
      this.max = var2.length > 1 ? Integer.parseInt(var2[1]) : 0;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getRandomAmount() {
      return this.max > 0 ? this.min + RANDOM.nextInt(this.max - this.min + 1) : this.min;
   }
}
