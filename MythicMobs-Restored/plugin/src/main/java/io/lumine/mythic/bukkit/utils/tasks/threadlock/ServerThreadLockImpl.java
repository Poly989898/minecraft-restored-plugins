package io.lumine.mythic.bukkit.utils.tasks.threadlock;

import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.promise.ThreadContext;
import java.util.concurrent.CountDownLatch;
import org.bukkit.Bukkit;

final class ServerThreadLockImpl implements ServerThreadLock {
   private final CountDownLatch obtainedSignal = new CountDownLatch(1);
   private final CountDownLatch doneSignal = new CountDownLatch(1);

   ServerThreadLockImpl() {
      if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
         Log.info("Thread is already main thread");
         this.obtainedSignal.countDown();
      } else {
         Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderUtils.getPlugin(), this::signal);
         this.await();
         Log.info("ddddddd");
      }
   }

   @Override
   public void close() {
      Log.info("closed");
      this.doneSignal.countDown();
   }

   private void await() {
      Log.info("aaaaaaa");

      try {
         this.obtainedSignal.await();
      } catch (InterruptedException e) {
         e.printStackTrace();
         Thread.currentThread().interrupt();
      }
   }

   private void signal() {
      Log.info("On main thread");
      this.obtainedSignal.countDown();

      try {
         this.doneSignal.await();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
