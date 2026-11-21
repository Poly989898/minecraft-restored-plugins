package io.lumine.mythic.bukkit.utils.events.functional.merged;

import java.util.function.Function;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

class MergedHandlerMapping<T, E extends Event> {
   private final EventPriority priority;
   private final Function<Object, T> function;

   MergedHandlerMapping(EventPriority priority, Function<E, T> function) {
      this.priority = priority;
      this.function = o -> function.apply((E)o);
   }

   public Function<Object, T> getFunction() {
      return this.function;
   }

   public EventPriority getPriority() {
      return this.priority;
   }
}
