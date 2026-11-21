package io.lumine.mythic.core.spawning.random;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.persistence.PersistentDataType;

@Getter
@ToString
public class PlayerGroup {
   private static final SpawnCategory[] SPAWN_CATEGORIES = SpawnCategory.values();
   private final RandomSpawnerManager manager;
   private final AbstractWorld world;
   private final List<AbstractPlayer> players;

   public PlayerGroup(RandomSpawnerManager manager, AbstractWorld world, List<AbstractPlayer> clusterEntities) {
      this.manager = manager;
      this.players = clusterEntities;
      this.world = world;
   }

   public PlayerGroup.LocalMobData getLocalMobData() {
      return this.getLocalMobData(Double.POSITIVE_INFINITY);
   }

   public PlayerGroup.LocalMobData getLocalMobData(double localMobCap) {
      PlayerGroup.LocalMobData localMobData = new PlayerGroup.LocalMobData();
      int spawnRadiusMax = this.getManager().getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMax();
      int chunkRadius = spawnRadiusMax / 16;
      World bukkitWorld = BukkitAdapter.adapt(this.world);
      VolatileWorldHandler worldHandler = this.getManager().getPlugin().getVolatileCodeHandler().getWorldHandler();
      boolean folia = ServerVersion.isFolia();
      int[] categoryCounts = new int[SPAWN_CATEGORIES.length];
      LongOpenHashSet visited = this.players.size() > 1 ? new LongOpenHashSet((2 * chunkRadius + 1) * (2 * chunkRadius + 1)) : null;

      playerLoop:
      for (AbstractPlayer player : this.players) {
         AbstractLocation playerLoc = player.getLocation();
         int chunkX = playerLoc.getBlockX() >> 4;
         int chunkZ = playerLoc.getBlockZ() >> 4;

         for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
               int cx = dx + chunkX;
               int cz = dz + chunkZ;
               if ((visited == null || visited.add((long)cx << 32 | cz & 4294967295L)) && (!folia || Bukkit.isOwnedByCurrentRegion(bukkitWorld, cx, cz))) {
                  for (Entity entity : worldHandler.getLoadedChunkEntities(this.world, cx, cz)) {
                     localMobData.getLocalMobs().add(entity);
                     categoryCounts[entity.getSpawnCategory().ordinal()]++;
                  }

                  if (categoryCounts[SpawnCategory.MONSTER.ordinal()] > localMobCap || localMobData.getLocalMobs().size() > localMobCap * 2.0) {
                     break playerLoop;
                  }
               }
            }
         }
      }

      for (int i = 0; i < categoryCounts.length; i++) {
         if (categoryCounts[i] > 0) {
            localMobData.getLocalMobCount().put(SPAWN_CATEGORIES[i], categoryCounts[i]);
         }
      }

      return localMobData;
   }

   public RandomSpawnPoint getRandomSpawnablePoint() {
      long startTime = System.currentTimeMillis();
      int spawnRadiusMax = this.getManager().getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMax();
      int spawnRadiusMaxSq = Numbers.floor(Math.pow(spawnRadiusMax, 2.0));
      int spawnRadiusMinSq = Numbers.floor(Math.pow(this.getManager().getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMin(), 2.0));
      int spawnRadiusY = this.getManager().getPlugin().getConfiguration().getRandomSpawningPlayerRadiusY();
      int attempt = 0;

      whileLoop:
      while (++attempt <= this.getMaxGenerationAttempts()) {
         if (System.currentTimeMillis() - startTime > this.getMaxGenerationTime()) {
            return null;
         }

         AbstractPlayer player = this.players.get(Numbers.randomInt(this.players.size()));
         AbstractWorld world = player.getWorld();
         Location location = BukkitAdapter.adapt(player.getLocation());
         int nx = (int)(location.getX() - spawnRadiusMax) + Numbers.randomInt(spawnRadiusMax * 2);
         int nz = (int)(location.getZ() - spawnRadiusMax) + Numbers.randomInt(spawnRadiusMax * 2);
         Location spawnLocation = new Location(location.getWorld(), nx, 0.0, nz);
         int scx = spawnLocation.getBlockX() >> 4;
         int scz = spawnLocation.getBlockZ() >> 4;
         if (!ServerVersion.isFolia() || Bukkit.isOwnedByCurrentRegion(spawnLocation.getWorld(), scx, scz)) {
            if (!world.isChunkLoaded(scx, scz)) {
               MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Skipping player, area chunks not loaded.");
            } else {
               Chunk spawnChunk = spawnLocation.getWorld().getChunkAt(scx, scz, false);
               if (spawnChunk != null && spawnChunk.isLoaded()) {
                  int ny = (int)(location.getY() - spawnRadiusY) + Numbers.randomInt(spawnRadiusY * 2);
                  int maxBlockY = spawnLocation.getWorld().getMaxHeight() - 1;
                  int maxHeight = Math.min(spawnLocation.getWorld().getHighestBlockYAt(nx, nz) + 2, maxBlockY - 2);
                  int minHeight = spawnLocation.getWorld().getMinHeight() + 1;
                  ny = Math.clamp((long)ny, minHeight, maxHeight);
                  spawnLocation.setY(ny);

                  for (AbstractPlayer clusterPlayer : this.players) {
                     Location playerLocation = BukkitAdapter.adapt(clusterPlayer.getLocation());
                     if (spawnLocation.getWorld().equals(playerLocation.getWorld()) && spawnLocation.distanceSquared(playerLocation) < spawnRadiusMinSq) {
                        MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Too close to player, skipping");
                        continue whileLoop;
                     }
                  }

                  BlockData spawnBlock = spawnLocation.getBlock().getBlockData();
                  RandomSpawnPoint point;
                  if (spawnBlock.getMaterial() != Material.LAVA && spawnBlock.getMaterial() != Material.WATER) {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Adding Land point");
                     SpawnPointType type = null;
                     if (spawnBlock.getMaterial().isSolid()) {
                        type = SpawnPointType.LAND;
                     } else {
                        for (int j = 0; j < spawnRadiusY * 2 && ny - 1 >= minHeight && ny <= maxHeight; j++) {
                           spawnLocation.setY(--ny);
                           spawnBlock = spawnLocation.getBlock().getBlockData();
                           if (spawnBlock.getMaterial() == Material.WATER) {
                              type = SpawnPointType.SEA;
                              break;
                           }

                           if (spawnBlock.getMaterial() == Material.LAVA) {
                              type = SpawnPointType.LAVA;
                              break;
                           }

                           if (spawnBlock.getMaterial().isSolid()) {
                              type = SpawnPointType.LAND;
                              break;
                           }
                        }

                        if (type == null) {
                           continue;
                        }
                     }

                     if (type == SpawnPointType.LAND) {
                        for (int k = 1; k < 3; k++) {
                           spawnLocation.setY(spawnLocation.getY() + 1.0);
                           spawnBlock = spawnLocation.getBlock().getBlockData();
                           if (spawnBlock.getMaterial().isSolid()) {
                              type = null;
                              break;
                           }
                        }
                     }

                     if (type == null) {
                        MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Couldn't find land position this time");
                        continue;
                     }

                     spawnLocation.setY(ny);
                     if (type == SpawnPointType.LAND) {
                        point = new RandomSpawnPoint(BukkitAdapter.adapt(spawnLocation).center().add(0.0, 0.5, 0.0), type);
                     } else {
                        point = new RandomSpawnPoint(BukkitAdapter.adapt(spawnLocation).center().subtract(0.0, 0.5, 0.0), type);
                     }
                  } else if (spawnBlock.getMaterial() == Material.LAVA) {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Found lava. Adding Lava point");
                     point = new RandomSpawnPoint(BukkitAdapter.adapt(spawnLocation).center(), SpawnPointType.LAVA);
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Found water. Adding Sea point");
                     point = new RandomSpawnPoint(BukkitAdapter.adapt(spawnLocation).center(), SpawnPointType.SEA);
                  }

                  point.setTriggeringPlayer(player);
                  return point;
               }
            }
         }
      }

      return null;
   }

   private int getMaxGenerationTime() {
      return this.manager.getPlugin().getConfiguration().getRandomSpawningMaxGenerationTimeMillis();
   }

   private int getMaxGenerationAttempts() {
      return this.manager.getPlugin().getConfiguration().getRandomSpawningMaxGenerationAttempts();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PlayerGroup other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$manager = this.getManager();
         Object other$manager = other.getManager();
         if (this$manager == null ? other$manager == null : this$manager.equals(other$manager)) {
            Object this$world = this.getWorld();
            Object other$world = other.getWorld();
            if (this$world == null ? other$world == null : this$world.equals(other$world)) {
               Object this$players = this.getPlayers();
               Object other$players = other.getPlayers();
               return this$players == null ? other$players == null : this$players.equals(other$players);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PlayerGroup;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $manager = this.getManager();
      result = result * 59 + ($manager == null ? 43 : $manager.hashCode());
      Object $world = this.getWorld();
      result = result * 59 + ($world == null ? 43 : $world.hashCode());
      Object $players = this.getPlayers();
      return result * 59 + ($players == null ? 43 : $players.hashCode());
   }

   @Setter
   @ToString
   public static class LocalMobData {
      @Getter
      private Collection<Entity> localMobs = new ArrayList<>();
      @Getter
      private Map<SpawnCategory, Integer> localMobCount = Maps.newConcurrentMap();
      private Map<String, Integer> localMobTypes = null;

      public Map<String, Integer> getLocalMobTypes() {
         if (this.localMobTypes != null) {
            return this.localMobTypes;
         } else {
            this.localMobTypes = Maps.newConcurrentMap();

            for (Entity entity : this.localMobs) {
               String mobType = (String)entity.getPersistentDataContainer().getOrDefault(MobKeys.TYPE, PersistentDataType.STRING, entity.getType().toString());
               this.localMobTypes.compute(mobType, (k, v) -> v == null ? 1 : v + 1);
            }

            return this.localMobTypes;
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PlayerGroup.LocalMobData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$localMobs = this.getLocalMobs();
            Object other$localMobs = other.getLocalMobs();
            if (this$localMobs == null ? other$localMobs == null : this$localMobs.equals(other$localMobs)) {
               Object this$localMobCount = this.getLocalMobCount();
               Object other$localMobCount = other.getLocalMobCount();
               if (this$localMobCount == null ? other$localMobCount == null : this$localMobCount.equals(other$localMobCount)) {
                  Object this$localMobTypes = this.getLocalMobTypes();
                  Object other$localMobTypes = other.getLocalMobTypes();
                  return this$localMobTypes == null ? other$localMobTypes == null : this$localMobTypes.equals(other$localMobTypes);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof PlayerGroup.LocalMobData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $localMobs = this.getLocalMobs();
         result = result * 59 + ($localMobs == null ? 43 : $localMobs.hashCode());
         Object $localMobCount = this.getLocalMobCount();
         result = result * 59 + ($localMobCount == null ? 43 : $localMobCount.hashCode());
         Object $localMobTypes = this.getLocalMobTypes();
         return result * 59 + ($localMobTypes == null ? 43 : $localMobTypes.hashCode());
      }
   }
}
