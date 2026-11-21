package io.lumine.mythic.api.adapters;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.Collection;
import java.util.UUID;

public interface AbstractBossBar extends Terminable {
   String getTitle();

   void setTitle(String str);

   AbstractBossBar.BarColor getColor();

   void setColor(String str);

   AbstractBossBar.BarStyle getStyle();

   void setStyle(String str);

   void removeFlag(String str);

   void addFlag(String str);

   boolean hasFlag(String str);

   void setProgress(double value);

   double getProgress();

   void addPlayer(AbstractPlayer abstractPlayer);

   void removePlayer(AbstractPlayer abstractPlayer);

   void removeAll();

   Collection<AbstractPlayer> getPlayers();

   void setVisible(boolean flag);

   boolean isVisible();

   void setCreateFog(boolean flag);

   void setDarkenSky(boolean flag);

   void setPlayBossMusic(boolean flag);

   Collection<UUID> getPlayerIds();

   void removePlayer(UUID uuid);

   boolean isViewing(AbstractPlayer abstractPlayer);

   public static enum BarColor {
      PINK,
      BLUE,
      RED,
      GREEN,
      YELLOW,
      PURPLE,
      WHITE;
   }

   public static enum BarFlag {
      DARKEN_SKY,
      PLAY_BOSS_MUSIC,
      CREATE_FOG;
   }

   public static enum BarStyle {
      SOLID,
      SEGMENTED_6,
      SEGMENTED_10,
      SEGMENTED_12,
      SEGMENTED_20;
   }
}
