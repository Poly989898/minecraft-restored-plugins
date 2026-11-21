package litebans;

import java.util.concurrent.ScheduledExecutorService;

final class NoopLeakTask extends LeakTask {
   NoopLeakTask() {
      super((NoopLeakTask)null);
   }

   @Override
   void handleScheduledExecutorService(ScheduledExecutorService scheduledExecutorService, long value) {
   }

   @Override
   public void run() {
   }

   @Override
   public void perform() {
   }
}
