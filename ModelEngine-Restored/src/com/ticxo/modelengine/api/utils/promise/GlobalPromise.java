package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;

public class GlobalPromise<T> extends AbstractFoliaPromise<T> {
   private GlobalPromise() {
   }

   private GlobalPromise(T val) {
      super(val);
   }

   @Override
   protected <U> AbstractPromise<U> createEmpty() {
      return empty();
   }

   @Override
   protected void executeSync(Runnable runnable) {
      Bukkit.getGlobalRegionScheduler().execute(ModelEngineAPI.getAPI(), runnable);
   }

   @Override
   protected void executeSync(Runnable runnable, int delay) {
      Bukkit.getGlobalRegionScheduler().runDelayed(ModelEngineAPI.getAPI(), task -> runnable.run(), delay);
   }

   static <T> GlobalPromise<T> empty() {
      return new GlobalPromise<>();
   }

   static <T> GlobalPromise<T> completed(T val) {
      return new GlobalPromise<>(val);
   }
}
