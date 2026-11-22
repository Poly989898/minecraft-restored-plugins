package com.Zrips.CMI.Modules.InvEndEditor;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EnderChestListener implements Listener {
  private CMI plugin;

  public EnderChestListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onEnderChestClick(InventoryClickEvent inventoryClickEvent) {
    Player player = (Player) inventoryClickEvent.getWhoClicked();
    if (this.plugin.getEnderChestManager().isWatching(player)) {
      if (!PermissionsManager.CMIPerm.enderedit.hasPermission(player, true)) {
        inventoryClickEvent.setCancelled(true);
      } else {
        EndWatcher endWatcher = this.plugin.getEnderChestManager().getWatcher(player);
        if (PermissionsManager.CMIPerm.command_ender_preventmodify.hasPermission(
                endWatcher.getTarget())
            && !PermissionsManager.CMIPerm.command_ender_preventmodify_bypass.hasPermission(
                player)) {
          inventoryClickEvent.setCancelled(true);
        } else {
          this.plugin.getEnderChestManager().updateTargetEnderChest(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerLoginEnder(PlayerLoginEvent playerLoginEvent) {
    Player player = playerLoginEvent.getPlayer();
    if (this.plugin.getEnderChestManager().isBeingWatched(player)) {
      EndWatcher endWatcher = this.plugin.getEnderChestManager().getSlave(player);
      if (endWatcher != null) {
        if (endWatcher.getSaveTask() != null) {
          endWatcher.getSaveTask().cancel();
          endWatcher.setSaveTask(null);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLoginEnder(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (this.plugin.getEnderChestManager().isBeingWatched(player)) {
      EndWatcher endWatcher = this.plugin.getEnderChestManager().getSlave(player);
      if (endWatcher != null) {
        CMIScheduler.runTask(
            this.plugin,
            () ->
                this.plugin.getEnderChestManager().openEnderChest(endWatcher.getMaster(), player));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEnder(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (this.plugin.getEnderChestManager().isBeingWatched(player)) {
      EndWatcher endWatcher = this.plugin.getEnderChestManager().getSlave(player);
      if (endWatcher != null) {
        CMIScheduler.runTask(
            this.plugin,
            () ->
                this.plugin
                    .getEnderChestManager()
                    .openEnderChest(
                        endWatcher.getMaster(),
                        this.plugin.getPlayerManager().getUser(player).getPlayer()));
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onEnderChestDrag(InventoryDragEvent inventoryDragEvent) {
    Player player = (Player) inventoryDragEvent.getWhoClicked();
    if (this.plugin.getEnderChestManager().isWatching(player)) {
      if (!PermissionsManager.CMIPerm.enderedit.hasPermission(player, true)) {
        inventoryDragEvent.setCancelled(true);
      } else {
        EndWatcher endWatcher = this.plugin.getEnderChestManager().getWatcher(player);
        if (PermissionsManager.CMIPerm.command_ender_preventmodify.hasPermission(
                endWatcher.getTarget())
            && !PermissionsManager.CMIPerm.command_ender_preventmodify_bypass.hasPermission(
                player)) {
          inventoryDragEvent.setCancelled(true);
        } else {
          this.plugin.getEnderChestManager().updateTargetEnderChest(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onNormalInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
    this.plugin.getEnderChestManager().removeWatcher((Player) inventoryCloseEvent.getPlayer());
  }
}
