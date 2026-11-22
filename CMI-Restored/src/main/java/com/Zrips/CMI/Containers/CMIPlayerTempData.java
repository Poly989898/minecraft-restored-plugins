package com.Zrips.CMI.Containers;

import com.Zrips.CMI.Modules.CmdCooldown.CmdCooldown;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CMIPlayerTempData {
  private CmdCooldown commandCooldown = new CmdCooldown();
  private String bungeeBackLocation;
  private long lastPatrol = 0L;
  private long playTimeOptimized = 0L;
  private long afkImunity = 0L;
  private long lastRespawn = 0L;
  private long backExpiration = 0L;
  private long deathBackExpiration = 0L;
  private HashMap<UserDataUpdateType, Long> lastInfoUpdate = new HashMap<>();
  private static ConcurrentHashMap<UUID, CMIPlayerTempData> playerTempData =
      new ConcurrentHashMap<>();

  public static void clearOnLogOff(UUID uuid) {
    CMIPlayerTempData cmiPlayerTempData = playerTempData.get(uuid);
    if (cmiPlayerTempData != null) {
      cmiPlayerTempData.setLastInfoUpdate(null);
    }
  }

  public CmdCooldown getCommandCooldown() {
    return this.commandCooldown;
  }

  public void setCommandCooldown(CmdCooldown cmdCooldown) {
    this.commandCooldown = cmdCooldown;
  }

  public String getBungeeBackLocation() {
    return this.bungeeBackLocation;
  }

  public void setBungeeBackLocation(String string) {
    this.bungeeBackLocation = string;
  }

  public long getLastPatrol() {
    return this.lastPatrol;
  }

  public void setLastPatrol(long longValue) {
    this.lastPatrol = longValue;
  }

  public long getPlayTimeOptimized() {
    return this.playTimeOptimized;
  }

  public void setPlayTimeOptimized(long longValue) {
    this.playTimeOptimized = longValue;
  }

  public long getAfkImunity() {
    return this.afkImunity;
  }

  public void setAfkImunity(long longValue) {
    this.afkImunity = longValue;
  }

  public long getLastRespawn() {
    return this.lastRespawn;
  }

  public void setLastRespawn(long longValue) {
    this.lastRespawn = longValue;
  }

  public HashMap<UserDataUpdateType, Long> getLastInfoUpdate() {
    return this.lastInfoUpdate;
  }

  public void setLastInfoUpdate(HashMap<UserDataUpdateType, Long> hashMap) {
    this.lastInfoUpdate = hashMap;
  }

  public static CMIPlayerTempData get(UUID uuid) {
    return playerTempData.get(uuid);
  }

  public static CMIPlayerTempData computeIfAbsent(UUID uuid) {
    return playerTempData.computeIfAbsent(uuid, playerUuid -> new CMIPlayerTempData());
  }

  public long getBackExpiration() {
    return this.backExpiration;
  }

  public void setBackExpiration(long longValue) {
    this.backExpiration = longValue;
  }

  public long getDeathBackExpiration() {
    return this.deathBackExpiration;
  }

  public void setDeathBackExpiration(long longValue) {
    this.deathBackExpiration = longValue;
  }
}
