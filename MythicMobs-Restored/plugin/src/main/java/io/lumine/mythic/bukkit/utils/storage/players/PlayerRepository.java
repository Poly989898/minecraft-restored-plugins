package io.lumine.mythic.bukkit.utils.storage.players;

import com.google.common.collect.Maps;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.storage.StorageModule;
import io.lumine.mythic.bukkit.utils.storage.events.LumineProfileLoadEvent;
import io.lumine.mythic.bukkit.utils.storage.events.LumineProfileSaveEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class PlayerRepository<T extends LuminePlugin, P extends Profile> extends StorageModule<T> implements ProfileRepository<P> {
   private final Map<UUID, P> profilesById = Maps.newConcurrentMap();
   private final Map<String, P> profilesByName = Maps.newConcurrentMap();
   private final Class<P> clazz;
   private PlayerStorageAdapter<P> adapter;
   protected boolean autoLoadProfiles = true;

   public PlayerRepository(T plugin, Class<P> clazz, PlayerStorageAdapter<P> adapter, boolean autoLoadProfiles) {
      super(plugin);
      this.clazz = clazz;
      this.autoLoadProfiles = autoLoadProfiles;
      this.initialize(adapter);
   }

   public PlayerRepository(T plugin, Class<P> clazz, PlayerStorageAdapter<P> adapter) {
      super(plugin);
      this.clazz = clazz;
      this.initialize(adapter);
   }

   public PlayerRepository(T plugin, Class<P> clazz) {
      super(plugin);
      this.clazz = clazz;
   }

   public void initialize(PlayerStorageAdapter<P> adapter) {
      this.adapter = adapter;
      this.load(this.plugin);
   }

   @Override
   public void load(T plugin) {
      if (this.autoLoadProfiles) {
         Events.subscribe(PlayerJoinEvent.class).handler(event -> this.loadProfile(event.getPlayer())).bindWith(this);
      }

      Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR).handler(event -> {
         UUID uuid = event.getPlayer().getUniqueId();
         P profile = this.profilesById.remove(uuid);
         this.profilesByName.remove(event.getPlayer().getName());
         if (profile != null) {
            this.unloadProfile(profile, event.getPlayer());
            Events.call(new LumineProfileSaveEvent(uuid, profile));
            this.adapter.save(uuid, profile);
         }
      }).bindWith(this);
   }

   @Override
   public void unload() {
      for (Entry<UUID, P> entry : this.profilesById.entrySet()) {
         UUID uuid = entry.getKey();
         P profile = entry.getValue();
         this.adapter.saveSync(uuid, profile);
      }
   }

   @Override
   public void saveAll() {
      for (Entry<UUID, P> entry : this.profilesById.entrySet()) {
         UUID uuid = entry.getKey();
         P profile = entry.getValue();
         Events.call(new LumineProfileSaveEvent(uuid, profile));
         this.adapter.save(uuid, profile);
      }
   }

   public void loadProfile(Player player) {
      this.loadProfile(player, 0);
   }

   public void loadProfile(Player player, int subProfile) {
      this.adapter.load(player.getUniqueId(), subProfile).thenAcceptSync(maybeProfile -> {
         P profile;
         if (!maybeProfile.isPresent()) {
            profile = this.createProfile(player.getUniqueId(), player.getName(), subProfile);
            Events.call(new LumineProfileSaveEvent(profile.getUniqueId(), profile));
            this.adapter.save(profile.getUniqueId(), profile);
         } else {
            profile = maybeProfile.get();
         }

         this.profilesById.put(player.getUniqueId(), profile);
         this.profilesByName.put(player.getName(), profile);
         this.initProfile(profile, player);
         Events.call(new LumineProfileLoadEvent(player, profile));
      });
   }

   public abstract P createProfile(UUID uuid, String str, int value);

   public abstract void initProfile(P p, Player player);

   public abstract void unloadProfile(P p, Player player);

   @Override
   public P getProfile(Player player) {
      return this.profilesById.getOrDefault(player.getUniqueId(), null);
   }

   @Override
   public Optional<P> getProfile(UUID uniqueId) {
      return Optional.ofNullable(this.profilesById.getOrDefault(uniqueId, null));
   }

   @Override
   public Optional<P> getProfile(String name) {
      return Optional.ofNullable(this.profilesByName.getOrDefault(name, null));
   }

   @Override
   public Collection<P> getKnownProfiles() {
      return this.profilesById.values();
   }

   public Promise<Optional<P>> awaitProfile(Player player) {
      Promise<Optional<P>> promise = Promise.empty();
      UUID uuid = player.getUniqueId();
      if (this.profilesById.containsKey(uuid)) {
         promise.supply(Optional.of(this.profilesById.get(uuid)));
      } else {
         Events.subscribe(LumineProfileLoadEvent.class, EventPriority.MONITOR)
            .expireAfter(1L)
            .expireAfter(5L, TimeUnit.SECONDS)
            .filter(event -> event.getPlayer().getUniqueId().equals(player.getUniqueId()))
            .filter(event -> this.clazz.isInstance(event.getProfile()))
            .biHandler((handler, event) -> {
               promise.supply((Optional<P>)Optional.of(event.getProfile()));
               handler.terminate();
            });
         if (this.profilesById.containsKey(uuid)) {
            promise.supply(Optional.of(this.profilesById.get(uuid)));
         }
      }

      return promise;
   }

   @Override
   public Promise<Optional<P>> lookupProfile(UUID uniqueId, int subProfile) {
      return this.adapter.load(uniqueId, subProfile);
   }

   @Override
   public Promise<Optional<P>> lookupProfile(String name, int subProfile) {
      return this.adapter.loadByName(name, subProfile);
   }

   @Override
   public Collection<P> getAvailableProfiles(UUID uniqueId) {
      if (!this.clazz.isAssignableFrom(MultiProfile.class)) {
         throw new IllegalStateException("PlayerRepository must implement MultiProfile to look up profiles");
      } else {
         return this.adapter.getAvailableProfiles(uniqueId);
      }
   }

   @Override
   public Collection<P> getAvailableProfiles(String name) {
      if (!this.clazz.isAssignableFrom(MultiProfile.class)) {
         throw new IllegalStateException("PlayerRepository must implement MultiProfile to look up profiles");
      } else {
         return this.adapter.getAvailableProfiles(name);
      }
   }

   public PlayerStorageAdapter<P> getAdapter() {
      return this.adapter;
   }
}
