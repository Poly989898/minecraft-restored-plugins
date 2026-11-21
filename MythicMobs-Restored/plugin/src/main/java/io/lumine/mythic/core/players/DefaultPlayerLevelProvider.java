package io.lumine.mythic.core.players;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DefaultPlayerLevelProvider extends PlayerLevelProvider {
   @Override
   public long getPlayerLevel(UUID uuid) {
      Player player = Bukkit.getPlayer(uuid);
      return player == null ? 0L : player.getLevel();
   }

   @Override
   public long getPlayerLevel(UUID uuid, String key) {
      Player player = Bukkit.getPlayer(uuid);
      return player == null ? 0L : player.getLevel();
   }
}
