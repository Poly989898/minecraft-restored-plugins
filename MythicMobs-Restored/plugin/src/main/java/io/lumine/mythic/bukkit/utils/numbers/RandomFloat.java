package io.lumine.mythic.bukkit.utils.numbers;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class RandomFloat {
   private static Pattern PATTERN = Pattern.compile("^-?[0-9]*(\\.\\d+)?(to|-)?[0-9]\\d*(\\.\\d+)?");
   private static Pattern PATTERN_RANGE = Pattern.compile("^-?[0-9]*(\\.\\d+)?(to|-)+[0-9]\\d*(\\.\\d+)?");
   private boolean isStatic;
   private float min;
   private float max;

   public RandomFloat(float i) {
      this.min = i;
      this.max = i;
      this.isStatic = true;
   }

   public RandomFloat(float i, float j) {
      this.min = i;
      this.max = j;
      this.isStatic = false;
   }

   public RandomFloat(String value) {
      try {
         if (value.contains("to")) {
            String[] split = value.split("to");
            this.min = Float.valueOf(split[0]);
            this.max = Float.valueOf(split[1]);
            this.isStatic = false;
         } else if (!value.startsWith("-") && value.contains("-")) {
            String[] split = value.split("-");
            this.min = Float.valueOf(split[0]);
            this.max = Float.valueOf(split[1]);
            this.isStatic = false;
         } else {
            this.min = Float.valueOf(value);
            this.max = Float.valueOf(value);
            this.isStatic = true;
         }
      } catch (Exception e) {
         this.min = 1.0F;
         this.max = 1.0F;
         this.isStatic = true;
         e.printStackTrace();
      }
   }

   public float get() {
      return this.isStatic ? this.min : this.min + (float)ThreadLocalRandom.current().nextDouble() * (this.max - this.min);
   }

   @Override
   public String toString() {
      return "RandomFloat{" + this.min + " to " + this.max + "}";
   }

   public static boolean matches(String value) {
      return PATTERN.matcher(value).matches();
   }

   public static boolean matchesRange(String value) {
      return PATTERN_RANGE.matcher(value).matches();
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }
}
