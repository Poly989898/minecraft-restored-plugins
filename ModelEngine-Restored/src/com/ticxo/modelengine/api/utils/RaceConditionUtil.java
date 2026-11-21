package com.ticxo.modelengine.api.utils;

import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.ConcurrentModificationException;

public class RaceConditionUtil {
   public static void wrapConmod(Runnable runnable) {
      wrapConmod(runnable, 1);
   }

   public static void wrapConmod(Runnable runnable, int delay) {
      wrapConmod(runnable, delay, 1);
   }

   public static void wrapConmod(Runnable runnable, int delay, int attempts) {
      try {
         runnable.run();
      } catch (ConcurrentModificationException ignored) {
         if (attempts > 0) {
            DualTicker.queueDelayedSyncTask(() -> wrapConmod(runnable, delay, attempts - 1), delay);
         }
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   public static void wrapAll(Runnable runnable) {
      wrapAll(runnable, 1);
   }

   public static void wrapAll(Runnable runnable, int delay) {
      wrapAll(runnable, delay, 1);
   }

   public static void wrapAll(Runnable runnable, int delay, int attempts) {
      try {
         runnable.run();
      } catch (Throwable e) {
         if (attempts > 0) {
            DualTicker.queueDelayedSyncTask(() -> wrapConmod(runnable, delay, attempts - 1), delay);
         } else {
            e.printStackTrace();
         }
      }
   }
}
