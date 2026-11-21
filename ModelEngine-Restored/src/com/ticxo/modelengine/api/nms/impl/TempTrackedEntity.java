package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
@Setter
public class TempTrackedEntity implements TrackedEntity {
   private final Entity entity;
   private final Set<UUID> forcePaired = new HashSet<>();
   private final Set<UUID> forceHidden = new HashSet<>();
   private int baseRange = -1;
   private Predicate<Player> playerPredicate = DEFAULT_PREDICATE;

   @Override
   public int getEffectiveRange() {
      return this.baseRange;
   }

   @Override
   public Set<UUID> getTrackedPlayer() {
      if (ServerInfo.IS_PAPER) {
         Set<UUID> set = new HashSet<>(this.forcePaired);

         for (Player player : this.entity.getTrackedPlayers()) {
            if (this.playerPredicate.test(player) && !this.forceHidden.contains(player.getUniqueId())) {
               set.add(player.getUniqueId());
            }
         }

         return set;
      } else {
         return new HashSet<>();
      }
   }

   @Override
   public Set<UUID> getTrackedPlayer(Predicate<Player> predicate) {
      if (ServerInfo.IS_PAPER) {
         Set<UUID> set = new HashSet<>(this.forcePaired);

         for (Player player : this.entity.getTrackedPlayers()) {
            if (predicate.test(player) && this.playerPredicate.test(player) && !this.forceHidden.contains(player.getUniqueId())) {
               set.add(player.getUniqueId());
            }
         }

         return set;
      } else {
         return new HashSet<>();
      }
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

   @Override
   public void addForcedPairing(UUID uuid) {
      this.forcePaired.add(uuid);
      this.removeForcedHidden(uuid);
   }

   @Override
   public void removeForcedPairing(UUID uuid) {
      this.forcePaired.remove(uuid);
   }

   @Override
   public void clearForcedPairing() {
      this.forcePaired.clear();
   }

   @Override
   public void addForcedHidden(UUID uuid) {
      this.forceHidden.add(uuid);
      this.removeForcedPairing(uuid);
   }

   @Override
   public void removeForcedHidden(UUID uuid) {
      this.forceHidden.remove(uuid);
   }

   @Override
   public void clearForcedHidden() {
      this.forceHidden.clear();
   }

   @Generated
   public TempTrackedEntity(Entity entity) {
      this.entity = entity;
   }
}
