package com.ticxo.modelengine.api.utils.scheduling;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public interface PlatformScheduler {
   PlatformTask scheduleRepeating(Plugin plugin, Runnable task, long delay, long period);

   PlatformTask scheduleRepeating(Plugin plugin, Entity entity, Runnable task, long delay, long period);

   PlatformTask scheduleRepeating(Plugin plugin, Location location, Runnable task, long delay, long period);

   PlatformTask scheduleRepeatingAsync(Plugin plugin, Runnable task, long delay, long period);
}
