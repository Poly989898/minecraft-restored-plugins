package io.lumine.mythic.bukkit.utils.promise;

import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;

public enum ThreadContext {
   SYNC,
   ASYNC;

   public static ThreadContext forCurrentThread() {
      return forThread(Thread.currentThread());
   }

   public static ThreadContext forThread(Thread thread) {
      return thread == LoaderUtils.getMainThread() ? SYNC : ASYNC;
   }
}
