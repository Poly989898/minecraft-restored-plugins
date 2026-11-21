package io.lumine.mythic.bukkit.utils.bossbar;

public enum BossBarStyle {
   SOLID,
   SEGMENTED_6,
   SEGMENTED_10,
   SEGMENTED_12,
   SEGMENTED_20;

   public static BossBarStyle defaultStyle() {
      return SOLID;
   }
}
