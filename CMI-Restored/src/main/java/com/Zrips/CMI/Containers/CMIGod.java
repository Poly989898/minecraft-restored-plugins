package com.Zrips.CMI.Containers;

import org.bukkit.entity.Player;

public class CMIGod {
  public static boolean applyNoDamageTick(Player player, int value) {
    if (player == null) {
      return false;
    }

    if (player.getNoDamageTicks() > value) {
      return false;
    }

    player.setNoDamageTicks(value);
    return true;
  }

  public static boolean forceApplyNoDamageTick(Player player, int value) {
    if (player == null) {
      return false;
    }

    player.setNoDamageTicks(value);
    return true;
  }
}
