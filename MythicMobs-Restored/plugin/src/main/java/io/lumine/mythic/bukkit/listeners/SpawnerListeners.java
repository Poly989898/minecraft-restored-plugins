package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerListeners implements Listener {
   private final SpawnerManager MANAGER;

   public SpawnerListeners(SpawnerManager manager) {
      this.MANAGER = manager;
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent e) {
      AbstractLocation location = BukkitAdapter.adapt(e.getBlock().getLocation());
      if (this.MANAGER.hasBreakableSpawner(location)) {
         MythicSpawner ms = this.MANAGER.getSpawnerAtLocation(location);
         this.MANAGER.removeSpawner(ms);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onMobSpawn(SpawnerSpawnEvent event) {
      if (event.getSpawner() != null && this.MANAGER.getSpawnerAtLocation(BukkitAdapter.adapt(event.getSpawner().getLocation())) != null) {
         event.setCancelled(true);
         event.getSpawner().setDelay(Integer.MAX_VALUE);
      }
   }
}
