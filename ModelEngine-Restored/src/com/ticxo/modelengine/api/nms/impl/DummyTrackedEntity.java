package com.ticxo.modelengine.api.nms.impl;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DummyTrackedEntity implements TrackedEntity {
   private final Set<UUID> tracked = Sets.newConcurrentHashSet();
   private final Set<UUID> forcedPairing = Sets.newConcurrentHashSet();
   private final Set<UUID> forcedRemove = Sets.newConcurrentHashSet();
   private int baseRange = 64;
   private Predicate<Player> playerPredicate = DEFAULT_PREDICATE;

   public void detectPlayers(@NotNull Location location) {
      World world = location.getWorld();
      if (world == null) {
         throw new IllegalArgumentException("World of location cannot be null.");
      }

      int r2 = this.baseRange * this.baseRange;
      this.tracked.clear();
      double lx = location.getX();
      double ly = location.getY();
      double lz = location.getZ();

      for (Player player : Bukkit.getOnlinePlayers()) {
         if (player.getWorld() == world) {
            double dx = player.getX() - lx;
            double dy = player.getY() - ly;
            double dz = player.getZ() - lz;
            if (dx * dx + dy * dy + dz * dz <= r2) {
               this.tracked.add(player.getUniqueId());
            }
         }
      }
   }

   @Override
   public int getEffectiveRange() {
      return this.baseRange;
   }

   @Override
   public Set<UUID> getTrackedPlayer() {
      HashSet<UUID> set = new HashSet<>(this.forcedPairing);

      for (UUID player : this.tracked) {
         Player p = Bukkit.getPlayer(player);
         if (p != null && this.playerPredicate.test(p) && !this.forcedRemove.contains(player)) {
            set.add(player);
         }
      }

      return set;
   }

   @Override
   public Set<UUID> getTrackedPlayer(Predicate<Player> predicate) {
      HashSet<UUID> set = new HashSet<>(this.forcedPairing);

      for (UUID uuid : this.tracked) {
         Player player = Bukkit.getPlayer(uuid);
         if (predicate.test(player) && this.playerPredicate.test(player) && !this.forcedRemove.contains(uuid)) {
            set.add(uuid);
         }
      }

      return set;
   }

   @Override
   public void addForcedPairing(UUID uuid) {
      this.forcedPairing.add(uuid);
      this.removeForcedHidden(uuid);
   }

   @Override
   public void removeForcedPairing(UUID uuid) {
      this.forcedPairing.remove(uuid);
   }

   @Override
   public void clearForcedPairing() {
      this.forcedPairing.clear();
   }

   @Override
   public void addForcedHidden(UUID uuid) {
      this.forcedRemove.add(uuid);
      this.removeForcedPairing(uuid);
   }

   @Override
   public void removeForcedHidden(UUID uuid) {
      this.forcedRemove.remove(uuid);
   }

   @Override
   public void clearForcedHidden() {
      this.forcedRemove.clear();
   }

   @Override
   public Entity getEntity() {
      return null;
   }

   @Override
   public void sendPairingData(Player player) {
   }

   @Override
   public void broadcastSpawn() {
   }

   @Override
   public void broadcastRemove() {
   }

   @Generated
   @Override
   public int getBaseRange() {
      return this.baseRange;
   }

   @Generated
   @Override
   public void setBaseRange(int baseRange) {
      this.baseRange = baseRange;
   }

   @Generated
   @Override
   public Predicate<Player> getPlayerPredicate() {
      return this.playerPredicate;
   }

   @Generated
   @Override
   public void setPlayerPredicate(Predicate<Player> playerPredicate) {
      this.playerPredicate = playerPredicate;
   }
}
