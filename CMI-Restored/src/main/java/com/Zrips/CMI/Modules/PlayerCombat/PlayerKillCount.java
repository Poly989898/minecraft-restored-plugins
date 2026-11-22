package com.Zrips.CMI.Modules.PlayerCombat;

import java.util.UUID;

public class PlayerKillCount {
  UUID uuid = null;
  private long lastKill = 0L;
  private int kills = 0;
  private int headDropCount = 0;

  public PlayerKillCount(UUID newUuid) {
    this.uuid = newUuid;
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

  public void setLastKill(Long lastKillTime) {
    this.lastKill = lastKillTime == null ? 0L : lastKillTime;
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
