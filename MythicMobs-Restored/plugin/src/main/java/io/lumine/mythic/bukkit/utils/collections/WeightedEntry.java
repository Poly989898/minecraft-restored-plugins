package io.lumine.mythic.bukkit.utils.collections;

public class WeightedEntry<T> extends AWeightedItem {
   private T value;

   public WeightedEntry(T value, double weight) {
      super(weight);
      this.value = value;
   }

   public WeightedEntry(T value) {
      this(value, 1.0);
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T value) {
      this.value = value;
   }
}
