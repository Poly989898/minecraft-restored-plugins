package com.ticxo.modelengine.api.utils.data.tracker;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class UpdateDataTracker<T> extends DataTracker<T> {
   private final BiConsumer<T, T> setter;
   private final Function<T, T> supplier;
   private final BiPredicate<T, T> comparer;

   public UpdateDataTracker(BiConsumer<T, T> setter) {
      this.setter = setter;
      this.supplier = t -> t;
      this.comparer = Objects::equals;
   }

   public UpdateDataTracker(T value, BiConsumer<T, T> setter) {
      super(value);
      this.setter = setter;
      this.supplier = t -> t;
      this.comparer = Objects::equals;
   }

   public UpdateDataTracker(T value, BiConsumer<T, T> setter, Function<T, T> supplier) {
      super(value);
      this.setter = setter;
      this.supplier = supplier;
      this.comparer = Objects::equals;
   }

   public UpdateDataTracker(T value, BiConsumer<T, T> setter, Function<T, T> supplier, BiPredicate<T, T> comparer) {
      super(value);
      this.setter = setter;
      this.supplier = supplier;
      this.comparer = comparer;
   }

   @Override
   public void set(T value) {
      this.set(value, null);
   }

   @Override
   public void set(T value, Runnable ifDirty) {
      if (this.value == null || !this.comparer.test(this.value, value)) {
         this.setter.accept(this.value, value);
         this.isDirty = true;
         if (ifDirty != null) {
            ifDirty.run();
         }
      }
   }

   @Override
   public T get() {
      return this.supplier.apply(this.value);
   }
}
