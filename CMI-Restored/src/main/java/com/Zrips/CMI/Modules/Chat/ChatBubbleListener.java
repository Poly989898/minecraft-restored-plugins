package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class ChatBubbleListener implements Listener {
  private static CMI plugin;

  public ChatBubbleListener(CMI cmi) {
    plugin = cmi;
    plugin
        .getServer()
        .getPluginManager()
        .registerEvent(
            PlayerTeleportEvent.class,
            this,
            EventPriority.MONITOR,
            (listener, event) -> onPlayerTeleport((PlayerTeleportEvent) event),
            plugin,
            true);
  }

  private static void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (player != null) {
      CMIChatBubble cmiChatBubble = plugin.getChatBubbleManager().getBubble(player.getUniqueId());
      if (cmiChatBubble != null) {
        if (playerTeleportEvent.getTo() == null
            || !playerTeleportEvent
                .getFrom()
                .getWorld()
                .equals(playerTeleportEvent.getTo().getWorld())) {
          cmiChatBubble.delete();
        } else if (cmiChatBubble.getDisplay() != null) {
          plugin
              .getTeleportHandler()
              .teleportEntity(
                  cmiChatBubble.getDisplay().getDisplayEntity(),
                  playerTeleportEvent.getTo(),
                  TeleportCause.PLUGIN)
              .thenApply(
                  enabled -> {
                    if (!enabled) {
                      if (!cmiChatBubble.getDisplay().getDisplayEntity().isDead()) {
                        CMIScheduler.runAtLocation(
                            plugin,
                            cmiChatBubble.getDisplay().getDisplayEntity().getLocation(),
                            cmiChatBubble::delete);
                      }

                      return null;
                    } else {
                      if (!player.isOnline()) {
                        return null;
                      }

                      player.addPassenger(cmiChatBubble.getDisplay().getDisplayEntity());
                      return null;
                    }
                  });
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null) {
      plugin.getChatBubbleManager().clearCache(player.getUniqueId());
    }
  }
}
