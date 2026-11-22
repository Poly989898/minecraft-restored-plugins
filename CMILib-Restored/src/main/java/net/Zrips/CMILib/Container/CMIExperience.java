package net.Zrips.CMILib.Container;

import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;

public class CMIExperience {
  public static long getXpToLevelUpFrom(int level) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      return level <= 16 ? 17L : (level <= 31 ? 3 * level - 31 : 7L * level - 155L);
    } else {
      return level <= 15 ? 2 * level + 7 : (level <= 30 ? 5 * level - 38 : 9L * level - 158L);
    }
  }

  public static long getTotalExpToLevel(int level) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      return level <= 16
          ? 17 * level
          : (level <= 31
              ? 3 * level * level / 2 - 59 * level / 2 + 360
              : 7 * level * level / 2 - 303 * level / 2 + 2220);
    } else {
      return (long)
          (level <= 16
              ? level * level + 6 * level
              : (level <= 31
                  ? 2.5 * level * level - 40.5 * level + 360.0
                  : 4.5 * level * level - 162.5 * level + 2220.0));
    }
  }

  public static void setLevel(Player player, int level) {
    if (player != null) {
      player.setLevel(0);
      player.setExp(0.0F);
      player.setTotalExperience(0);
      player.setLevel(level);
    }
  }

  public static int levelFromExp(long exp) {
    return (int) Math.floor((Math.sqrt(72.0 * exp - 54215.0) + 325.0) / 18.0);
  }

  public static void setExp(Player player, long exp) {
    if (player != null) {
      exp = (long) CMINumber.clamp(exp, 0.0, Float.MAX_VALUE);
      if (!Version.isCurrentLower(Version.v1_8_R1) && exp >= 2147483647L) {
        int level = levelFromExp(exp);
        float leftOver = (float) (exp - getTotalExpToLevel(level));
        float delta = (float) getXpToLevelUpFrom(level);
        player.setTotalExperience(0);
        player.setLevel(level);
        player.setExp((float) CMINumber.clamp(leftOver / delta, 0.0, 1));
      } else {
        player.setLevel(0);
        player.setExp(0.0F);
        player.setTotalExperience(0);
        player.giveExp((int) exp);
      }
    }
  }

  public static void addExp(Player player, long exp) {
    if (player != null) {
      setExp(player, getTotalExp(player) + exp);
    }
  }

  public static void takeExp(Player player, long exp) {
    if (player != null) {
      setExp(player, getTotalExp(player) - exp);
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
