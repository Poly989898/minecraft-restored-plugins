package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Particl.CMIPEAnimationInterface;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportCondition;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportFeedback;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import java.awt.Color;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Colors.CMIColors;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {
  private CMI plugin;
  HashMap<UUID, Long> elevatorUsage = new HashMap<>();

  public ElevatorListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onSignChangeEventElevator(SignChangeEvent signChangeEvent) {
    if (!signChangeEvent.isCancelled()) {
      Player player = signChangeEvent.getPlayer();
      if (player != null) {
        CMIScheduler.runAtLocation(
            this.plugin,
            signChangeEvent.getBlock().getLocation(),
            () -> {
              if (signChangeEvent.getBlock().getState() instanceof Sign) {
                Sign sign = (Sign) signChangeEvent.getBlock().getState();
                String string = CMIChatColor.stripColor(sign.getLine(0));
                if (this.plugin
                    .getConfigManager()
                    .getElevatorIndicators()
                    .contains(string.toLowerCase())) {
                  if (PermissionsManager.CMIPerm.elevator_create.hasPermission(
                      signChangeEvent.getPlayer(), true)) {
                    CMILC.info_Elevator_created.sendMessage(signChangeEvent.getPlayer());
                  } else {
                    sign.setLine(0, CMIChatColor.translate("*&m" + string + "&r*"));
                    sign.update();
                  }
                }
              }
            });
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()) {
      Block block = playerInteractEvent.getClickedBlock();
      if (block != null
          && block.getState() instanceof Sign
          && playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        Sign sign = (Sign) block.getState();
        String string = CMIChatColor.stripColor(sign.getLine(0));
        if (this.plugin.getConfigManager().getElevatorIndicators().contains(string.toLowerCase())) {
          if (PermissionsManager.CMIPerm.elevator_use.hasPermission(
              playerInteractEvent.getPlayer(), true)) {
            Boolean up = !playerInteractEvent.getPlayer().isSneaking();
            var completableFuture =
                this.findTeleportLocation(playerInteractEvent.getPlayer(), sign.getLocation(), up);
            completableFuture.thenAccept(
                object -> {
                  if (object != null && object.getCondition().equals(CMITeleportCondition.Good)) {
                    Long lastUse =
                        this.elevatorUsage.get(playerInteractEvent.getPlayer().getUniqueId());
                    if (lastUse != null && lastUse > System.currentTimeMillis()) {
                      playerInteractEvent.setCancelled(true);
                    } else {
                      this.elevatorUsage.put(
                          playerInteractEvent.getPlayer().getUniqueId(),
                          System.currentTimeMillis() + 500L);
                      Location location = object.getLocation().clone();
                      Location destination = playerInteractEvent.getPlayer().getLocation().clone();
                      location.setPitch(destination.getPitch());
                      location.setYaw(destination.getYaw());
                      UserEvents.addBackLocationExclusion(
                          playerInteractEvent.getPlayer().getUniqueId());
                      this.plugin
                          .getTeleportHandler()
                          .teleportPlayer(
                              playerInteractEvent.getPlayer(), location, CMITeleportType.Elevator)
                          .thenAccept(
                              cmiTeleportFeedback -> {
                                if (cmiTeleportFeedback.isSuccess()) {
                                  this.playEffectsAndSound(
                                      up,
                                      destination,
                                      playerInteractEvent.getPlayer().getLocation());
                                }
                              });
                    }
                  } else {
                    LC.info_nolocation.sendMessage(playerInteractEvent.getPlayer(), new Object[0]);
                  }
                });
            playerInteractEvent.setCancelled(true);
          }
        }
      }
    }
  }

  private void playEffectsAndSound(boolean flag, Location location, Location target) {
    CMIScheduler.runAtLocation(
        this.plugin,
        target,
        () -> {
          CMIPEAnimationInterface cmipeAnimationInterface = null;
          if (flag) {
            if (ParticleManager.CMIPresetAnimations.TpUp.isEnabled()) {
              cmipeAnimationInterface = ParticleManager.CMIPresetAnimations.TpUp.getInter();
            }

            CMISoundPreset.TeleportUp.play(target);
            CMISoundPreset.TeleportUp.play(location);
          } else {
            if (ParticleManager.CMIPresetAnimations.TpDown.isEnabled()) {
              cmipeAnimationInterface = ParticleManager.CMIPresetAnimations.TpDown.getInter();
            }

            CMISoundPreset.TeleportDown.play(target);
            CMISoundPreset.TeleportDown.play(location);
          }

          if (cmipeAnimationInterface != null) {
            cmipeAnimationInterface.setColor(
                getColor(location.clone().add(0.0, -1.0, 0.0).getBlock()));
            cmipeAnimationInterface.setCenter(location);
            cmipeAnimationInterface.show();
            cmipeAnimationInterface = ParticleManager.CMIPresetAnimations.TpDown.getInter();
            cmipeAnimationInterface.setColor(
                getColor(target.clone().add(0.0, -2.0, 0.0).getBlock()));
            cmipeAnimationInterface.setCenter(target.clone().add(0.0, -1.0, 0.0));
            cmipeAnimationInterface.show();
          }
        });
  }

  private static Color getColor(Block block) {
    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    CMIColors cmiColors = CMIColors.getColor(cmiMaterial);
    Color color = null;
    if (cmiColors != null) {
      color = cmiColors.getColor();
    }

    if (color == null) {
      color = Color.CYAN;
    }

    return color;
  }

  private CompletableFuture<CMITeleportFeedback> findTeleportLocation(
      Player player, Location location, boolean flag) {
    if (flag) {
      Location signLocation = location.clone().add(0.0, 2.0, 0.0);

      for (int value = signLocation.getBlockY();
          value < signLocation.getWorld().getHighestBlockAt(signLocation).getY();
          value++) {
        signLocation.add(0.0, 1.0, 0.0);
        Block block = signLocation.getBlock();
        if (block.getState() instanceof Sign) {
          Sign sign = (Sign) block.getState();
          String string = CMIChatColor.stripColor(sign.getLine(0));
          if (this.plugin
              .getConfigManager()
              .getElevatorIndicators()
              .contains(string.toLowerCase())) {
            Location signCopy = signLocation.clone();
            boolean valid =
                CMIChatColor.stripColor(sign.getLine(1))
                    .equalsIgnoreCase(this.plugin.getConfigManager().getElevatorStaticIndicator());
            if (valid) {
              CMIBlock cmiBlock = new CMIBlock(block);
              Location signLoc = sign.getLocation().clone();
              if (cmiBlock.getFacing() != null) {
                signLoc.add(cmiBlock.getFacing().getModX(), 0.0, cmiBlock.getFacing().getModZ());
              }

              signCopy.setX(signLoc.getBlockX() + 0.5);
              signCopy.setZ(signLoc.getBlockZ() + 0.5);
            } else {
              signCopy.setX(player.getLocation().getX());
              signCopy.setZ(player.getLocation().getZ());
            }

            return this.plugin
                .getTeleportHandler()
                .getSafeLocation(player, signCopy.clone(), 2, 2, false);
          }
        }
      }
    } else {
      Location below = location.clone().add(0.0, -2.0, 0.0);

      for (int scanY = below.getBlockY();
          scanY > CMIWorld.getMinHeight(location.getWorld()) + 1;
          scanY--) {
        below.setY(scanY + 0.01);
        Block belowBlock = below.getBlock();
        if (belowBlock.getState() instanceof Sign) {
          Sign belowSign = (Sign) belowBlock.getState();
          String belowLine = CMIChatColor.stripColor(belowSign.getLine(0));
          if (this.plugin
              .getConfigManager()
              .getElevatorIndicators()
              .contains(belowLine.toLowerCase())) {
            Location belowCenter = below.clone().add(0.0, -0.9, 0.0);
            boolean isStaticIndicator =
                CMIChatColor.stripColor(belowSign.getLine(1))
                    .equalsIgnoreCase(this.plugin.getConfigManager().getElevatorStaticIndicator());
            if (isStaticIndicator) {
              CMIBlock cmiBelowBlock = new CMIBlock(belowBlock);
              Location signLocation = belowSign.getLocation().clone();
              if (cmiBelowBlock.getFacing() != null) {
                signLocation.add(
                    cmiBelowBlock.getFacing().getModX(), 0.0, cmiBelowBlock.getFacing().getModZ());
              }

              belowCenter.setX(signLocation.getBlockX() + 0.5);
              belowCenter.setZ(signLocation.getBlockZ() + 0.5);
            } else {
              belowCenter.setX(player.getLocation().getX());
              belowCenter.setZ(player.getLocation().getZ());
            }

            return this.plugin
                .getTeleportHandler()
                .getSafeLocation(player, belowCenter.clone(), 2, 2, false);
          }
        }
      }
    }

    return CompletableFuture.completedFuture(null);
  }
}
