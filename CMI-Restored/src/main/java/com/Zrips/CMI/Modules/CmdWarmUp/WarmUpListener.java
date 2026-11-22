package com.Zrips.CMI.Modules.CmdWarmUp;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class WarmUpListener implements Listener {
  private CMI plugin;

  public WarmUpListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerVelocityEvent(PlayerVelocityEvent playerVelocityEvent) {}

  @EventHandler(priority = EventPriority.HIGH)
  public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      Player player = playerCommandPreprocessEvent.getPlayer();
      String string = playerCommandPreprocessEvent.getMessage();
      if (this.plugin.getWarmUpManager().startCmdUsage(player, string)) {
        playerCommandPreprocessEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    if (!playerMoveEvent.isCancelled()) {
      Player player = playerMoveEvent.getPlayer();
      if (this.plugin.getWarmUpManager().isOnWarmUp(player)) {
        Location location = playerMoveEvent.getTo();
        if (playerMoveEvent.getTo() != null) {
          Location fromLocation = playerMoveEvent.getFrom();
          if (!this.plugin.getWarmUpManager().canMove(player)) {
            if ((!location.getWorld().equals(fromLocation.getWorld())
                    || location.getBlockX() != fromLocation.getBlockX()
                    || location.getBlockY() != fromLocation.getBlockY()
                    || location.getBlockZ() != fromLocation.getBlockZ())
                && this.plugin.getWarmUpManager().cancel(player.getUniqueId())) {
              CMILC.info_WarmUp_canceled.sendMessage(player);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerMove(PlayerTeleportEvent playerTeleportEvent) {
    if (!playerTeleportEvent.isCancelled()) {
      Player player = playerTeleportEvent.getPlayer();
      if (this.plugin.getWarmUpManager().isOnWarmUp(player)) {
        if (this.plugin.getWarmUpManager().cancel(player.getUniqueId())) {
          CMILC.info_WarmUp_canceled.sendMessage(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryOpenEvent(InventoryOpenEvent inventoryOpenEvent) {
    Player player = (Player) inventoryOpenEvent.getPlayer();
    if (this.plugin.getWarmUpManager().isOnWarmUp(player.getUniqueId())) {
      this.plugin.getWarmUpManager().cancel(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (this.plugin.getWarmUpManager().isOnWarmUp(player.getUniqueId())) {
      this.plugin.getWarmUpManager().cancel(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      if (this.plugin.getWarmUpManager().isOnWarmUp(player)
          && !this.plugin.getWarmUpManager().canMove(player)) {
        if (this.plugin.getWarmUpManager().cancel(player.getUniqueId())) {
          CMIScheduler.runTaskLater(
              this.plugin, () -> CMILC.info_WarmUp_canceled.sendMessage(player), 1L);
        }
      }
    }
  }
}
