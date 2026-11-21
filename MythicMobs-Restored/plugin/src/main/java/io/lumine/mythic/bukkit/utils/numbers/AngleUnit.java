package io.lumine.mythic.bukkit.utils.numbers;

public enum AngleUnit {
   DEGREES {
      @Override
      public double toRadians(double value) {
         return Math.toRadians(value);
      }

      @Override
      public double fromRadians(double radians) {
         return Math.toDegrees(radians);
      }

      @Override
      public String[] suffixes() {
         return new String[]{"d"};
      }
   },
   RADIANS {
      @Override
      public double toRadians(double value) {
         return value;
      }

      @Override
      public double fromRadians(double radians) {
         return radians;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"r"};
      }
   },
   SLOPE {
      @Override
      public double toRadians(double value) {
         return Math.atan(value);
      }

      @Override
      public double fromRadians(double radians) {
         return Math.tan(radians);
      }

      @Override
      public String[] suffixes() {
         return new String[]{"s"};
      }
   };

   public abstract double toRadians(double value);

   public abstract double fromRadians(double value);

   public abstract String[] suffixes();

   public static AngleUnit fromSuffix(String suffix) {
      if (suffix == null) {
         return null;
      } else {
         String s = suffix.toLowerCase();

         for (AngleUnit u : values()) {
            for (String suf : u.suffixes()) {
               if (s.equals(suf)) {
                  return u;
               }
            }
         }

         return null;
      }
   }
}
