package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners1_8 implements Listener {
  private CMI plugin;

  public PlayerListeners1_8(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerInteractMinecart(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()) {
      Entity entity = playerInteractAtEntityEvent.getRightClicked();
      if (entity != null) {
        try {
          if (Version.isCurrentHigher(Version.v1_8_R3)
              && playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) {
            return;
          }
        } catch (Exception exception) {
        }

        if (entity.getType().equals(EntityType.MINECART)) {
          Player player = playerInteractAtEntityEvent.getPlayer();
          if (player.isSneaking()) {
            CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
            if (cmiUser != null) {
              ItemStack itemStack = cmiUser.getItemInHand();
              if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                Minecart minecart = (Minecart) entity;

                try {
                  if (minecart.getPassenger() != null) {
                    return;
                  }
                } catch (Exception | NoSuchMethodError value) {
                  value.printStackTrace();
                  return;
                }

                Location location = minecart.getLocation();
                Entity replacement = null;
                switch (itemStack.getType()) {
                  case CHEST:
                  case FURNACE:
                  case HOPPER:
                  case TNT:
                    if (!PermissionsManager.CMIPerm.minecart_change_$1.hasPermission(
                        player, true, itemStack.getType().name().toLowerCase())) {
                      return;
                    }
                  default:
                    VehicleDestroyEvent vehicleDestroyEvent =
                        new VehicleDestroyEvent(
                            minecart,
                            DamageSource.builder(DamageType.PLAYER_ATTACK)
                                .withCausingEntity(player)
                                .withDirectEntity(player)
                                .build(),
                            player);
                    Bukkit.getPluginManager().callEvent(vehicleDestroyEvent);
                    if (!vehicleDestroyEvent.isCancelled()) {
                      switch (itemStack.getType()) {
                        case CHEST:
                          minecart.remove();
                          cmiUser
                              .getInventory()
                              .removeItemFromMainHand(new ItemStack(Material.CHEST));
                          replacement =
                              location
                                  .getWorld()
                                  .spawnEntity(
                                      location.clone(), CMIEntityType.CHEST_MINECART.getType());
                          break;
                        case FURNACE:
                          minecart.remove();
                          cmiUser
                              .getInventory()
                              .removeItemFromMainHand(new ItemStack(Material.FURNACE));
                          replacement =
                              location
                                  .getWorld()
                                  .spawnEntity(
                                      location.clone(), CMIEntityType.FURNACE_MINECART.getType());
                          break;
                        case HOPPER:
                          minecart.remove();
                          cmiUser
                              .getInventory()
                              .removeItemFromMainHand(new ItemStack(Material.HOPPER));
                          replacement =
                              location
                                  .getWorld()
                                  .spawnEntity(
                                      location.clone(), CMIEntityType.HOPPER_MINECART.getType());
                          break;
                        case TNT:
                          minecart.remove();
                          cmiUser
                              .getInventory()
                              .removeItemFromMainHand(new ItemStack(Material.TNT));
                          replacement =
                              location
                                  .getWorld()
                                  .spawnEntity(
                                      location.clone(), CMIEntityType.TNT_MINECART.getType());
                      }

                      if (replacement != null) {
                        replacement.teleport(location);
                        replacement.setCustomName(minecart.getCustomName());
                        replacement.setCustomNameVisible(minecart.isCustomNameVisible());

                        try {
                          replacement.setGlowing(minecart.isGlowing());
                        } catch (Exception | NoSuchMethodError value2) {
                        }

                        try {
                          replacement.setGravity(minecart.hasGravity());
                        } catch (Exception | NoSuchMethodError value3) {
                        }

                        replacement.setVelocity(minecart.getVelocity());
                        replacement.setTicksLived(minecart.getTicksLived());
                        replacement.setFireTicks(minecart.getFireTicks());
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
