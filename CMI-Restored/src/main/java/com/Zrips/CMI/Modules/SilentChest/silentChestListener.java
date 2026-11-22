package com.Zrips.CMI.Modules.SilentChest;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.SilentChest;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.commands.list.silentchest;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class silentChestListener implements Listener {
  private static HashMap<UUID, SilentChest> silentChest = new HashMap<>();
  public static HashMap<UUID, Boolean> toggledSilentChest = new HashMap<>();
  private CMI plugin;
  private static HashMap<UUID, Long> sleep = new HashMap<>();

  public silentChestListener(CMI cmi) {
    this.plugin = cmi;
  }

  public static void clearCache(UUID uuid) {
    silentChest.remove(uuid);
    sleep.remove(uuid);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void animation(PlayerAnimationEvent playerAnimationEvent) {
    if (!playerAnimationEvent.isCancelled()) {
      try {
        if (playerAnimationEvent.getAnimationType() != PlayerAnimationType.ARM_SWING) {
          return;
        }
      } catch (Exception exception) {
        return;
      }

      if (!toggledSilentChest.isEmpty()) {
        Player player = playerAnimationEvent.getPlayer();
        if (toggledSilentChest.containsKey(player.getUniqueId())) {
          Block block = CMI.getInstance().getUtilManager().getTargetBlock(player, 6);
          if (block != null) {
            if (validBlock(block)) {
              playerAnimationEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()) {
      Block block = blockBreakEvent.getBlock();
      if (!silentChest.isEmpty() && validBlock(block)) {
        for (var entry : new HashMap<>(silentChest).entrySet()) {
          if (((SilentChest) entry.getValue()).getLocation().equals(block.getLocation())) {
            ((SilentChest) entry.getValue()).getPlayer().closeInventory();
            silentChest.remove(entry.getKey());
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()
        && playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Player player = playerInteractEvent.getPlayer();
      Block block = playerInteractEvent.getClickedBlock();
      if (validBlock(block)) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          Boolean toggled = toggledSilentChest.get(player.getUniqueId());
          if (toggled != null) {
            if (!toggled) {
              return;
            }
          } else {
            if (!cmiUser.isCMIVanished()) {
              return;
            }

            if (VanishManager.is(cmiUser.getUniqueId(), VanishAction.noisyChest)) {
              return;
            }
          }

          BlockState blockState = block.getState();
          InventoryHolder doubleChest = null;
          Location location = block.getLocation();
          Inventory inventory = null;
          String string = null;
          if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1) && chest.getLootTable() != null) {
              CMIActionBar.send(player, CMILC.getIM(silentchest.class, "cantOpen"));
              playerInteractEvent.setCancelled(true);
              return;
            }

            inventory = chest.getInventory();

            try {
              string = chest.getCustomName();
            } catch (Throwable throwable) {
            }

            doubleChest = inventory.getHolder();
            location = chest.getLocation();
            if (doubleChest instanceof DoubleChest) {
              DoubleChest doubleChestBlock = (DoubleChest) doubleChest;
              location = doubleChestBlock.getLocation();
            }
          } else if (CMIMaterial.isShulkerBox(block.getType())) {
            inventory = CMIShulkerBox.getShulkerInv(block);
            if (inventory != null) {
              InventoryHolder inventoryHolder = inventory.getHolder();
            }
          } else if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();
            inventory = holder.getInventory();
          }

          if (block.getType() == Material.ENDER_CHEST) {
            inventory = player.getEnderChest();
          }

          if (inventory != null) {
            Inventory matchedInventory = null;

            for (var entry : silentChest.entrySet()) {
              Location chestLocation = ((SilentChest) entry.getValue()).getLocation();
              if (chestLocation.getWorld().getName().equals(location.getWorld().getName())
                  && chestLocation.getBlockX() == location.getBlockX()
                  && chestLocation.getBlockY() == location.getBlockY()
                  && chestLocation.getBlockZ() == location.getBlockZ()) {
                matchedInventory = ((SilentChest) entry.getValue()).getInv();
              }
            }

            label110:
            if (matchedInventory != null) {
              playerInteractEvent.setCancelled(true);
              player.openInventory(matchedInventory);
              sleep.put(player.getUniqueId(), System.currentTimeMillis() + 100L);
              SilentChest silentChestEntry = new SilentChest(player, matchedInventory, location);
              silentChestEntry.setSourceInventory(inventory);
              silentChestEntry.setEditing(
                  PermissionsManager.CMIPerm.silentchest_editing.hasPermission(player));
              CMILC.info("silentchest", player, "onOpen");
              silentChest.put(player.getUniqueId(), silentChestEntry);
            } else if (inventory.getSize() % 9 == 0) {
              try {
                int value = (int) (Math.ceil(inventory.getSize() / 9.0) * 9.0);
                if (Version.isCurrentEqualOrHigher(Version.v1_11_R1)
                    && CMIMaterial.isShulkerBox(block.getType())) {
                  try {
                    matchedInventory =
                        this.plugin.getServer().createInventory(player, InventoryType.SHULKER_BOX);
                  } catch (Throwable throwable2) {
                    matchedInventory = this.plugin.getServer().createInventory(player, value);
                  }
                } else if (block.getType() == Material.ENDER_CHEST) {
                  matchedInventory = inventory;
                } else if (string != null) {
                  matchedInventory = this.plugin.getServer().createInventory(player, value, string);
                } else {
                  matchedInventory = this.plugin.getServer().createInventory(player, value);
                }

                matchedInventory.setContents(inventory.getContents());
                break label110;
              } catch (Throwable throwable3) {
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSilentInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
    if (!silentChest.isEmpty()
        && silentChest.containsKey(inventoryCloseEvent.getPlayer().getUniqueId())) {
      silentChest.remove(inventoryCloseEvent.getPlayer().getUniqueId());
    }
  }

  private static Location getLocation(InventoryHolder inventoryHolder) {
    Location location = null;
    if (inventoryHolder instanceof Chest) {
      Chest chest = (Chest) inventoryHolder;
      location = chest.getLocation();
    } else if (inventoryHolder instanceof DoubleChest) {
      DoubleChest doubleChest = (DoubleChest) inventoryHolder;
      location = doubleChest.getLocation();
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_11_R1)
          && inventoryHolder instanceof ShulkerBox) {
        ShulkerBox shulkerBox = (ShulkerBox) inventoryHolder;
        location = shulkerBox.getLocation();
      }
    } catch (Exception exception) {
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_14_R1) && inventoryHolder instanceof Barrel) {
        Barrel barrel = (Barrel) inventoryHolder;
        location = barrel.getLocation();
      }
    } catch (Exception exception2) {
    }

    return location;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onSilentInventoryClickWatcher(InventoryClickEvent inventoryClickEvent) {
    if (!silentChest.isEmpty()) {
      Inventory inventory = inventoryClickEvent.getClickedInventory();
      if (inventory != null) {
        if (sleep.containsKey(inventoryClickEvent.getWhoClicked().getUniqueId())
            && sleep.get(inventoryClickEvent.getWhoClicked().getUniqueId())
                > System.currentTimeMillis()) {
          inventoryClickEvent.setCancelled(true);
        } else {
          SilentChest silentChestEntry =
              getSilentChestByInventory(inventoryClickEvent.getInventory());
          if (silentChestEntry != null) {
            CMIScheduler.runAtLocation(
                this.plugin,
                silentChestEntry.getLocation(),
                () ->
                    silentChestEntry
                        .getInv()
                        .setContents(inventoryClickEvent.getInventory().getContents()));
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onSilentInventoryClickWatcher(InventoryDragEvent inventoryDragEvent) {
    if (!silentChest.isEmpty()) {
      Inventory inventory = inventoryDragEvent.getInventory();
      if (inventory != null) {
        if (sleep.containsKey(inventoryDragEvent.getWhoClicked().getUniqueId())
            && sleep.get(inventoryDragEvent.getWhoClicked().getUniqueId())
                > System.currentTimeMillis()) {
          inventoryDragEvent.setCancelled(true);
        } else {
          SilentChest silentChestEntry = getSilentChestByInventory(inventory);
          if (silentChestEntry != null) {
            CMIScheduler.runAtLocation(
                this.plugin,
                silentChestEntry.getLocation(),
                () ->
                    silentChestEntry
                        .getInv()
                        .setContents(inventoryDragEvent.getInventory().getContents()));
          }
        }
      }
    }
  }

  private boolean sameInventories(Inventory inventory, Inventory matchedInventory) {
    if (inventory.getSize() != matchedInventory.getSize()) {
      return false;
    }

    for (int value = 0; value < inventory.getSize(); value++) {
      ItemStack itemStack = inventory.getItem(value);
      ItemStack chestItem = matchedInventory.getItem(value);
      if (itemStack != null || chestItem != null) {
        if (itemStack == null && chestItem != null || itemStack != null && chestItem == null) {
          return false;
        }

        if (!inventory.getItem(value).equals(matchedInventory.getItem(value))) {
          return false;
        }
      }
    }

    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onSilentInventoryClick(InventoryClickEvent inventoryClickEvent) {
    if (!silentChest.isEmpty()
        && silentChest.containsKey(inventoryClickEvent.getWhoClicked().getUniqueId())) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      SilentChest silentChestEntry = silentChest.get(player.getUniqueId());
      if (!silentChestEntry.isEditing()) {
        inventoryClickEvent.setCancelled(true);
      }

      Block block = silentChestEntry.getLocation().getBlock();
      if (validBlock(block)) {
        if (sleep.containsKey(inventoryClickEvent.getWhoClicked().getUniqueId())
            && sleep.get(inventoryClickEvent.getWhoClicked().getUniqueId())
                > System.currentTimeMillis()) {
          inventoryClickEvent.setCancelled(true);
        } else if (!this.sameInventories(
                silentChestEntry.getSourceInventory(), silentChestEntry.getInv())
            && !inventoryClickEvent.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
          if (silentChestEntry.getTask() == null) {
            silentChestEntry
                .getInv()
                .setContents(silentChestEntry.getSourceInventory().getContents());
          }

          inventoryClickEvent.setCancelled(true);
        } else if (silentChestEntry.getTask() == null) {
          silentChestEntry.setTask(
              CMIScheduler.runAtLocation(
                  this.plugin,
                  silentChestEntry.getLocation(),
                  () -> {
                    BlockState blockState = silentChestEntry.getLocation().getBlock().getState();
                    Inventory inventory = null;
                    if (blockState instanceof InventoryHolder) {
                      InventoryHolder inventoryHolder = (InventoryHolder) block.getState();
                      inventory = inventoryHolder.getInventory();
                    }

                    if (inventory != null) {
                      if (!inventoryClickEvent.isCancelled()) {
                        inventory.setContents(silentChestEntry.getInv().getContents());

                        for (HumanEntity humanEntity : inventory.getViewers()) {
                          ((Player) humanEntity).updateInventory();
                        }

                        for (HumanEntity viewer : silentChestEntry.getInv().getViewers()) {
                          ((Player) viewer).updateInventory();
                        }
                      }
                    }
                  }));
          silentChestEntry.getTask().thenRun(() -> silentChestEntry.setTask(null));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onSilentInventoryClick(InventoryDragEvent inventoryDragEvent) {
    if (!silentChest.isEmpty()
        && silentChest.containsKey(inventoryDragEvent.getWhoClicked().getUniqueId())) {
      Player player = (Player) inventoryDragEvent.getWhoClicked();
      SilentChest silentChestEntry = silentChest.get(player.getUniqueId());
      if (!silentChestEntry.isEditing()) {
        inventoryDragEvent.setCancelled(true);
      }

      Block block = silentChestEntry.getLocation().getBlock();
      if (validBlock(block)) {
        if (!this.sameInventories(
            silentChestEntry.getSourceInventory(), silentChestEntry.getInv())) {
          if (silentChestEntry.getTask() == null) {
            silentChestEntry
                .getInv()
                .setContents(silentChestEntry.getSourceInventory().getContents());
          }

          inventoryDragEvent.setCancelled(true);
        } else if (silentChestEntry.getTask() == null) {
          silentChestEntry.setTask(
              CMIScheduler.runAtLocation(
                  this.plugin,
                  silentChestEntry.getLocation(),
                  () -> {
                    BlockState blockState = silentChestEntry.getLocation().getBlock().getState();
                    Inventory inventory = null;
                    if (blockState instanceof InventoryHolder) {
                      InventoryHolder inventoryHolder = (InventoryHolder) block.getState();
                      inventory = inventoryHolder.getInventory();
                    }

                    if (inventory != null) {
                      if (!inventoryDragEvent.isCancelled()) {
                        inventory.setContents(silentChestEntry.getInv().getContents());

                        for (HumanEntity humanEntity : inventory.getViewers()) {
                          ((Player) humanEntity).updateInventory();
                        }

                        for (HumanEntity viewer : silentChestEntry.getInv().getViewers()) {
                          ((Player) viewer).updateInventory();
                        }
                      }
                    }
                  }));
          silentChestEntry.getTask().thenRun(() -> silentChestEntry.setTask(null));
        }
      }
    }
  }

  private static SilentChest getSilentChestByInventory(Inventory inventory) {
    if (inventory == null) {
      return null;
    }

    for (var entry : silentChest.entrySet()) {
      if (((SilentChest) entry.getValue()).getSourceInventory().equals(inventory)) {
        return (SilentChest) entry.getValue();
      }
    }

    return null;
  }

  private static boolean validBlock(Block block) {
    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    return cmiMaterial == null
        ? false
        : cmiMaterial.containsCriteria(CMIMC.CHEST)
            || cmiMaterial.containsCriteria(CMIMC.COPPERCHEST)
            || cmiMaterial.isShulkerBox()
            || cmiMaterial.equals(CMIMaterial.BARREL)
            || cmiMaterial.equals(CMIMaterial.ENDER_CHEST);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryMoveItemEvent(InventoryMoveItemEvent inventoryMoveItemEvent) {
    if (!silentChest.isEmpty()) {
      Inventory inventory = inventoryMoveItemEvent.getDestination();
      Location location = inventory.getLocation();
      SilentChest silentChestEntry = getSilentChestByInventory(inventory);
      if (silentChestEntry != null) {
        updateMovedItem(location, silentChestEntry, inventory, true);
      }

      inventory = inventoryMoveItemEvent.getSource();
      location = inventory.getLocation();
      silentChestEntry = getSilentChestByInventory(inventory);
      if (silentChestEntry != null) {
        updateMovedItem(location, silentChestEntry, inventory, false);
      }
    }
  }

  private static void updateMovedItem(
      Location location, SilentChest silentChestEntry, Inventory inventory, boolean flag) {
    if (location != null && inventory != null) {
      Block block = location.getBlock();
      if (!validBlock(block)) {
        silentChestEntry.getPlayer().closeInventory();
      } else {
        Inventory matchedInventory = silentChestEntry.getInv();
        if (flag) {
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              location,
              () -> matchedInventory.setContents(inventory.getContents()));
        } else {
          matchedInventory.setContents(inventory.getContents());
        }
      }
    } else {
      silentChestEntry.getPlayer().closeInventory();
    }
  }
}
