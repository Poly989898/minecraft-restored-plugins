package me.TechsCode.UltraPermissions;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CachedOnlineChecker {
   private Set<UUID> cache;

   public static CachedOnlineChecker scan() {
      return new CachedOnlineChecker(Bukkit.getOnlinePlayers().stream().<UUID>map(OfflinePlayer::getUniqueId).collect(Collectors.toSet()));
   }

   private CachedOnlineChecker(Set<UUID> cache) {
      this.cache = cache;
   }

   public boolean isOnline(UUID uuid) {
      return this.cache.contains(uuid);
   }

   public boolean isOnline(Player player) {
      return this.isOnline(player.getUniqueId());
   }
}
