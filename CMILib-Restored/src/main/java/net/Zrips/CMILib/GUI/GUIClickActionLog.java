package net.Zrips.CMILib.GUI;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class GUIClickActionLog {
  private int indication;
  private long time = 0L;
  private GUIManager.GUIClickType clickType;
  private Set<Integer> slots;
  private ItemStack holdingItem;
  private ItemStack currentItem;
  private Map<Integer, ItemStack> newItems;
  private ClickType bukkitClickType;
  private InventoryAction action;
  private InventoryType inv;
  private DragType dragtype;
  private boolean canceled;

  public GUIClickActionLog(boolean canceled) {
    this.canceled = canceled;
    this.time = System.currentTimeMillis();
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public GUIManager.GUIClickType getClickType() {
    return this.clickType;
  }

  public void setClickType(GUIManager.GUIClickType clickType) {
    this.clickType = clickType;
  }

  public Set<Integer> getSlot() {
    return this.slots;
  }

  public void addSlot(int slot) {
    if (this.slots == null) {
      this.slots = new HashSet<>();
    }

    this.slots.add(slot);
  }

  public void addSlots(Set<Integer> slots) {
    if (this.slots == null) {
      this.slots = new HashSet<>();
    }

    this.slots.addAll(slots);
  }

  public ItemStack getHoldingItem() {
    return this.holdingItem;
  }

  public void setHoldingItem(ItemStack holdingItem) {
    this.holdingItem = holdingItem;
  }

  public ClickType getBukkitClickType() {
    return this.bukkitClickType;
  }

  public void setBukkitClickType(ClickType bukkitClickType) {
    this.bukkitClickType = bukkitClickType;
  }

  public InventoryAction getAction() {
    return this.action;
  }

  public void setAction(InventoryAction action) {
    this.action = action;
  }

  public DragType getDragtype() {
    return this.dragtype;
  }

  public void setDragtype(DragType dragtype) {
    this.dragtype = dragtype;
  }

  public int getIndication() {
    return this.indication;
  }

  public void setIndication(int indication) {
    this.indication = indication;
  }

  public Map<Integer, ItemStack> getNewItems() {
    return this.newItems;
  }

  public void setNewItems(Map<Integer, ItemStack> newItems) {
    this.newItems = newItems;
  }

  public ItemStack getCurrentItem() {
    return this.currentItem;
  }

  public void setCurrentItem(ItemStack currentItem) {
    this.currentItem = currentItem;
  }

  public boolean isCanceled() {
    return this.canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  public InventoryType getInv() {
    return this.inv;
  }

  public void setInv(InventoryType inv) {
    this.inv = inv;
  }
}
