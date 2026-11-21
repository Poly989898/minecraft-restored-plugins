package com.ticxo.modelengine.api.utils.scheduling;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaPlatformScheduler implements PlatformScheduler {
   @Override
   public PlatformTask scheduleRepeating(Plugin plugin, Runnable task, long delay, long period) {
      return new FoliaPlatformTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, ignore -> task.run(), Math.max(1L, delay), Math.max(1L, period)));
   }

   @Override
   public PlatformTask scheduleRepeating(Plugin plugin, Entity entity, Runnable task, long delay, long period) {
      return new FoliaPlatformTask(entity.getScheduler().runAtFixedRate(plugin, ignore -> task.run(), null, Math.max(1L, delay), Math.max(1L, period)));
   }

   @Override
   public PlatformTask scheduleRepeating(Plugin plugin, Location location, Runnable task, long delay, long period) {
      return new FoliaPlatformTask(
         Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, ignore -> task.run(), Math.max(1L, delay), Math.max(1L, period))
      );
   }

   @Override
   public PlatformTask scheduleRepeatingAsync(Plugin plugin, Runnable task, long delay, long period) {
      return new FoliaPlatformTask(
         Bukkit.getAsyncScheduler().runAtFixedRate(plugin, ignore -> task.run(), Math.max(1L, delay) * 50L, Math.max(1L, period) * 50L, TimeUnit.MILLISECONDS)
      );
   }
}
