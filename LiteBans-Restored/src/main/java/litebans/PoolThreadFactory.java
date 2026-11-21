package litebans;

import java.util.concurrent.ThreadFactory;

public final class PoolThreadFactory implements ThreadFactory {
   private final String text2;
   private final boolean flag2;

   public PoolThreadFactory(String text, boolean flag) {
      this.text2 = text;
      this.flag2 = flag;
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, this.text2);
      thread.setDaemon(this.flag2);
      return thread;
   }
}
