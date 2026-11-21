package io.lumine.mythic.core.spawning.random.generators;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.caffeine.cache.Cache;
import io.lumine.mythic.bukkit.utils.caffeine.cache.Caffeine;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.spawning.random.RandomSpawnGenerator;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.lumine.mythic.core.spawning.random.RandomSpawner;
import io.lumine.mythic.core.spawning.random.RandomSpawnerManager;
import io.lumine.mythic.core.spawning.random.SpawnPointType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.world.WorldUnloadEvent;

public class LegacyGenerator extends RandomSpawnGenerator {
   public static final double MAX_MOBS_PER_CYCLE_PERCENT = 0.1;
   public Map<UUID, Cache<RandomSpawnPoint, Boolean>> generatedSpawnPoints = Maps.newConcurrentMap();
   private int spawnRadiusY = this.getPlugin().getConfiguration().getRandomSpawningPlayerRadiusY();
   private int minSpawnRadiusSquared = Numbers.floor(Math.pow(this.getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMin(), 2.0));

   public LegacyGenerator(RandomSpawnerManager manager) {
      super(manager);
      Events.subscribe(WorldUnloadEvent.class).handler(event -> {
         World world = event.getWorld();
         this.generatedSpawnPoints.remove(world.getUID());
      }).bindWith(this);
   }

   @Override
   public void tick() {
      for (AbstractWorld world : this.getPlugin().getBootstrap().getWorlds()) {
         if (world.getPlayers().size() != 0) {
            this.getPlugin().getTimingsHandler().markRandomGeneratorsNew();
            long startTime = System.currentTimeMillis();
            ArrayList<AbstractPlayer> players = Lists.newArrayList(world.getPlayers());
            Collections.shuffle(players);
            int spawnRadiusMax = this.getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMax();
            int spawnRadiusMin = this.getPlugin().getConfiguration().getRandomSpawningPlayerRadiusMin();
            int spawnRadiusY = this.getPlugin().getConfiguration().getRandomSpawningPlayerRadiusY();

            for (AbstractPlayer player : players) {
               if (player != null && !player.isInCreativeMode()) {
                  if (System.currentTimeMillis() - startTime > this.getPlugin().getConfiguration().getRandomSpawningMaxGenerationTimeMillis()) {
                     break;
                  }

                  Schedulers.runOnRegion(player.getBukkitEntity(), () -> {
                     try {
                        this.generatePointsNearPlayer(world, player, spawnRadiusMax, spawnRadiusMin, spawnRadiusY);
                     } catch (Exception var7x) {
                        var7x.printStackTrace();
                     }
                  });
               }
            }
         }
      }

      MythicBukkit.inst().getTimingsHandler().markRandomGeneratorsComplete();
   }

   private void generatePointsNearPlayer(AbstractWorld world, AbstractPlayer player, int spawnRadiusMax, int spawnRadiusMin, int spawnRadiusY) {
      long startTime = System.currentTimeMillis();
      int n = 0;
      int bucketLand = this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecond();
      int bucketSea = this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecondSea();
      int bucketAir = this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecondAir();
      int bucketLava = this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecondLava();
      int totalBucket = bucketLand + bucketSea + bucketAir + bucketLava;
      MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Generating RandomSpawnPoints near Player {0}", player.getName());

      while (totalBucket > 0 && System.currentTimeMillis() - startTime <= this.getPlugin().getConfiguration().getRandomSpawningMaxGenerationTimeMillis()) {
         Location location = BukkitAdapter.adapt(player.getLocation());
         Location spawnLocation = new Location(location.getWorld(), 0.0, 0.0, 0.0);
         int nx = (int)(location.getX() - spawnRadiusMax) + Numbers.randomInt(spawnRadiusMax * 2);
         int nz = (int)(location.getZ() - spawnRadiusMax) + Numbers.randomInt(spawnRadiusMax * 2);
         spawnLocation.setX(nx);
         spawnLocation.setZ(nz);
         if (!player.getWorld().isChunkLoaded(spawnLocation.getBlockX() >> 4, spawnLocation.getBlockZ() >> 4)) {
            MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Skipping player, area chunks not loaded.");
            totalBucket--;
         } else {
            int ny = (int)(location.getY() - spawnRadiusY) + Numbers.randomInt(spawnRadiusY * 2);
            int maxHeight = location.getChunk().getChunkSnapshot(true, false, false).getHighestBlockYAt(Math.abs(nx % 16), Math.abs(nz % 16)) + 1;
            int minHeight = location.getWorld().getMinHeight();
            if (ny < minHeight) {
               ny = minHeight;
            } else if (ny > maxHeight) {
               ny = maxHeight;
            }

            spawnLocation.setY(ny);
            if (spawnLocation.distanceSquared(location) < spawnRadiusMin) {
               MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Distance too far, skipping");
            } else {
               RandomSpawnPoint point;
               if (spawnLocation.getBlock().isLiquid()) {
                  if (spawnLocation.getBlock().getType() == Material.WATER) {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Found water. Adding Sea point");
                     AbstractLocation returnLocation = new AbstractLocation(player.getWorld(), nx + 0.5, (double)ny, nz + 0.5);
                     point = new RandomSpawnPoint(returnLocation, SpawnPointType.SEA);
                  } else {
                     if (spawnLocation.getBlock().getType() != Material.LAVA) {
                        continue;
                     }

                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Found lava. Adding Lava point");
                     AbstractLocation returnLocation = new AbstractLocation(player.getWorld(), nx + 0.5, (double)ny, nz + 0.5);
                     point = new RandomSpawnPoint(returnLocation, SpawnPointType.LAVA);
                  }
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "+ Adding Land point");
                  SpawnPointType type = null;
                  if (spawnLocation.getBlock().getType().isSolid()) {
                     type = SpawnPointType.LAND;
                  } else {
                     boolean goodd = false;

                     for (int j = 0; j < spawnRadiusY * 2; j++) {
                        spawnLocation.setY(--ny);
                        if (spawnLocation.getBlock().getType() == Material.WATER) {
                           type = SpawnPointType.SEA;
                           break;
                        }

                        if (spawnLocation.getBlock().getType() == Material.LAVA) {
                           type = SpawnPointType.LAVA;
                           break;
                        }

                        if (spawnLocation.getBlock().getType().isSolid()) {
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
                        if (spawnLocation.getBlock().getType().isSolid()) {
                           MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! {0} is NOT pathable, skipping", spawnLocation.getBlock().getType().toString());
                           type = null;
                           break;
                        }
                     }
                  }

                  if (type == null) {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Couldn't find land position this time");
                     continue;
                  }

                  if (type == SpawnPointType.LAND) {
                     AbstractLocation returnLocation = new AbstractLocation(player.getWorld(), nx + 0.5, (double)(ny + 1), nz + 0.5);
                     point = new RandomSpawnPoint(returnLocation, type);
                  } else {
                     AbstractLocation returnLocation = new AbstractLocation(player.getWorld(), nx + 0.5, (double)ny, nz + 0.5);
                     point = new RandomSpawnPoint(returnLocation, type);
                  }
               }

               if (point != null) {
                  Iterator<RandomSpawner> it = this.getManager().getRandomSpawnersDeny().iterator();
                  boolean good = true;

                  while (it.hasNext()) {
                     RandomSpawner RS = it.next();
                     if (RS.checkSpawn(point)) {
                        good = false;
                        break;
                     }
                  }

                  if (good) {
                     boolean cached = false;
                     if (point.getPointType() == SpawnPointType.LAND && bucketLand > 0) {
                        this.generatedSpawnPoints.compute(world.getUniqueId(), (w, l) -> l == null ? this.createSpawningCache() : l).put(point, true);
                        bucketLand--;
                        n++;
                        cached = true;
                     } else if (point.getPointType() == SpawnPointType.SEA && bucketSea > 0) {
                        this.generatedSpawnPoints.compute(world.getUniqueId(), (w, l) -> l == null ? this.createSpawningCache() : l).put(point, true);
                        bucketSea--;
                        n++;
                        cached = true;
                     } else if (point.getPointType() == SpawnPointType.LAVA && bucketLava > 0) {
                        this.generatedSpawnPoints.compute(world.getUniqueId(), (w, l) -> l == null ? this.createSpawningCache() : l).put(point, true);
                        bucketLava--;
                        n++;
                        cached = true;
                     }

                     if (cached) {
                        totalBucket--;
                     }
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "! Skipped spawn point due to DENY action");
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "++ Generated Random Point at {0}", point.getLocation().toString());
               }
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "[i] Generated Points: {0}", n);
      MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "[i] Land {0} Sea {1} Lava {2}", bucketLand, bucketSea, bucketLava);
      MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "[i] Time taken: {0}", System.currentTimeMillis() - startTime);
   }

   public void spawnMobs() {
      for (AbstractWorld world : this.getPlugin().getBootstrap().getWorlds()) {
         if (world.getPlayerCount() != 0) {
            Map<SpawnPointType, List<RandomSpawner>> worldSpawners = this.getManager().getRandomSpawnersAdd().get(world.getName());
            if (worldSpawners != null) {
               MythicSpawnState spawnState = this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getSpawnState(world);
               int mythicMobCap = this.getManager().getMythicMobCap(spawnState);
               if (spawnState.getMonsterCount() < mythicMobCap) {
                  Cache<RandomSpawnPoint, Boolean> cache = this.generatedSpawnPoints
                     .compute(world.getUniqueId(), (w, l) -> l == null ? this.createSpawningCache() : l);
                  int maxMobsPerCycle = Numbers.ceil(mythicMobCap * 0.1);
                  int available = Math.min(mythicMobCap - spawnState.getMonsterCount(), maxMobsPerCycle);

                  for (RandomSpawnPoint rsp : cache.asMap().keySet()) {
                     cache.invalidate(rsp);
                     List<RandomSpawner> spawners = worldSpawners.get(rsp.getPointType());
                     Location spawnLocation = BukkitAdapter.adapt(rsp.getLocation());
                     if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(spawnLocation)) {
                        Schedulers.of(spawnLocation).run(() -> this.getManager().doSpawns(rsp, spawners));
                        available--;
                     } else if (this.getManager().doSpawns(rsp, spawners)) {
                        available--;
                     }

                     if (available <= 0) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   public Cache<RandomSpawnPoint, Boolean> createSpawningCache() {
      return Caffeine.newBuilder().maximumSize(1000L).expireAfterWrite(Duration.ofSeconds(30L)).build(key -> true);
   }
}
