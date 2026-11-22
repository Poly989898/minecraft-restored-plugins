package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Kits.Kit;

public class CMIKitUsage {
  private String kitName;
  private long lastUsage = 0L;
  private int usedTimes = 0;

  public CMIKitUsage(Kit kit) {
    this(kit, System.currentTimeMillis(), 0);
  }

  public CMIKitUsage(String string, Long usageTime) {
    this(string, usageTime, 0);
  }

  public CMIKitUsage(Kit kit, Long usageTime) {
    this(kit, usageTime, 0);
  }

  public CMIKitUsage(Kit kit, Long usageTime, int value) {
    this(kit.getConfigName(), usageTime, value);
  }

  public CMIKitUsage(String string, Long usageTime, int value) {
    this.kitName = string;
    this.lastUsage = usageTime == null ? 0L : usageTime;
    this.usedTimes = value;
  }

  public Long getLastUsage() {
    return this.lastUsage;
  }

  public void setLastUsage(Long usageTime) {
    this.lastUsage = usageTime == null ? 0L : usageTime;
  }

  public void addUsedTimes() {
    this.usedTimes++;
  }

  public int getUsedTimes() {
    return this.usedTimes;
  }

  public void setUsedTimes(int value) {
    this.usedTimes = value;
  }

  public String getKitName() {
    return this.kitName;
  }

  public Kit getKit() {
    return CMI.getInstance().getKitsManager().getKit(this.kitName);
  }
}
