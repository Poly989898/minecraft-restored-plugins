package litebans;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public final class QueueRejectedHandler implements RejectedExecutionHandler {
   final DatabaseModule databaseModule;

   public QueueRejectedHandler(DatabaseModule databaseModule) {
      this.databaseModule = databaseModule;
   }

   @Override
   public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
      if (!threadPoolExecutor.isShutdown()) {
         try {
            if (this.databaseModule.platform.isPrimaryThread()) {
               this.databaseModule.platform.runAsync(runnable);
            } else {
               runnable.run();
            }
         } catch (Throwable e) {
            if (!(e instanceof InterruptedException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e);
            }
         }
      }
   }
}
