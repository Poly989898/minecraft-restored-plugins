package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.Schedulers;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class Metadata {
   private static LoadingCache<UUID, MetadataMap> players = null;
   private static LoadingCache<UUID, MetadataMap> entities = null;
   private static LoadingCache<UUID, MetadataMap> worlds = null;

   private static synchronized void setup() {
      if (players == null) {
         players = makeCache();
         entities = makeCache();
         worlds = makeCache();
         Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR).handler(e -> {
            MetadataMap map = (MetadataMap)players.asMap().remove(e.getPlayer().getUniqueId());
            if (map != null) {
               map.clear();
            }
         });
         Schedulers.async().runRepeating(() -> {
            players.asMap().values().removeIf(MetadataMap::isEmpty);
            entities.asMap().values().removeIf(MetadataMap::isEmpty);
            worlds.asMap().values().removeIf(MetadataMap::isEmpty);
         }, 1200L + ThreadLocalRandom.current().nextInt(20), 1200L);
      }
   }

   private static <T> LoadingCache<T, MetadataMap> makeCache() {
      return CacheBuilder.newBuilder().build(new CacheLoader<T, MetadataMap>() {
         public MetadataMap load(T uuid) {
            return MetadataMap.create();
         }
      });
   }

   public static MetadataMap provide(Object obj) {
      if (obj instanceof Player) {
         return provideForPlayer((Player)obj);
      } else if (obj instanceof UUID) {
         return provideForPlayer((UUID)obj);
      } else if (obj instanceof Entity) {
         return provideForEntity((Entity)obj);
      } else if (obj instanceof World) {
         return provideForWorld((World)obj);
      } else {
         throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
      }
   }

   public static MetadataMap provideForPlayer(Player player) {
      setup();
      return (MetadataMap)players.getUnchecked(player.getUniqueId());
   }

   public static MetadataMap provideForPlayer(UUID uuid) {
      setup();
      return (MetadataMap)players.getUnchecked(uuid);
   }

   public static <T> Map<Player, T> lookupPlayersWithKey(MetadataKey<T> key) {
      Preconditions.checkNotNull("key", key);
      setup();
      Builder<Player, T> ret = ImmutableMap.builder();
      players.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
         Player player = Players.getNullable(uuid);
         if (player != null) {
            ret.put(player, t);
         }
      }));
      return ret.build();
   }

   public static MetadataMap provideForEntity(Entity entity) {
      setup();
      return entity instanceof Player ? provideForPlayer((Player)entity) : (MetadataMap)entities.getUnchecked(entity.getUniqueId());
   }

   public static <T> Map<Entity, T> lookupEntitiesWithKey(MetadataKey<T> key) {
      Preconditions.checkNotNull("key", key);
      setup();
      Builder<Entity, T> ret = ImmutableMap.builder();
      entities.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
         Entity entity = Bukkit.getEntity(uuid);
         if (entity != null) {
            ret.put(entity, t);
         }
      }));
      return ret.build();
   }

   public static MetadataMap provideForWorld(World world) {
      setup();
      return (MetadataMap)worlds.getUnchecked(world.getUID());
   }

   public static <T> Map<World, T> lookupWorldsWithKey(MetadataKey<T> key) {
      Preconditions.checkNotNull("key", key);
      setup();
      Builder<World, T> ret = ImmutableMap.builder();
      worlds.asMap().forEach((uuid, map) -> map.get(key).ifPresent(t -> {
         World world = Bukkit.getWorld(uuid);
         if (world != null) {
            ret.put(world, t);
         }
      }));
      return ret.build();
   }

   private Metadata() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
