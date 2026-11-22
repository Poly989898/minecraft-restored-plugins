package com.Zrips.CMI.Containers;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SilentChest {
  private Player player;
  private Inventory inv;
  private Inventory sourceInventory;
  private Location loc;
  private boolean editing = true;
  private CompletableFuture<Void> task = null;

  public SilentChest(Player newPlayer, Inventory inventory, Location location) {
    this.player = newPlayer;
    this.inv = inventory;
    this.loc = location;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Inventory getInv() {
    return this.inv;
  }

  public Location getLocation() {
    return this.loc;
  }

  public void setEditing(boolean flag) {
    this.editing = flag;
  }

  public boolean isEditing() {
    return this.editing;
  }

  public Inventory getSourceInventory() {
    return this.sourceInventory;
  }

  public void setSourceInventory(Inventory inventory) {
    this.sourceInventory = inventory;
  }

  public CompletableFuture<Void> getTask() {
    return this.task;
  }

  public void setTask(CompletableFuture<Void> completableFuture) {
    this.task = completableFuture;
  }
}
