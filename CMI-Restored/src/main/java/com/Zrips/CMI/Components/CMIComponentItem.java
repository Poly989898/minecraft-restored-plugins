package com.Zrips.CMI.Components;

import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public class CMIComponentItem extends CMIComponentRoot {
  private ItemStack item = null;

  public CMIComponentItem() {}

  public CMIComponentItem(String string, @Nonnull ItemStack itemStack) {
    this.setText(string);
    this.item = itemStack;
  }

  public CMIComponentItem setAction(CMIComponentAction cmiComponentAction) {
    this.action = cmiComponentAction;
    return this;
  }

  public ItemStack getItem() {
    return this.item;
  }
}
