package com.ticxo.modelengine.api.utils.math;

import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Easing {
   LINEAR(Easing.Fn.LINEAR),
   EASE_IN_SINE(Easing.Fn.IN_SINE),
   EASE_OUT_SINE(Easing.Fn.OUT_SINE),
   EASE_IN_OUT_SINE(Easing.Fn.IN_OUT_SINE),
   EASE_IN_QUAD(Easing.Fn.IN_QUAD),
   EASE_OUT_QUAD(Easing.Fn.OUT_QUAD),
   EASE_IN_OUT_QUAD(Easing.Fn.IN_OUT_QUAD),
   EASE_IN_CUBIC(Easing.Fn.IN_CUBIC),
   EASE_OUT_CUBIC(Easing.Fn.OUT_CUBIC),
   EASE_IN_OUT_CUBIC(Easing.Fn.IN_OUT_CUBIC),
   EASE_IN_QUART(Easing.Fn.IN_QUART),
   EASE_OUT_QUART(Easing.Fn.OUT_QUART),
   EASE_IN_OUT_QUART(Easing.Fn.IN_OUT_QUART),
   EASE_IN_QUINT(Easing.Fn.IN_QUINT),
   EASE_OUT_QUINT(Easing.Fn.OUT_QUINT),
   EASE_IN_OUT_QUINT(Easing.Fn.IN_OUT_QUINT),
   EASE_IN_EXPO(Easing.Fn.IN_EXPO),
   EASE_OUT_EXPO(Easing.Fn.OUT_EXPO),
   EASE_IN_OUT_EXPO(Easing.Fn.IN_OUT_EXPO),
   EASE_IN_CIRC(Easing.Fn.IN_CIRC),
   EASE_OUT_CIRC(Easing.Fn.OUT_CIRC),
   EASE_IN_OUT_CIRC(Easing.Fn.IN_OUT_CIRC),
   EASE_IN_BACK(Easing.Fn.IN_BACK),
   EASE_OUT_BACK(Easing.Fn.OUT_BACK),
   EASE_IN_OUT_BACK(Easing.Fn.IN_OUT_BACK),
   EASE_IN_ELASTIC(Easing.Fn.IN_ELASTIC),
   EASE_OUT_ELASTIC(Easing.Fn.OUT_ELASTIC),
   EASE_IN_OUT_ELASTIC(Easing.Fn.IN_OUT_ELASTIC),
   EASE_IN_BOUNCE(Easing.Fn.IN_BOUNCE),
   EASE_OUT_BOUNCE(Easing.Fn.OUT_BOUNCE),
   EASE_IN_OUT_BOUNCE(Easing.Fn.IN_OUT_BOUNCE);

   private static final Pattern CAMEL_BOUNDARY = Pattern.compile("([a-z])([A-Z])");
   private final DoubleUnaryOperator function;

   public double apply(double t) {
      return this.function.applyAsDouble(t);
   }

   public static Easing get(String name) {
      Easing easing = getOrNull(name);
      return easing == null ? LINEAR : easing;
   }

   @Nullable
   public static Easing getOrNull(String name) {
      if (name == null) {
         return null;
      }

      try {
         return valueOf(normalize(name));
      } catch (IllegalArgumentException e) {
         return null;
      }
   }

   private static String normalize(String name) {
      return CAMEL_BOUNDARY.matcher(name.trim().replace('-', '_')).replaceAll("$1_$2").toUpperCase(Locale.ENGLISH);
   }

   private static class Fn {
      private static final double C1 = 1.70158;
      private static final double C2 = 2.5949095;
      private static final double C3 = 2.70158;
      private static final double C4 = Math.PI * 2.0 / 3.0;
      private static final double C5 = Math.PI * 4.0 / 9.0;
      private static final double N1 = 7.5625;
      private static final double D1 = 2.75;
      private static final DoubleUnaryOperator LINEAR = x -> x;
      private static final DoubleUnaryOperator IN_SINE = x -> 1.0 - Math.cos(x * Math.PI / 2.0);
      private static final DoubleUnaryOperator OUT_SINE = x -> Math.sin(x * Math.PI / 2.0);
      private static final DoubleUnaryOperator IN_OUT_SINE = x -> -(Math.cos(Math.PI * x) - 1.0) / 2.0;
      private static final DoubleUnaryOperator IN_QUAD = x -> x * x;
      private static final DoubleUnaryOperator OUT_QUAD = x -> 1.0 - (1.0 - x) * (1.0 - x);
      private static final DoubleUnaryOperator IN_OUT_QUAD = x -> x < 0.5 ? 2.0 * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0;
      private static final DoubleUnaryOperator IN_CUBIC = x -> x * x * x;
      private static final DoubleUnaryOperator OUT_CUBIC = x -> 1.0 - Math.pow(1.0 - x, 3.0);
      private static final DoubleUnaryOperator IN_OUT_CUBIC = x -> x < 0.5 ? 4.0 * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0;
      private static final DoubleUnaryOperator IN_QUART = x -> x * x * x * x;
      private static final DoubleUnaryOperator OUT_QUART = x -> 1.0 - Math.pow(1.0 - x, 4.0);
      private static final DoubleUnaryOperator IN_OUT_QUART = x -> x < 0.5 ? 8.0 * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 4.0) / 2.0;
      private static final DoubleUnaryOperator IN_QUINT = x -> x * x * x * x * x;
      private static final DoubleUnaryOperator OUT_QUINT = x -> 1.0 - Math.pow(1.0 - x, 5.0);
      private static final DoubleUnaryOperator IN_OUT_QUINT = x -> x < 0.5 ? 16.0 * x * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 5.0) / 2.0;
      private static final DoubleUnaryOperator IN_EXPO = x -> x <= 0.0 ? 0.0 : Math.pow(2.0, 10.0 * x - 10.0);
      private static final DoubleUnaryOperator OUT_EXPO = x -> x >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * x);
      private static final DoubleUnaryOperator IN_OUT_EXPO = x -> {
         if (x <= 0.0) {
            return 0.0;
         } else if (x >= 1.0) {
            return 1.0;
         } else {
            return x < 0.5 ? Math.pow(2.0, 20.0 * x - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * x + 10.0)) / 2.0;
         }
      };
      private static final DoubleUnaryOperator IN_CIRC = x -> 1.0 - Math.sqrt(1.0 - x * x);
      private static final DoubleUnaryOperator OUT_CIRC = x -> Math.sqrt(1.0 - (x - 1.0) * (x - 1.0));
      private static final DoubleUnaryOperator IN_OUT_CIRC = x -> x < 0.5
         ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0
         : (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
      private static final DoubleUnaryOperator IN_BACK = x -> 2.70158 * x * x * x - 1.70158 * x * x;
      private static final DoubleUnaryOperator OUT_BACK = x -> 1.0 + 2.70158 * Math.pow(x - 1.0, 3.0) + 1.70158 * Math.pow(x - 1.0, 2.0);
      private static final DoubleUnaryOperator IN_OUT_BACK = x -> x < 0.5
         ? Math.pow(2.0 * x, 2.0) * (7.189819 * x - 2.5949095) / 2.0
         : (Math.pow(2.0 * x - 2.0, 2.0) * (3.5949095 * (2.0 * x - 2.0) + 2.5949095) + 2.0) / 2.0;
      private static final DoubleUnaryOperator IN_ELASTIC = x -> {
         if (x <= 0.0) {
            return 0.0;
         } else {
            return x >= 1.0 ? 1.0 : -Math.pow(2.0, 10.0 * x - 10.0) * Math.sin((x * 10.0 - 10.75) * (Math.PI * 2.0 / 3.0));
         }
      };
      private static final DoubleUnaryOperator OUT_ELASTIC = x -> {
         if (x <= 0.0) {
            return 0.0;
         } else {
            return x >= 1.0 ? 1.0 : Math.pow(2.0, -10.0 * x) * Math.sin((x * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0;
         }
      };
      private static final DoubleUnaryOperator IN_OUT_ELASTIC = x -> {
         if (x <= 0.0) {
            return 0.0;
         } else if (x >= 1.0) {
            return 1.0;
         } else {
            return x < 0.5
               ? -(Math.pow(2.0, 20.0 * x - 10.0) * Math.sin((20.0 * x - 11.125) * (Math.PI * 4.0 / 9.0))) / 2.0
               : Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * (Math.PI * 4.0 / 9.0)) / 2.0 + 1.0;
         }
      };
      private static final DoubleUnaryOperator OUT_BOUNCE = Easing.Fn::outBounce;
      private static final DoubleUnaryOperator IN_BOUNCE = x -> 1.0 - outBounce(1.0 - x);
      private static final DoubleUnaryOperator IN_OUT_BOUNCE = x -> x < 0.5 ? (1.0 - outBounce(1.0 - 2.0 * x)) / 2.0 : (1.0 + outBounce(2.0 * x - 1.0)) / 2.0;

      private static double outBounce(double x) {
         if (x < 0.36363636363636365) {
            return 7.5625 * x * x;
         } else if (x < 0.7272727272727273) {
            x -= 0.5454545454545454;
            return 7.5625 * x * x + 0.75;
         } else if (x < 0.9090909090909091) {
            x -= 0.8181818181818182;
            return 7.5625 * x * x + 0.9375;
         } else {
            x -= 0.9545454545454546;
            return 7.5625 * x * x + 0.984375;
         }
      }
   }
}
