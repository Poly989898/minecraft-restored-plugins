package io.lumine.mythic.bukkit.utils.collections;

import java.util.Collection;

public interface ISelector<T extends AWeightedItem> {
   default T get(WeightedCollection<T> items) {
      return this.get(items, 0.0);
   }

   default T get(WeightedCollection<T> items, double offset) {
      throw new IllegalArgumentException("Selector does not support offsets");
   }

   default Collection<T> get(WeightedCollection<T> items, int count) {
      return this.get(items, count, 0.0);
   }

   default Collection<T> get(WeightedCollection<T> items, int count, double offset) {
      throw new IllegalArgumentException("Selector does not support offsets");
   }

   default Collection<T> getBiased(WeightedCollection<T> items, int count, double bias) {
      throw new IllegalArgumentException("Selector does not support bias");
   }

   default T getBiased(WeightedCollection<T> items, double bias) {
      throw new IllegalArgumentException("Selector does not support bias");
   }

   default Collection<T> getNonRepeating(WeightedCollection<T> items, int count) {
      return this.getNonRepeating(items, count, 0.0);
   }

   default Collection<T> getNonRepeating(WeightedCollection<T> items, int count, double offset) {
      throw new IllegalArgumentException("Selector does not support offsets");
   }
}
