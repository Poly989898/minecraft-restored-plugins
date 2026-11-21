package com.ticxo.modelengine.api.utils.registry;

public abstract class TUnaryRegistry<T> extends TRegistry<T, T> {
   @Override
   public T get(String id) {
      if (id == null) {
         return this.getDefault();
      }

      T item = this.registry.get(id);
      return item == null ? this.getDefault() : item;
   }

   @Override
   public T getDefault() {
      return this.defaultItem;
   }

   @Override
   protected T convert(T item) {
      return item;
   }
}
