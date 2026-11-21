package io.lumine.mythic.bukkit.utils.collections;

public abstract class AWeightedItem implements Comparable<AWeightedItem> {
   protected double weight = 1.0;

   protected AWeightedItem() {
   }

   protected AWeightedItem(double weight) {
      this.weight = weight;
   }

   public double getWeight() {
      return this.weight;
   }

   public int compareTo(AWeightedItem o) {
      return Double.compare(this.weight, o.getWeight());
   }
}
