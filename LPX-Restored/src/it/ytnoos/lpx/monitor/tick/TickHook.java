package it.ytnoos.lpx.monitor.tick;

public interface TickHook extends AutoCloseable {
   void start();

   @Override
   void close();

   int getCurrentTick();

   void addCallback(Object object);

   void removeCallback(Object object);

   interface Callback {
      void onTick(int value);
   }
}
