package com.ticxo.modelengine.api.entity.data;

import com.ticxo.modelengine.api.entity.CullType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TrackingMap extends ConcurrentHashMap<UUID, CullType> {
   private static final long serialVersionUID = 1L;
   private final AtomicLong version = new AtomicLong();
   private transient volatile TrackingMap.Snapshot snapshot;

   public Map<UUID, CullType> snapshot() {
      long current = this.version.get();
      TrackingMap.Snapshot cached = this.snapshot;
      if (cached != null && cached.version == current && cached.map.size() == this.size()) {
         return cached.map;
      }

      Map<UUID, CullType> copy = Collections.unmodifiableMap(new HashMap<>(this));
      this.snapshot = new TrackingMap.Snapshot(current, copy);
      return copy;
   }

   public long getVersion() {
      return this.version.get();
   }

   private void touch() {
      this.version.incrementAndGet();
   }

   public CullType put(UUID key, CullType value) {
      CullType previous = super.put(key, value);
      if (previous != value) {
         this.touch();
      }

      return previous;
   }

   public CullType putIfAbsent(UUID key, CullType value) {
      CullType previous = super.putIfAbsent(key, value);
      if (previous == null) {
         this.touch();
      }

      return previous;
   }

   @Override
   public void putAll(Map<? extends UUID, ? extends CullType> m) {
      try {
         super.putAll(m);
      } finally {
         this.touch();
      }
   }

   public CullType remove(Object key) {
      CullType previous = super.remove(key);
      if (previous != null) {
         this.touch();
      }

      return previous;
   }

   @Override
   public boolean remove(Object key, Object value) {
      boolean removed = super.remove(key, value);
      if (removed) {
         this.touch();
      }

      return removed;
   }

   public CullType replace(UUID key, CullType value) {
      CullType previous = super.replace(key, value);
      if (previous != null && previous != value) {
         this.touch();
      }

      return previous;
   }

   public boolean replace(UUID key, CullType oldValue, CullType newValue) {
      boolean replaced = super.replace(key, oldValue, newValue);
      if (replaced && oldValue != newValue) {
         this.touch();
      }

      return replaced;
   }

   @Override
   public void clear() {
      try {
         super.clear();
      } finally {
         this.touch();
      }
   }

   public CullType compute(UUID key, BiFunction<? super UUID, ? super CullType, ? extends CullType> function) {
      try {
         return super.compute(key, function);
      } finally {
         this.touch();
      }
   }

   public CullType computeIfAbsent(UUID key, Function<? super UUID, ? extends CullType> function) {
      try {
         return super.computeIfAbsent(key, function);
      } finally {
         this.touch();
      }
   }

   public CullType computeIfPresent(UUID key, BiFunction<? super UUID, ? super CullType, ? extends CullType> function) {
      try {
         return super.computeIfPresent(key, function);
      } finally {
         this.touch();
      }
   }

   public CullType merge(UUID key, CullType value, BiFunction<? super CullType, ? super CullType, ? extends CullType> function) {
      try {
         return super.merge(key, value, function);
      } finally {
         this.touch();
      }
   }

   @Override
   public void replaceAll(BiFunction<? super UUID, ? super CullType, ? extends CullType> function) {
      try {
         super.replaceAll(function);
      } finally {
         this.touch();
      }
   }

   private static final class Snapshot {
      private final long version;
      private final Map<UUID, CullType> map;

      private Snapshot(long version, Map<UUID, CullType> map) {
         this.version = version;
         this.map = map;
      }
   }
}
