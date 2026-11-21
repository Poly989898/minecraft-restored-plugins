package com.ticxo.modelengine.api.utils.data;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedImmutableMap<K, V> {
   private final Supplier<Map<K, V>> mapSupplier;
   private volatile Map<K, V> cache;

   public CachedImmutableMap(Map<K, V> map) {
      this(() -> map);
   }

   public Map<K, V> get() {
      if (this.cache != null) {
         return this.cache;
      }

      synchronized (this) {
         if (this.cache != null) {
            return this.cache;
         }

         Map<K, V> supplied = this.mapSupplier.get();
         return this.cache = supplied == null ? ImmutableMap.of() : ImmutableMap.copyOf(supplied);
      }
   }

   public Map<K, V> getIfCached() {
      return this.cache;
   }

   public void invalidate() {
      this.cache = null;
   }
}
