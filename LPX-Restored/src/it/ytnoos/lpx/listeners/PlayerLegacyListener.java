package it.ytnoos.lpx.listeners;

import it.ytnoos.lpx.LPXBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLegacyListener implements Listener {
   private final LPXBukkit lpxBukkit;

   public PlayerLegacyListener(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
      if (playerLoginEvent.getAddress() == null) {
         playerLoginEvent.setResult(Result.KICK_OTHER);
         playerLoginEvent.setKickMessage(this.lpxBukkit.getConfigManager().getMessagesConfig().getNullAddress());
      }
   }

}
