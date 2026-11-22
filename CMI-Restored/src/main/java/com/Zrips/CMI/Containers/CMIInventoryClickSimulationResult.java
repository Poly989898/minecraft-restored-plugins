package com.Zrips.CMI.Containers;

import org.bukkit.inventory.Inventory;

public class CMIInventoryClickSimulationResult {
  private final Inventory top;
  private final Inventory bottom;

  public CMIInventoryClickSimulationResult(Inventory inventory, Inventory bottomInventory) {
    this.top = inventory;
    this.bottom = bottomInventory;
  }

  public Inventory getTop() {
    return this.top;
  }

  public Inventory getBottom() {
    return this.bottom;
  }
}
