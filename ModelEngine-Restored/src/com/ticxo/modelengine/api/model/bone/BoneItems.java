package com.ticxo.modelengine.api.model.bone;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoneItems {
   private static final boolean COPY_FOR_ITERATION_ORDER = !MiscUtils.isJavaVersionOrHigher(21);
   private final Map<Integer, ItemStack> stacks = Maps.newConcurrentMap();
   private final Map<ItemStack, Integer> toHash = Maps.newConcurrentMap();
   @Getter
   private final DataTracker<BoneItems> tracker = new DataTracker<>(this);
   private volatile ItemStack[] bakedStacks = new ItemStack[0];

   public ItemStack getFirst() {
      this.prepStacks();
      return this.stacks.entrySet().iterator().next().getValue();
   }

   public Map<Integer, ItemStack> getItems() {
      return new HashMap<>(this.stacks);
   }

   private void bakeStacks() {
      this.bakedStacks = this.stacks.values().toArray(new ItemStack[0]);
   }

   public void forEach(BiConsumer<Integer, ItemStack> consumer) {
      ItemStack[] current = this.bakedStacks;

      for (int i = 0; i < current.length; i++) {
         consumer.accept(current[i].hashCode(), current[i]);
      }
   }

   public void forEach(Consumer<ItemStack> consumer) {
      this.forEach(consumer, false);
   }

   public void forEach(Consumer<ItemStack> consumer, boolean markDirty) {
      this.forEach(consumer, () -> markDirty);
   }

   public void forEach(Consumer<ItemStack> consumer, Supplier<Boolean> markDirty) {
      ItemStack[] current = this.bakedStacks;

      for (int i = 0; i < current.length; i++) {
         consumer.accept(current[i]);
      }

      if (markDirty.get()) {
         this.rebuildMaps(current);
      }
   }

   public void forEachMutating(Predicate<ItemStack> consumer) {
      ItemStack[] current = this.bakedStacks;
      boolean dirty = false;

      for (int i = 0; i < current.length; i++) {
         dirty |= consumer.test(current[i]);
      }

      if (dirty) {
         this.rebuildMaps(current);
      }
   }

   private void rebuildMaps(ItemStack[] current) {
      this.stacks.clear();
      this.toHash.clear();

      for (int i = 0; i < current.length; i++) {
         int hash = current[i].hashCode();
         this.stacks.put(hash, current[i]);
         this.toHash.put(current[i], hash);
      }

      this.bakeStacks();
      this.markDirty();
   }

   public void update(BlueprintBone bone, Color color) {
      Set<ItemStack> created = bone.getModelData().createItemStack(ItemModelData.context().color(color).build());
      Set<ItemStack> items = COPY_FOR_ITERATION_ORDER ? new HashSet<>(created) : created;
      if (!items.isEmpty() || !this.stacks.isEmpty()) {
         this.stacks.clear();
         this.toHash.clear();
         items.forEach(this::addWithoutBaking);
         this.bakeStacks();
      }
   }

   public void clear() {
      if (!this.stacks.isEmpty()) {
         this.stacks.clear();
         this.toHash.clear();
         this.markDirty();
         this.bakeStacks();
      }
   }

   public void add(ItemStack stack) {
      if (this.addWithoutBaking(stack)) {
         this.bakeStacks();
      }
   }

   private boolean addWithoutBaking(ItemStack stack) {
      if (this.toHash.containsKey(stack)) {
         return false;
      }

      if (stack == null) {
         stack = new ItemStack(Material.AIR);
      }

      Integer hash = stack.hashCode();
      this.stacks.put(hash, stack);
      this.toHash.put(stack, hash);
      this.markDirty();
      return true;
   }

   public void remove(ItemStack stack) {
      if (this.toHash.containsKey(stack)) {
         this.stacks.remove(this.toHash.remove(stack));
         this.markDirty();
         this.bakeStacks();
      }
   }

   public void markDirty() {
      this.tracker.markDirty();
   }

   public void clearDirty() {
      this.tracker.clearDirty();
   }

   public boolean isDirty() {
      return this.tracker.isDirty();
   }

   public boolean isEqual(Set<ItemStack> stackSet) {
      return stackSet.equals(this.toHash.keySet());
   }

   private void prepStacks() {
      if (this.stacks.isEmpty()) {
         this.add(new ItemStack(Material.AIR));
      }
   }
}
