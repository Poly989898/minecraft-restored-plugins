package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CMIPermissionPlayerData {
  private String nameColor;
  private String prefix;
  private String suffix;
  private String group;
  HashMap<UserDataUpdateType, Long> lastInfoUpdate = new HashMap<>();
  private static ConcurrentHashMap<UUID, CMIPermissionPlayerData> data = new ConcurrentHashMap<>();

  public static void clearCache(UUID uuid) {
    data.remove(uuid);
  }

  private static boolean timeToUpdate(CMIUser cmiUser, UserDataUpdateType userDataUpdateType) {
    CMIPermissionPlayerData cmiPermissionPlayerData = data.get(cmiUser.getUniqueId());
    if (cmiPermissionPlayerData == null) {
      return true;
    }

    Long lastUpdate = cmiPermissionPlayerData.lastInfoUpdate.get(userDataUpdateType);
    if (lastUpdate != null && lastUpdate + 1000L >= System.currentTimeMillis()) {
      return false;
    }

    cmiPermissionPlayerData.lastInfoUpdate.put(userDataUpdateType, System.currentTimeMillis());
    return true;
  }

  public String getNameColor() {
    return this.nameColor;
  }

  public CMIPermissionPlayerData setNameColor(String string) {
    this.nameColor = string;
    return this;
  }

  public String getPrefix() {
    return this.prefix == null ? "" : this.prefix;
  }

  public CMIPermissionPlayerData setPrefix(String string) {
    this.prefix = string;
    return this;
  }

  public String getSuffix() {
    return this.suffix == null ? "" : this.suffix;
  }

  public CMIPermissionPlayerData setSuffix(String string) {
    this.suffix = string;
    return this;
  }

  public String getGroup() {
    return this.group == null ? "" : this.group;
  }

  public CMIPermissionPlayerData setGroup(String string) {
    this.group = string;
    return this;
  }

  public static synchronized void updatePrefix(CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      String string = CMI.getInstance().getPermissionsManager().getPrefix(cmiUser.getPlayer(false));
      data.computeIfAbsent(cmiUser.getUniqueId(), uuid -> new CMIPermissionPlayerData())
          .setPrefix(string);
    }
  }

  public static synchronized void updateSuffix(CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      String string = CMI.getInstance().getPermissionsManager().getSufix(cmiUser.getPlayer(false));
      data.computeIfAbsent(cmiUser.getUniqueId(), uuid -> new CMIPermissionPlayerData())
          .setSuffix(string);
    }
  }

  public static void updateGroup(CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      String string =
          CMI.getInstance().getPermissionsManager().getMainGroup(cmiUser.getPlayer(false));
      data.computeIfAbsent(cmiUser.getUniqueId(), uuid -> new CMIPermissionPlayerData())
          .setGroup(string);
    }
  }

  public static String getGroupName(CMIUser cmiUser) {
    if (cmiUser.isOnline() && timeToUpdate(cmiUser, UserDataUpdateType.group)) {
      updateGroup(cmiUser);
    }

    CMIPermissionPlayerData cmiPermissionPlayerData = data.get(cmiUser.getUniqueId());
    return cmiPermissionPlayerData == null ? "" : cmiPermissionPlayerData.getGroup();
  }

  public static String getNameColor(CMIUser cmiUser) {
    if (cmiUser.isOnline() && timeToUpdate(cmiUser, UserDataUpdateType.nameColor)) {
      String string =
          CMI.getInstance().getPermissionsManager().getNameColor(cmiUser.getPlayer(false));
      data.computeIfAbsent(cmiUser.getUniqueId(), uuid -> new CMIPermissionPlayerData())
          .setNameColor(string);
    }

    CMIPermissionPlayerData cmiPermissionPlayerData = data.get(cmiUser.getUniqueId());
    return cmiPermissionPlayerData == null ? "" : cmiPermissionPlayerData.getNameColor();
  }

  public static String getPrefix(CMIUser cmiUser) {
    if (timeToUpdate(cmiUser, UserDataUpdateType.prefix)) {
      updatePrefix(cmiUser);
    }

    CMIPermissionPlayerData cmiPermissionPlayerData = data.get(cmiUser.getUniqueId());
    return cmiPermissionPlayerData == null ? "" : cmiPermissionPlayerData.getPrefix();
  }

  public static String getSuffix(CMIUser cmiUser) {
    if (timeToUpdate(cmiUser, UserDataUpdateType.suffix)) {
      updateSuffix(cmiUser);
    }

    CMIPermissionPlayerData cmiPermissionPlayerData = data.get(cmiUser.getUniqueId());
    return cmiPermissionPlayerData == null ? "" : cmiPermissionPlayerData.getSuffix();
  }
}
