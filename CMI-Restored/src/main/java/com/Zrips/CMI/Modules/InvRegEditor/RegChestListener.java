package com.Zrips.CMI.Modules.InvRegEditor;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegChestListener implements Listener {
  CMI plugin;

  public RegChestListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLoginEnder(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (this.plugin.getRegChestManager().isSlave(player)) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getRegChestManager()
                  .openInventory(
                      this.plugin.getRegChestManager().getInformation(player).getWatcher(),
                      player));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEnder(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (this.plugin.getRegChestManager().isSlave(player)) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getRegChestManager()
                  .openInventory(
                      this.plugin.getRegChestManager().getInformation(player).getWatcher(),
                      player));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIPlayerVanishEvent(CMIPlayerVanishEvent cmiPlayerVanishEvent) {
    Player player = cmiPlayerVanishEvent.getPlayer();
    if (this.plugin.getRegChestManager().isSlave(player)) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getRegChestManager()
                  .openInventory(
                      this.plugin.getRegChestManager().getInformation(player).getWatcher(),
                      player));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIPlayerUnVanishEvent(CMIPlayerUnVanishEvent cmiPlayerUnVanishEvent) {
    Player player = cmiPlayerUnVanishEvent.getPlayer();
    if (this.plugin.getRegChestManager().isSlave(player)) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getRegChestManager()
                  .openInventory(
                      this.plugin.getRegChestManager().getInformation(player).getWatcher(),
                      player));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    if (!inventoryClickEvent.isCancelled()
        && inventoryClickEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      this.plugin.getRegChestManager().tempLockWatcher(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryDragEventLog(InventoryDragEvent inventoryDragEvent) {
    if (!inventoryDragEvent.isCancelled() && inventoryDragEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryDragEvent.getWhoClicked();
      this.plugin.getRegChestManager().tempLockWatcher(player);
    }
  }
}
