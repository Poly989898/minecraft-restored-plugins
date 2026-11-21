package litebans;

import java.sql.SQLTransientException;
import java.util.concurrent.Semaphore;

public class SuspendResumeLock {
   public static final SuspendResumeLock SUSPEND_RESUME_LOCK = new NoopSuspendResumeLock(false);
   private static final int VALUE_10000 = 10000;
   private final Semaphore semaphore;

   public SuspendResumeLock() {
      this(true);
   }

   private SuspendResumeLock(boolean flag) {
      this.semaphore = flag ? new Semaphore(10000, true) : null;
   }

   public void perform() {
      try {
         if (!this.semaphore.tryAcquire()) {
            if (Boolean.getBoolean("com.zaxxer.hikari.throwIfSuspended")) {
               throw new SQLTransientException("The pool is currently suspended and configured to throw exceptions upon acquisition");
            }

            this.semaphore.acquireUninterruptibly();
         }
      } catch (SQLTransientException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public void perform2() {
      this.semaphore.release();
   }

   public void perform3() {
      this.semaphore.acquireUninterruptibly(10000);
   }

   public void perform4() {
      this.semaphore.release(10000);
   }

   SuspendResumeLock(boolean flag, NoopSuspendResumeLock noopSuspendResumeLock) {
      this(flag);
   }
}
