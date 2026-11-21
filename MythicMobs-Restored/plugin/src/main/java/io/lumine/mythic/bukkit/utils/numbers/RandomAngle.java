package io.lumine.mythic.bukkit.utils.numbers;

import java.util.Random;

public class RandomAngle {
   private static final Random RANDOM = new Random();
   private boolean isStatic;
   private final Angle min;
   private final Angle max;

   public RandomAngle(Angle angle) {
      this.min = angle;
      this.max = angle;
      this.isStatic = true;
   }

   public RandomAngle(Angle min, Angle max) {
      this.min = min;
      this.max = max;
      this.isStatic = false;
   }

   public RandomAngle(String value, AngleUnit defaultUnit) {
      String v = value.trim();

      Angle a;
      Angle b;
      try {
         if (v.contains("to")) {
            String[] parts = v.split("to", 2);
            a = Angle.parse(parts[0], defaultUnit);
            b = Angle.parse(parts[1], defaultUnit);
            this.isStatic = false;
         } else if (!v.startsWith("-") && v.contains("-")) {
            String[] parts = v.split("-", 2);
            a = Angle.parse(parts[0], defaultUnit);
            b = Angle.parse(parts[1], defaultUnit);
            this.isStatic = false;
         } else {
            a = Angle.parse(v, defaultUnit);
            b = a;
            this.isStatic = true;
         }
      } catch (Exception e) {
         a = Angle.fromDegrees(0.0);
         b = a;
         this.isStatic = true;
         e.printStackTrace();
      }

      this.min = a;
      this.max = b;
   }

   public RandomAngle(String value) {
      this(value, AngleUnit.DEGREES);
   }

   public Angle get() {
      if (this.isStatic) {
         return this.min;
      } else {
         double lo = this.min.getRadians();
         double hi = this.max.getRadians();
         double randRad = lo + RANDOM.nextDouble() * (hi - lo);
         return Angle.fromRadians(randRad);
      }
   }

   @Override
   public String toString() {
      return "RandomAngle{" + this.min + " to " + this.max + "}";
   }

   public boolean isStatic() {
      return this.isStatic;
   }

   public static boolean matches(String value) {
      return Angle.isAngle(value);
   }

   public static boolean matchesRange(String value) {
      if (value == null) {
         return false;
      } else {
         String v = value.trim();
         if (v.contains("to")) {
            String[] p = v.split("to", 2);
            return Angle.isAngle(p[0]) && Angle.isAngle(p[1]);
         } else if (!v.startsWith("-") && v.contains("-")) {
            String[] p = v.split("-", 2);
            return Angle.isAngle(p[0]) && Angle.isAngle(p[1]);
         } else {
            return false;
         }
      }
   }

   public Angle getMin() {
      return this.min;
   }

   public Angle getMax() {
      return this.max;
   }
}
