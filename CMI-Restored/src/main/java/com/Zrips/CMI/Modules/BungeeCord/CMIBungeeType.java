package com.Zrips.CMI.Modules.BungeeCord;

public enum CMIBungeeType {
  BungeeCord,
  Velocity,
  Unknown;

  static CMIBungeeType getByName(String string) {
    CMIBungeeType[] cmiBungeeTypes;
    int value = (cmiBungeeTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMIBungeeType cmiBungeeType = cmiBungeeTypes[index];
      if (cmiBungeeType.toString().equalsIgnoreCase(string)) {
        return cmiBungeeType;
      }
    }

    return Unknown;
  }
}
