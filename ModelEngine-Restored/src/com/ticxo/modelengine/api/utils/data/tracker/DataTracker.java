package com.ticxo.modelengine.api.utils.data.tracker;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import lombok.Getter;

public class DataTracker<T> {
   protected final BiPredicate<T, T> equal;
   @Getter
   protected boolean isDirty;
   protected T value;

   public DataTracker() {
      this.equal = Object::equals;
   }

   public DataTracker(T value) {
      this(value, Objects::equals);
   }

   public DataTracker(BiPredicate<T, T> equal) {
      this(null, equal);
   }

   public DataTracker(T value, BiPredicate<T, T> equal) {
      this.value = value;
      this.equal = equal;
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public void clearDirty() {
      this.isDirty = false;
   }

   public void ifDirty(Consumer<T> consumer) {
      if (this.isDirty) {
         consumer.accept(this.get());
      }
   }

   public void ifDirty(Consumer<T> consumer, boolean force) {
      if (this.isDirty || force) {
         consumer.accept(this.get());
      }
   }

   public void set(T value) {
      this.set(value, null);
   }

   public void set(T value, Runnable ifDirty) {
      if (this.value == null || !this.equal.test(this.value, value)) {
         this.value = value;
         this.isDirty = true;
         if (ifDirty != null) {
            ifDirty.run();
         }
      }
   }

   public T get() {
      return this.value;
   }
}
