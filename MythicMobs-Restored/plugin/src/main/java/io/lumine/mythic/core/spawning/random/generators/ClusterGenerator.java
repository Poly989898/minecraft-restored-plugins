package io.lumine.mythic.core.spawning.random.generators;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.lib.math3.ml.clustering.Cluster;
import io.lumine.mythic.bukkit.utils.lib.math3.ml.clustering.DBSCANClusterer;
import io.lumine.mythic.bukkit.utils.lib.math3.ml.clustering.DoublePoint;
import io.lumine.mythic.core.spawning.random.PlayerGroup;
import io.lumine.mythic.core.spawning.random.RandomSpawnGenerator;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.lumine.mythic.core.spawning.random.RandomSpawner;
import io.lumine.mythic.core.spawning.random.RandomSpawnerManager;
import io.lumine.mythic.core.spawning.random.SpawnPointType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.SpawnCategory;

public class ClusterGenerator extends RandomSpawnGenerator {
   private final Set<UUID> inFlightGroups = ConcurrentHashMap.newKeySet();

   public ClusterGenerator(RandomSpawnerManager manager) {
      super(manager);
   }

   protected boolean isApplicableWorld(AbstractWorld world) {
      return this.getManager().getRandomSpawnersAdd().containsKey(world.getName());
   }

   protected Map<SpawnPointType, List<RandomSpawner>> getApplicableSpawners(RandomSpawnPoint point) {
      return this.getManager().getRandomSpawnersAdd().get(point.getWorld().getName());
   }

   @Override
   public void tick() {
      for (AbstractWorld world : this.getPlugin().getBootstrap().getWorlds()) {
         if (!world.getPlayers().isEmpty() && this.isApplicableWorld(world)) {
            List<PlayerGroup> playerGroups = this.getPlayerGroups(world);
            MythicSpawnState spawnState = this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getSpawnState(world);
            if (spawnState.getMonsterCount() <= this.getManager().getMythicMobCap(spawnState)) {
               Collections.shuffle(playerGroups);
               double limitMultiplier = this.getPlugin().getConfiguration().getRandomSpawningLimitMultiplier();
               double groupMultiplier = this.getPlugin().getConfiguration().getRandomSpawningGroupMultiplier();

               for (PlayerGroup playerGroup : playerGroups) {
                  if (!playerGroup.getPlayers().isEmpty()) {
                     AbstractPlayer anchorPlayer = playerGroup.getPlayers().get(0);
                     UUID anchorId = anchorPlayer.getUniqueId();
                     if (this.inFlightGroups.add(anchorId)) {
                        Location anchor = BukkitAdapter.adapt(anchorPlayer.getLocation());
                        Schedulers.of(anchor).run(() -> {
                           try {
                              this.evaluateGroup(playerGroup, spawnState, limitMultiplier, groupMultiplier);
                           } catch (Exception var12x) {
                              var12x.printStackTrace();
                           } finally {
                              this.inFlightGroups.remove(anchorId);
                           }
                        });
                     }
                  }
               }
            }
         }
      }
   }

   private void evaluateGroup(PlayerGroup playerGroup, MythicSpawnState spawnState, double limitMultiplier, double groupMultiplier) {
      double localMobCap = spawnState.getMaxMobsPerPlayer() * limitMultiplier;
      if (playerGroup.getPlayers().size() > 1) {
         localMobCap *= (playerGroup.getPlayers().size() - 1) * groupMultiplier;
      }

      PlayerGroup.LocalMobData localMobData = playerGroup.getLocalMobData(localMobCap);
      Map<SpawnCategory, Integer> mobTypeCounts = localMobData.getLocalMobCount();
      int localMobCount = 0;

      for (int v : mobTypeCounts.values()) {
         localMobCount += v;
      }

      Integer localMonsterCount = mobTypeCounts.getOrDefault(SpawnCategory.MONSTER, 0);
      if (!(localMonsterCount.intValue() > localMobCap) && !(localMobCount > localMobCap * 2.0)) {
         RandomSpawnPoint spawnLocation = playerGroup.getRandomSpawnablePoint();
         if (spawnLocation != null) {
            spawnLocation.setNearbyPlayers(playerGroup.getPlayers().size());
            spawnLocation.setLocalMobCount(localMobCount);

            for (RandomSpawner denySpawner : this.getManager().getRandomSpawnersDeny()) {
               if (denySpawner.checkSpawn(spawnLocation)) {
                  return;
               }
            }

            Map<SpawnPointType, List<RandomSpawner>> applicableSpawners = this.getApplicableSpawners(spawnLocation);
            if (applicableSpawners != null) {
               for (RandomSpawner spawner : applicableSpawners.getOrDefault(spawnLocation.getPointType(), Collections.emptyList())) {
                  if (spawner.checkSpawn(spawnLocation)) {
                     if (spawner.getMaxSpawnerMobCount() > 0) {
                        Map<String, Integer> localMobTypes = localMobData.getLocalMobTypes();
                        int count = 0;

                        for (String type : spawner.getConfiguredMobTypes()) {
                           count += localMobTypes.getOrDefault(type, 0);
                        }

                        if (count >= spawner.getMaxSpawnerMobCount()) {
                           continue;
                        }
                     }

                     Schedulers.of(BukkitAdapter.adapt(spawnLocation.getLocation())).run(() -> spawner.spawn(spawnLocation));
                     break;
                  }
               }
            }
         }
      }
   }

   private List<PlayerGroup> getPlayerGroups(AbstractWorld world) {
      double groupingRadius = this.getPlugin().getConfiguration().getRandomSpawningGroupDistance();
      List<AbstractPlayer> playerList = world.getPlayers();
      int playerCount = playerList.size();
      List<DoublePoint> points = new ArrayList<>(playerCount);
      IdentityHashMap<DoublePoint, Integer> pointIndex = new IdentityHashMap<>(playerCount);

      for (int i = 0; i < playerCount; i++) {
         AbstractLocation loc = playerList.get(i).getLocation();
         DoublePoint dp = new DoublePoint(new double[]{loc.getX(), loc.getZ()});
         points.add(dp);
         pointIndex.put(dp, i);
      }

      DBSCANClusterer<DoublePoint> clusterer = new DBSCANClusterer<>(groupingRadius, 0);
      List<PlayerGroup> clusters = new ArrayList<>();

      for (Cluster<DoublePoint> cluster : clusterer.cluster(points)) {
         List<AbstractPlayer> clusterEntities = new ArrayList<>(cluster.getPoints().size());

         for (DoublePoint dp : cluster.getPoints()) {
            clusterEntities.add(playerList.get(pointIndex.get(dp)));
         }

         clusters.add(new PlayerGroup(this.getManager(), world, clusterEntities));
      }

      return clusters;
   }
}
