package litebans;

import java.util.concurrent.ScheduledExecutorService;

class LeakTaskFactory {
   private ScheduledExecutorService scheduledExecutorService;
   private long longValue;

   LeakTaskFactory(long value, ScheduledExecutorService scheduledExecutorService) {
      this.scheduledExecutorService = scheduledExecutorService;
      this.longValue = value;
   }

   LeakTask computeLeakTask(PoolEntry poolEntry) {
      return this.longValue == 0L ? LeakTask.LEAK_TASK : this.computeLeakTask2(poolEntry);
   }

   void setLongValue(long value) {
      this.longValue = value;
   }

   private final LeakTask computeLeakTask2(PoolEntry poolEntry) {
      LeakTask leakTask = new LeakTask(poolEntry);
      leakTask.handleScheduledExecutorService(this.scheduledExecutorService, this.longValue);
      return leakTask;
   }
}
