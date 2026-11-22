package com.Zrips.CMI.Modules.ShulkerBoxInventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShulkerInv {
  private Player player;
  private ItemStack shulkerBox;
  private Inventory customInv;
  private int sourceId = -1;

  public ShulkerInv(Player newPlayer, Inventory inventory) {
    this.player = newPlayer;
    this.customInv = inventory;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public Inventory getCustomInv() {
    return this.customInv;
  }

  public void setCustomInv(Inventory inventory) {
    this.customInv = inventory;
  }

  public int getSourceId() {
    return this.sourceId;
  }

  public void setSourceId(int value) {
    this.sourceId = value;
  }

  public ItemStack getShulkerBox() {
    return this.shulkerBox;
  }

  public void setShulkerBox(ItemStack itemStack) {
    this.shulkerBox = itemStack;
  }
}
