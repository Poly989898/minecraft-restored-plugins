package net.Zrips.CMILib.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
  CMILib plugin;
  private HashMap<UUID, Long> LastClick = new HashMap<>();

  public GUIListener(CMILib plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onNormalInventoryClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    if (this.plugin.getGUIManager().isOpenedGui(player)
        && this.plugin.getGUIManager().removePlayer(player)) {
      player.updateInventory();
      clearIconItems(player);
    }
  }

  private static void clearIconItems(Player player) {
    for (ItemStack one : player.getInventory().getContents()) {
      CMINBT nbt = new CMINBT(one);
      String res = nbt.getString("CMIGUIIcon");
      if (res != null && res.equalsIgnoreCase("LIProtection")) {
        player.getInventory().remove(one);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerDeathEvent(PlayerDeathEvent event) {
    if (event.getEntity() != null) {
      clearIconItems(event.getEntity());
    }
  }

  private boolean canClickByTimer(UUID uuid) {
    Long time = this.LastClick.get(uuid);
    if (time == null) {
      this.LastClick.put(uuid, System.currentTimeMillis());
      return true;
    } else if (time + 51L > System.currentTimeMillis()) {
      this.LastClick.put(uuid, System.currentTimeMillis());
      return false;
    } else {
      this.LastClick.put(uuid, System.currentTimeMillis());
      return true;
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInventoryClickLog(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    if (this.plugin.getGUIManager().isOpenedGui(player)) {
      CMIGui gui = this.plugin.getGUIManager().getGui(player);
      if (gui.isClickLogging()) {
        if (!gui.isAllowShift()
            || !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
            || event.getClickedInventory() != null
                && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
          try {
            gui.addClickLog(
                event.getClickedInventory() == null ? null : event.getClickedInventory().getType(),
                event.isCancelled(),
                event.getClick(),
                event.getAction(),
                event.getCurrentItem(),
                event.getCursor(),
                event.getSlot());
          } catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onInventoryOpenEvent(InventoryOpenEvent event) {
    if (event.getPlayer() instanceof Player) {
      clearIconItems((Player) event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onItemDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (this.plugin.getGUIManager().isOpenedGui(player)) {
      event.setCancelled(true);
    }

    ItemStack one = event.getItemDrop().getItemStack();
    CMINBT nbt = new CMINBT(one);
    String res = nbt.getString("CMIGUIIcon");
    if (res != null && res.equalsIgnoreCase("LIProtection")) {
      event.setCancelled(true);
      clearIconItems(player);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!event.isCancelled()
        || event.getWhoClicked().getGameMode().toString().equalsIgnoreCase("Spectator")) {
      Player player = (Player) event.getWhoClicked();
      if (this.plugin.getGUIManager().isOpenedGui(player)) {
        CMIGui gui = this.plugin.getGUIManager().getGui(player);
        if (player.isSleeping()) {
          player.closeInventory();
          event.setCancelled(true);
        } else {
          if (event.getAction().toString().equalsIgnoreCase("HOTBAR_SWAP")
              || event.getAction().toString().equalsIgnoreCase("HOTBAR_MOVE_AND_READD")) {
            ItemStack iioh = CMIItemStack.getItemInOffHand(player).clone();
            if (iioh != null && !iioh.getType().equals(Material.AIR)) {
              CMIItemStack.setItemInOffHand(player, null);
              CMIScheduler.runTask(
                  this.plugin,
                  () -> {
                    try {
                      player.getInventory().setItemInOffHand(iioh);
                      player.updateInventory();
                    } catch (Throwable var3x) {
                    }
                  });
            }

            event.setCancelled(true);
          }

          if (event.getClick() != ClickType.DOUBLE_CLICK && event.getHotbarButton() == -1) {
            if (!gui.isAllowShift() && event.isShiftClick()) {
              event.setCancelled(true);
            }

            if (event.isShiftClick()
                && gui.isNoItemPlacement(GUIManager.InvType.Gui)
                && !event.getClickedInventory().equals(gui.getInv())) {
              event.setCancelled(true);
            }

            InventoryAction action = event.getAction();
            boolean allowed =
                action == InventoryAction.PICKUP_ALL
                    || action == InventoryAction.PICKUP_ONE
                    || action == InventoryAction.PICKUP_HALF
                    || action == InventoryAction.PICKUP_SOME
                    || action == InventoryAction.PLACE_ALL
                    || action == InventoryAction.PLACE_ONE
                    || action == InventoryAction.PLACE_SOME
                    || gui.isAllowShift() && action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                    || gui.isAllowClone() && action == InventoryAction.CLONE_STACK;
            if (!allowed) {
              event.setCancelled(true);
            }

            if (!gui.isAllowPickUpAll() && !this.canClickByTimer(player.getUniqueId())) {
              event.setCancelled(true);
            } else {
              List<Integer> buttons = new ArrayList<>();
              buttons.add(event.getRawSlot());
              if (!this.plugin.getGUIManager().canClick(player, buttons, event.getCursor())) {
                event.setCancelled(true);
                if (GUIManager.usePackets) {
                  CMIScheduler.get()
                      .runTask(() -> player.setItemOnCursor(player.getItemOnCursor()));
                }

                gui.updateButtons();
              }

              if (this.plugin.getGUIManager().isLockedPart(player, buttons)) {
                event.setCancelled(true);
                if (GUIManager.usePackets) {
                  CMIScheduler.get()
                      .runTask(() -> player.setItemOnCursor(player.getItemOnCursor()));
                }
              }

              if (this.plugin.getGUIManager().isLockedPart(player, buttons, event.getCursor())) {
                event.setCancelled(true);
                if (GUIManager.usePackets) {
                  CMIScheduler.get()
                      .runTask(() -> player.setItemOnCursor(player.getItemOnCursor()));
                }
              }

              if (!gui.isAllowMoveAll() && action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                GUIManager.limit.remove(player.getUniqueId());
              }

              boolean click =
                  this.plugin
                      .getGUIManager()
                      .processClick(
                          player,
                          event.getCurrentItem(),
                          buttons,
                          this.plugin
                              .getGUIManager()
                              .getClickType(
                                  event.isLeftClick(),
                                  event.isShiftClick(),
                                  action,
                                  event.getClick()));
              if (!click) {
                event.setCancelled(true);
              } else {
                if (!event.isCancelled()
                    && gui.isAllowShift()
                    && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
                    && event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                  event.setCancelled(true);
                  ItemStack item = event.getCurrentItem().clone();

                  for (int i = 0; i < gui.getInvSize().getFields(); i++) {
                    CMIGuiButton b = gui.getButtons().get(i);
                    if ((b == null || !b.isLocked() && b.getItem() == null)
                        && gui.getInv().getItem(i) == null
                        && i < gui.getInv().getSize()) {
                      if (gui.isClickLogging()) {
                        try {
                          gui.addClickLog(
                              event.getClickedInventory() == null
                                  ? null
                                  : event.getClickedInventory().getType(),
                              event.isCancelled(),
                              event.getClick(),
                              event.getAction(),
                              event.getCurrentItem(),
                              event.getCursor(),
                              event.getSlot());
                        } catch (Throwable e) {
                          e.printStackTrace();
                        }
                      }

                      gui.getInv().setItem(i, item);
                      player.getInventory().setItem(event.getSlot(), null);
                      event.setCurrentItem(null);
                      break;
                    }
                  }
                }

                CMIScheduler.get().runTask(() -> clearIconItems(player));
              }
            }
          } else {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryDragEventLog(InventoryDragEvent event) {
    Player player = (Player) event.getWhoClicked();
    if (this.plugin.getGUIManager().isOpenedGui(player)) {
      if (player.isSleeping()) {
        player.closeInventory();
        event.setCancelled(true);
      } else {
        CMIGui gui = this.plugin.getGUIManager().getGui(player);
        if (gui.isClickLogging()) {
          try {
            gui.addClickLog(
                event.getInventory().getType(),
                event.isCancelled(),
                event.getCursor(),
                event.getNewItems(),
                event.getInventorySlots(),
                event.getType());
          } catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryMove(InventoryDragEvent event) {
    if (!event.isCancelled()
        || event.getWhoClicked().getGameMode().toString().equalsIgnoreCase("Spectator")) {
      Player player = (Player) event.getWhoClicked();
      if (this.plugin.getGUIManager().isOpenedGui(player)) {
        CMIGui gui = this.plugin.getGUIManager().getGui(player);
        if (!gui.isAllowPickUpAll() && !this.canClickByTimer(player.getUniqueId())) {
          event.setCancelled(true);
        } else {
          List<Integer> buttons = new ArrayList<>();
          buttons.addAll(event.getRawSlots());
          if (!this.plugin.getGUIManager().canClick(player, buttons, event.getCursor())) {
            event.setCancelled(true);
          }

          if (this.plugin.getGUIManager().isLockedPart(player, buttons, event.getCursor())) {
            event.setCancelled(true);
          }

          if (!this.plugin
              .getGUIManager()
              .processClick(
                  player,
                  event.getOldCursor(),
                  buttons,
                  this.plugin.getGUIManager().getClickType(true, false, null, null))) {
            event.setCancelled(true);
          }

          CMIScheduler.get().runTask(() -> clearIconItems(player));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerPickupItemEvent(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    if (this.plugin.getGUIManager().isOpenedGui(player)) {
      CMIGui gui = this.plugin.getGUIManager().getGui(player);
      if (!gui.isAllowItemPickup()) {
        event.setCancelled(true);
        event
            .getItem()
            .setPickupDelay(
                event.getItem().getPickupDelay() < 20 ? 20 : event.getItem().getPickupDelay());
      }
    }
  }
}
