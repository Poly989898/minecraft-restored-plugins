package com.Zrips.CMI.Modules.SavedInv;

import com.Zrips.CMI.Containers.CMIUser;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SavedInventories {
  private CMIUser owner;
  private Map<Integer, CMIInventory> inventories =
      Collections.synchronizedMap(new LinkedHashMap<>());

  public SavedInventories(CMIUser cmiUser) {
    this.owner = cmiUser;
  }

  public synchronized void addInventory(CMIInventory cmiInventory) {
    this.inventories.put(cmiInventory.getId(), cmiInventory);
  }

  public synchronized CMIInventory removeInventory(int value) {
    return this.inventories.remove(value);
  }

  public synchronized void removeAllInventories() {
    this.inventories.clear();
  }

  public synchronized CMIInventory removeInventory(CMIInventory cmiInventory) {
    return this.inventories.remove(cmiInventory.getId());
  }

  public CMIUser getOwner() {
    return this.owner;
  }

  public synchronized int getSavedInventoryCount() {
    return this.inventories.size();
  }

  public void setOwner(CMIUser cmiUser) {
    this.owner = cmiUser;
  }

  private synchronized int getLastId() {
    int value = 0;

    for (var entry : this.inventories.entrySet()) {
      if ((Integer) entry.getKey() > value) {
        value = (Integer) entry.getKey();
      }
    }

    return value;
  }

  public int getNextId() {
    return this.getLastId() + 1;
  }

  public synchronized CMIInventory getInventory(int value) {
    return this.inventories.get(value);
  }

  public synchronized LinkedHashMap<Integer, CMIInventory> getInventories() {
    return new LinkedHashMap<>(this.inventories);
  }

  public synchronized CMIInventory getFirstInv() {
    return this.inventories != null && !this.inventories.isEmpty()
        ? this.inventories.values().iterator().next()
        : null;
  }

  public synchronized CMIInventory getLastInv() {
    if (this.inventories != null && !this.inventories.isEmpty()) {
      int value = 0;

      for (var entry : this.inventories.entrySet()) {
        if ((Integer) entry.getKey() > value) {
          value = (Integer) entry.getKey();
        }
      }

      return this.getInventory(value);
    } else {
      return null;
    }
  }

  public synchronized CMIInventory getNextInv(int value) {
    if (this.inventories == null) {
      return null;
    }

    int invId = 0;
    boolean flag = false;

    for (var entry : this.inventories.entrySet()) {
      if (invId == 0) {
        invId = (Integer) entry.getKey();
      }

      if (flag) {
        value = (Integer) entry.getKey();
        flag = false;
        break;
      }

      if ((Integer) entry.getKey() == value) {
        flag = true;
      }
    }

    return flag ? this.inventories.get(invId) : this.inventories.get(value);
  }

  public synchronized CMIInventory getPrevInv(int value) {
    if (this.inventories == null) {
      return null;
    }

    int invId = 0;

    for (var entry : this.inventories.entrySet()) {
      if ((Integer) entry.getKey() == value) {
        break;
      }

      invId = (Integer) entry.getKey();
    }

    return invId == 0 ? this.getLastInv() : this.inventories.get(invId);
  }
}
