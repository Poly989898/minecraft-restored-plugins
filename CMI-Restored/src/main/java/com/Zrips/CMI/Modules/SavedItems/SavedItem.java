package com.Zrips.CMI.Modules.SavedItems;

import org.bukkit.inventory.ItemStack;

public class SavedItem {
  private ItemStack item;
  private String name;
  private String category = null;

  public SavedItem(String string) {
    this.name = string;
  }

  public SavedItem(String string, ItemStack itemStack) {
    this.name = string;
    this.item = itemStack;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public void setItem(ItemStack itemStack) {
    this.item = itemStack;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public void reset() {
    this.item = null;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(String string) {
    this.category = string;
  }
}
