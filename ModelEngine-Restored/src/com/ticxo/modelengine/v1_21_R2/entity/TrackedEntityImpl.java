package com.ticxo.modelengine.v1_21_R2.entity;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_21_R2.NMSFields;
import com.ticxo.modelengine.v1_21_R2.NMSMethods;
import com.ticxo.modelengine.v1_21_R2.network.utils.NetworkUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrackedEntityImpl implements com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity {
   @Getter
   private final Entity entity;
   private final Supplier<TrackedEntity> trackedEntitySupplier;
   private final Set<UUID> forcedPairing = Sets.newConcurrentHashSet();
   private final Set<UUID> forcedRemove = Sets.newConcurrentHashSet();
   @Getter @Setter
   private Predicate<Player> playerPredicate = DEFAULT_PREDICATE;
   @NotNull
   private TrackedEntity lastTrackedEntity;
   private static final int RECONCILE_INTERVAL = 20;
   private Object[] lastSeenBy;
   private volatile boolean viewersDirty = true;
   private volatile boolean mayHaveForced;
   private int ticksSinceReconcile;
   private final int initialBaseRange;

   public TrackedEntityImpl(Entity entity, Supplier<TrackedEntity> trackedEntitySupplier, @NotNull TrackedEntity lastTrackedEntity) {
      this.entity = entity;
      this.trackedEntitySupplier = trackedEntitySupplier;
      this.lastTrackedEntity = lastTrackedEntity;
      this.initialBaseRange = this.getBaseRange();
   }

   @Override
   public int getInitialBaseRange() {
      return this.initialBaseRange;
   }

   @Override
   public int getBaseRange() {
      Integer range = ReflectionUtils.get(this.getTrackedEntity(), NMSFields.TRACKED_ENTITY_range);
      if (range == null) {
         throw new NullPointerException(String.format("Unable to retrieve base range of entity with UUID %s.", this.entity.getUniqueId()));
      } else {
         return range;
      }
   }

   @Override
   public void setBaseRange(int range) {
      ReflectionUtils.set(this.getTrackedEntity(), NMSFields.TRACKED_ENTITY_range, range);
   }

   @Override
   public int getEffectiveRange() {
      Integer range = ReflectionUtils.call(this.getTrackedEntity(), NMSMethods.TRACKED_ENTITY_getEffectiveRange);
      if (range == null) {
         throw new NullPointerException(String.format("Unable to retrieve range of entity with UUID %s.", this.entity.getUniqueId()));
      } else {
         return range;
      }
   }

   @Override
   public Set<UUID> getTrackedPlayer() {
      boolean hasForced = !this.forcedPairing.isEmpty();
      boolean hasRemoved = !this.forcedRemove.isEmpty();
      Set<ServerPlayerConnection> seenBy = this.getTrackedEntity().seenBy;
      HashSet<UUID> set = new HashSet<>(Math.max(8, (int)((seenBy.size() + (hasForced ? this.forcedPairing.size() : 0)) / 0.75F) + 1));
      if (hasForced) {
         set.addAll(this.forcedPairing);
      }

      for (ServerPlayerConnection connection : seenBy) {
         Player player = connection.getPlayer().getBukkitEntity();
         if (player != null && this.playerPredicate.test(player) && (!hasRemoved || !this.forcedRemove.contains(player.getUniqueId()))) {
            set.add(player.getUniqueId());
         }
      }

      return set;
   }

   @Override
   public Set<UUID> getTrackedPlayer(Predicate<Player> predicate) {
      boolean hasForced = !this.forcedPairing.isEmpty();
      boolean hasRemoved = !this.forcedRemove.isEmpty();
      Set<ServerPlayerConnection> seenBy = this.getTrackedEntity().seenBy;
      HashSet<UUID> set = new HashSet<>(Math.max(8, (int)((seenBy.size() + (hasForced ? this.forcedPairing.size() : 0)) / 0.75F) + 1));
      if (hasForced) {
         set.addAll(this.forcedPairing);
      }

      for (ServerPlayerConnection connection : seenBy) {
         Player player = connection.getPlayer().getBukkitEntity();
         if (player != null
            && predicate.test(player)
            && this.playerPredicate.test(player)
            && (!hasRemoved || !this.forcedRemove.contains(player.getUniqueId()))) {
            set.add(player.getUniqueId());
         }
      }

      return set;
   }

   @Override
   public Set<UUID> getTrackedPlayer(Predicate<Player> predicate, Set<UUID> dest) {
      dest.clear();
      boolean hasForced = !this.forcedPairing.isEmpty();
      boolean hasRemoved = !this.forcedRemove.isEmpty();
      Set<ServerPlayerConnection> seenBy = this.getTrackedEntity().seenBy;
      if (hasForced) {
         dest.addAll(this.forcedPairing);
      }

      for (ServerPlayerConnection connection : seenBy) {
         Player player = connection.getPlayer().getBukkitEntity();
         if (player != null
            && predicate.test(player)
            && this.playerPredicate.test(player)
            && (!hasRemoved || !this.forcedRemove.contains(player.getUniqueId()))) {
            dest.add(player.getUniqueId());
         }
      }

      return dest;
   }

   @Override
   public boolean isViewerSetUnchanged() {
      if ((!this.mayHaveForced || this.forcedPairing.isEmpty() && this.forcedRemove.isEmpty()) && this.playerPredicate == DEFAULT_PREDICATE) {
         if (!this.viewersDirty && ++this.ticksSinceReconcile < 20) {
            return true;
         }

         this.viewersDirty = false;
         this.ticksSinceReconcile = 0;
         return this.snapshotUnchanged(this.getTrackedEntity().seenBy);
      } else {
         this.lastSeenBy = null;
         this.viewersDirty = true;
         return false;
      }
   }

   @Override
   public void markViewersDirty() {
      this.viewersDirty = true;
   }

   private boolean snapshotUnchanged(Collection<?> viewers) {
      int size = viewers.size();
      Object[] previous = this.lastSeenBy;
      if (previous != null && previous.length == size) {
         int index = 0;
         boolean same = true;

         for (Object viewer : viewers) {
            if (index >= size || previous[index] != viewer) {
               same = false;
               break;
            }

            index++;
         }

         if (same && index == size) {
            return true;
         }
      }

      Object[] snapshot = new Object[size];
      int index = 0;

      for (Object viewer : viewers) {
         if (index >= size) {
            break;
         }

         snapshot[index++] = viewer;
      }

      this.lastSeenBy = index == size ? snapshot : Arrays.copyOf(snapshot, index);
      return false;
   }

   @Override
   public void sendPairingData(Player player) {
      if (player != null) {
         ServerPlayer nms = ((CraftPlayer)player).getHandle();
         this.getTrackedEntity().serverEntity.sendPairingData(nms, packet -> NetworkUtils.send(player.getUniqueId(), packet));
      }
   }

   @Override
   public void broadcastSpawn() {
      for (UUID player : this.getTrackedPlayer()) {
         this.sendPairingData(Bukkit.getPlayer(player));
      }
   }

   @Override
   public void broadcastRemove() {
      this.getTrackedEntity().broadcastRemoved();
   }

   @Override
   public void addForcedPairing(UUID uuid) {
      this.mayHaveForced = true;
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
      this.mayHaveForced = true;
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

   @NotNull
   private TrackedEntity getTrackedEntity() {
      TrackedEntity trackedEntity = this.trackedEntitySupplier.get();
      if (trackedEntity != null && this.lastTrackedEntity != trackedEntity) {
         this.syncInstance(trackedEntity);
         this.lastTrackedEntity = trackedEntity;
      }

      return this.lastTrackedEntity;
   }

   private void syncInstance(TrackedEntity target) {
      Integer range = ReflectionUtils.get(this.lastTrackedEntity, NMSFields.TRACKED_ENTITY_range);
      if (range != null) {
         ReflectionUtils.set(target, NMSFields.TRACKED_ENTITY_range, range);
      }
   }
}
