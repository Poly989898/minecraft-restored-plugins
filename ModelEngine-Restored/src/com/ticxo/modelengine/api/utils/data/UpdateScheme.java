package com.ticxo.modelengine.api.utils.data;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import lombok.Getter;

@Getter
public class UpdateScheme<T> {
   private final Map<T, UpdateScheme.Mode> updates = new HashMap<>();
   private final Set<T> added = new UpdateScheme.View(UpdateScheme.Mode.ADD);
   private final Set<T> updated = new UpdateScheme.View(UpdateScheme.Mode.UPDATE);
   private final Set<T> removed = new UpdateScheme.View(UpdateScheme.Mode.REMOVE);

   public void addUpdated(T element) {
      this.updates.compute(element, (k, v) -> (v == UpdateScheme.Mode.ADD ? v : UpdateScheme.Mode.UPDATE));
   }

   public void addAdded(T element) {
      this.updates.put(element, UpdateScheme.Mode.ADD);
   }

   public void addRemove(T element) {
      this.updates.put(element, UpdateScheme.Mode.REMOVE);
   }

   public boolean hasUpdate() {
      return !this.updates.isEmpty();
   }

   public UpdateScheme.Mode getUpdateMode(T element) {
      UpdateScheme.Mode mode = this.updates.get(element);
      return mode == null ? UpdateScheme.Mode.NONE : mode;
   }

   public void reset() {
      this.updates.clear();
   }

   public enum Mode {
      NONE,
      UPDATE,
      ADD,
      REMOVE;
   }

   private class View extends AbstractSet<T> {
      private final UpdateScheme.Mode mode;

      private View(UpdateScheme.Mode mode) {
         this.mode = mode;
      }

      @Override
      public Iterator<T> iterator() {
         final Iterator<Entry<T, UpdateScheme.Mode>> it = UpdateScheme.this.updates.entrySet().iterator();
         return new Iterator<T>() {
            private Entry<T, UpdateScheme.Mode> next;

            @Override
            public boolean hasNext() {
               while (it.hasNext()) {
                  this.next = it.next();
                  if (this.next.getValue() == View.this.mode) {
                     return true;
                  }
               }

               return false;
            }

            @Override
            public T next() {
               if (this.next == null) {
                  throw new NoSuchElementException();
               } else {
                  return this.next.getKey();
               }
            }
         };
      }

      @Override
      public int size() {
         int size = 0;

         for (UpdateScheme.Mode m : UpdateScheme.this.updates.values()) {
            if (m == this.mode) {
               size++;
            }
         }

         return size;
      }

      @Override
      public boolean contains(Object o) {
         return UpdateScheme.this.updates.get(o) == this.mode;
      }

      @Override
      public boolean remove(Object o) {
         UpdateScheme.Mode current = UpdateScheme.this.updates.get(o);
         if (current == this.mode) {
            UpdateScheme.this.updates.remove(o);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         UpdateScheme.this.updates.values().removeIf(m -> m == this.mode);
      }
   }
}
