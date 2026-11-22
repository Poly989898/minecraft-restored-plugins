package com.Zrips.CMI.Modules.Portals;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class PortalListener implements Listener {
  private CMI plugin;
  protected Map<UUID, Long> lastUpdate;
  protected Map<UUID, Long> lastUpdateRange;

  public PortalListener(CMI cmi) {
    this.plugin = cmi;
    this.lastUpdate = new HashMap<>();
    this.lastUpdateRange = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onVehicleMove(VehicleMoveEvent vehicleMoveEvent) {
    Entity entity = vehicleMoveEvent.getVehicle().getPassenger();
    if (entity instanceof Player) {
      Player player = (Player) entity;
      if (!CMINPCChecker.isNPC(player)) {
        Location location = vehicleMoveEvent.getFrom();
        Location destination = vehicleMoveEvent.getTo();
        if (vehicleMoveEvent.getTo() != null
            && (location.getBlockX() != destination.getBlockX()
                || location.getBlockY() != destination.getBlockY()
                || location.getBlockZ() != destination.getBlockZ())) {
          if (!this.processMove(player, location, destination)) {
            vehicleMoveEvent.getVehicle().teleport(location);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onVehicleMove(VehicleEnterEvent vehicleEnterEvent) {
    Entity entity = vehicleEnterEvent.getEntered();
    if (entity instanceof Player) {
      Player player = (Player) entity;
      if (!CMINPCChecker.isNPC(player)) {
        Location location = player.getLocation();
        Location vehicleLocation = vehicleEnterEvent.getVehicle().getLocation();
        if (location.getBlockX() != vehicleLocation.getBlockX()
            || location.getBlockY() != vehicleLocation.getBlockY()
            || location.getBlockZ() != vehicleLocation.getBlockZ()) {
          if (!this.processMove(player, location, vehicleLocation)) {
            vehicleEnterEvent.getVehicle().teleport(location);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (player != null) {
      Location location = playerMoveEvent.getFrom();
      Location destination = playerMoveEvent.getTo();
      if (playerMoveEvent.getTo() != null
          && (location.getBlockX() != destination.getBlockX()
              || location.getBlockY() != destination.getBlockY()
              || location.getBlockZ() != destination.getBlockZ())) {
        if (!CMINPCChecker.isNPC(player)) {
          Long lastUpdate = this.lastUpdateRange.get(player.getUniqueId());
          long longValue = System.currentTimeMillis();
          long particleInterval = this.plugin.getPortalManager().getPortalsCheckParticleInterval();
          if (lastUpdate == null || longValue - lastUpdate > particleInterval) {
            this.lastUpdateRange.put(player.getUniqueId(), longValue);
            this.plugin
                .getPortalManager()
                .handlePortalVisualizerUpdates(player, location, destination);
          }

          if (!this.processMove(player, location, destination)) {
            playerMoveEvent.setCancelled(true);
          }
        }
      }
    }
  }

  private boolean processMove(Player player, Location location, Location destination) {
    var set = this.plugin.getPortalManager().getByLocList(destination);
    var portals = this.plugin.getPortalManager().getByLocList(location);
    HashSet hashSet = new HashSet(set);
    hashSet.removeAll(portals);
    HashSet portalSet = new HashSet(portals);
    portalSet.removeAll(set);

    for (CMIPortal cmiPortal : ((Iterable<CMIPortal>) portalSet)) {
      if (cmiPortal.isCancelCmdWarmup() && cmiPortal.isEnabled()) {
        this.plugin.getWarmUpManager().cancel(player.getUniqueId());
        break;
      }
    }

    for (CMIPortal portal : ((Iterable<CMIPortal>) hashSet)) {
      if (portal != null && portal.isEnabled()) {
        if (portal.isRequiresPermission()
            && !PermissionsManager.CMIPerm.command_portal_$1.hasPermission(
                player, portal.getName().toLowerCase())) {
          if (portal.isInformOnMissingPerm()) {
            CMILC.info("portals", player, "cantUsePortal");
          }

          if (portal.isKickBack()) {
            byte b = 0;
            byte signX = 0;
            byte signZ = 0;
            if (destination.getBlockX() > location.getBlockX()) {
              b = 1;
            } else if (destination.getBlockX() < location.getBlockX()) {
              b = -1;
            }

            if (destination.getBlockY() > location.getBlockY()) {
              signX = 1;
            } else if (destination.getBlockY() < location.getBlockY()) {
              signX = -1;
            }

            if (destination.getBlockZ() > location.getBlockZ()) {
              signZ = 1;
            } else if (destination.getBlockZ() < location.getBlockZ()) {
              signZ = -1;
            }

            Vector vector = destination.toVector().subtract(location.toVector());
            double number = vector.getX() * 0.9;
            double scaledY = vector.getY() * 0.9;
            double scaledZ = vector.getZ() * 0.9;
            if ((b < 0 || b > 0)
                && this.plugin.getPortalManager().getByLoc(destination.clone().add(-b, 0.0, 0.0))
                    == portal) {
              b = 0;
            }

            if ((signX < 0 || signX > 0)
                && this.plugin
                        .getPortalManager()
                        .getByLoc(destination.clone().add(0.0, -signX, 0.0))
                    == portal) {
              signX = 0;
            }

            if ((signZ < 0 || signZ > 0)
                && this.plugin
                        .getPortalManager()
                        .getByLoc(destination.clone().add(0.0, 0.0, -signZ))
                    == portal) {
              signZ = 0;
            }

            if (signZ < 0 || signZ > 0) {
              scaledZ = -scaledZ;
            }

            if (b < 0 || b > 0) {
              number = -number;
            }

            if (signX < 0 || signX > 0) {
              scaledY = -scaledY;
            }

            player.setVelocity(new Vector(number, scaledY, scaledZ));
            player.setFallDistance(0.0F);
            return false;
          }

          return true;
        }

        if (portal.teleport(player)) {
          break;
        }
      }
    }

    return true;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRange(PlayerMoveEvent playerMoveEvent) {}

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (player != null) {
      Location location = playerTeleportEvent.getFrom();
      Location destination = playerTeleportEvent.getTo();
      if (playerTeleportEvent.getTo() != null
          && (location.getBlockX() != destination.getBlockX()
              || location.getBlockY() != destination.getBlockY()
              || location.getBlockZ() != destination.getBlockZ())) {
        if (!CMINPCChecker.isNPC(player)) {
          Long lastUpdate = this.lastUpdateRange.get(player.getUniqueId());
          long longValue = System.currentTimeMillis();
          if (lastUpdate == null || longValue - lastUpdate >= 20L) {
            this.lastUpdateRange.put(player.getUniqueId(), longValue);
            this.plugin
                .getPortalManager()
                .handlePortalVisualizerUpdates(player, location, destination);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null) {
      this.lastUpdate.remove(player.getUniqueId());
      this.lastUpdateRange.remove(player.getUniqueId());
      this.plugin.getPortalManager().removeLastPortalInRange(player.getUniqueId());
      this.plugin.getPortalManager().removeNearPortal(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player != null) {
      this.plugin
          .getPortalManager()
          .handlePortalVisualizerUpdates(player, null, player.getLocation());
      CMIPortal cmiPortal = this.plugin.getPortalManager().getByLoc(player.getLocation());
      if (cmiPortal != null && cmiPortal.getSafeLoc() != null) {
        CMI.getInstance()
            .getTeleportHandler()
            .teleportPlayer(player, cmiPortal.getSafeLoc(), CMITeleportType.Portal);
      }
    }
  }
}
