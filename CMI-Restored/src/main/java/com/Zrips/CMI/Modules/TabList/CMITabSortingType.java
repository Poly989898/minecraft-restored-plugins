package com.Zrips.CMI.Modules.TabList;

import net.Zrips.CMILib.Locale.LC;

public enum CMITabSortingType {
  Weight,
  Group,
  Name,
  NickName,
  Balance,
  World;

  public static CMITabSortingType getByName(String string) {
    CMITabSortingType[] cmiTabSortingTypes;
    int value = (cmiTabSortingTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMITabSortingType cmiTabSortingType = cmiTabSortingTypes[index];
      if (cmiTabSortingType.toString().equalsIgnoreCase(string)) {
        return cmiTabSortingType;
      }
    }

    return null;
  }

  public static String toStringList() {
    StringBuilder stringBuilder = new StringBuilder();
    CMITabSortingType[] cmiTabSortingTypes;
    int value = (cmiTabSortingTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMITabSortingType cmiTabSortingType = cmiTabSortingTypes[index];
      stringBuilder.append(cmiTabSortingType.toString()).append(LC.info_ListSpliter);
    }

    return stringBuilder.toString();
  }
}
