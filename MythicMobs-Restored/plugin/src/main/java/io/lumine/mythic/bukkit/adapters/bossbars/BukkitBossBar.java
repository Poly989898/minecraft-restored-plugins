package io.lumine.mythic.bukkit.adapters.bossbars;

import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BukkitBossBar implements AbstractBossBar {
   BossBar bar;

   public BukkitBossBar(String title, AbstractBossBar.BarColor aColor, AbstractBossBar.BarStyle aStyle) {
      org.bukkit.boss.BarColor color = org.bukkit.boss.BarColor.valueOf(aColor.name());
      org.bukkit.boss.BarStyle style = org.bukkit.boss.BarStyle.valueOf(aStyle.name());
      this.bar = Bukkit.getServer().createBossBar(title, color, style, new org.bukkit.boss.BarFlag[0]);
   }

   @Override
   public String getTitle() {
      return this.bar.getTitle();
   }

   @Override
   public void setTitle(String title) {
      this.bar.setTitle(title);
   }

   @Override
   public AbstractBossBar.BarColor getColor() {
      return null;
   }

   @Override
   public void setColor(String color) {
      this.bar.setColor(org.bukkit.boss.BarColor.valueOf(color));
   }

   @Override
   public AbstractBossBar.BarStyle getStyle() {
      return null;
   }

   @Override
   public void setStyle(String style) {
   }

   @Override
   public void removeFlag(String flag) {
      this.bar.removeFlag(org.bukkit.boss.BarFlag.valueOf(flag));
   }

   @Override
   public void addFlag(String flag) {
      this.bar.addFlag(org.bukkit.boss.BarFlag.valueOf(flag));
   }

   @Override
   public boolean hasFlag(String flag) {
      return this.bar.hasFlag(org.bukkit.boss.BarFlag.valueOf(flag));
   }

   @Override
   public void setProgress(double progress) {
      if (progress > 1.0) {
         progress = 1.0;
      } else if (progress < 0.0) {
         progress = 0.0;
      }

      this.bar.setProgress(progress);
   }

   @Override
   public double getProgress() {
      return this.bar.getProgress();
   }

   @Override
   public void addPlayer(AbstractPlayer player) {
      this.bar.addPlayer((Player)player.getBukkitEntity());
   }

   @Override
   public void removePlayer(AbstractPlayer player) {
      this.bar.removePlayer((Player)player.getBukkitEntity());
   }

   @Override
   public boolean isViewing(AbstractPlayer player) {
      return this.bar.getPlayers().contains((Player)player.getBukkitEntity());
   }

   @Override
   public void removeAll() {
      this.bar.removeAll();
   }

   @Override
   public Collection<AbstractPlayer> getPlayers() {
      return this.bar.getPlayers().stream().map(player -> BukkitAdapter.adapt(player)).collect(Collectors.toSet());
   }

   @Override
   public Collection<UUID> getPlayerIds() {
      return this.bar.getPlayers().stream().map(player -> player.getUniqueId()).collect(Collectors.toList());
   }

   @Override
   public void setVisible(boolean visible) {
      this.bar.setVisible(visible);
   }

   @Override
   public boolean isVisible() {
      return this.bar.isVisible();
   }

   @Override
   public void setCreateFog(boolean b) {
      if (b) {
         this.bar.addFlag(org.bukkit.boss.BarFlag.CREATE_FOG);
      } else {
         this.bar.removeFlag(org.bukkit.boss.BarFlag.CREATE_FOG);
      }
   }

   @Override
   public void setDarkenSky(boolean b) {
      if (b) {
         this.bar.addFlag(org.bukkit.boss.BarFlag.DARKEN_SKY);
      } else {
         this.bar.removeFlag(org.bukkit.boss.BarFlag.DARKEN_SKY);
      }
   }

   @Override
   public void setPlayBossMusic(boolean b) {
      if (b) {
         this.bar.addFlag(org.bukkit.boss.BarFlag.PLAY_BOSS_MUSIC);
      } else {
         this.bar.removeFlag(org.bukkit.boss.BarFlag.PLAY_BOSS_MUSIC);
      }
   }

   @Override
   public void close() {
      this.setVisible(false);
      this.removeAll();
   }

   @Override
   public void removePlayer(UUID player) {
   }
}
