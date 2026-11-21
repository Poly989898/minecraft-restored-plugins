package com.ticxo.modelengine.api.utils.data;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PooledCollection<T> {
   private final Supplier<T> instanceSupplier;
   private final Queue<T> available = new ConcurrentLinkedQueue<>();
   @Getter
   private final Set<T> inUse = ConcurrentHashMap.newKeySet();
   @Getter
   private final Set<T> created = ConcurrentHashMap.newKeySet();
   @Getter
   private final Set<T> released = ConcurrentHashMap.newKeySet();

   public T getOrCreate() {
      T instance = this.available.poll();
      if (instance == null) {
         instance = this.instanceSupplier.get();
         this.created.add(instance);
      }

      this.inUse.add(instance);
      this.released.remove(instance);
      return instance;
   }

   public void release(T instance) {
      if (this.inUse.remove(instance)) {
         this.available.add(instance);
         this.released.add(instance);
      }
   }

   public void releaseAll() {
      this.released.addAll(this.inUse);
      this.released.addAll(this.created);
      this.available.addAll(this.inUse);
      this.available.addAll(this.created);
      this.inUse.clear();
      this.created.clear();
   }

   public Set<T> getAll() {
      HashSet<T> set = new HashSet<>(this.available);
      set.addAll(this.inUse);
      return set;
   }

   public void processAll(@Nullable Consumer<T> createdConsumer, @Nullable Consumer<T> oldConsumer, @Nullable Consumer<T> releasedConsumer) {
      if (createdConsumer != null || oldConsumer != null) {
         for (T t : this.inUse) {
            if (this.created.contains(t)) {
               if (createdConsumer != null) {
                  createdConsumer.accept(t);
               }
            } else if (oldConsumer != null) {
               oldConsumer.accept(t);
            }
         }
      }

      if (releasedConsumer != null) {
         for (T t : this.released) {
            releasedConsumer.accept(t);
         }
      }
   }
}
