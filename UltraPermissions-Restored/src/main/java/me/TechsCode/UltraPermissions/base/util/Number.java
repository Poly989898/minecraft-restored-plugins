package me.TechsCode.UltraPermissions.base.util;

import java.text.DecimalFormat;
import java.util.TreeMap;

public class Number {
   private static final TreeMap<Integer, String> ROMAN_NUMERAL_MAP = new TreeMap<>();
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

   public Number() {
   }

   public static String toRoman(int index) {
      Integer value = ROMAN_NUMERAL_MAP.floorKey(index);
      if (value == null) {
         return String.valueOf(index);
      } else {
         return index == value ? ROMAN_NUMERAL_MAP.get(index) : ROMAN_NUMERAL_MAP.get(value) + toRoman(index - value);
      }
   }

   public static String format2Decimals(double value) {
      return DECIMAL_FORMAT.format(value);
   }

   public static double sum(double value, double second) {
      return sum(value, second, 2);
   }

   public static double sum(double value, double second, int count) {
      double scale = Math.pow(10.0, count);
      return (Math.round(value * scale) + Math.round(second * scale)) / scale;
   }

   public static double clamp(double value, double min, double max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static int clamp(int count, int min, int max) {
      return count < min ? min : (count > max ? max : count);
   }

   public static long clamp(long value, long min, long max) {
      return value < min ? min : (value > max ? max : value);
   }

   static {
      ROMAN_NUMERAL_MAP.put(1000, "M");
      ROMAN_NUMERAL_MAP.put(900, "CM");
      ROMAN_NUMERAL_MAP.put(500, "D");
      ROMAN_NUMERAL_MAP.put(400, "CD");
      ROMAN_NUMERAL_MAP.put(100, "C");
      ROMAN_NUMERAL_MAP.put(90, "XC");
      ROMAN_NUMERAL_MAP.put(50, "L");
      ROMAN_NUMERAL_MAP.put(40, "XL");
      ROMAN_NUMERAL_MAP.put(10, "X");
      ROMAN_NUMERAL_MAP.put(9, "IX");
      ROMAN_NUMERAL_MAP.put(5, "V");
      ROMAN_NUMERAL_MAP.put(4, "IV");
      ROMAN_NUMERAL_MAP.put(1, "I");
   }
}
