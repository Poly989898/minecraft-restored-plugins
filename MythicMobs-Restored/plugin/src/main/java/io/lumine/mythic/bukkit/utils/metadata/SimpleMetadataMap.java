package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

final class SimpleMetadataMap implements MetadataMap {
   private final Map<MetadataKey<?>, Object> map = new HashMap<>();
   private final transient ReentrantLock lock = new ReentrantLock();

   @Override
   public <T> void put(MetadataKey<T> key, T value) {
      this.internalPut(key, value);
   }

   @Override
   public <T> void put(MetadataKey<T> key, TransientValue<T> value) {
      this.internalPut(key, value);
   }

   private void internalPut(MetadataKey<?> key, Object value) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(value, "value");
      this.lock.lock();

      try {
         MetadataKey<?> existing = null;

         for (MetadataKey<?> k : this.map.keySet()) {
            if (k.equals(key)) {
               existing = k;
               break;
            }
         }

         if (existing != null && !existing.getType().equals(key.getType())) {
            throw new ClassCastException(
               "Cannot cast key with id "
                  + key.getId()
                  + " with type "
                  + key.getType().getRawType()
                  + " to existing stored type "
                  + existing.getType().getRawType()
            );
         }

         this.map.put(key, value);
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public <T> void forcePut(MetadataKey<T> key, T value) {
      this.internalForcePut(key, value);
   }

   @Override
   public <T> void forcePut(MetadataKey<T> key, TransientValue<T> value) {
      this.internalForcePut(key, value);
   }

   private void internalForcePut(MetadataKey<?> key, Object value) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(value, "value");
      this.lock.lock();

      try {
         this.map.put(key, value);
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public <T> boolean putIfAbsent(MetadataKey<T> key, T value) {
      return this.internalPutIfAbsent(key, value);
   }

   @Override
   public <T> boolean putIfAbsent(MetadataKey<T> key, TransientValue<T> value) {
      return this.internalPutIfAbsent(key, value);
   }

   private boolean internalPutIfAbsent(MetadataKey<?> key, Object value) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(value, "value");
      this.lock.lock();

      boolean flag;
      try {
         this.doExpire();
         flag = this.map.putIfAbsent(key, value) == null;
      } finally {
         this.lock.unlock();
      }

      return flag;
   }

   @Override
   public <T> Optional<T> get(MetadataKey<T> key) {
      Preconditions.checkNotNull(key, "key");
      this.lock.lock();

      Optional optional;
      try {
         Entry<MetadataKey<?>, Object> existing = null;
         Iterator<Entry<MetadataKey<?>, Object>> it = this.map.entrySet().iterator();

         while (it.hasNext()) {
            Entry<MetadataKey<?>, Object> kv = it.next();
            if (kv.getValue() instanceof TransientValue) {
               TransientValue<?> transientValue = (TransientValue<?>)kv.getValue();
               Object unboxed = transientValue.getOrNull();
               if (unboxed == null) {
                  it.remove();
               } else if (kv.getKey().equals(key)) {
                  existing = Maps.immutableEntry(kv.getKey(), unboxed);
                  break;
               }
            } else if (kv.getKey().equals(key)) {
               existing = kv;
               break;
            }
         }

         if (existing != null) {
            if (!existing.getKey().getType().equals(key.getType())) {
               throw new ClassCastException(
                  "Cannot cast key with id "
                     + key.getId()
                     + " with type "
                     + key.getType().getRawType()
                     + " to existing stored type "
                     + existing.getKey().getType().getRawType()
               );
            }

            return Optional.of(key.cast(existing.getValue()));
         }

         optional = Optional.empty();
      } finally {
         this.lock.unlock();
      }

      return optional;
   }

   @Override
   public <T> T getOrNull(MetadataKey<T> key) {
      Preconditions.checkNotNull(key, "key");
      return this.get(key).orElse(null);
   }

   @Override
   public <T> T getOrDefault(MetadataKey<T> key, T def) {
      Preconditions.checkNotNull(key, "key");
      return this.get(key).orElse(def);
   }

   @Override
   public <T> T getOrPut(MetadataKey<T> key, Supplier<T> def) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(def, "def");
      this.lock.lock();

      TransientValue<?> transientValue;
      T result;
      try {
         Entry<MetadataKey<?>, Object> existing = null;
         Iterator<Entry<MetadataKey<?>, Object>> it = this.map.entrySet().iterator();

         while (it.hasNext()) {
            Entry<MetadataKey<?>, Object> kv = it.next();
            if (kv.getValue() instanceof TransientValue) {
               transientValue = (TransientValue<?>)kv.getValue();
               Object unboxed = transientValue.getOrNull();
               if (unboxed == null) {
                  it.remove();
               } else if (kv.getKey().equals(key)) {
                  existing = Maps.immutableEntry(kv.getKey(), unboxed);
                  break;
               }
            } else if (kv.getKey().equals(key)) {
               existing = kv;
               break;
            }
         }

         if (existing != null) {
            if (!existing.getKey().getType().equals(key.getType())) {
               throw new ClassCastException(
                  "Cannot cast key with id "
                     + key.getId()
                     + " with type "
                     + key.getType().getRawType()
                     + " to existing stored type "
                     + existing.getKey().getType().getRawType()
               );
            }

            return key.cast(existing.getValue());
         }

         T t = def.get();
         Preconditions.checkNotNull(t, "supplied def");
         this.map.put(key, t);
         result = t;
      } finally {
         this.lock.unlock();
      }

      return result;
   }

   @Override
   public <T> T getOrPutExpiring(MetadataKey<T> key, Supplier<TransientValue<T>> def) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(def, "def");
      this.lock.lock();

      Object unboxed;
      try {
         Entry<MetadataKey<?>, Object> existing = null;
         Iterator<Entry<MetadataKey<?>, Object>> it = this.map.entrySet().iterator();

         while (it.hasNext()) {
            Entry<MetadataKey<?>, Object> kv = it.next();
            if (kv.getValue() instanceof TransientValue) {
               TransientValue<?> transientValue = (TransientValue<?>)kv.getValue();
               unboxed = transientValue.getOrNull();
               if (unboxed == null) {
                  it.remove();
               } else if (kv.getKey().equals(key)) {
                  existing = Maps.immutableEntry(kv.getKey(), unboxed);
                  break;
               }
            } else if (kv.getKey().equals(key)) {
               existing = kv;
               break;
            }
         }

         if (existing != null) {
            if (!existing.getKey().getType().equals(key.getType())) {
               throw new ClassCastException(
                  "Cannot cast key with id "
                     + key.getId()
                     + " with type "
                     + key.getType().getRawType()
                     + " to existing stored type "
                     + existing.getKey().getType().getRawType()
               );
            }

            return key.cast(existing.getValue());
         }

         TransientValue<T> t = def.get();
         Preconditions.checkNotNull(t, "supplied def");
         T value = t.getOrNull();
         if (value == null) {
            throw new IllegalArgumentException("Transient value already expired: " + t);
         }

         this.map.put(key, t);
         unboxed = value;
      } finally {
         this.lock.unlock();
      }

      return (T)unboxed;
   }

   @Override
   public boolean has(MetadataKey<?> key) {
      Preconditions.checkNotNull(key, "key");
      this.lock.lock();

      boolean flag;
      try {
         Entry<MetadataKey<?>, Object> existing = null;
         Iterator<Entry<MetadataKey<?>, Object>> it = this.map.entrySet().iterator();

         while (it.hasNext()) {
            Entry<MetadataKey<?>, Object> kv = it.next();
            if (kv.getValue() instanceof TransientValue) {
               TransientValue<?> transientValue = (TransientValue<?>)kv.getValue();
               if (transientValue.shouldExpire()) {
                  it.remove();
                  continue;
               }
            }

            if (kv.getKey().equals(key)) {
               existing = kv;
               break;
            }
         }

         flag = existing != null && existing.getKey().getType().equals(key.getType());
      } finally {
         this.lock.unlock();
      }

      return flag;
   }

   @Override
   public boolean remove(MetadataKey<?> key) {
      Preconditions.checkNotNull(key, "key");
      this.lock.lock();

      boolean flag;
      try {
         flag = this.map.remove(key) != null;
      } finally {
         this.lock.unlock();
      }

      return flag;
   }

   @Override
   public void clear() {
      this.lock.lock();

      try {
         this.map.clear();
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public ImmutableMap<MetadataKey<?>, Object> asMap() {
      this.lock.lock();

      ImmutableMap immutableMap;
      try {
         immutableMap = ImmutableMap.copyOf(this.map);
      } finally {
         this.lock.unlock();
      }

      return immutableMap;
   }

   @Override
   public boolean isEmpty() {
      this.lock.lock();

      boolean flag;
      try {
         this.doExpire();
         flag = this.map.isEmpty();
      } finally {
         this.lock.unlock();
      }

      return flag;
   }

   private void doExpire() {
      this.lock.lock();

      try {
         this.map.values().removeIf(o -> o instanceof TransientValue && ((TransientValue)o).shouldExpire());
      } finally {
         this.lock.unlock();
      }
   }
}
