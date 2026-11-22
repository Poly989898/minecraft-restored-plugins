package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIShulkerBox {
  @Nullable
  public static Inventory getShulkerInv(ItemStack itemStack) {
    if (!CMIMaterial.get(itemStack).isShulkerBox()) {
      return null;
    }

    try {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof BlockStateMeta)) {
        return null;
      }

      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      if (!(blockStateMeta.getBlockState() instanceof ShulkerBox)) {
        return null;
      }

      ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
      return shulkerBox.getInventory();
    } catch (Throwable throwable) {
      return null;
    }
  }

  @NotNull
  public static ItemStack setShulkerInv(ItemStack itemStack, ItemStack[] itemStacks) {
    if (!CMIMaterial.get(itemStack).isShulkerBox()) {
      return itemStack;
    }

    try {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof BlockStateMeta)) {
        return itemStack;
      }

      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      if (!(blockStateMeta.getBlockState() instanceof ShulkerBox)) {
        return itemStack;
      }

      ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
      shulkerBox.getInventory().setContents(itemStacks);
      blockStateMeta.setBlockState(shulkerBox);
      itemStack.setItemMeta(blockStateMeta);
      return itemStack;
    } catch (Throwable throwable) {
      return itemStack;
    }
  }

  public static boolean isShulkerBox(ItemStack itemStack) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      try {
        if (itemStack == null) {
          return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta instanceof BlockStateMeta && CMIMaterial.get(itemStack).isShulkerBox();
      } catch (Exception exception) {
        return false;
      }
    } else {
      try {
        if (itemStack == null) {
          return false;
        }

        ItemMeta shulkerItemMeta = itemStack.getItemMeta();
        if (shulkerItemMeta != null && shulkerItemMeta instanceof BlockStateMeta) {
          BlockStateMeta blockStateMeta = (BlockStateMeta) shulkerItemMeta;
          if (!blockStateMeta.hasBlockState()) {
            return false;
          } else {
            return blockStateMeta.getBlockState() == null
                ? false
                : blockStateMeta.getBlockState() instanceof ShulkerBox;
          }
        } else {
          return false;
        }
      } catch (Exception exception2) {
        return false;
      }
    }
  }

  public static List<ItemStack> getShulkerBoxContents(ItemStack itemStack) {
    ArrayList<ItemStack> arrayList = new ArrayList();
    Inventory inventory = getShulkerInv(itemStack);
    if (inventory == null) {
      return arrayList;
    }

    ItemStack[] itemStacks;
    int value = (itemStacks = inventory.getContents()).length;

    for (int index = 0; index < value; index++) {
      ItemStack stack = itemStacks[index];
      arrayList.add(stack);
    }

    return arrayList;
  }

  public static Inventory getShulkerInv(Block block) {
    if (CMIMaterial.get(block).isShulkerBox() && block.getState() instanceof ShulkerBox) {
      ShulkerBox shulkerBox = (ShulkerBox) block.getState();
      return shulkerBox.getInventory();
    } else {
      return null;
    }
  }
}
