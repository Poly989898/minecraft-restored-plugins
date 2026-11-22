package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;

public class CMIExperience {
  public static long getXpToLevelUpFrom(int value) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      return value <= 16 ? 17L : (value <= 31 ? 3 * value - 31 : 7L * value - 155L);
    } else {
      return value <= 15 ? 2 * value + 7 : (value <= 30 ? 5 * value - 38 : 9L * value - 158L);
    }
  }

  public static long getTotalExpToLevel(int value) {
    return Version.isCurrentLower(Version.v1_8_R1)
        ? (value <= 16
            ? 17 * value
            : (value <= 31
                ? 3 * value * value / 2 - 59 * value / 2 + 360
                : 7 * value * value / 2 - 303 * value / 2 + 2220))
        : (long)
            (value <= 16
                ? value * value + 6 * value
                : (value <= 31
                    ? 2.5 * value * value - 40.5 * value + 360.0
                    : 4.5 * value * value - 162.5 * value + 2220.0));
  }

  public static void setLevel(Player player, int value) {
    if (player != null) {
      player.setLevel(0);
      player.setExp(0.0F);
      player.setTotalExperience(0);
      player.setLevel(value);
    }
  }

  public static int levelFromExp(long longValue) {
    return (int) Math.floor((Math.sqrt(72.0 * longValue - 54215.0) + 325.0) / 18.0);
  }

  public static void setExp(Player player, long longValue) {
    if (player != null) {
      longValue = (long) CMINumber.clamp(longValue, 0.0, Float.MAX_VALUE);
      if (!Version.isCurrentLower(Version.v1_8_R1) && longValue >= 2147483647L) {
        int value = levelFromExp(longValue);
        float floatValue = (float) (longValue - getTotalExpToLevel(value));
        float xpToLevelUp = (float) getXpToLevelUpFrom(value);
        player.setTotalExperience(0);
        player.setLevel(value);
        player.setExp((float) CMINumber.clamp(floatValue / xpToLevelUp, 0.0, 1));
      } else {
        player.setLevel(0);
        player.setExp(0.0F);
        player.setTotalExperience(0);
        player.giveExp((int) longValue);
      }
    }
  }

  public static void addExp(Player player, long longValue) {
    if (player != null) {
      setExp(player, getTotalExp(player) + longValue);
    }
  }

  public static void takeExp(Player player, long longValue) {
    if (player != null) {
      setExp(player, getTotalExp(player) - longValue);
    }
  }

  public static long getMissingExp(Player player) {
    return player == null
        ? 0L
        : getXpToLevelUpFrom(player.getLevel()) - getExpForCurrentLevel(player);
  }

  public static int getLevel(Player player) {
    return player == null ? 0 : player.getLevel();
  }

  public static long getExpForCurrentLevel(Player player) {
    return player == null ? 0L : getTotalExp(player) - getTotalExpToLevel(player.getLevel());
  }

  public static long getTotalExp(Player player) {
    return player == null
        ? 0L
        : getTotalExpToLevel(player.getLevel())
            + Math.round((float) getXpToLevelUpFrom(player.getLevel()) * player.getExp());
  }
}
