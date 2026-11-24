package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.gui.PluginInventory;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
   @EventHandler(priority = EventPriority.LOWEST)
   public void loadData(PlayerJoinEvent event) {
      MMOPlayerData data = MMOPlayerData.setup(event.getPlayer());
      UtilityMethods.flushOldModifiers(data.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerQuit(PlayerQuitEvent event) {
      MMOPlayerData playerData = MMOPlayerData.getOrNull(event.getPlayer());
      if (playerData != null) {
         playerData.updatePlayer(null);
      } else {
         MythicLib.plugin
            .getLogger()
            .log(Level.SEVERE, "Player data of " + event.getPlayer().getName() + " not loaded on logout. Were they kicked when joining the server?");
      }
   }
}
