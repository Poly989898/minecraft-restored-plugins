package io.lumine.mythic.bukkit.utils.numbers;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class RandomDouble {
   private static Pattern PATTERN = Pattern.compile("^(-?(?:\\d*\\.)?\\d+)((?:to|-)(-?(?:\\d*\\.)?\\d+))?$");
   private static Pattern PATTERN_RANGE = Pattern.compile("^-?\\d+(?:\\.\\d+)?(to|-)\\d+(?:\\.\\d+)?$");
   private static Object2BooleanMap<String> matchesCache = Object2BooleanMaps.synchronize(new Object2BooleanOpenHashMap());
   private static Object2BooleanMap<String> matchesRangeCache = Object2BooleanMaps.synchronize(new Object2BooleanOpenHashMap());
   private boolean isStatic;
   private double min;
   private double max;

   public RandomDouble(double i) {
      this.min = i;
      this.max = i;
      this.isStatic = true;
   }

   public RandomDouble(double i, double j) {
      this.min = i;
      this.max = j;
      this.isStatic = false;
   }

   public RandomDouble(String value) {
      try {
         if (value.contains("to")) {
            String[] split = value.split("to");
            this.min = Double.valueOf(split[0]);
            this.max = Double.valueOf(split[1]);
            this.isStatic = false;
         } else if (!value.startsWith("-") && value.contains("-")) {
            String[] split = value.split("-");
            this.min = Double.valueOf(split[0]);
            this.max = Double.valueOf(split[1]);
            this.isStatic = false;
         } else {
            this.min = Double.valueOf(value);
            this.max = Double.valueOf(value);
            this.isStatic = true;
         }
      } catch (Exception e) {
         this.min = 1.0;
         this.max = 1.0;
         this.isStatic = true;
         e.printStackTrace();
      }
   }

   public double get() {
      return this.isStatic ? this.min : this.min + ThreadLocalRandom.current().nextDouble() * (this.max - this.min);
   }

   public double get(double bias) {
      return this.get((min, max) -> {
         if (bias == 0.0) {
            return min;
         } else {
            double range = Numbers.randomDouble();
            range = Math.pow(range, 1.0 / bias);
            return min + range * (max - min);
         }
      });
   }

   public double get(BiFunction<Double, Double, Double> randomFunc) {
      return randomFunc.apply(this.min, this.max);
   }

   @Override
   public String toString() {
      return "RandomDouble{" + this.min + " to " + this.max + "}";
   }

   public static boolean matches(String value) {
      if (matchesCache.containsKey(value)) {
         return matchesCache.getBoolean(value);
      } else {
         boolean matches = PATTERN.matcher(value).matches();
         matchesCache.put(value, matches);
         return matches;
      }
   }

   public static boolean matchesRange(String value) {
      if (matchesRangeCache.containsKey(value)) {
         return matchesRangeCache.getBoolean(value);
      } else {
         boolean matches = PATTERN_RANGE.matcher(value).matches();
         matchesRangeCache.put(value, matches);
         return matches;
      }
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }
}
