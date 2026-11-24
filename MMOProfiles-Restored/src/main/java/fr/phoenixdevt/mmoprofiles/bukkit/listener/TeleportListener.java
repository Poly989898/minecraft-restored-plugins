package fr.phoenixdevt.mmoprofiles.bukkit.listener;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void registerLastTeleportLocation(PlayerTeleportEvent event) {
      if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
         if (MMOProfiles.plugin.configManager.permanentWorlds.contains(event.getFrom().getWorld().getName())) {
            ProfileListImpl list = MMOProfiles.plugin.getPlayerData(event.getPlayer().getUniqueId());
            if (list.savesLastLocation()) {
               PlayerProfileImpl current = list.getCurrent();
               if (current != null) {
                  current.setLastTeleportLocation(event.getFrom());
               }
            }
         }
      }
   }
}
