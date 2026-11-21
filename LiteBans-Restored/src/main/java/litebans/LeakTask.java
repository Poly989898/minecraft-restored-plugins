package litebans;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class LeakTask implements Runnable {
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(LeakTask.class);
   static final LeakTask LEAK_TASK = new NoopLeakTask();
   private ScheduledFuture scheduledFuture;
   private String text;
   private Exception exception;
   private String text2;
   private boolean flag;

   LeakTask(PoolEntry poolEntry) {
      this.exception = new Exception("Apparent connection leak detected");
      this.text2 = Thread.currentThread().getName();
      this.text = poolEntry.connection2.toString();
   }

   private LeakTask() {
   }

   void handleScheduledExecutorService(ScheduledExecutorService scheduledExecutorService, long value) {
      this.scheduledFuture = scheduledExecutorService.schedule(this, value, TimeUnit.MILLISECONDS);
   }

   @Override
   public void run() {
      this.flag = true;
      StackTraceElement[] stackTrace = this.exception.getStackTrace();
      StackTraceElement[] stackTraceElements = new StackTraceElement[stackTrace.length - 5];
      System.arraycopy(stackTrace, 5, stackTraceElements, 0, stackTraceElements.length);
      this.exception.setStackTrace(stackTraceElements);
      POOL_LOGGER.handleText4("Connection leak detection triggered for %s on thread %s, stack trace follows", new Object[]{this.text, this.text2, this.exception});
   }

   void perform() {
      this.scheduledFuture.cancel(false);
      if (this.flag) {
         POOL_LOGGER.handleText3("Previously reported leaked connection %s on thread %s was returned to the pool (unleaked)", new Object[]{this.text, this.text2});
      }
   }

   LeakTask(NoopLeakTask noopLeakTask) {
      this();
   }
}
