package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerHolder {
   private ScheduledExecutorService scheduledExecutor = null;

   public void perform() {
      try {
         if (this.scheduledExecutor != null && !this.scheduledExecutor.isShutdown()) {
            this.scheduledExecutor.shutdown();
            if (!this.scheduledExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
               this.scheduledExecutor.shutdownNow();
            }
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public ScheduledExecutorService computeScheduledExecutorService(PluginLifecycle pluginLifecycle) {
      ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
      if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
         return this.scheduledExecutor;
      }

      ThreadFactoryBuilder threadFactoryBuilder = pluginLifecycle.obtainThreadFactoryBuilder();
      ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1, threadFactoryBuilder.build());
      this.scheduledExecutor = scheduledThreadPool;
      return scheduledThreadPool;
   }
}
