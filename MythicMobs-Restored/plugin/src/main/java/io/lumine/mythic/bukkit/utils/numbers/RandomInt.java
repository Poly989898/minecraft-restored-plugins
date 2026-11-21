package io.lumine.mythic.bukkit.utils.numbers;

import java.util.regex.Pattern;

public class RandomInt {
   private static Pattern PATTERN = Pattern.compile("^-?[0-9]*(to|-)?[0-9]\\d*");
   private static Pattern PATTERN_RANGE = Pattern.compile("^-?[0-9]*(to|-)+[0-9]\\d*");
   private final boolean isStatic;
   private final int min;
   private final int max;

   public RandomInt(int i) {
      this.min = i;
      this.max = i;
      this.isStatic = true;
   }

   public RandomInt(int i, int j) {
      this.min = i;
      this.max = j;
      this.isStatic = false;
   }

   public RandomInt(String value) {
      try {
         if (value.contains("to")) {
            String[] split = value.split("to");
            this.min = Integer.valueOf(split[0]);
            this.max = Integer.valueOf(split[1]);
            this.isStatic = false;
         } else if (!value.startsWith("-") && value.contains("-")) {
            String[] split = value.split("-");
            this.min = Integer.valueOf(split[0]);
            this.max = Integer.valueOf(split[1]);
            this.isStatic = false;
         } else {
            this.min = Integer.valueOf(value);
            this.max = Integer.valueOf(value);
            this.isStatic = true;
         }
      } catch (Exception e) {
         throw new IllegalArgumentException();
      }
   }

   public int get() {
      return this.isStatic ? this.min : this.min + Numbers.randomInt(this.max - this.min + 1);
   }

   @Override
   public String toString() {
      return this.isStatic ? String.valueOf(this.min) : this.min + "to" + this.max;
   }

   public static boolean matches(String value) {
      return PATTERN.matcher(value).matches();
   }

   public static boolean matchesRange(String value) {
      return PATTERN_RANGE.matcher(value).matches();
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }
}
