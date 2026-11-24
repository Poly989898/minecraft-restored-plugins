package me.TechsCode.UltraPermissions.base.scheduler;

public interface Scheduler {
   void run(Runnable task);

   void runAsync(Runnable task);

   Task runTaskLater(Runnable task, long value);

   RecurringTask runTaskTimer(Runnable task, long value, long period);

   Task runTaskLaterAsync(Runnable task, long value);

   RecurringTask runTaskTimerAsync(Runnable task, long value, long period);
}
