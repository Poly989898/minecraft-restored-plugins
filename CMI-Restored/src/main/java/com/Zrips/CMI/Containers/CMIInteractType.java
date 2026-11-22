package com.Zrips.CMI.Containers;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public enum CMIInteractType {
  LeftClick,
  RightClick,
  LeftShiftClick,
  RightShiftClick;

  public static CMIInteractType getByName(String string) {
    if (string == null) {
      return null;
    }

    if (!string.toLowerCase().endsWith("click")) {
      string = string + "click";
    }

    CMIInteractType[] cmiInteractTypes;
    int value = (cmiInteractTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMIInteractType cmiInteractType = cmiInteractTypes[index];
      if (cmiInteractType.toString().equalsIgnoreCase(string)) {
        return cmiInteractType;
      }
    }

    return null;
  }

  public static CMIInteractType getFromAction(Action action, Player player) {
    CMIInteractType cmiInteractType = LeftClick;
    switch (action) {
      case LEFT_CLICK_BLOCK:
      case LEFT_CLICK_AIR:
        if (player.isSneaking()) {
          cmiInteractType = LeftShiftClick;
        } else {
          cmiInteractType = LeftClick;
        }
        break;
      case RIGHT_CLICK_BLOCK:
      case RIGHT_CLICK_AIR:
        if (player.isSneaking()) {
          cmiInteractType = RightShiftClick;
        } else {
          cmiInteractType = RightClick;
        }
    }

    return cmiInteractType;
  }
}
