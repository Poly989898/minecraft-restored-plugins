package com.Zrips.CMI.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIAnvilItemRepairEvent extends CMIPlayerCancellableEvent {
  private ItemStack itemFrom;
  private ItemStack itemTo;
  private int repairCost;

  public CMIAnvilItemRepairEvent(
      Player player, ItemStack itemStack, ItemStack resultItem, int value) {
    super(player);
    this.itemFrom = itemStack;
    this.itemTo = resultItem;
    this.repairCost = value;
  }

  public ItemStack getItemFrom() {
    return this.itemFrom;
  }

  public ItemStack getItemTo() {
    return this.itemTo;
  }

  public void setItemTo(ItemStack itemStack) {
    this.itemTo = itemStack;
  }

  public int getRepairCost() {
    return this.repairCost;
  }
}
