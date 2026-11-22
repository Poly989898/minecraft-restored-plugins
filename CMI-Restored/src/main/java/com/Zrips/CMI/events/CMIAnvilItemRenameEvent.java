package com.Zrips.CMI.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIAnvilItemRenameEvent extends CMIPlayerCancellableEvent {
  private ItemStack itemFrom;
  private ItemStack itemTo;
  private int cost;

  public CMIAnvilItemRenameEvent(
      Player player, ItemStack itemStack, ItemStack resultItem, int value) {
    super(player);
    this.itemFrom = itemStack;
    this.itemTo = resultItem;
    this.cost = value;
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

  public int getCost() {
    return this.cost;
  }
}
