package io.lumine.mythic.bukkit.utils.numbers;

import java.util.regex.Pattern;

public class RandomLong {
   private static Pattern PATTERN = Pattern.compile("^-?[0-9]*(to|-)?[0-9]\\d*");
   final boolean isStatic;
   final long min;
   final long max;

   public RandomLong(long i) {
      this.min = i;
      this.max = i;
      this.isStatic = true;
   }

   public RandomLong(long i, long j) {
      this.min = i;
      this.max = j;
      this.isStatic = false;
   }

   public RandomLong(String value) {
      try {
         if (value.contains("to")) {
            String[] split = value.split("to");
            this.min = Long.valueOf(split[0]);
            this.max = Long.valueOf(split[1]);
            this.isStatic = false;
         } else if (!value.startsWith("-") && value.contains("-")) {
            String[] split = value.split("-");
            this.min = Long.valueOf(split[0]);
            this.max = Long.valueOf(split[1]);
            this.isStatic = false;
         } else {
            this.min = Long.valueOf(value);
            this.max = Long.valueOf(value);
            this.isStatic = true;
         }
      } catch (Exception e) {
         throw new IllegalArgumentException();
      }
   }

   public long get() {
      return this.isStatic ? this.min : this.min + Numbers.randomInt((int)(this.max - this.min + 1L));
   }

   @Override
   public String toString() {
      return this.isStatic ? String.valueOf(this.min) : this.min + "to" + this.max;
   }

   public static boolean matches(String value) {
      return PATTERN.matcher(value).matches();
   }
}
