package net.Indyuce.mmocore.api.util.math.formula;

import java.util.Random;
import org.apache.commons.lang.Validate;

public class RandomAmount {
   private final double min;
   private final double max;
   private static final Random random = new Random();

   public RandomAmount(double min, double max) {
      this.min = min;
      this.max = max;
      Validate.isTrue(max >= min, "Max value must be greater than min");
   }

   public RandomAmount(String value) {
      String[] split = value.split("-");
      this.min = Double.parseDouble(split[0]);
      this.max = split.length > 1 ? Double.parseDouble(split[1]) : this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getMin() {
      return this.min;
   }

   public double calculate() {
      return this.min + random.nextDouble() * (this.max - this.min);
   }

   public int calculateInt() {
      return (int)(this.min + random.nextInt((int)(this.max - this.min + 1.0)));
   }
}
