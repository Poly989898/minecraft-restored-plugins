package net.Indyuce.mmoitems.listener;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BiomeChangeListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerMove(PlayerMoveEvent event) {
      if (!event.isCancelled()
         && PlayerData.has(event.getPlayer())
         && (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
         Biome biome = event.getFrom().getBlock().getBiome();
         Biome toBiome = event.getTo().getBlock().getBiome();
         if (toBiome != biome) {
            PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
         }
      }
   }
}
