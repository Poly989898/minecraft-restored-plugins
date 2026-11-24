package net.Indyuce.mmoitems.api.util;

import java.util.Random;

public class RandomAmount {
   private final int min;
   private final int max;
   private static final Random RANDOM = new Random();

   public RandomAmount(int count, int max) {
      this.min = count;
      this.max = max;
   }

   public RandomAmount(String text) {
      String[] texts = text.split("-");
      this.min = Integer.parseInt(texts[0]);
      this.max = texts.length > 1 ? Integer.parseInt(texts[1]) : 0;
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
