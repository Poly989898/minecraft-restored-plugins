package it.ytnoos.lpx.inventories.smartinvs;

import java.util.function.Consumer;

public class InventoryListener<T> {
   private final Class<T> type;
   private final Consumer<T> consumer;

   public InventoryListener(Class<T> class2, Consumer<T> consumer2) {
      this.type = class2;
      this.consumer = consumer2;
   }

   public void accept(Object object) {
      this.consumer.accept((T)object);
   }

   public Class getType() {
      return this.type;
   }
}
