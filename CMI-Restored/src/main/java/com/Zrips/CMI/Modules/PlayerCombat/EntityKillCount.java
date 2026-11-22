package com.Zrips.CMI.Modules.PlayerCombat;

import org.bukkit.entity.EntityType;

public class EntityKillCount {
  EntityType type = null;
  private long lastKill = 0L;
  private int kills = 0;
  private int headDropCount = 0;

  public EntityKillCount(EntityType entityType) {
    this.type = entityType;
  }

  public int addKill() {
    this.lastKill = System.currentTimeMillis();
    return this.kills++;
  }

  public int getKills() {
    return this.kills;
  }

  public void setKills(int value) {
    this.kills = value;
  }

  public Long getLastKill() {
    return this.lastKill;
  }

  public void setLastKill(Long newLastKill) {
    this.lastKill = newLastKill == null ? 0L : newLastKill;
  }

  public int getHeadDropCount() {
    return this.headDropCount;
  }

  public void setHeadDropCount(int value) {
    this.headDropCount = value;
  }

  public void addHeadDropCount() {
    this.headDropCount++;
  }
}
