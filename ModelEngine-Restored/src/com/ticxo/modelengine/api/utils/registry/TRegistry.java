package com.ticxo.modelengine.api.utils.registry;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.Setter;

@Setter
public abstract class TRegistry<T, R> {
   protected final Map<String, T> registry = this.mapSupplier();
   protected T defaultItem;

   public void register(String id, T item) {
      this.registry.put(id, item);
   }

   public void registerAndDefault(String id, T item) {
      this.registry.put(id, item);
      this.defaultItem = item;
   }

   public R get(String id) {
      T item = this.registry.get(id);
      return item == null ? this.getDefault() : this.convert(item);
   }

   public R getDefault() {
      return this.convert(this.defaultItem);
   }

   public Set<String> getKeys() {
      return this.registry.keySet();
   }

   public Collection<T> getValues() {
      return this.registry.values();
   }

   protected abstract R convert(T item);

   protected Map<String, T> mapSupplier() {
      return Maps.newConcurrentMap();
   }
}
