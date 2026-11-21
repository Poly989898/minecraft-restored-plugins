package io.lumine.mythic.bukkit.utils.events;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public interface Subscription extends Terminable {
   boolean isActive();

   long getCallCounter();

   boolean unregister();

   @Override
   default void close() {
      this.unregister();
   }
}
