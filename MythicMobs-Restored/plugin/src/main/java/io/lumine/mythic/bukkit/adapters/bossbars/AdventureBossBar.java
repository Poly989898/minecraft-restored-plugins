package io.lumine.mythic.bukkit.adapters.bossbars;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Flag;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdventureBossBar implements AbstractBossBar {
   private final Set<UUID> players = Sets.newHashSet();
   private final Set<UUID> unmodifiablePlayers;
   BossBar bar;

   public AdventureBossBar(String title, float progress, AbstractBossBar.BarColor color, AbstractBossBar.BarStyle style) {
      this.bar = BossBar.bossBar(Text.parse(title), progress, Color.valueOf(color.name()), this.getOverlay(style));
      this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
   }

   @Override
   public String getTitle() {
      return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(this.bar.name());
   }

   @Override
   public void setTitle(String title) {
      this.bar.name(Text.parse(title));
   }

   @Override
   public AbstractBossBar.BarColor getColor() {
      return AbstractBossBar.BarColor.valueOf(this.bar.color().name());
   }

   @Override
   public void setColor(String color) {
      this.bar.color(Color.valueOf(color));
   }

   @Override
   public AbstractBossBar.BarStyle getStyle() {
      return null;
   }

   @Override
   public void setStyle(String style) {
      if (!style.toLowerCase().contains("solid") && !style.toLowerCase().contains("segmented")) {
         this.bar.overlay(Overlay.valueOf(style));
      } else {
         this.bar.overlay(this.getOverlay(AbstractBossBar.BarStyle.valueOf(style)));
      }
   }

   @Override
   public void removeFlag(String flag) {
      this.bar.removeFlag(Flag.valueOf(flag));
   }

   @Override
   public void addFlag(String flag) {
      this.bar.addFlag(Flag.valueOf(flag));
   }

   @Override
   public boolean hasFlag(String flag) {
      return this.bar.hasFlag(Flag.valueOf(flag));
   }

   @Override
   public void setProgress(double progress) {
      progress = Numbers.clamp(progress, 0.0, 1.0);
      this.bar.progress((float)progress);
   }

   @Override
   public double getProgress() {
      return this.bar.progress();
   }

   @Override
   public void addPlayer(AbstractPlayer player) {
      this.players.add(player.getUniqueId());
      Audience.audience(new Audience[]{player.getBukkitEntity()}).showBossBar(this.bar);
   }

   @Override
   public void removePlayer(AbstractPlayer player) {
      this.players.remove(player.getUniqueId());
      Audience.audience(new Audience[]{player.getBukkitEntity()}).hideBossBar(this.bar);
   }

   @Override
   public void removeAll() {
      this.players.forEach(player -> {
         Player p = Bukkit.getPlayer(player);
         if (p != null) {
            Audience.audience(new Audience[]{p}).hideBossBar(this.bar);
         }
      });
      this.players.clear();
   }

   @Override
   public Collection<AbstractPlayer> getPlayers() {
      Collection<AbstractPlayer> players = this.players.stream().map(uuid -> MythicBukkit.inst().getBootstrap().getPlayer(uuid)).collect(Collectors.toSet());
      players.removeAll(Collections.singletonList(null));
      return players;
   }

   @Override
   public void setVisible(boolean visible) {
   }

   @Override
   public boolean isVisible() {
      return true;
   }

   @Override
   public void setCreateFog(boolean b) {
      if (b) {
         this.bar.addFlag(Flag.CREATE_WORLD_FOG);
      } else {
         this.bar.removeFlag(Flag.CREATE_WORLD_FOG);
      }
   }

   @Override
   public void setDarkenSky(boolean b) {
      if (b) {
         this.bar.addFlag(Flag.DARKEN_SCREEN);
      } else {
         this.bar.removeFlag(Flag.DARKEN_SCREEN);
      }
   }

   @Override
   public void setPlayBossMusic(boolean b) {
      if (b) {
         this.bar.addFlag(Flag.PLAY_BOSS_MUSIC);
      } else {
         this.bar.removeFlag(Flag.PLAY_BOSS_MUSIC);
      }
   }

   @Override
   public Collection<UUID> getPlayerIds() {
      return this.unmodifiablePlayers;
   }

   @Override
   public void removePlayer(UUID player) {
      this.players.remove(player);
      Player p = Bukkit.getPlayer(player);
      if (p != null) {
         Audience.audience(new Audience[]{p}).hideBossBar(this.bar);
      }
   }

   @Override
   public boolean isViewing(AbstractPlayer player) {
      return this.players.contains(player.getUniqueId());
   }

   @Override
   public void close() {
      this.removeAll();
   }

   private Overlay getOverlay(AbstractBossBar.BarStyle barStyle) {
      switch (barStyle) {
         case SEGMENTED_6:
            return Overlay.NOTCHED_6;
         case SEGMENTED_10:
            return Overlay.NOTCHED_10;
         case SEGMENTED_12:
            return Overlay.NOTCHED_12;
         case SEGMENTED_20:
            return Overlay.NOTCHED_20;
         default:
            return Overlay.PROGRESS;
      }
   }
}
