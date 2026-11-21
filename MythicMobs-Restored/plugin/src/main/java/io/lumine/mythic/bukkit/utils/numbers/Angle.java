package io.lumine.mythic.bukkit.utils.numbers;

public class Angle {
   private final double radians;

   public Angle(double value, AngleUnit unit) {
      this.radians = unit.toRadians(value);
   }

   public static Angle fromDegrees(double deg) {
      return new Angle(deg, AngleUnit.DEGREES);
   }

   public static Angle fromRadians(double rad) {
      return new Angle(rad, AngleUnit.RADIANS);
   }

   public double getDegrees() {
      return AngleUnit.DEGREES.fromRadians(this.radians);
   }

   public double getRadians() {
      return this.radians;
   }

   public double getSlope() {
      return AngleUnit.SLOPE.fromRadians(this.radians);
   }

   public static Angle parse(String input, AngleUnit defaultUnit) {
      if (input != null && !input.isBlank()) {
         String s = input.trim();
         int i = s.length() - 1;

         while (i >= 0 && (Character.isLetter(s.charAt(i)) || s.charAt(i) == 176)) {
            i--;
         }

         String numPart = s.substring(0, i + 1).trim();
         String sufPart = s.substring(i + 1).trim();

         double value;
         try {
            value = Double.parseDouble(numPart);
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in angle: " + numPart, e);
         }

         if (sufPart.isEmpty()) {
            return new Angle(value, defaultUnit);
         } else {
            AngleUnit unit = AngleUnit.fromSuffix(sufPart);
            if (unit == null) {
               throw new IllegalArgumentException("Unknown angle suffix: " + sufPart);
            } else {
               return new Angle(value, unit);
            }
         }
      } else {
         throw new IllegalArgumentException("Empty angle string");
      }
   }

   public static boolean isAngle(String input) {
      if (input != null && !input.isBlank()) {
         String s = input.trim();
         int i = s.length() - 1;

         while (i >= 0 && (Character.isLetter(s.charAt(i)) || s.charAt(i) == 176)) {
            i--;
         }

         String numPart = s.substring(0, i + 1).trim();
         String sufPart = s.substring(i + 1).trim();

         try {
            Double.parseDouble(numPart);
         } catch (NumberFormatException e) {
            return false;
         }

         return sufPart.isEmpty() ? true : AngleUnit.fromSuffix(sufPart) != null;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("Angle{degrees=%.2f,radians=%.4f}", this.getDegrees(), this.getRadians());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Angle other) ? false : Double.compare(this.radians, other.radians) == 0;
      }
   }

   @Override
   public int hashCode() {
      return Double.hashCode(this.radians);
   }
}
