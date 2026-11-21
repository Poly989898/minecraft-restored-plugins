package io.lumine.mythic.bukkit.utils.collections;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WeightedSelector<T extends AWeightedItem> implements ISelector<T> {
   private Random random = new Random();

   public WeightedSelector() {
      this(new Random());
   }

   public WeightedSelector(long seed) {
      this(new Random(seed));
   }

   public WeightedSelector(Random random) {
      this.random = random;
   }

   @Override
   public T get(WeightedCollection<T> items) {
      double weight = this.random.nextDouble() * items.getWeight();
      List<T> itemsView = items.getView();
      if (itemsView.isEmpty()) {
         return null;
      } else {
         for (T item : itemsView) {
            if (weight <= item.getWeight()) {
               return item;
            }

            weight -= item.getWeight();
         }

         return itemsView.get(0);
      }
   }

   @Override
   public T getBiased(WeightedCollection<T> items, double bias) {
      List<T> list = items.getView();
      double maxWeight = items.getMaxWeight();
      double minWeight = items.getMinWeight();
      if (list.isEmpty()) {
         return null;
      } else {
         double[] cumulativeWeights = new double[items.size()];
         double weightPool = 0.0;

         for (int i = 0; i < list.size(); i++) {
            double weight = this.calculateNewWeight(list.get(i).getWeight(), bias, maxWeight, minWeight);
            weightPool += weight;
            cumulativeWeights[i] = weightPool;
         }

         return this.roll(weightPool, cumulativeWeights, list);
      }
   }

   private T roll(double weightPool, double[] cumulativeWeights, List<T> list) {
      double r = this.random.nextDouble() * weightPool;
      int index = Arrays.binarySearch(cumulativeWeights, r);
      if (index < 0) {
         index = -index - 1;
      }

      return list.get(index);
   }

   private double calculateNewWeight(double weight, double bias, double maxWeight, double minWeight) {
      return weight + (bias - 1.0) * minWeight * (1.0 - weight / maxWeight);
   }

   @Override
   public Collection<T> getBiased(WeightedCollection<T> items, int count, double bias) {
      if (bias == 1.0) {
         return this.get(items, count);
      } else {
         List<T> list = items.getView();
         List<T> itemList = Lists.newLinkedList();
         if (list.isEmpty()) {
            return itemList;
         } else {
            double maxWeight = items.getMaxWeight();
            double minWeight = items.getMinWeight();
            double[] cumulativeWeights = new double[items.size()];
            double weightPool = 0.0;

            for (int i = 0; i < list.size(); i++) {
               double weight = this.calculateNewWeight(list.get(i).getWeight(), bias, maxWeight, minWeight);
               weightPool += weight;
               cumulativeWeights[i] = weightPool;
            }

            for (int i = 0; i < count; i++) {
               T item = this.roll(weightPool, cumulativeWeights, list);
               if (item != null) {
                  itemList.add(item);
               }
            }

            return itemList;
         }
      }
   }

   @Override
   public Collection<T> get(WeightedCollection<T> items, int count) {
      List<T> itemList = Lists.newLinkedList();

      for (int i = 0; i < count; i++) {
         T item = this.get(items);
         if (item != null) {
            itemList.add(item);
         }
      }

      return itemList;
   }

   @Override
   public Collection<T> getNonRepeating(WeightedCollection<T> items, int count) {
      List<T> itemList = Lists.newLinkedList();
      List<T> itemsCopy = items.getCopy();
      double maxWeight = items.getWeight();

      for (int i = 0; i < count && !itemsCopy.isEmpty(); i++) {
         double weight = this.random.nextDouble() * maxWeight;
         Iterator<T> it = itemsCopy.iterator();

         while (it.hasNext()) {
            T next = it.next();
            if (weight <= next.getWeight()) {
               itemList.add(next);
               maxWeight -= next.getWeight();
               it.remove();
               break;
            }

            weight -= next.getWeight();
         }
      }

      return itemsCopy;
   }
}
