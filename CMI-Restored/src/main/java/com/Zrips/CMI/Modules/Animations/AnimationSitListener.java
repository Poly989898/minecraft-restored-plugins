package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.sit;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AnimationSitListener implements Listener {
  private CMI plugin;

  public AnimationSitListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
    this.plugin
        .getAnimationManager()
        .removePlayerFromChair(playerTeleportEvent.getPlayer(), false, false, false);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerTeleportEvent(PlayerDeathEvent playerDeathEvent) {
    this.plugin.getAnimationManager().removePlayerFromChair(playerDeathEvent.getEntity());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerTeleportEvent(PlayerQuitEvent playerQuitEvent) {
    if (this.plugin.getAnimationManager().isSitting(playerQuitEvent.getPlayer())) {
      playerQuitEvent.getPlayer().leaveVehicle();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void EntityTeleportEvent(EntityTeleportEvent entityTeleportEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void VehicleEnterEvent(VehicleEnterEvent vehicleEnterEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void VehicleExitEvent(VehicleExitEvent vehicleExitEvent) {
    if (!vehicleExitEvent.isCancelled()
        && !this.plugin.getAnimationManager().playerSittingMap.isEmpty()
        && vehicleExitEvent.getExited() instanceof Player) {
      Player player = (Player) vehicleExitEvent.getExited();
      if (this.plugin.getAnimationManager().isSitting(player)) {
        this.plugin
            .getAnimationManager()
            .removePlayerFromChair((Player) vehicleExitEvent.getExited(), false);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void BlockBreakEvent(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()
        && !this.plugin.getAnimationManager().playerSittingMap.isEmpty()) {
      String string =
          this.plugin
              .getUtilManager()
              .convertLocToStringShort(blockBreakEvent.getBlock().getLocation());

      for (var entry :
          new HashMap<>(this.plugin.getAnimationManager().playerSittingMap).entrySet()) {
        Player player = CMIUser.getOnlinePlayer((UUID) entry.getKey());
        if (player == null) {
          this.plugin.getAnimationManager().playerSittingMap.remove(entry.getKey());
        } else if (this.plugin
            .getUtilManager()
            .convertLocToStringShort(player.getLocation().clone().add(0.0, 1.0, 0.0))
            .equals(string)) {
          this.plugin.getAnimationManager().removePlayerFromChair((UUID) entry.getKey());
          return;
        }
      }

      UUID uuid = this.plugin.getAnimationManager().chairLoc.get(string);
      if (uuid == null) {
        uuid =
            this.plugin
                .getAnimationManager()
                .chairLoc
                .get(
                    this.plugin
                        .getUtilManager()
                        .convertLocToStringShort(
                            blockBreakEvent.getBlock().getLocation().clone().add(0.0, -1.0, 0.0)));
        if (uuid == null) {
          if (!CMIMaterial.isBed(blockBreakEvent.getBlock().getType())) {
            return;
          }

          CMIBlock cmiBlock = new CMIBlock(blockBreakEvent.getBlock());
          Block block = cmiBlock.getSecondaryBedBlock();
          if (block != null) {
            uuid =
                this.plugin
                    .getAnimationManager()
                    .chairLoc
                    .get(
                        this.plugin
                            .getUtilManager()
                            .convertLocToStringShort(
                                block.getLocation().clone().add(0.0, -1.0, 0.0)));
          }

          if (uuid == null) {
            uuid =
                this.plugin
                    .getAnimationManager()
                    .chairLoc
                    .get(
                        this.plugin
                            .getUtilManager()
                            .convertLocToStringShort(
                                blockBreakEvent
                                    .getBlock()
                                    .getLocation()
                                    .clone()
                                    .add(0.0, -1.0, 0.0)));
            if (uuid == null) {
              return;
            }
          }
        }
      }

      this.plugin.getAnimationManager().removePlayerFromChair(uuid);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void PlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()
        && !this.plugin.getAnimationManager().playerSittingMap.isEmpty()
        && playerInteractAtEntityEvent.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
      Entity entity = playerInteractAtEntityEvent.getRightClicked();
      if (entity.hasMetadata("CMIArmorStandForSit")) {
        playerInteractAtEntityEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && !this.plugin.getAnimationManager().playerSittingMap.isEmpty()
        && this.plugin.getAnimationManager().isRemoveFromChairOnDamage()
        && entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      if (this.plugin.getAnimationManager().isSitting(player)) {
        this.plugin.getAnimationManager().removePlayerFromChair(player, false, true);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerTeleportEvent(PlayerInteractEvent playerInteractEvent) {
    if (this.plugin.getAnimationManager().isSitOnStairs()
        && playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
      try {
        if (Version.isCurrentHigher(Version.v1_8_R3)
            && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception exception) {
      }

      Block block = playerInteractEvent.getClickedBlock();
      if (this.plugin.getAnimationManager().isValidChairBlock(block)) {
        Player player = playerInteractEvent.getPlayer();
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          if (!cmiUser.isSitting() || player.getVehicle() == null) {
            ItemStack itemStack =
                cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.MainHand);
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
              if (!this.plugin.getAnimationManager().isDoubleClickWait(player)) {
                if (!(this.plugin
                        .getUtilManager()
                        .getDistance(block.getLocation(), player.getLocation())
                    > this.plugin.getAnimationManager().getChairRange())) {
                  if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
                    try {
                      if (player.isGliding()) {
                        return;
                      }
                    } catch (NoSuchMethodError noSuchMethodError) {
                    }
                  }

                  if (PermissionsManager.CMIPerm.command_sit_stairs.hasPermission(player, false)) {
                    PlayerTeleportEvent playerTeleportEvent =
                        new PlayerTeleportEvent(player, player.getLocation(), block.getLocation());
                    Bukkit.getServer().getPluginManager().callEvent(playerTeleportEvent);
                    if (!playerTeleportEvent.isCancelled()) {
                      if (this.plugin.getAnimationManager().isSomeOneSittingHere(block)) {
                        CMILC.info("sit", player, "accupied");
                      } else {
                        CMIScheduler.runTaskLater(
                            this.plugin, () -> CMILC.info(sit.class, player, "onSit"), 2L);
                        this.plugin.getAnimationManager().sit(player, block);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
