package io.lumine.mythic.bukkit.utils.bossbar;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface BossBar extends Terminable {
   static BossBarFactory create() {
      return new BukkitBossBarFactory(Bukkit.getServer());
   }

   @Nonnull
   String title();

   @Nonnull
   BossBar title(@Nonnull String str);

   double progress();

   @Nonnull
   BossBar progress(double value);

   @Nonnull
   BossBarColor color();

   @Nonnull
   BossBar color(@Nonnull BossBarColor bossBarColor);

   @Nonnull
   BossBarStyle style();

   @Nonnull
   BossBar style(@Nonnull BossBarStyle bossBarStyle);

   boolean visible();

   @Nonnull
   BossBar visible(boolean flag);

   @Nonnull
   List<Player> players();

   void addPlayer(@Nonnull Player player);

   void removePlayer(@Nonnull Player player);

   default void addPlayers(@Nonnull Iterable<Player> players) {
      for (Player player : players) {
         this.addPlayer(player);
      }
   }

   default void removePlayers(@Nonnull Iterable<Player> players) {
      for (Player player : players) {
         this.removePlayer(player);
      }
   }

   void removeAll();

   @Override
   void close();
}
