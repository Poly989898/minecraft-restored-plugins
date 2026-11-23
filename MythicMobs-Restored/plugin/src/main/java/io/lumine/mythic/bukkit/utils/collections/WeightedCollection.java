package io.lumine.mythic.bukkit.utils.collections;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.functions.IParamFunction;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WeightedCollection<T extends AWeightedItem> {
   private List<T> items = Lists.newArrayList();
   private double weight = 0.0;
   private ISelector<T> selector = new WeightedSelector<>();

   public WeightedCollection() {
   }

   public WeightedCollection(ISelector<T> selector) {
      this.selector = selector;
   }

   public WeightedCollection(WeightedCollection<T> source) {
      this.items = source.getCopy();
      this.weight = source.weight;
   }

   public double getWeight() {
      return this.weight;
   }

   public double getMaxWeight() {
      return this.items.isEmpty() ? 0.0 : this.items.getFirst().weight;
   }

   public double getMinWeight() {
      return this.items.isEmpty() ? 0.0 : this.items.getLast().weight;
   }

   public int size() {
      return this.items.size();
   }

   public boolean add(T item) {
      return this.sortedAction(this.items::add, item);
   }

   public boolean addAll(Collection<? extends T> items) {
      return this.sortedAction(this.items::addAll, items);
   }

   public boolean remove(T item) {
      return this.sortedAction(this.items::remove, item);
   }

   public boolean removeAll(Collection<T> items) {
      return this.sortedAction(this.items::removeAll, items);
   }

   public void clear() {
      this.items.clear();
      this.weight = 0.0;
   }

   private final <T1, T2> T1 sortedAction(IParamFunction<T1, T2> action, T2 param) {
      Object obj;
      try {
         obj = action.execute(param);
      } finally {
         this.items.sort(Collections.reverseOrder());
         this.weight = this.items.stream().mapToDouble(d -> d.weight).sum();
      }

      return (T1)obj;
   }

   public List<T> getView() {
      return Collections.unmodifiableList(this.items);
   }

   public List<T> getCopy() {
      return Lists.newArrayList(this.items);
   }

   public WeightedCollection<T> copy() {
      return new WeightedCollection<>(this);
   }

   public T get() {
      return this.selector.get(this);
   }

   public T get(double offset) {
      return this.selector.get(this, offset);
   }

   public Collection<T> getBiased(int count, double bias) {
      return this.selector.getBiased(this, count, bias);
   }

   public Collection<T> get(int count) {
      return this.selector.get(this, count);
   }

   public Collection<T> get(int count, double offset) {
      return this.selector.get(this, count, offset);
   }

   public Collection<T> getNonRepeating(int count) {
      return this.selector.getNonRepeating(this, count);
   }

   public Collection<T> getNonRepeating(int count, double offset) {
      return this.selector.getNonRepeating(this, count, offset);
   }

   public List<T> getRandomWeightedSortedList() {
      List<Pair<T, Double>> weightedItems = Lists.newArrayList();
      double cumulativeWeight = 0.0;

      for (T item : this.items) {
         cumulativeWeight += item.weight;
         weightedItems.add(Pair.of(item, cumulativeWeight));
      }

      Random random = new Random();
      List<T> randomSortedList = Lists.newArrayList();
      double totalWeight = cumulativeWeight;

      while (!weightedItems.isEmpty()) {
         double r = random.nextDouble() * totalWeight;
         int selectedIndex = -1;

         for (int i = 0; i < weightedItems.size(); i++) {
            if (r < weightedItems.get(i).getRight()) {
               selectedIndex = i;
               break;
            }
         }

         if (selectedIndex != -1) {
            T selectedItem = weightedItems.get(selectedIndex).getLeft();
            randomSortedList.add(selectedItem);
            weightedItems.remove(selectedIndex);
            totalWeight = weightedItems.isEmpty() ? 0.0 : weightedItems.get(weightedItems.size() - 1).getRight();
         }
      }

      return randomSortedList;
   }
}
