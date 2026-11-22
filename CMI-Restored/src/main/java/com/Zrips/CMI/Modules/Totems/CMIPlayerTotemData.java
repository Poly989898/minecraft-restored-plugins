package com.Zrips.CMI.Modules.Totems;

import java.util.HashMap;
import java.util.UUID;

public class CMIPlayerTotemData {
  private long totemCooldown = 0L;
  private long totemWarmup = 0L;
  private static HashMap<UUID, CMIPlayerTotemData> data = new HashMap<>();

  public long getTotemCooldown() {
    return this.totemCooldown;
  }

  public CMIPlayerTotemData setTotemCooldown(long longValue) {
    this.totemCooldown = longValue;
    return this;
  }

  public long getTotemWarmup() {
    return this.totemWarmup;
  }

  public CMIPlayerTotemData setTotemWarmup(long longValue) {
    this.totemWarmup = longValue;
    return this;
  }

  public static void removeData(UUID uuid) {
    data.remove(uuid);
  }

  public static long getTotemCooldown(UUID uuid) {
    CMIPlayerTotemData cmiPlayerTotemData = data.get(uuid);
    return cmiPlayerTotemData == null ? 0L : cmiPlayerTotemData.getTotemCooldown();
  }

  public static void setTotemCooldown(UUID uuid, long longValue) {
    CMIPlayerTotemData cmiPlayerTotemData =
        data.computeIfAbsent(uuid, playerUuid -> new CMIPlayerTotemData());
    cmiPlayerTotemData.setTotemCooldown(longValue);
    if (cmiPlayerTotemData.getTotemWarmup() == 0L && cmiPlayerTotemData.getTotemCooldown() == 0L) {
      removeData(uuid);
    }
  }

  public static long getTotemWarmup(UUID uuid) {
    CMIPlayerTotemData cmiPlayerTotemData = data.get(uuid);
    return cmiPlayerTotemData == null ? 0L : cmiPlayerTotemData.getTotemWarmup();
  }

  public static void setTotemWarmup(UUID uuid, long longValue) {
    CMIPlayerTotemData cmiPlayerTotemData =
        data.computeIfAbsent(uuid, playerUuid -> new CMIPlayerTotemData());
    cmiPlayerTotemData.setTotemWarmup(longValue);
    if (cmiPlayerTotemData.getTotemWarmup() == 0L && cmiPlayerTotemData.getTotemCooldown() == 0L) {
      removeData(uuid);
    }
  }
}
