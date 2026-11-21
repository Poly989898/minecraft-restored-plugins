package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;

public abstract class AbstractFoliaPromise<T> extends AbstractPromise<T> {
   protected AbstractFoliaPromise() {
   }

   protected AbstractFoliaPromise(T val) {
      super(val);
   }

   @Override
   protected final void executeAsync(Runnable runnable) {
      Bukkit.getAsyncScheduler().runNow(ModelEngineAPI.getAPI(), task -> runnable.run());
   }

   @Override
   protected final void executeAsync(Runnable runnable, int delay) {
      Bukkit.getAsyncScheduler().runDelayed(ModelEngineAPI.getAPI(), task -> runnable.run(), delay * 50, TimeUnit.MILLISECONDS);
   }
}
