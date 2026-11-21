package io.lumine.mythic.core.players;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerCombatTracker {
   private final Map<UUID, Long> lastCombatTimes = new ConcurrentHashMap<>();

   public PlayerCombatTracker(MythicBukkit plugin) {
      Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.MONITOR)
         .handleSubclasses()
         .filter(event -> !event.isCancelled())
         .filter(event -> event.getFinalDamage() > 0.0)
         .handler(this::onCombat)
         .bindWith(plugin);
      Events.subscribe(PlayerQuitEvent.class).handler(event -> this.lastCombatTimes.remove(event.getPlayer().getUniqueId())).bindWith(plugin);
   }

   private void onCombat(EntityDamageByEntityEvent event) {
      long now = System.currentTimeMillis();
      UUID damagerId = this.resolvePlayer(event.getDamager());
      if (damagerId != null) {
         this.lastCombatTimes.put(damagerId, now);
      }

      if (event.getEntity() instanceof Player player) {
         this.lastCombatTimes.put(player.getUniqueId(), now);
      }
   }

   private UUID resolvePlayer(Entity damager) {
      if (damager instanceof Player player) {
         return player.getUniqueId();
      } else {
         return damager instanceof Projectile projectile && projectile.getShooter() instanceof Player player ? player.getUniqueId() : null;
      }
   }

   public void tag(UUID playerId) {
      this.lastCombatTimes.put(playerId, System.currentTimeMillis());
   }

   public boolean isInCombat(UUID playerId, long timeoutMs) {
      Long last = this.lastCombatTimes.get(playerId);
      if (last == null) {
         return false;
      } else if (System.currentTimeMillis() - last <= timeoutMs) {
         return true;
      } else {
         this.lastCombatTimes.remove(playerId, last);
         return false;
      }
   }

   public void clear(UUID playerId) {
      this.lastCombatTimes.remove(playerId);
   }
}
