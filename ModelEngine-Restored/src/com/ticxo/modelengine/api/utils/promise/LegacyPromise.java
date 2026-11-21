package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;

public class LegacyPromise<T> extends AbstractPromise<T> {
   private LegacyPromise() {
   }

   private LegacyPromise(T val) {
      super(val);
   }

   @Override
   protected <U> AbstractPromise<U> createEmpty() {
      return empty();
   }

   @Override
   protected void executeSync(Runnable runnable) {
      if (Bukkit.isPrimaryThread()) {
         runnable.run();
      } else {
         Bukkit.getScheduler().runTask(ModelEngineAPI.getAPI(), runnable);
      }
   }

   @Override
   protected void executeSync(Runnable runnable, int delay) {
      Bukkit.getScheduler().runTaskLater(ModelEngineAPI.getAPI(), runnable, delay);
   }

   @Override
   protected void executeAsync(Runnable runnable) {
      if (Bukkit.isPrimaryThread()) {
         Bukkit.getScheduler().runTaskAsynchronously(ModelEngineAPI.getAPI(), runnable);
      } else {
         runnable.run();
      }
   }

   @Override
   protected void executeAsync(Runnable runnable, int delay) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(ModelEngineAPI.getAPI(), runnable, delay);
   }

   static <T> LegacyPromise<T> empty() {
      return new LegacyPromise<>();
   }

   static <T> LegacyPromise<T> completed(T val) {
      return new LegacyPromise<>(val);
   }
}
