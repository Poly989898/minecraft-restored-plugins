package me.TechsCode.UltraPermissions.base.util;

import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import org.bukkit.entity.Player;

public class Experience {
   public Experience() {
   }

   public static int levelToExp(int count) {
      MinecraftVersion serversVersion = MinecraftVersion.getServersVersion();
      if (serversVersion.isBelow(MinecraftVersion.V1_8_R1)) {
         if (count <= 15) {
            return 17 * count;
         } else {
            return count <= 30 ? 3 * count * count / 2 - 59 * count / 2 + 360 : 7 * count * count / 2 - 303 * count / 2 + 2220;
         }
      } else if (count <= 15) {
         return count * count + 6 * count;
      } else {
         return count <= 30 ? (int)(2.5 * count * count - 40.5 * count + 360.0) : (int)(4.5 * count * count - 162.5 * count + 2220.0);
      }
   }

   public static int totalExpToLevelUp(int count) {
      MinecraftVersion serversVersion = MinecraftVersion.getServersVersion();
      if (serversVersion.isBelow(MinecraftVersion.V1_8_R1)) {
         if (count <= 16) {
            return 17;
         } else {
            return count <= 31 ? 3 * count - 31 : 7 * count - 155;
         }
      } else if (count <= 15) {
         return 2 * count + 7;
      } else {
         return count <= 30 ? 5 * count - 38 : 9 * count - 158;
      }
   }

   public static int getLevel(Player player) {
      return player == null ? 0 : player.getLevel();
   }

   public static void setLevel(Player player, int count) {
      if (player != null) {
         player.setLevel(0);
         player.setExp(0.0F);
         player.setTotalExperience(0);
         player.setLevel(count);
      }
   }

   public static int levelFromExp(long value) {
      return (int)Math.floor((Math.sqrt(72.0 * value - 54215.0) + 325.0) / 18.0);
   }

   public static void setTotalExperience(Player player, long value) {
      if (player != null) {
         value = (long)Number.clamp(value, 0.0, 3.4028234663852886E38);
         MinecraftVersion serversVersion = MinecraftVersion.getServersVersion();
         if (!serversVersion.isBelow(MinecraftVersion.V1_8_R1) && value >= 2147483647L) {
            int count = levelFromExp(value);
            float remainingExp = (float)(value - levelToExp(count));
            float expToNextLevel = totalExpToLevelUp(count);
            player.setTotalExperience(0);
            player.setLevel(count);
            player.setExp((float)Number.clamp(remainingExp / expToNextLevel, 0.0, 1.0));
         } else {
            player.setLevel(0);
            player.setExp(0.0F);
            player.setTotalExperience(0);
            player.giveExp((int)value);
         }
      }
   }

   public static void addExp(Player player, int count) {
      if (player != null) {
         setTotalExperience(player, getTotalExp(player) + count);
      }
   }

   public static void takeExp(Player player, int count) {
      if (player != null) {
         setTotalExperience(player, getTotalExp(player) - count);
      }
   }

   public static long getMissingExp(Player player) {
      return player == null ? 0L : totalExpToLevelUp(player.getLevel()) - getExpForCurrentLevel(player);
   }

   public static long getExpForCurrentLevel(Player player) {
      return player == null ? 0L : getTotalExp(player) - levelToExp(player.getLevel());
   }

   public static long getTotalExp(Player player) {
      return player == null ? 0L : levelToExp(player.getLevel()) + Math.round(totalExpToLevelUp(player.getLevel()) * player.getExp());
   }
}
