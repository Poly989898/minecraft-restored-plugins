package io.lumine.mythic.core.skills.cinematic;

public enum Easing {
   LINEAR {
      @Override
      public double apply(double t) {
         return t;
      }
   },
   EASE_IN_QUAD {
      @Override
      public double apply(double t) {
         return t * t;
      }
   },
   EASE_OUT_QUAD {
      @Override
      public double apply(double t) {
         return 1.0 - (1.0 - t) * (1.0 - t);
      }
   },
   EASE_IN_OUT_QUAD {
      @Override
      public double apply(double t) {
         return t < 0.5 ? 2.0 * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0;
      }
   },
   EASE_IN_CUBIC {
      @Override
      public double apply(double t) {
         return t * t * t;
      }
   },
   EASE_OUT_CUBIC {
      @Override
      public double apply(double t) {
         double u = 1.0 - t;
         return 1.0 - u * u * u;
      }
   },
   EASE_IN_OUT_CUBIC {
      @Override
      public double apply(double t) {
         return t < 0.5 ? 4.0 * t * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;
      }
   },
   EASE_IN_SINE {
      @Override
      public double apply(double t) {
         return 1.0 - Math.cos(t * Math.PI / 2.0);
      }
   },
   EASE_OUT_SINE {
      @Override
      public double apply(double t) {
         return Math.sin(t * Math.PI / 2.0);
      }
   },
   EASE_IN_OUT_SINE {
      @Override
      public double apply(double t) {
         return -(Math.cos(Math.PI * t) - 1.0) / 2.0;
      }
   },
   SMOOTHSTEP {
      @Override
      public double apply(double t) {
         return t * t * (3.0 - 2.0 * t);
      }
   },
   SMOOTHERSTEP {
      @Override
      public double apply(double t) {
         return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
      }
   },
   CATMULL_ROM {
      @Override
      public double apply(double t) {
         return t;
      }
   },
   STEP {
      @Override
      public double apply(double t) {
         return t >= 1.0 ? 1.0 : 0.0;
      }
   };

   public abstract double apply(double value);

   public static Easing fromString(String name, Easing fallback) {
      if (name == null) {
         return fallback;
      } else {
         try {
            return valueOf(name.trim().toUpperCase());
         } catch (IllegalArgumentException e) {
            return fallback;
         }
      }
   }
}
