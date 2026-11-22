package net.Zrips.CMILib.Container;

import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;

public class CMIExp {
  public static int levelToExp(int level) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      if (level <= 15) {
        return 17 * level;
      } else {
        return level <= 30
            ? 3 * level * level / 2 - 59 * level / 2 + 360
            : 7 * level * level / 2 - 303 * level / 2 + 2220;
      }
    } else if (level <= 15) {
      return level * level + 6 * level;
    } else {
      return level <= 30
          ? (int) (2.5 * level * level - 40.5 * level + 360.0)
          : (int) (4.5 * level * level - 162.5 * level + 2220.0);
    }
  }

  public static int deltaLevelToExp(int level) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      if (level <= 16) {
        return 17;
      } else {
        return level <= 31 ? 3 * level - 31 : 7 * level - 155;
      }
    } else if (level <= 15) {
      return 2 * level + 7;
    } else {
      return level <= 30 ? 5 * level - 38 : 9 * level - 158;
    }
  }

  public static int currentlevelxpdelta(Player player) {
    return deltaLevelToExp(player.getLevel())
        - (levelToExp(player.getLevel())
            + Math.round(deltaLevelToExp(player.getLevel()) * player.getExp())
            - levelToExp(player.getLevel()));
  }

  public static int getPlayerExperience(Player player) {
    return (int)
        (levelToExp(player.getLevel())
            + Math.floor(deltaLevelToExp(player.getLevel()) * player.getExp()));
  }

  public static void setTotalExperience(Player player, int xp) {
    player.setTotalExperience(0);
    player.setLevel(0);
    player.setExp(0.0F);
    player.setTotalExperience(0);
    if (xp >= 1) {
      player.giveExp(xp);
      int xpForLevel = levelToExp(player.getLevel());
      int delta = deltaLevelToExp(player.getLevel());
      player.setExp((xp - xpForLevel) / Float.valueOf((float) delta));
    }
  }

  public static int expToLevel(double sourceleftexp, double d) {
    if (d > 21863.0) {
      d = 21863.0;
    }

    for (int i = 1; i <= d + 1.0; i++) {
      double levelexp = levelToExp(i);
      if (levelexp > sourceleftexp) {
        return i - 1;
      }
    }

    return 0;
  }
}
