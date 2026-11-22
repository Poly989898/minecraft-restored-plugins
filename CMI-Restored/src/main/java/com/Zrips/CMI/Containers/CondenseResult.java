package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class CondenseResult {
  private int iAmount = 0;
  private int rAmount = 0;
  private List<ItemStack> items = new ArrayList<>();

  public CondenseResult() {}

  public CondenseResult(int value, int rAmountValue) {
    this.iAmount = value;
    this.rAmount = rAmountValue;
  }

  public int getImputAmount() {
    return this.iAmount;
  }

  public void addImputAmount(int value) {
    this.iAmount += value;
  }

  public int getResultAmount() {
    return this.rAmount;
  }

  public void addResultAmount(int value) {
    this.rAmount += value;
  }

  public List<ItemStack> getToAdd() {
    return this.items;
  }

  public void addToAdd(ItemStack itemStack) {
    this.items.add(itemStack);
  }
}
