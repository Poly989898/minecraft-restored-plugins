package com.Zrips.CMI.Modules.PlayTimeRewards;

public class PlaytimeClaimCache {
  private long lastUpdate = 0L;
  private int count = 0;

  public long getLastUpdate() {
    return this.lastUpdate;
  }

  public void setLastUpdate(long longValue) {
    this.lastUpdate = longValue;
  }

  public int getCount() {
    return this.count;
  }

  public void setCount(int value) {
    this.count = value;
  }
}
