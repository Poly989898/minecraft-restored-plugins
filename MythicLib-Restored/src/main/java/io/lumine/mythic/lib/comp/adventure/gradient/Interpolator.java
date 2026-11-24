package io.lumine.mythic.lib.comp.adventure.gradient;

@FunctionalInterface
public interface Interpolator {
   Interpolator LINEAR = (from, to, max) -> {
      double[] res = new double[max];

      for (int i = 0; i < max; i++) {
         res[i] = from + i * ((to - from) / (max - 1));
      }

      return res;
   };
   Interpolator QUADRATIC_SLOW_TO_FAST = (from, to, max) -> {
      double[] results = new double[max];
      double a = (to - from) / (max * max);

      for (int i = 0; i < results.length; i++) {
         results[i] = a * i * i + from;
      }

      return results;
   };
   Interpolator QUADRATIC_FAST_TO_SLOW = (from, to, max) -> {
      double[] results = new double[max];
      double a = (from - to) / (max * max);
      double b = -2.0 * a * max;

      for (int i = 0; i < results.length; i++) {
         results[i] = a * i * i + b * i + from;
      }

      return results;
   };

   double[] interpolate(double value, double value2, int count);
}
