package net.Zrips.CMILib.Container;

import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CMIPlayer {
  public static Player getByName(String name) {
    if (name.length() == 36) {
      try {
        UUID uuid = UUID.fromString(name);
        return Bukkit.getPlayer(uuid);
      } catch (Throwable var4) {
      }
    }

    name = CMIChatColor.stripColor(name);
    Player player = Bukkit.getPlayerExact(name);
    if (player != null) {
      return player;
    }

    player = Bukkit.getPlayer(name);
    if (player != null) {
      return player;
    }

    for (Player one : Bukkit.getOnlinePlayers()) {
      if (one.getDisplayName() != null
          && !one.getDisplayName().isEmpty()
          && CMIChatColor.stripColor(one.getDisplayName()).equalsIgnoreCase(name)) {
        return one;
      }
    }

    return player;
  }

  public static double getMaxHealth(Player player) {
    return Version.isCurrentEqualOrHigher(Version.v1_9_R1)
        ? CMIAttribute.MAX_HEALTH.getValue(player)
        : player.getMaxHealth();
  }

  public static double getBaseMaxHealth(Player player) {
    return Version.isCurrentEqualOrHigher(Version.v1_9_R1)
        ? CMIAttribute.MAX_HEALTH.getBaseValue(player)
        : player.getMaxHealth();
  }

  public static void setMaxHealth(Player player, double amount) {
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      CMIAttribute.MAX_HEALTH.setBaseValue(player, amount);
    } else {
      player.setMaxHealth(amount);
    }
  }
}
