package io.lumine.mythic.bukkit.utils.cache;

import java.util.function.Consumer;

public class DataTracker<T> {
   protected boolean isDirty = true;
   protected T value;

   public static <T> DataTracker<T> of(T object) {
      return new DataTracker<>(object);
   }

   public static <T> DataTracker<T> of(T object, boolean dirty) {
      return new DataTracker<>(object, dirty);
   }

   public DataTracker() {
   }

   public DataTracker(T value) {
      this.value = value;
      if (value == null) {
         this.isDirty = false;
      } else {
         this.isDirty = true;
      }
   }

   public DataTracker(T value, boolean dirty) {
      this.value = value;
      this.isDirty = dirty;
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public void clearDirty() {
      this.isDirty = false;
   }

   public void ifDirty(Consumer<T> consumer) {
      if (this.isDirty) {
         consumer.accept(this.value);
         this.isDirty = false;
      }
   }

   public void ifDirty(Consumer<T> consumer, boolean force) {
      if (this.isDirty || force) {
         consumer.accept(this.value);
         this.isDirty = false;
      }
   }

   public boolean set(T value) {
      if (this.value != null && this.value.equals(value)) {
         return false;
      } else {
         this.value = value;
         this.isDirty = true;
         return true;
      }
   }

   public void poke(T value) {
      this.value = value;
   }

   public T peek() {
      return this.value;
   }

   public T get() {
      this.isDirty = false;
      return this.value;
   }

   public boolean isDirty() {
      return this.isDirty;
   }
}
