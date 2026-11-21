package io.lumine.mythic.core.players;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlayerLoadedEvent;
import io.lumine.mythic.bukkit.events.MythicPlayerQuitEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.storage.players.PlayerRepository;
import io.lumine.mythic.bukkit.utils.storage.players.adapters.file.JsonPlayerStorageAdapter;
import io.lumine.mythic.core.players.factions.FactionProvider;
import io.lumine.mythic.core.players.factions.PermissionFactionProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager extends PlayerRepository<MythicBukkit, PlayerData> {
   protected FactionProvider factionProvider = new PermissionFactionProvider();
   protected PlayerLevelProvider playerLevelProvider = new DefaultPlayerLevelProvider();
   protected final PlayerCombatTracker combatTracker;
   protected Map<UUID, PlayerManager.PlayerMovementData> playerPositions = Maps.newConcurrentMap();
   private final Map<UUID, AtomicReference<Location>> latestPlayerPositions = Maps.newConcurrentMap();
   private final Map<UUID, Location> lastSampledPlayerPositions = Maps.newConcurrentMap();
   private AtomicBoolean trackingPlayerMovement = new AtomicBoolean(false);

   public PlayerManager(MythicBukkit plugin) {
      super(plugin, PlayerData.class);
      JsonPlayerStorageAdapter<MythicBukkit, PlayerData> storageAdapter = new JsonPlayerStorageAdapter<>(plugin, PlayerData.class);
      storageAdapter.setDataDirectory("data");
      this.initialize(storageAdapter);
      this.combatTracker = new PlayerCombatTracker(plugin);
   }

   public PlayerData createProfile(UUID id, String name, int subProfileOffset) {
      return new PlayerData(id, name);
   }

   public void initProfile(PlayerData profile, Player player) {
      AbstractPlayer adapted = BukkitAdapter.adapt(player);
      adapted.clearAllStatAttributeModifiers();
      profile.initialize(adapted);
      Events.call(new MythicPlayerLoadedEvent(player, profile));
   }

   public PlayerData getProfile(AbstractPlayer player) {
      return this.getProfile(BukkitAdapter.adapt(player));
   }

   public void unloadProfile(PlayerData profile, Player player) {
      Events.call(new MythicPlayerQuitEvent(player, profile));
      BukkitAdapter.adapt(player).clearAllStatAttributeModifiers();
   }

   public void registerFactionProvider(FactionProvider provider) {
      this.factionProvider = provider;
   }

   public void registerPlayerLevelProvider(PlayerLevelProvider provider) {
      this.playerLevelProvider = provider;
   }

   public void trackPlayerMovement() {
      if (!this.trackingPlayerMovement.getAndSet(true)) {
         Events.subscribe(PlayerMoveEvent.class, EventPriority.MONITOR).filter(event -> !event.isCancelled()).handler(event -> {
            Location to = event.getTo() != null ? event.getTo() : event.getFrom();
            if (to != null) {
               UUID uuid = event.getPlayer().getUniqueId();
               AtomicReference<Location> ref = this.latestPlayerPositions.get(uuid);
               if (ref == null) {
                  ref = this.latestPlayerPositions.computeIfAbsent(uuid, k -> new AtomicReference<>());
               }

               ref.set(to);
            }
         }).bindWith(this);
         Schedulers.sync().runRepeating(this::samplePlayerMovement, 0L, 1L);
         Events.subscribe(PlayerQuitEvent.class).handler(event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            this.playerPositions.remove(uuid);
            this.latestPlayerPositions.remove(uuid);
            this.lastSampledPlayerPositions.remove(uuid);
         }).bindWith(this);
      }
   }

   private void samplePlayerMovement() {
      this.latestPlayerPositions.forEach((uuid, ref) -> {
         Location latestPosition = ref.get();
         if (latestPosition != null) {
            Location to = latestPosition.clone();
            Location from = this.lastSampledPlayerPositions.put(uuid, to);
            if (from == null) {
               from = to.clone();
            }

            this.playerPositions.put(uuid, new PlayerManager.PlayerMovementData(from, to));
         }
      });
   }

   @Generated
   public FactionProvider getFactionProvider() {
      return this.factionProvider;
   }

   @Generated
   public PlayerLevelProvider getPlayerLevelProvider() {
      return this.playerLevelProvider;
   }

   @Generated
   public PlayerCombatTracker getCombatTracker() {
      return this.combatTracker;
   }

   @Generated
   public Map<UUID, PlayerManager.PlayerMovementData> getPlayerPositions() {
      return this.playerPositions;
   }

   public class PlayerMovementData {
      private final long lastMovementTime = System.currentTimeMillis();
      private final Location from;
      private final Location to;

      @Generated
      public PlayerMovementData(final Location from, final Location to) {
         this.from = from;
         this.to = to;
      }

      @Generated
      public long getLastMovementTime() {
         return this.lastMovementTime;
      }

      @Generated
      public Location getFrom() {
         return this.from;
      }

      @Generated
      public Location getTo() {
         return this.to;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PlayerManager.PlayerMovementData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getLastMovementTime() != other.getLastMovementTime()) {
            return false;
         } else {
            Object this$from = this.getFrom();
            Object other$from = other.getFrom();
            if (this$from == null ? other$from == null : this$from.equals(other$from)) {
               Object this$to = this.getTo();
               Object other$to = other.getTo();
               return this$to == null ? other$to == null : this$to.equals(other$to);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof PlayerManager.PlayerMovementData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $lastMovementTime = this.getLastMovementTime();
         result = result * 59 + (int)($lastMovementTime >>> 32 ^ $lastMovementTime);
         Object $from = this.getFrom();
         result = result * 59 + ($from == null ? 43 : $from.hashCode());
         Object $to = this.getTo();
         return result * 59 + ($to == null ? 43 : $to.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PlayerManager.PlayerMovementData(lastMovementTime=" + this.getLastMovementTime() + ", from=" + this.getFrom() + ", to=" + this.getTo() + ")";
      }
   }
}
