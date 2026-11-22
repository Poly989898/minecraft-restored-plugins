package com.Zrips.CMI.Modules.DynamicSigns;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SignListener implements Listener {
  private CMI plugin;
  protected Map<UUID, Long> lastUpdateRange;

  public SignListener(CMI cmi) {
    this.plugin = cmi;
    this.lastUpdateRange = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (player != null) {
      Location location = playerMoveEvent.getFrom();
      Location toLocation = playerMoveEvent.getTo();
      if (playerMoveEvent.getTo() != null) {
        if (location.getBlockX() != toLocation.getBlockX()
            || location.getBlockY() != toLocation.getBlockY()
            || location.getBlockZ() != toLocation.getBlockZ()) {
          if (!CMINPCChecker.isNPC(player)) {
            Long lastUpdate = this.lastUpdateRange.get(player.getUniqueId());
            long longValue = System.currentTimeMillis();
            if (lastUpdate == null
                || longValue - lastUpdate >= this.plugin.getSignManager().getSignCheckInterval()) {
              this.lastUpdateRange.put(player.getUniqueId(), longValue);
              this.plugin.getSignManager().handleSignUpdates(player, toLocation);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (player != null) {
      Location location = playerTeleportEvent.getFrom();
      Location toLocation = playerTeleportEvent.getTo();
      if (toLocation != null) {
        if (location.getBlockX() != toLocation.getBlockX()
            || location.getBlockY() != toLocation.getBlockY()
            || location.getBlockZ() != toLocation.getBlockZ()) {
          if (!CMINPCChecker.isNPC(player)) {
            Long lastUpdate = this.lastUpdateRange.get(player.getUniqueId());
            long longValue = System.currentTimeMillis();
            if (lastUpdate == null || longValue - lastUpdate >= 20L) {
              this.lastUpdateRange.put(player.getUniqueId(), longValue);
              this.plugin.getSignManager().handleSignUpdates(player, toLocation);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null) {
      this.lastUpdateRange.remove(player.getUniqueId());
      this.plugin.getSignManager().removeNearSign(player.getUniqueId());
      this.plugin.getSignManager().removeLastSignInRange(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(BlockBreakEvent blockBreakEvent) {
    Player player = blockBreakEvent.getPlayer();
    if (player != null) {
      if (!blockBreakEvent.isCancelled()) {
        if (blockBreakEvent.getBlock().getState() instanceof Sign) {
          CMISign cmiSign =
              this.plugin.getSignManager().getByLoc(blockBreakEvent.getBlock().getLocation());
          if (cmiSign != null) {
            this.plugin.getSignManager().removeSign(cmiSign);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player != null) {
      this.plugin.getSignManager().handleSignUpdates(player, player.getLocation());
    }
  }
}
