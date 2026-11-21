package io.lumine.mythic.api.adapters;

@Deprecated
public interface AbstractTaskManager {
   int scheduleAsyncTask(Runnable runnable, int value, int value2);

   int scheduleTask(Runnable runnable, int value, int value2);

   void cancelTask(int value);

   void runAsync(Runnable runnable);

   void runLater(Runnable runnable, int value);

   void runAsyncLater(Runnable runnable, int value);

   void run(Runnable runnable);
}
