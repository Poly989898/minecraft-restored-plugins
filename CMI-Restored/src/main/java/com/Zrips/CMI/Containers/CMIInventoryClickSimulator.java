package com.Zrips.CMI.Containers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CMIInventoryClickSimulator {
  public static CMIInventoryClickSimulationResult simulateClick(
      InventoryClickEvent inventoryClickEvent) {
    Player player = (Player) inventoryClickEvent.getWhoClicked();
    Inventory inventory = cloneInventory(CMIPlayerInventory.getTopInventory(player));
    Inventory bottomCopy = cloneInventory(CMIPlayerInventory.getBottomInventory(player));
    ItemStack itemStack = cloneOrNull(inventoryClickEvent.getCursor());
    applyClick(inventoryClickEvent, inventory, bottomCopy, itemStack);
    return new CMIInventoryClickSimulationResult(inventory, bottomCopy);
  }

  public static CMIInventoryClickSimulationResult simulateDrag(
      InventoryDragEvent inventoryDragEvent) {
    Player player = (Player) inventoryDragEvent.getWhoClicked();
    Inventory inventory = cloneInventory(CMIPlayerInventory.getTopInventory(player));
    Inventory bottomCopy = cloneInventory(CMIPlayerInventory.getBottomInventory(player));
    int value = inventory.getSize();

    for (var entry : inventoryDragEvent.getNewItems().entrySet()) {
      int slot = (Integer) entry.getKey();
      ItemStack itemStack = cloneOrNull((ItemStack) entry.getValue());
      if (slot < value) {
        inventory.setItem(slot, itemStack);
      } else {
        bottomCopy.setItem(slot - value, itemStack);
      }
    }

    return new CMIInventoryClickSimulationResult(inventory, bottomCopy);
  }

  private static void applyClick(
      InventoryClickEvent inventoryClickEvent,
      Inventory inventory,
      Inventory secondInventory,
      ItemStack itemStack) {
    int value = inventoryClickEvent.getRawSlot();
    boolean flag = value < inventory.getSize();
    Inventory sourceInventory = flag ? inventory : secondInventory;
    Inventory targetInventory = flag ? secondInventory : inventory;
    int slotIndex = flag ? value : value - inventory.getSize();
    ItemStack sourceItem = cloneOrNull(sourceInventory.getItem(slotIndex));
    switch (inventoryClickEvent.getAction()) {
      case NOTHING:
      case PICKUP_SOME:
      case DROP_ALL_CURSOR:
      case DROP_ONE_CURSOR:
      case CLONE_STACK:
      default:
        break;
      case PICKUP_ALL:
        sourceInventory.setItem(slotIndex, null);
        break;
      case PICKUP_HALF:
        if (sourceItem != null) {
          int halfAmount = sourceItem.getAmount() / 2;
          int remainder = sourceItem.getAmount() - halfAmount;
          if (remainder > 0) {
            sourceItem.setAmount(remainder);
            sourceInventory.setItem(slotIndex, sourceItem);
          } else {
            sourceInventory.setItem(slotIndex, null);
          }
        }
        break;
      case PICKUP_ONE:
        if (sourceItem != null) {
          int amountMinusOne = sourceItem.getAmount() - 1;
          if (amountMinusOne > 0) {
            sourceItem.setAmount(amountMinusOne);
            sourceInventory.setItem(slotIndex, sourceItem);
          } else {
            sourceInventory.setItem(slotIndex, null);
          }
        }
        break;
      case PLACE_ALL:
        sourceInventory.setItem(slotIndex, cloneOrNull(itemStack));
        break;
      case PLACE_SOME:
        if (itemStack != null) {
          if (sourceItem == null) {
            sourceInventory.setItem(slotIndex, itemStack.clone());
          } else if (sourceItem.isSimilar(itemStack)) {
            int maxStack = sourceItem.getMaxStackSize();
            int freeSpace = maxStack - sourceItem.getAmount();
            int moveAmount = Math.min(freeSpace, itemStack.getAmount());
            sourceItem.setAmount(sourceItem.getAmount() + moveAmount);
            sourceInventory.setItem(slotIndex, sourceItem);
          }
        }
        break;
      case PLACE_ONE:
        if (itemStack != null) {
          if (sourceItem == null) {
            ItemStack movingItem = itemStack.clone();
            movingItem.setAmount(1);
            sourceInventory.setItem(slotIndex, movingItem);
          } else if (sourceItem.isSimilar(itemStack)) {
            sourceItem.setAmount(sourceItem.getAmount() + 1);
            sourceInventory.setItem(slotIndex, sourceItem);
          }
        }
        break;
      case SWAP_WITH_CURSOR:
        sourceInventory.setItem(slotIndex, cloneOrNull(itemStack));
        break;
      case DROP_ALL_SLOT:
      case DROP_ONE_SLOT:
        if (sourceItem != null) {
          int placeAmount =
              inventoryClickEvent.getAction() == InventoryAction.DROP_ONE_SLOT
                  ? 1
                  : sourceItem.getAmount();
          int leftover = sourceItem.getAmount() - placeAmount;
          if (leftover > 0) {
            sourceItem.setAmount(leftover);
            sourceInventory.setItem(slotIndex, sourceItem);
          } else {
            sourceInventory.setItem(slotIndex, null);
          }
        }
        break;
      case MOVE_TO_OTHER_INVENTORY:
        if (sourceItem != null) {
          moveShift(sourceItem, targetInventory);
          sourceInventory.setItem(slotIndex, null);
        }
        break;
      case HOTBAR_MOVE_AND_READD:
      case HOTBAR_SWAP:
        int hotbarSlot = inventoryClickEvent.getHotbarButton();
        if (hotbarSlot >= 0 && hotbarSlot < 9) {
          ItemStack hotbarItem = cloneOrNull(secondInventory.getItem(hotbarSlot));
          secondInventory.setItem(hotbarSlot, sourceItem);
          sourceInventory.setItem(slotIndex, hotbarItem);
        }
        break;
      case COLLECT_TO_CURSOR:
        if (itemStack != null) {
          collect(itemStack, inventory);
          collect(itemStack, secondInventory);
        }
    }
  }

  private static void moveShift(ItemStack itemStack, Inventory inventory) {
    if (itemStack != null) {
      for (int value = 0; value < inventory.getSize(); value++) {
        ItemStack slotItem = inventory.getItem(value);
        if (slotItem != null
            && slotItem.isSimilar(itemStack)
            && slotItem.getAmount() < slotItem.getMaxStackSize()) {
          int freeSpace = slotItem.getMaxStackSize() - slotItem.getAmount();
          int moveAmount = Math.min(freeSpace, itemStack.getAmount());
          slotItem.setAmount(slotItem.getAmount() + moveAmount);
          itemStack.setAmount(itemStack.getAmount() - moveAmount);
          if (itemStack.getAmount() <= 0) {
            return;
          }
        }
      }

      for (int scanIndex = 0; scanIndex < inventory.getSize(); scanIndex++) {
        if (inventory.getItem(scanIndex) == null) {
          inventory.setItem(scanIndex, itemStack.clone());
          itemStack.setAmount(0);
          return;
        }
      }
    }
  }

  private static void collect(ItemStack itemStack, Inventory inventory) {
    for (int value = 0; value < inventory.getSize(); value++) {
      ItemStack slotItem = inventory.getItem(value);
      if (slotItem != null && slotItem.isSimilar(itemStack)) {
        int maxStack = itemStack.getMaxStackSize();
        int freeSpace = maxStack - itemStack.getAmount();
        int moveAmount = Math.min(freeSpace, slotItem.getAmount());
        itemStack.setAmount(itemStack.getAmount() + moveAmount);
        slotItem.setAmount(slotItem.getAmount() - moveAmount);
        if (slotItem.getAmount() <= 0) {
          inventory.setItem(value, null);
        }

        if (itemStack.getAmount() >= maxStack) {
          return;
        }
      }
    }
  }

  private static Inventory cloneInventory(Inventory inventory) {
    Inventory copy = Bukkit.createInventory(null, inventory.getSize());
    copy.setContents((ItemStack[]) inventory.getContents().clone());
    return copy;
  }

  private static ItemStack cloneOrNull(ItemStack itemStack) {
    return itemStack == null ? null : itemStack.clone();
  }
}
