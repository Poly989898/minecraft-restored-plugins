package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class AnimationRideListener implements Listener {
  private CMI plugin;

  public AnimationRideListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && DamageCause.SUFFOCATION.equals(entityDamageEvent.getCause())
        && entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      if (this.plugin.getAnimationManager().isRiding(player.getUniqueId())) {
        player.leaveVehicle();
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void PlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (this.plugin.getAnimationManager().isBeingRiden(player.getUniqueId())) {
      this.plugin.getTeleportHandler().shakePassengers(player);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void EntityDeathEvent(EntityDeathEvent entityDeathEvent) {
    if (entityDeathEvent.getEntityType() == EntityType.SLIME
        && entityDeathEvent.getEntity().getCustomName() != null
        && entityDeathEvent.getEntity().getCustomName().equals("CMIFakeSlime")) {
      entityDeathEvent.setDroppedExp(0);
      entityDeathEvent.getDrops().clear();
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void EntityTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
    if (!playerTeleportEvent.isCancelled()) {
      Player player = playerTeleportEvent.getPlayer();
      if (this.plugin.getAnimationManager().isBeingRiden(player.getUniqueId())) {
        this.plugin.getAnimationManager().removePassengers(player, null);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerMoveEvent(VehicleMoveEvent vehicleMoveEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
    if (!this.plugin.getAnimationManager().getRidingList().isEmpty()) {
      Player player = playerMoveEvent.getPlayer();
      if (this.plugin.getAnimationManager().isRiding(player.getUniqueId())
          && this.plugin.getAnimationManager().isRidingNonSteerable(player.getUniqueId())
          && playerMoveEvent.isCancelled()) {
        this.plugin.getAnimationManager().removePassengers(player, null);
      }
    }
  }
}
