package com.ticxo.modelengine.api.utils.data.tracker;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class CollectionDataTracker<T> extends DataTracker<Collection<T>> implements Collection<T> {
   public CollectionDataTracker(Collection<T> value) {
      super(value);
   }

   @Override
   public int size() {
      return this.value.size();
   }

   @Override
   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.value.contains(o);
   }

   @NotNull
   @Override
   public Iterator<T> iterator() {
      return this.value.iterator();
   }

   @NotNull
   @Override
   public Object[] toArray() {
      return this.value.toArray();
   }

   @NotNull
   @Override
   public <T1> T1[] toArray(@NotNull T1[] a) {
      return this.value.toArray(a);
   }

   @Override
   public boolean add(T t) {
      boolean flag = this.value.add(t);
      this.isDirty |= flag;
      return flag;
   }

   @Override
   public boolean remove(Object o) {
      boolean flag = this.value.remove(o);
      this.isDirty |= flag;
      return flag;
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.value.containsAll(c);
   }

   @Override
   public boolean addAll(@NotNull Collection<? extends T> c) {
      boolean flag = this.value.addAll(c);
      this.isDirty |= flag;
      return flag;
   }

   @Override
   public boolean removeAll(@NotNull Collection<?> c) {
      boolean flag = this.value.removeAll(c);
      this.isDirty |= flag;
      return flag;
   }

   @Override
   public boolean retainAll(@NotNull Collection<?> c) {
      boolean flag = this.value.retainAll(c);
      this.isDirty |= flag;
      return flag;
   }

   @Override
   public void clear() {
      if (!this.value.isEmpty()) {
         this.value.clear();
         this.isDirty = true;
      }
   }
}
