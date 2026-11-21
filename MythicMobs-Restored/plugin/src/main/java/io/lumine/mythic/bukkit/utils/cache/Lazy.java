package io.lumine.mythic.bukkit.utils.cache;

import java.util.Objects;
import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
   private volatile Supplier<T> supplier;
   private volatile boolean initialized = false;
   private T value;

   public static <T> Lazy<T> suppliedBy(Supplier<T> supplier) {
      return new Lazy<>(Objects.requireNonNull(supplier, "supplier"));
   }

   public static <T> Lazy<T> of(T value) {
      return new Lazy<>(Objects.requireNonNull(value, "value"));
   }

   private Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   private Lazy(T value) {
      this.value = value;
      this.initialized = true;
   }

   @Override
   public T get() {
      if (!this.initialized) {
         synchronized (this) {
            if (!this.initialized) {
               T t = this.supplier.get();
               this.value = t;
               this.initialized = true;
               this.supplier = null;
               return t;
            }
         }
      }

      return this.value;
   }
}
