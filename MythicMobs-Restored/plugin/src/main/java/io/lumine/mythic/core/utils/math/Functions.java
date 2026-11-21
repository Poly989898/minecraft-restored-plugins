package io.lumine.mythic.core.utils.math;

import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.concurrent.ThreadLocalRandom;
import net.objecthunter.exp4j.function.Function;

public class Functions {
   public static final Function min = new Function("min", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.min(doubles[0], doubles[1]);
      }
   };
   public static final Function max = new Function("max", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.max(doubles[0], doubles[1]);
      }
   };
   public static final Function clamp = new Function("clamp", 3) {
      @Override
      public double apply(double... doubles) {
         return Numbers.clamp(doubles[0], doubles[1], doubles[2]);
      }
   };
   public static final Function atan2 = new Function("atan2", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.atan2(doubles[0], doubles[1]);
      }
   };
   public static final Function random = new Function("random", 2) {
      @Override
      public double apply(double... doubles) {
         double min = doubles[0];
         double max = doubles[1];
         return min + ThreadLocalRandom.current().nextDouble() * (max - min);
      }
   };
   public static final Function todegree = new Function("todegree", 1) {
      @Override
      public double apply(double... doubles) {
         return Math.toDegrees(doubles[0]);
      }
   };
   public static final Function toradian = new Function("toradian", 1) {
      @Override
      public double apply(double... doubles) {
         return Math.toRadians(doubles[0]);
      }
   };
   public static final Function step = new Function("step", 2) {
      @Override
      public double apply(double... doubles) {
         return doubles[1] >= doubles[0] ? 1.0 : 0.0;
      }
   };
   public static final Function lerp = new Function("lerp", 3) {
      @Override
      public double apply(double... doubles) {
         return doubles[0] * (1.0 - doubles[2]) + doubles[1] * doubles[2];
      }
   };
   public static final Function round = new Function("round", 1) {
      @Override
      public double apply(double... doubles) {
         return Math.round(doubles[0]);
      }
   };
   public static final Function[] functions = new Function[]{min, max, atan2, random, clamp, todegree, toradian, step, lerp, round};
}
