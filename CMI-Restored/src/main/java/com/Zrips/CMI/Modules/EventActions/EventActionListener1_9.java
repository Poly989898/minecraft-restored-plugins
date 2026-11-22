package com.Zrips.CMI.Modules.EventActions;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventActionListener1_9 implements Listener {
  private CMI plugin;

  public EventActionListener1_9(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void EntityToggleGlideEvent(EntityToggleGlideEvent entityToggleGlideEvent) {
    if (entityToggleGlideEvent.getEntity() instanceof Player) {
      Player player = (Player) entityToggleGlideEvent.getEntity();
      if (entityToggleGlideEvent.isGliding()) {
        this.plugin
            .getEventActionManager()
            .performCommands(EventActionManager.eventAction.elytraStartGlide, player);
      } else {
        this.plugin
            .getEventActionManager()
            .performCommands(EventActionManager.eventAction.elytraEndGlide, player);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerSwapHandItemsEventCancel(PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
    if (!playerSwapHandItemsEvent.isCancelled()) {
      if (playerSwapHandItemsEvent.getPlayer().isSneaking()) {
        if (EventActionManager.eventAction.sneakingSwapHandItems.isEnabled()
            && EventActionManager.eventAction.sneakingSwapHandItems.isDisableEvent()) {
          playerSwapHandItemsEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
    if (EventActionManager.eventAction.sneakingSwapHandItems.isEnabled()
        || EventActionManager.eventAction.swapHandItems.isEnabled()) {
      if (playerSwapHandItemsEvent.getPlayer().isSneaking()) {
        if (playerSwapHandItemsEvent.isCancelled()
            && (!EventActionManager.eventAction.sneakingSwapHandItems.isEnabled()
                || !EventActionManager.eventAction.sneakingSwapHandItems.isDisableEvent())) {
          return;
        }

        this.plugin
            .getEventActionManager()
            .performCommands(
                EventActionManager.eventAction.sneakingSwapHandItems,
                playerSwapHandItemsEvent.getPlayer(),
                null,
                playerSwapHandItemsEvent.getMainHandItem(),
                playerSwapHandItemsEvent.getOffHandItem());
        if (EventActionManager.eventAction.sneakingSwapHandItems.isEnabled()
            && EventActionManager.eventAction.sneakingSwapHandItems.isDisableEvent()) {
          playerSwapHandItemsEvent.setCancelled(true);
        }
      } else {
        if (playerSwapHandItemsEvent.isCancelled()) {
          return;
        }

        this.plugin
            .getEventActionManager()
            .performCommands(
                EventActionManager.eventAction.swapHandItems,
                playerSwapHandItemsEvent.getPlayer(),
                null,
                playerSwapHandItemsEvent.getMainHandItem(),
                playerSwapHandItemsEvent.getOffHandItem());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}
}
