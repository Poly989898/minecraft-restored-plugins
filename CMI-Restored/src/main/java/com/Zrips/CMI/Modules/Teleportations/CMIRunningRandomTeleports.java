package com.Zrips.CMI.Modules.Teleportations;

public class CMIRunningRandomTeleports {
  private long time = 0L;

  public CMIRunningRandomTeleports() {
    this.time = System.currentTimeMillis() + 10000L;
  }

  public long getEndTime() {
    return this.time;
  }

  public CMIRunningRandomTeleports setTime(long longValue) {
    this.time = longValue;
    return this;
  }
}
