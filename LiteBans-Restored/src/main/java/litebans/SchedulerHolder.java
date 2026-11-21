package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerHolder {
   private ScheduledExecutorService scheduledExecutorService2 = null;

   public void perform() {
      try {
         if (this.scheduledExecutorService2 != null && !this.scheduledExecutorService2.isShutdown()) {
            this.scheduledExecutorService2.shutdown();
            if (!this.scheduledExecutorService2.awaitTermination(2L, TimeUnit.SECONDS)) {
               this.scheduledExecutorService2.shutdownNow();
            }
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public ScheduledExecutorService computeScheduledExecutorService(PluginLifecycle pluginLifecycle) {
      ScheduledExecutorService scheduledExecutorService = this.scheduledExecutorService2;
      if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
         return this.scheduledExecutorService2;
      }

      ThreadFactoryBuilder threadFactoryBuilder = pluginLifecycle.obtainThreadFactoryBuilder();
      ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1, threadFactoryBuilder.build());
      this.scheduledExecutorService2 = scheduledThreadPool;
      return scheduledThreadPool;
   }
}
