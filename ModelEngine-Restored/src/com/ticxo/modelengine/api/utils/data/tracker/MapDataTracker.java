package com.ticxo.modelengine.api.utils.data.tracker;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapDataTracker<T, U> extends DataTracker<Map<T, U>> implements Map<T, U> {
   public MapDataTracker(Map<T, U> value) {
      super(value);
   }

   public void set(Map<T, U> map, Runnable ifDirty) {
      if (this.value != map && (map == null || !this.value.isEmpty() || !map.isEmpty()) && !this.equal.test(this.value, map)) {
         this.isDirty = true;
         this.value.clear();
         this.value.putAll(map);
         if (ifDirty != null) {
            ifDirty.run();
         }
      }
   }

   @Override
   public int size() {
      return this.value.size();
   }

   @Override
   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.value.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.value.containsValue(value);
   }

   @Override
   public U get(Object key) {
      return this.value.get(key);
   }

   @Nullable
   @Override
   public U put(T key, U value) {
      U prev = this.value.put(key, value);
      this.isDirty |= prev != value;
      return prev;
   }

   @Override
   public U remove(Object key) {
      if (!this.isDirty) {
         this.isDirty = this.value.containsKey(key);
      }

      return this.value.remove(key);
   }

   @Override
   public void putAll(@NotNull Map<? extends T, ? extends U> m) {
      if (!this.isDirty) {
         for (Entry<? extends T, ? extends U> entry : m.entrySet()) {
            U t = this.value.get(entry.getKey());
            if (t == null || t != entry.getValue()) {
               this.isDirty = true;
               break;
            }
         }
      }

      this.value.putAll(m);
   }

   @Override
   public void clear() {
      if (!this.value.isEmpty()) {
         this.value.clear();
         this.isDirty = true;
      }
   }

   @NotNull
   @Override
   public Set<T> keySet() {
      return this.value.keySet();
   }

   @NotNull
   @Override
   public Collection<U> values() {
      return this.value.values();
   }

   @NotNull
   @Override
   public Set<Entry<T, U>> entrySet() {
      return this.value.entrySet();
   }
}
