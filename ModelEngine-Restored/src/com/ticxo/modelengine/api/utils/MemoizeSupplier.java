package com.ticxo.modelengine.api.utils;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoizeSupplier<T> {
   private final Supplier<T> supplier;
   private boolean initialized;
   private T value;

   public T get() {
      if (!this.initialized) {
         this.value = this.supplier.get();
         this.initialized = true;
      }

      return this.value;
   }

   public static <T> MemoizeSupplier<T> of(Supplier<T> supplier) {
      return new MemoizeSupplier<>(supplier);
   }
}
