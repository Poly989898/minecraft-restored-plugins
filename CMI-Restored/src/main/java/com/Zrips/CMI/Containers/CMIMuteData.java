package com.Zrips.CMI.Containers;

import java.util.HashMap;
import java.util.UUID;

public class CMIMuteData {
  private long mutedUntil = 0L;
  private long shadowMutedUntil = 0L;
  private String mutedReason = null;
  private static HashMap<UUID, CMIMuteData> muted = new HashMap<>();

  public long getMutedUntil() {
    return this.mutedUntil;
  }

  public CMIMuteData setMutedUntil(long longValue) {
    this.mutedUntil = longValue;
    return this;
  }

  public long getShadowMutedUntil() {
    return this.shadowMutedUntil;
  }

  public CMIMuteData setShadowMutedUntil(long longValue) {
    this.shadowMutedUntil = longValue;
    return this;
  }

  public String getMutedReason() {
    return this.mutedReason;
  }

  public CMIMuteData setMutedReason(String string) {
    this.mutedReason = string;
    return this;
  }

  public static String getMutedReason(UUID uuid) {
    if (!isMuted(uuid)) {
      return null;
    }

    CMIMuteData cmiMuteData = muted.get(uuid);
    return cmiMuteData == null ? null : cmiMuteData.getMutedReason();
  }

  public static void setMutedReason(UUID uuid, String string) {
    muted.computeIfAbsent(uuid, playerUuid -> new CMIMuteData()).setMutedReason(string);
  }

  public static Long getMutedUntil(UUID uuid) {
    CMIMuteData cmiMuteData = muted.get(uuid);
    return cmiMuteData == null ? 0L : cmiMuteData.getMutedUntil();
  }

  public static boolean isMuted(UUID uuid) {
    CMIMuteData cmiMuteData = muted.get(uuid);
    if (cmiMuteData == null) {
      return false;
    } else {
      return cmiMuteData.getShadowMutedUntil() > System.currentTimeMillis()
          ? true
          : cmiMuteData.getMutedUntil() > System.currentTimeMillis();
    }
  }

  public static void setMutedUntil(UUID uuid, long longValue) {
    muted.computeIfAbsent(uuid, playerUuid -> new CMIMuteData()).setMutedUntil(longValue);
  }

  public static long getShadowMutedUntil(UUID uuid) {
    CMIMuteData cmiMuteData = muted.get(uuid);
    return cmiMuteData == null ? 0L : cmiMuteData.getShadowMutedUntil();
  }

  public static void setShadowMutedUntil(UUID uuid, long longValue) {
    muted.computeIfAbsent(uuid, playerUuid -> new CMIMuteData()).setShadowMutedUntil(longValue);
  }

  public static boolean isShadowMuted(UUID uuid) {
    CMIMuteData cmiMuteData = muted.get(uuid);
    return cmiMuteData == null
        ? false
        : cmiMuteData.getShadowMutedUntil() > System.currentTimeMillis();
  }
}
