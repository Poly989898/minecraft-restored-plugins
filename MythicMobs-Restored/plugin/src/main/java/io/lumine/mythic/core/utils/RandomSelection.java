package io.lumine.mythic.core.utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import lombok.Getter;

public class RandomSelection<E> {
   private final NavigableMap<Double, E> map = new TreeMap<>();
   private final Random random;
   @Getter
   private double total = 0.0;

   public RandomSelection() {
      this(new Random());
   }

   public RandomSelection(Random random) {
      this.random = random;
   }

   public RandomSelection<E> add(double weight, E result) {
      if (weight <= 0.0) {
         return this;
      } else {
         this.total += weight;
         this.map.put(this.total, result);
         return this;
      }
   }

   public E get() {
      double value = this.random.nextDouble() * this.total;
      return this.map.higherEntry(value).getValue();
   }
}
