package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Modules.Holograms.Events.CMIHologramClickEvent;
import com.Zrips.CMI.Modules.Holograms.Events.CMIHologramStartHoverEvent;
import com.Zrips.CMI.Modules.Holograms.Events.CMIHologramStopHoverEvent;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.events.CMIPlayerFakeEntityInteractEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HologramListener implements Listener {
  private CMI plugin;
  protected Map<UUID, Long> lastUpdateRange;

  public HologramListener(CMI cmi) {
    this.plugin = cmi;
    this.lastUpdateRange = new ConcurrentHashMap<>();
  }

  public static double zPosYaw(double number, double distance, double yawDegrees) {
    return Math.sin(number) * distance * Math.sin((Math.PI / 180.0) * yawDegrees);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (player != null) {
      Location location = playerMoveEvent.getFrom();
      Location toLocation = playerMoveEvent.getTo();
      if (playerMoveEvent.getTo() != null
          && (location.getBlockX() != toLocation.getBlockX()
              || location.getBlockY() != toLocation.getBlockY()
              || location.getBlockZ() != toLocation.getBlockZ())) {
        if (!CMINPCChecker.isNPC(player)) {
          long longValue = System.currentTimeMillis();
          if (longValue - this.lastUpdateRange.getOrDefault(player.getUniqueId(), 0L) >= 100L) {
            this.lastUpdateRange.put(player.getUniqueId(), longValue);
            CompletableFuture.runAsync(
                () -> this.plugin.getHologramManager().updatePlayer(player, toLocation));
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (player != null) {
      CMIAiming.removeAimAt(player.getUniqueId());
      Location location = playerTeleportEvent.getFrom();
      Location toLocation = playerTeleportEvent.getTo();
      if (playerTeleportEvent.getTo() != null) {
        if ((location.getBlockX() != toLocation.getBlockX()
                || location.getBlockY() != toLocation.getBlockY()
                || location.getBlockZ() != toLocation.getBlockZ()
                || !location.getWorld().equals(toLocation.getWorld()))
            && !CMINPCChecker.isNPC(player)) {
          this.lastUpdateRange.put(player.getUniqueId(), System.currentTimeMillis());
          CompletableFuture.runAsync(
              () -> this.plugin.getHologramManager().updatePlayer(player, toLocation));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null) {
      this.lastUpdateRange.remove(player.getUniqueId());
      this.plugin.getHologramManager().onPlayerQuit(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player != null) {
      this.plugin.getHologramManager().hideAllHologramsForPlayer(player.getUniqueId());
      CMIScheduler.runLaterAsync(
          this.plugin,
          () ->
              CompletableFuture.runAsync(
                  () ->
                      this.plugin.getHologramManager().updatePlayer(player, player.getLocation())),
          10L);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInteract(PlayerInteractEvent playerInteractEvent) {
    Player player = playerInteractEvent.getPlayer();
    if (player != null) {
      try {
        if (Version.isCurrentHigher(Version.v1_8_R3)
            && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception exception) {
      }

      CMIAiming cmiAiming = CMIAiming.getAimAt(player.getUniqueId());
      if (cmiAiming != null) {
        CMIHologram cmiHologram = cmiAiming.getAimAt();
        if (cmiHologram != null) {
          int value = CMIHologramHoverData.getData(player).getHoveringLine();
          if (value >= 1) {
            if (cmiHologram.getSettings().isCheckLineOfSight()) {
              Block block =
                  CMI.getInstance()
                      .getNMS()
                      .getBlockLookingAt(
                          cmiHologram.getLocation().getWorld(),
                          player.getEyeLocation().toVector(),
                          cmiHologram.getCenterLocation(player).toVector(),
                          false);
              if (block != null) {
                return;
              }
            }

            playerInteractEvent.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteract(CMIHologramClickEvent cmiHologramClickEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPlayerFakeEntityInteractEvent(
      CMIPlayerFakeEntityInteractEvent cmiPlayerFakeEntityInteractEvent) {
    if (cmiPlayerFakeEntityInteractEvent.getObject() instanceof CMIHologram) {
      if (util.hologramTestUuid != null
          && util.hologramTestUuid.equals(
              cmiPlayerFakeEntityInteractEvent.getPlayer().getUniqueId())) {
        CMIMessages.sendMessage(
            cmiPlayerFakeEntityInteractEvent.getPlayer(),
            "Entity instanceof CMIHologram " + System.currentTimeMillis());
      }

      CMIHologram cmiHologram = (CMIHologram) cmiPlayerFakeEntityInteractEvent.getObject();
      CMIAiming cmiAiming =
          CMIAiming.getAimAt(cmiPlayerFakeEntityInteractEvent.getPlayer().getUniqueId());
      if (cmiAiming != null) {
        CMIHologramHoverData cmiHologramHoverData =
            CMIHologramHoverData.getData(cmiPlayerFakeEntityInteractEvent.getPlayer());
        int value = cmiHologramHoverData.getHoveringLine();
        if (util.hologramTestUuid != null
            && util.hologramTestUuid.equals(
                cmiPlayerFakeEntityInteractEvent.getPlayer().getUniqueId())) {
          CMIMessages.sendMessage(
              cmiPlayerFakeEntityInteractEvent.getPlayer(),
              "Bug here? Line " + value + System.currentTimeMillis());
        }

        if (value >= 1) {
          CMIInteractType cmiInteractType = cmiPlayerFakeEntityInteractEvent.getType();
          if (cmiPlayerFakeEntityInteractEvent.getPlayer().isSneaking()) {
            if (cmiInteractType == CMIInteractType.LeftClick) {
              cmiInteractType = CMIInteractType.LeftShiftClick;
            } else if (cmiInteractType == CMIInteractType.RightClick) {
              cmiInteractType = CMIInteractType.RightShiftClick;
            }
          }

          CMIHologramClickEvent cmiHologramClickEvent =
              new CMIHologramClickEvent(
                  cmiPlayerFakeEntityInteractEvent.getPlayer(),
                  cmiHologram,
                  cmiHologramHoverData.getHoveringLineObject(),
                  cmiHologramHoverData.getHoveringLinePortion(),
                  value,
                  cmiAiming.getGlobalPos(),
                  cmiInteractType);
          Bukkit.getPluginManager().callEvent(cmiHologramClickEvent);
          if (!cmiHologramClickEvent.isCancelled()) {
            cmiHologram.processClick(
                cmiPlayerFakeEntityInteractEvent.getPlayer(),
                cmiInteractType,
                cmiAiming.getGlobalPos(),
                cmiHologramHoverData.getHoveringLinePortion());
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerRespawnEvent(PlayerRespawnEvent playerRespawnEvent) {
    Player player = playerRespawnEvent.getPlayer();
    if (player != null) {
      this.lastUpdateRange.remove(player.getUniqueId());
      this.plugin.getHologramManager().hideAllHologramsForPlayer(player.getUniqueId());
      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            if (player.isOnline()) {
              this.plugin.getHologramManager().updatePlayer(player, player.getLocation());
            }
          },
          20L);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIHologramStartHoverEvent(CMIHologramStartHoverEvent cmiHologramStartHoverEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIHologramStopHoverEvent(CMIHologramStopHoverEvent cmiHologramStopHoverEvent) {}
}
