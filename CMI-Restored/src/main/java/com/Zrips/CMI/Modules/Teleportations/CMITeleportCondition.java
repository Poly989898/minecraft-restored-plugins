package com.Zrips.CMI.Modules.Teleportations;

public enum CMITeleportCondition {
  Good,
  BadFloor,
  Void,
  Suffocation,
  Fall,
  Unknown,
  Plugin,
  UnsafeTeleportation,
  NoPermission,
  MissingWorld;

  public static String getBadLocations() {
    String string = "";
    CMITeleportCondition[] cmiTeleportConditions;
    int value = (cmiTeleportConditions = values()).length;

    for (int index = 0; index < value; index++) {
      CMITeleportCondition cmiTeleportCondition = cmiTeleportConditions[index];
      if (!cmiTeleportCondition.equals(Good)) {
        if (!string.isEmpty()) {
          string = string + "/";
        }

        string = string + cmiTeleportCondition.name();
      }
    }

    return string;
  }
}
