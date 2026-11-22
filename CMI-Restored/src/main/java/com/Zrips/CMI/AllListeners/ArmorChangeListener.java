package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Items.ArmorTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorChangeListener implements Listener {
  private CMI plugin;
  public static Set<UUID> checked = new HashSet<>();

  public ArmorChangeListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void InventoryDragEvent(InventoryDragEvent inventoryDragEvent) {
    if (!inventoryDragEvent.isCancelled()) {
      checked.remove(inventoryDragEvent.getWhoClicked().getUniqueId());
      var set = inventoryDragEvent.getInventorySlots();
      if (set.contains(36) || set.contains(37) || set.contains(38) || set.contains(39)) {
        if (inventoryDragEvent.getWhoClicked() instanceof Player) {
          if (inventoryDragEvent.getOldCursor() != null) {
            ArmorTypes armorTypes = ArmorTypes.matchType(inventoryDragEvent.getOldCursor());
            ItemStack itemStack = inventoryDragEvent.getOldCursor();
            ItemStack item = inventoryDragEvent.getCursor();
            armorTypes =
                ArmorTypes.matchType(
                    item != null && item.getType() != Material.AIR
                        ? item
                        : inventoryDragEvent.getOldCursor());
            if (armorTypes != null
                && inventoryDragEvent.getRawSlots().contains(armorTypes.getSlot())) {
              CMIArmorChangeEvent.EquipMethod equipMethod = CMIArmorChangeEvent.EquipMethod.DRAG;
              CMIArmorChangeEvent cmiArmorChangeEvent =
                  new CMIArmorChangeEvent(
                      (Player) inventoryDragEvent.getWhoClicked(),
                      equipMethod,
                      armorTypes,
                      item,
                      itemStack);
              Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
              if (cmiArmorChangeEvent.isCancelled()) {
                inventoryDragEvent.setCancelled(true);
              }
            }
          }
        }
      }
    }
  }

  private static boolean validItem(ItemStack itemStack) {
    return itemStack != null && itemStack.getType() != Material.AIR;
  }

  private static boolean valueCheck(boolean flag, ItemStack itemStack) {
    return flag ^ validItem(itemStack);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    if (!inventoryClickEvent.isCancelled()) {
      checked.remove(inventoryClickEvent.getWhoClicked().getUniqueId());
      boolean flag = false;
      boolean flagSecond = false;
      if (!inventoryClickEvent.isCancelled()) {
        ClickType clickType = inventoryClickEvent.getClick();
        if (clickType.equals(ClickType.SHIFT_LEFT) || clickType.equals(ClickType.SHIFT_RIGHT)) {
          flag = true;
        }

        if (inventoryClickEvent.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
          if (inventoryClickEvent.getSlot() != 36
              && inventoryClickEvent.getSlot() != 37
              && inventoryClickEvent.getSlot() != 38
              && inventoryClickEvent.getSlot() != 39) {
            return;
          }
        } else if (!flag
            && inventoryClickEvent.getSlot() != 36
            && inventoryClickEvent.getSlot() != 37
            && inventoryClickEvent.getSlot() != 38
            && inventoryClickEvent.getSlot() != 39) {
          return;
        }

        if (clickType == ClickType.NUMBER_KEY) {
          flagSecond = true;
        }

        SlotType slotType = inventoryClickEvent.getSlotType();
        if (slotType == SlotType.ARMOR && slotType == SlotType.QUICKBAR
            || inventoryClickEvent.getInventory().getType().equals(InventoryType.CRAFTING)) {
          if (inventoryClickEvent.getWhoClicked() instanceof Player) {
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            if (inventoryClickEvent.getCurrentItem() != null) {
              ArmorTypes armorTypes =
                  ArmorTypes.matchType(
                      flag
                          ? inventoryClickEvent.getCurrentItem()
                          : inventoryClickEvent.getCursor());
              if (flag
                  || armorTypes == null
                  || inventoryClickEvent.getRawSlot() == armorTypes.getSlot()) {
                if (flag) {
                  if (armorTypes != null) {
                    boolean flagThird = true;
                    if (inventoryClickEvent.getRawSlot() == armorTypes.getSlot()) {
                      flagThird = false;
                    }

                    PlayerInventory playerInventory = player.getInventory();
                    if (armorTypes == ArmorTypes.HELMET
                            && valueCheck(flagThird, playerInventory.getHelmet())
                        || (armorTypes == ArmorTypes.CHESTPLATE
                                || armorTypes.equals(ArmorTypes.ELYTRA))
                            && valueCheck(flagThird, playerInventory.getChestplate())
                        || armorTypes == ArmorTypes.LEGGINGS
                            && valueCheck(flagThird, playerInventory.getLeggings())
                        || armorTypes == ArmorTypes.BOOTS
                            && valueCheck(flagThird, playerInventory.getBoots())) {
                      CMIArmorChangeEvent cmiArmorChangeEvent =
                          new CMIArmorChangeEvent(
                              player,
                              CMIArmorChangeEvent.EquipMethod.SHIFT_CLICK,
                              armorTypes,
                              flagThird ? null : inventoryClickEvent.getCurrentItem(),
                              flagThird ? inventoryClickEvent.getCurrentItem() : null);
                      Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
                      if (cmiArmorChangeEvent.isCancelled()) {
                        inventoryClickEvent.setCancelled(true);
                      }
                    }
                  }
                } else {
                  ItemStack itemStack = inventoryClickEvent.getCursor();
                  ItemStack item = inventoryClickEvent.getCurrentItem();
                  if (flagSecond) {
                    if (inventoryClickEvent
                        .getClickedInventory()
                        .getType()
                        .equals(InventoryType.PLAYER)) {
                      ItemStack itemSecond =
                          inventoryClickEvent
                              .getClickedInventory()
                              .getItem(inventoryClickEvent.getHotbarButton());
                      if (itemSecond != null) {
                        armorTypes = ArmorTypes.matchType(itemSecond);
                        itemStack = itemSecond;
                        item =
                            inventoryClickEvent
                                .getClickedInventory()
                                .getItem(inventoryClickEvent.getSlot());
                      } else {
                        armorTypes =
                            ArmorTypes.matchType(
                                validItem(item) ? item : inventoryClickEvent.getCursor());
                      }
                    }
                  } else {
                    armorTypes =
                        ArmorTypes.matchType(
                            validItem(item) ? item : inventoryClickEvent.getCursor());
                  }

                  if (armorTypes != null
                      && inventoryClickEvent.getRawSlot() == armorTypes.getSlot()) {
                    CMIArmorChangeEvent.EquipMethod equipMethod =
                        CMIArmorChangeEvent.EquipMethod.DRAG;
                    if (inventoryClickEvent.getAction().equals(InventoryAction.HOTBAR_SWAP)
                        || flagSecond) {
                      equipMethod = CMIArmorChangeEvent.EquipMethod.HOTBAR_SWAP;
                    }

                    CMIArmorChangeEvent cmiArmorChangeEventValue =
                        new CMIArmorChangeEvent(
                            (Player) inventoryClickEvent.getWhoClicked(),
                            equipMethod,
                            armorTypes,
                            item,
                            itemStack);
                    Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEventValue);
                    if (cmiArmorChangeEventValue.isCancelled()) {
                      inventoryClickEvent.setCancelled(true);
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

  @EventHandler
  public void playerInteractEvent(PlayerPickupItemEvent playerPickupItemEvent) {
    checked.remove(playerPickupItemEvent.getPlayer().getUniqueId());
  }

  @EventHandler
  public void playerInteractEvent(PlayerItemHeldEvent playerItemHeldEvent) {
    checked.remove(playerItemHeldEvent.getPlayer().getUniqueId());
  }

  @EventHandler
  public void playerInteractEvent(PlayerQuitEvent playerQuitEvent) {
    checked.remove(playerQuitEvent.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void playerInteractEvent(PlayerInteractEvent playerInteractEventValue) {
    if (!checked.contains(playerInteractEventValue.getPlayer().getUniqueId())) {
      Action action = playerInteractEventValue.getAction();
      if (action != Action.PHYSICAL) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
          checked.add(playerInteractEventValue.getPlayer().getUniqueId());
          Player player = playerInteractEventValue.getPlayer();
          ArmorTypes armorTypes = ArmorTypes.matchType(playerInteractEventValue.getItem());
          if (armorTypes != null) {
            if (armorTypes.equals(ArmorTypes.ELYTRA)) {
              checked.remove(playerInteractEventValue.getPlayer().getUniqueId());
            }

            PlayerInventory playerInventory = player.getInventory();
            if (armorTypes.equals(ArmorTypes.HELMET) && !validItem(playerInventory.getHelmet())
                || (armorTypes.equals(ArmorTypes.CHESTPLATE)
                        || armorTypes.equals(ArmorTypes.ELYTRA))
                    && !validItem(playerInventory.getChestplate())
                || armorTypes.equals(ArmorTypes.LEGGINGS)
                    && !validItem(playerInventory.getLeggings())
                || armorTypes.equals(ArmorTypes.BOOTS) && !validItem(playerInventory.getBoots())) {
              CMIArmorChangeEvent cmiArmorChangeEvent =
                  new CMIArmorChangeEvent(
                      player,
                      CMIArmorChangeEvent.EquipMethod.HOTBAR,
                      ArmorTypes.matchType(playerInteractEventValue.getItem()),
                      null,
                      playerInteractEventValue.getItem());
              Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
              if (cmiArmorChangeEvent.isCancelled()) {
                playerInteractEventValue.setCancelled(true);
                player.updateInventory();
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void dispenserFireEvent(BlockDispenseEvent blockDispenseEvent) {
    if (!blockDispenseEvent.isCancelled()) {
      ItemStack itemStack = blockDispenseEvent.getItem();
      ArmorTypes armorTypes = ArmorTypes.matchType(itemStack);
      if (ArmorTypes.matchType(itemStack) != null) {
        Location location = blockDispenseEvent.getBlock().getLocation();

        for (Player player : location.getWorld().getPlayers()) {
          Location locationSecond = player.getLocation();
          if (location.getBlockY() - locationSecond.getBlockY() >= -1
              && location.getBlockY() - locationSecond.getBlockY() <= 1) {
            PlayerInventory playerInventory = player.getInventory();
            if ((!validItem(playerInventory.getHelmet()) && armorTypes.equals(ArmorTypes.HELMET)
                    || !validItem(playerInventory.getChestplate())
                        && (armorTypes.equals(ArmorTypes.CHESTPLATE)
                            || armorTypes.equals(ArmorTypes.ELYTRA))
                    || !validItem(playerInventory.getLeggings())
                        && armorTypes.equals(ArmorTypes.LEGGINGS)
                    || !validItem(playerInventory.getBoots())
                        && armorTypes.equals(ArmorTypes.BOOTS))
                && blockDispenseEvent.getBlock().getState() instanceof Dispenser) {
              BlockFace blockFace = new CMIBlock(blockDispenseEvent.getBlock()).getFacing();
              if (blockFace == BlockFace.EAST
                      && locationSecond.getBlockX() != location.getBlockX()
                      && locationSecond.getX() <= location.getX() + 2.3
                      && locationSecond.getX() >= location.getX()
                  || blockFace == BlockFace.WEST
                      && locationSecond.getX() >= location.getX() - 1.3
                      && locationSecond.getX() <= location.getX()
                  || blockFace == BlockFace.SOUTH
                      && locationSecond.getBlockZ() != location.getBlockZ()
                      && locationSecond.getZ() <= location.getZ() + 2.3
                      && locationSecond.getZ() >= location.getZ()
                  || blockFace == BlockFace.NORTH
                      && locationSecond.getZ() >= location.getZ() - 1.3
                      && locationSecond.getZ() <= location.getZ()) {
                CMIArmorChangeEvent cmiArmorChangeEvent =
                    new CMIArmorChangeEvent(
                        player,
                        CMIArmorChangeEvent.EquipMethod.DISPENSER,
                        armorTypes,
                        null,
                        itemStack);
                Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
                if (cmiArmorChangeEvent.isCancelled()) {
                  blockDispenseEvent.setCancelled(true);
                  return;
                }
              }
            }
          }
        }
      }
    }
  }
}
