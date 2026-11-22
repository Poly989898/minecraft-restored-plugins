package com.Zrips.CMI.Modules.ShulkerBoxInventory;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.Random;
import net.Zrips.CMILib.Container.CMIPlayerInventory;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShulkerBoxListener implements Listener {
  private CMI plugin;

  public ShulkerBoxListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMove(PlayerMoveEvent playerMoveEvent) {
    if (playerMoveEvent.getTo() != null) {
      if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
          || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
          || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
        ShulkerBoxManager.recordMove(playerMoveEvent.getPlayer());
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onQuit(PlayerQuitEvent playerQuitEvent) {
    ShulkerBoxManager.removeMove(playerQuitEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void CheckForChulker(InventoryClickEvent inventoryClickEvent) {
    if (inventoryClickEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      if (this.plugin.getShulkerBoxManager().isEditing(player)) {
        if (this.plugin.getShulkerBoxManager().isRequirePermission()
            && !PermissionsManager.CMIPerm.openshulker_edit.hasPermission(player)) {
          inventoryClickEvent.setCancelled(true);
        }

        try {
          if (inventoryClickEvent.getClick() == ClickType.NUMBER_KEY) {
            inventoryClickEvent.setCancelled(true);
            return;
          }
        } catch (Throwable throwable) {
        }

        ItemStack itemStack = inventoryClickEvent.getCurrentItem();
        if (itemStack != null) {
          if (CMIMaterial.isShulkerBox(itemStack.getType())) {
            if (inventoryClickEvent.getClickedInventory().getType() == InventoryType.PLAYER) {
              inventoryClickEvent.setCancelled(true);
            }

            if (inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT
                && inventoryClickEvent.getClickedInventory().getType() == InventoryType.PLAYER) {
              if (!CMIShulkerBox.isShulkerBox(itemStack)) {
                ItemStack shulkerItem =
                    this.plugin.getReflectionManager().tryToMakeShulkerBox(itemStack);
                if (!CMIShulkerBox.isShulkerBox(shulkerItem)) {
                  return;
                }

                itemStack = shulkerItem;
                player.getInventory().setItem(inventoryClickEvent.getSlot(), itemStack);
              }

              ShulkerInv shulkerInv =
                  this.plugin.getShulkerBoxManager().getShulkerInvRecord(player);
              if (shulkerInv != null) {
                this.plugin
                    .getShulkerBoxManager()
                    .updateEntireShulkerInv(player, shulkerInv.getCustomInv().getContents());
              }

              CMINBT cminbt = new CMINBT(itemStack);
              itemStack =
                  (ItemStack) cminbt.setInt("CMIBackPack", new Random().nextInt(Integer.MAX_VALUE));
              player.getInventory().setItem(inventoryClickEvent.getSlot(), itemStack);
              inventoryClickEvent.setCancelled(true);
              this.plugin
                  .getShulkerBoxManager()
                  .openShulkerBox(
                      player, player.getInventory().getItem(inventoryClickEvent.getSlot()));
              if (player.isOnline()) {
                ;
              }
            }
          } else {
            try {
              if (inventoryClickEvent.getAction() == InventoryAction.HOTBAR_SWAP) {
                inventoryClickEvent.setCancelled(true);
                return;
              }
            } catch (Exception exception) {
            }

            try {
              if (inventoryClickEvent.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
                inventoryClickEvent.setCancelled(true);
                return;
              }
            } catch (Exception exception2) {
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void ShulkerItemOpen(InventoryClickEvent inventoryClickEvent) {
    if (!inventoryClickEvent.isCancelled()
        && inventoryClickEvent.getWhoClicked() instanceof Player
        && inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      if (!this.plugin.getShulkerBoxManager().isEditing(player)) {
        ItemStack itemStack = inventoryClickEvent.getCurrentItem();
        if (inventoryClickEvent.getClickedInventory() != null) {
          if (inventoryClickEvent.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (inventoryClickEvent.getCurrentItem() != null) {
              if (CMIMaterial.isShulkerBox(inventoryClickEvent.getCurrentItem().getType())) {
                if (inventoryClickEvent.getRawSlot() >= 9
                    && inventoryClickEvent.getRawSlot() <= 44) {
                  if (PermissionsManager.CMIPerm.openshulker.hasPermission(player)) {
                    if (CMIPlayerInventory.getTopInventory(player)
                        .getType()
                        .equals(InventoryType.CRAFTING)) {
                      if (!CMIShulkerBox.isShulkerBox(itemStack)) {
                        ItemStack shulkerItem =
                            this.plugin.getReflectionManager().tryToMakeShulkerBox(itemStack);
                        if (!CMIShulkerBox.isShulkerBox(shulkerItem)) {
                          return;
                        }

                        itemStack = shulkerItem;
                        player.getInventory().setItem(inventoryClickEvent.getSlot(), itemStack);
                      }

                      CMINBT cminbt = new CMINBT(itemStack);
                      itemStack =
                          (ItemStack)
                              cminbt.setInt("CMIBackPack", new Random().nextInt(Integer.MAX_VALUE));
                      player.getInventory().setItem(inventoryClickEvent.getSlot(), itemStack);
                      inventoryClickEvent.setCancelled(true);
                      if (player.isOnline()) {
                        CMIScheduler.runTask(
                            this.plugin,
                            () -> {
                              if (player.isOnline()) {
                                this.plugin
                                    .getShulkerBoxManager()
                                    .openShulkerBox(
                                        player,
                                        player
                                            .getInventory()
                                            .getItem(inventoryClickEvent.getSlot()));
                              }
                            });
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

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR
        || playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) {
      Player player = playerInteractEvent.getPlayer();
      if (!this.plugin.getShulkerBoxManager().isEditing(player) && player.isSneaking()) {
        ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
        if (CMIMaterial.isShulkerBox(itemStack.getType())) {
          try {
            if (Version.isCurrentHigher(Version.v1_8_R3)
                && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
              return;
            }
          } catch (Exception exception) {
          }

          if (PermissionsManager.CMIPerm.openshulker_shift.hasPermission(player)) {
            ItemStack shulkerItem = itemStack;
            if (!CMIShulkerBox.isShulkerBox(shulkerItem)) {
              shulkerItem = this.plugin.getReflectionManager().tryToMakeShulkerBox(itemStack);
            }

            if (CMIShulkerBox.isShulkerBox(shulkerItem)) {
              CMINBT cminbt = new CMINBT(shulkerItem);
              shulkerItem =
                  (ItemStack) cminbt.setInt("CMIBackPack", new Random().nextInt(Integer.MAX_VALUE));
              CMIItemStack.setItemInMainHand(player, shulkerItem);
              player.updateInventory();
              playerInteractEvent.setCancelled(true);
              ItemStack mainHandItem = CMIItemStack.getItemInMainHand(player);
              if (player.isOnline()) {
                CMIScheduler.runTask(
                    this.plugin,
                    () -> this.plugin.getShulkerBoxManager().openShulkerBox(player, mainHandItem));
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void ShulkerEdit(InventoryClickEvent inventoryClickEvent) {
    if (!inventoryClickEvent.isCancelled()
        && inventoryClickEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      if (this.plugin.getShulkerBoxManager().isEditing(player)) {
        if (!this.plugin.getShulkerBoxManager().stillHaveItem(player)) {
          inventoryClickEvent.setCancelled(true);
          player.closeInventory();
        }

        if (inventoryClickEvent.getAction() == InventoryAction.HOTBAR_SWAP) {
          inventoryClickEvent.setCancelled(true);
          CMIScheduler.runTask(this.plugin, player::updateInventory);
        } else {
          CMIScheduler.runTask(
              this.plugin, () -> this.plugin.getShulkerBoxManager().updateShulkerItem(player));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryDragEvent(InventoryDragEvent inventoryDragEvent) {
    if (!inventoryDragEvent.isCancelled() && inventoryDragEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryDragEvent.getWhoClicked();
      if (this.plugin.getShulkerBoxManager().isEditing(player)) {
        if (!this.plugin.getShulkerBoxManager().stillHaveItem(player)) {
          inventoryDragEvent.setCancelled(true);
          player.closeInventory();
        }

        CMIScheduler.runTask(
            this.plugin, () -> this.plugin.getShulkerBoxManager().updateShulkerItem(player));
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void InventoryCloseEvent(InventoryCloseEvent inventoryCloseEvent) {
    Player player = (Player) inventoryCloseEvent.getPlayer();
    if (this.plugin.getShulkerBoxManager().isEditing(player)) {
      this.plugin
          .getShulkerBoxManager()
          .updateEntireShulkerInv(player, inventoryCloseEvent.getInventory().getContents());
      ShulkerInv shulkerInv = this.plugin.getShulkerBoxManager().getShulkerInvRecord(player);
      if (shulkerInv != null) {
        CMINBT cminbt = new CMINBT(shulkerInv.getShulkerBox());
        cminbt.remove("CMIBackPack");
      }

      this.plugin.getShulkerBoxManager().remove(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {}
}
