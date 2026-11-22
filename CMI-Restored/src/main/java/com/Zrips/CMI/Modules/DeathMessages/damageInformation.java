package com.Zrips.CMI.Modules.DeathMessages;

import com.Zrips.CMI.CMI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class damageInformation {
  private static HashMap<UUID, TreeMap<Long, damageInformation>> cache = new HashMap<>();
  private Entity ent;
  private double damage = 0.0;
  private ItemStack item;
  private DamageCause cause;
  private long time;
  private Block block;

  public static void cacheClear(UUID uuid) {
    cache.remove(uuid);
  }

  public static TreeMap<Long, damageInformation> getLastDamage(UUID uuid) {
    return cache.get(uuid);
  }

  public static TreeMap<Long, damageInformation> addLastDamage(
      UUID uuid, damageInformation damageInformation) {
    var treeMap =
        cache.computeIfAbsent(uuid, playerUuid -> new TreeMap<>(Collections.reverseOrder()));
    Iterator iterator = treeMap.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry entry = (Entry) iterator.next();
      if ((Long) entry.getKey()
              + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
          < System.currentTimeMillis()) {
        iterator.remove();
      }
    }

    treeMap.put(damageInformation.getTime(), damageInformation);
    return treeMap;
  }

  public static damageInformation getLastDamage(UUID uuid, damageInformation damageInformation) {
    var treeMap = getLastDamage(uuid);
    if (treeMap == null) {
      return null;
    }

    if (damageInformation == null) {
      return treeMap.isEmpty() ? null : (damageInformation) treeMap.firstEntry().getValue();
    }

    for (Entry entry : treeMap.entrySet()) {
      damageInformation otherInfo = (damageInformation) entry.getValue();
      if ((damageInformation.getBlock() != null || otherInfo.getBlock() != null)
              && (damageInformation.getBlock() == null
                  || otherInfo.getBlock() == null
                  || !damageInformation.getBlock().getType().equals(otherInfo.getBlock().getType()))
          || (damageInformation.getEnt() != null || otherInfo.getEnt() != null)
              && (damageInformation.getEnt() == null
                  || otherInfo.getEnt() == null
                  || !damageInformation.getEnt().getType().equals(otherInfo.getEnt().getType()))
          || (damageInformation.getItem() != null || otherInfo.getItem() != null)
              && (damageInformation.getItem() == null
                  || otherInfo.getItem() == null
                  || !damageInformation.getItem().getType().equals(otherInfo.getItem().getType()))
          || damageInformation.getCause() != otherInfo.getCause()) {
        return otherInfo;
      }
    }

    return null;
  }

  public void reset() {
    this.ent = null;
    this.damage = 0.0;
    this.item = null;
    this.cause = null;
    this.time = 0L;
  }

  public Entity getEnt() {
    return this.ent;
  }

  public void setEnt(Entity entity) {
    this.ent = entity;
  }

  public double getDamage() {
    return this.damage;
  }

  public void setDamage(double number) {
    this.damage = number;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public void setItem(ItemStack itemStack) {
    this.item = itemStack;
  }

  public DamageCause getCause() {
    return this.cause;
  }

  public void setCause(DamageCause damageCause) {
    this.cause = damageCause;
  }

  public Long getTime() {
    if (this.time == 0L) {
      this.time = System.currentTimeMillis();
    }

    return this.time;
  }

  public void setTime(Long newTime) {
    this.time = newTime == null ? 0L : newTime;
  }

  public void setTime() {
    this.time = System.currentTimeMillis();
  }

  public Block getBlock() {
    return this.block;
  }

  public void setBlock(Block newBlock) {
    this.block = newBlock;
  }
}
