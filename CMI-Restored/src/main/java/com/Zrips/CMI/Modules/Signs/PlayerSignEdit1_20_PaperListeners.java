package com.Zrips.CMI.Modules.Signs;

import com.Zrips.CMI.CMI;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import io.papermc.paper.event.player.PlayerOpenSignEvent.Cause;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerSignEdit1_20_PaperListeners implements Listener {
  public PlayerSignEdit1_20_PaperListeners(CMI cmi) {}

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerSignOpenEvent(PlayerOpenSignEvent playerOpenSignEvent) {
    if (playerOpenSignEvent.getCause().equals(Cause.INTERACT)) {
      if (PlayerSignEdit1_20Listeners.shouldCancel(
          playerOpenSignEvent.getPlayer(),
          playerOpenSignEvent.getSign(),
          playerOpenSignEvent.getSide())) {
        playerOpenSignEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerSignOpenEventEditor(PlayerOpenSignEvent playerOpenSignEvent) {
    if (playerOpenSignEvent.getCause().equals(Cause.INTERACT)) {
      Player player = playerOpenSignEvent.getPlayer();
      if (CMI.getInstance().getSignManager().isSignDialogEditor()) {
        PlayerSignEdit1_20Listeners.openSignDialog(
            player, playerOpenSignEvent.getSign(), playerOpenSignEvent.getSide());
        playerOpenSignEvent.setCancelled(true);
      }
    }
  }
}
