package com.Zrips.CMI.Containers;

import org.bukkit.inventory.ItemStack;

public class BasicRecipe {
  private ItemStack result;
  private ItemStack input;

  public BasicRecipe(ItemStack itemStack, ItemStack inputItem) {
    this.result = itemStack;
    this.input = inputItem;
  }

  public ItemStack getResult() {
    return this.result.clone();
  }

  public ItemStack getInput() {
    return this.input.clone();
  }
}
