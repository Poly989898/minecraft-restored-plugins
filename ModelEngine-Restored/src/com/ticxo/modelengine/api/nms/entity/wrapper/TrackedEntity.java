package com.ticxo.modelengine.api.nms.entity.wrapper;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TrackedEntity {
   Predicate<Player> DEFAULT_PREDICATE = player -> true;

   Entity getEntity();

   default int getInitialBaseRange() {
      return 0;
   }

   int getBaseRange();

   void setBaseRange(int baseRange);

   int getEffectiveRange();

   Set<UUID> getTrackedPlayer();

   Set<UUID> getTrackedPlayer(Predicate<Player> predicate);

   default Set<UUID> getTrackedPlayer(Predicate<Player> predicate, Set<UUID> dest) {
      return this.getTrackedPlayer(predicate);
   }

   default boolean isViewerSetUnchanged() {
      return false;
   }

   default void markViewersDirty() {
   }

   void sendPairingData(Player player);

   void broadcastSpawn();

   void broadcastRemove();

   void addForcedPairing(UUID uuid);

   void removeForcedPairing(UUID uuid);

   void clearForcedPairing();

   void addForcedHidden(UUID uuid);

   void removeForcedHidden(UUID uuid);

   void clearForcedHidden();

   void setPlayerPredicate(@NotNull Predicate<Player> playerPredicate);

   @NotNull
   Predicate<Player> getPlayerPredicate();
}
