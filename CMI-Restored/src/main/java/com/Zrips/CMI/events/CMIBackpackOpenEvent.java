package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.ShulkerBoxInventory.ShulkerInv;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CMIBackpackOpenEvent extends CMIPlayerCancellableEvent {
  private ShulkerInv shulkerbackpack;
  private double costToOpen = 0.0;

  public CMIBackpackOpenEvent(ShulkerInv shulkerInv) {
    super(shulkerInv.getPlayer());
    this.shulkerbackpack = shulkerInv;
  }

  public CMIBackpackOpenEvent(ShulkerInv shulkerInv, double number) {
    super(shulkerInv.getPlayer());
    this.shulkerbackpack = shulkerInv;
    this.costToOpen = number;
  }

  public ItemStack getShulkerBox() {
    return this.shulkerbackpack.getShulkerBox();
  }

  public Inventory getGuiInv() {
    return this.shulkerbackpack.getCustomInv();
  }

  public ShulkerInv getShulkerbackpack() {
    return this.shulkerbackpack;
  }

  public double getCostToOpen() {
    return this.costToOpen;
  }

  public void setCostToOpen(double number) {
    this.costToOpen = number;
  }
}
