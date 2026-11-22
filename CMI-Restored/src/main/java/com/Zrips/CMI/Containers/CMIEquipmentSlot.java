package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Version.Version;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public enum CMIEquipmentSlot {
  NONE,
  CHEST,
  FEET,
  HAND,
  HEAD,
  LEGS,
  OFF_HAND;

  public static CMIEquipmentSlot get(String string) {
    CMIEquipmentSlot[] cmiEquipmentSlots;
    int value = (cmiEquipmentSlots = values()).length;

    for (int index = 0; index < value; index++) {
      CMIEquipmentSlot cmiEquipmentSlot = cmiEquipmentSlots[index];
      if (cmiEquipmentSlot.toString().equalsIgnoreCase(string)) {
        return cmiEquipmentSlot;
      }
    }

    return null;
  }

  public static CMIEquipmentSlot get(EquipmentSlot equipmentSlot) {
    return get(equipmentSlot.toString());
  }

  public static CMIEquipmentSlot get(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    return !Version.isCurrentEqualOrLower(Version.v1_8_R3)
            && playerInteractAtEntityEvent.getHand() != null
        ? get(playerInteractAtEntityEvent.getHand().toString())
        : HAND;
  }

  public static CMIEquipmentSlot get(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction().equals(Action.PHYSICAL)) {
      return NONE;
    } else {
      return !Version.isCurrentEqualOrLower(Version.v1_8_R3)
              && playerInteractEvent.getHand() != null
          ? get(playerInteractEvent.getHand().toString())
          : HAND;
    }
  }
}
