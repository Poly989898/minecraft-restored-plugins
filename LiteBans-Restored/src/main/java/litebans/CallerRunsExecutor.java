package litebans;

import java.util.concurrent.Executor;

class CallerRunsExecutor implements Executor {
   private CallerRunsExecutor() {
   }

   @Override
   public void execute(Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception e) {
         PoolLogger.computePoolLogger(PoolBase.class).handleText("Failed to execute: %s", new Object[]{runnable.toString(), e});
      }
   }

   CallerRunsExecutor(EnumSwitchMap2 enumSwitchMap) {
      this();
   }
}
