package io.lumine.mythic.core.spawning.random.generators;

import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.skills.pins.PinRegion;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.lumine.mythic.core.spawning.random.RandomSpawner;
import io.lumine.mythic.core.spawning.random.RandomSpawnerManager;
import io.lumine.mythic.core.spawning.random.SpawnPointType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RegionalClusterGenerator extends ClusterGenerator {
   private final Map<String, Map<String, Map<SpawnPointType, List<RandomSpawner>>>> worldTagCache;

   public RegionalClusterGenerator(RandomSpawnerManager manager) {
      super(manager);
      Map<String, Map<SpawnPointType, List<RandomSpawner>>> spawnerMap = manager.getRandomSpawnersAdd();
      Map<String, Map<String, Map<SpawnPointType, List<RandomSpawner>>>> cache = new HashMap<>();

      for (Entry<String, Map<SpawnPointType, List<RandomSpawner>>> worldEntry : spawnerMap.entrySet()) {
         String world = worldEntry.getKey();
         Map<String, Map<SpawnPointType, List<RandomSpawner>>> tagMap = new HashMap<>();

         for (Entry<SpawnPointType, List<RandomSpawner>> typeEntry : worldEntry.getValue().entrySet()) {
            SpawnPointType type = typeEntry.getKey();

            for (RandomSpawner spawner : typeEntry.getValue()) {
               for (String tag : spawner.getRegionTags()) {
                  tagMap.computeIfAbsent(tag, k -> new HashMap<>()).computeIfAbsent(type, k -> new ArrayList<>()).add(spawner);
               }
            }
         }

         cache.put(world, Collections.unmodifiableMap(tagMap));
      }

      this.worldTagCache = Collections.unmodifiableMap(cache);
   }

   @Override
   protected Map<SpawnPointType, List<RandomSpawner>> getApplicableSpawners(RandomSpawnPoint point) {
      Map<SpawnPointType, List<RandomSpawner>> result = new HashMap<>();
      String worldName = point.getWorld().getName();
      Map<String, Map<SpawnPointType, List<RandomSpawner>>> tagMap = this.worldTagCache.get(worldName);
      if (tagMap == null) {
         return result;
      } else {
         List<PinRegion> regions = this.getManager().getPlugin().getPinManager().getRegions(point.getLocation());
         if (regions != null && !regions.isEmpty()) {
            Collection<String> regionTags = new ArrayList<>();

            for (PinRegion region : regions) {
               regionTags.addAll(region.getTags());
            }

            for (String tag : regionTags) {
               Map<SpawnPointType, List<RandomSpawner>> typeMap = tagMap.get(tag);
               if (typeMap != null) {
                  for (Entry<SpawnPointType, List<RandomSpawner>> entry : typeMap.entrySet()) {
                     SpawnPointType type = entry.getKey();
                     result.computeIfAbsent(type, k -> new ArrayList<>()).addAll(entry.getValue());
                  }
               }
            }

            return result;
         } else {
            return result;
         }
      }
   }

   public void tickz() {
      long start = System.nanoTime();
      super.tick();
      long end = System.nanoTime();
      Log.info("Regional Generator tick {0}", end - start);
   }
}
