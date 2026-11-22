package com.Zrips.CMI.Containers;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class CMIItemDonations {
  private UUID donor;
  private UUID receiver;
  private ItemStack item;
  private long time = 0L;
  private static HashMap<UUID, HashMap<UUID, CMIItemDonations>> donationsCache = new HashMap<>();

  public static void addItemDonation(CMIItemDonations cmiItemDonations) {
    var hashMap =
        donationsCache.computeIfAbsent(cmiItemDonations.getReceiver(), uuid -> new HashMap<>());
    hashMap.put(cmiItemDonations.getDonor(), cmiItemDonations);
  }

  public static HashMap<UUID, CMIItemDonations> getPendingDonations(UUID uuid) {
    return donationsCache.getOrDefault(uuid, new HashMap<>());
  }

  public UUID getDonor() {
    return this.donor;
  }

  public void setDonor(UUID uuid) {
    this.donor = uuid;
  }

  public UUID getReceiver() {
    return this.receiver;
  }

  public void setReceiver(UUID uuid) {
    this.receiver = uuid;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public void setItem(ItemStack itemStack) {
    this.item = itemStack;
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long newTime) {
    this.time = newTime;
  }
}
