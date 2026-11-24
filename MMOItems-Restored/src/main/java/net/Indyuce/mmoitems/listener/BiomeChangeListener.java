package net.Indyuce.mmoitems.listener;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BiomeChangeListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerMove(PlayerMoveEvent var1) {
      if (!var1.isCancelled()
         && PlayerData.has(var1.getPlayer())
         && (var1.getFrom().getBlockX() != var1.getTo().getBlockX() || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ())) {
         Biome var2 = var1.getFrom().getBlock().getBiome();
         Biome var3 = var1.getTo().getBlock().getBiome();
         if (var3 != var2) {
            PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
         }
      }
   }
}
