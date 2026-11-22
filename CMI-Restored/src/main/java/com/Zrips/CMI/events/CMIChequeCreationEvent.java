package com.Zrips.CMI.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIChequeCreationEvent extends CMIPlayerCancellableEvent {
  private ItemStack cheque;
  private double price;

  public CMIChequeCreationEvent(Player player, ItemStack itemStack, double number) {
    super(player);
    this.cheque = itemStack;
    this.price = number;
  }

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double number) {
    this.price = number;
  }

  public ItemStack getCheque() {
    return this.cheque;
  }

  public void setCheque(ItemStack itemStack) {
    this.cheque = itemStack;
  }
}
