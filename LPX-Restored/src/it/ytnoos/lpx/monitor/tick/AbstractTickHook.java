package it.ytnoos.lpx.monitor.tick;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractTickHook implements TickHook {
   private final Set<TickHook.Callback> tasks = new CopyOnWriteArraySet<>();
   private int tick = 0;

   public AbstractTickHook() {
   }

   protected void runCallbacks() {
      for (TickHook.Callback callback : this.tasks) {
         callback.onTick(this.tick);
      }

      this.tick++;
   }

   @Override
   public int getCurrentTick() {
      return this.tick;
   }

   @Override
   public void addCallback(Object object) {
      this.tasks.add((TickHook.Callback)object);
   }

   @Override
   public void removeCallback(Object object) {
      this.tasks.remove(object);
   }

}
