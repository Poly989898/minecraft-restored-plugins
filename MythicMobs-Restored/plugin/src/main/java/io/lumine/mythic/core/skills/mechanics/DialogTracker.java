package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public final class DialogTracker {
   private static final AtomicLong tokens = new AtomicLong();
   private static final Map<UUID, Long> openDialogs = new ConcurrentHashMap<>();

   private DialogTracker() {
   }

   public static long nextToken() {
      return tokens.incrementAndGet();
   }

   public static void markOpen(UUID uuid, long token) {
      openDialogs.put(uuid, token);
   }

   public static void markClosed(UUID uuid) {
      openDialogs.remove(uuid);
   }

   public static boolean isOpen(UUID uuid) {
      return openDialogs.containsKey(uuid);
   }

   public static boolean isCurrent(UUID uuid, long token) {
      Long current = openDialogs.get(uuid);
      return current != null && current == token;
   }

   public static void close(Player player) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_8)) {
         player.closeDialog();
      }

      markClosed(player.getUniqueId());
   }

   static {
      Events.subscribe(PlayerQuitEvent.class).handler(event -> openDialogs.remove(event.getPlayer().getUniqueId()));
   }
}
