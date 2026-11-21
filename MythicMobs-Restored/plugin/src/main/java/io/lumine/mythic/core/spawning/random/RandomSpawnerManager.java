package io.lumine.mythic.core.spawning.random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.spawning.random.generators.ClusterGenerator;
import io.lumine.mythic.core.spawning.random.generators.LegacyGenerator;
import io.lumine.mythic.core.spawning.random.generators.RegionalClusterGenerator;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class RandomSpawnerManager extends ReloadableModule<MythicBukkit> {
   private List<File> spawnFiles;
   private IOLoader<MythicBukkit> defaultSpawns;
   private List<IOLoader<MythicBukkit>> spawnLoaders;
   @Getter
   private Map<String, Map<SpawnPointType, List<RandomSpawner>>> randomSpawnersAdd = Maps.newConcurrentMap();
   @Getter
   private List<RandomSpawner> randomSpawnersReplace = Lists.newArrayList();
   @Getter
   private List<RandomSpawner> randomSpawnersDeny = Lists.newArrayList();
   private RandomSpawnGenerator generatorTask;

   public RandomSpawnerManager(MythicBukkit core) {
      super(core);
   }

   public void loadRandomSpawns() {
      MythicLogger.log("Loading Random Spawns...");
      this.defaultSpawns = new IOLoader<>(MythicBukkit.inst(), "ExampleRandomSpawns.yml", "RandomSpawns");
      this.spawnFiles = IOHandler.getAllFiles(this.defaultSpawns.getFile().getParent());
      this.spawnFiles
         .addAll(this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.RANDOM_SPAWN).stream().map(PackExecutor.ConfigFile::file).toList());
      Set<Path> loadedPaths = new HashSet<>();
      this.spawnFiles.removeIf(filex -> !loadedPaths.add(filex.toPath().toAbsolutePath().normalize()));
      this.spawnLoaders = IOHandler.getSaveLoad(MythicBukkit.inst(), this.spawnFiles, "RandomSpawns");
      this.randomSpawnersAdd.clear();
      this.randomSpawnersReplace.clear();
      this.randomSpawnersDeny.clear();

      for (IOLoader<MythicBukkit> sl : this.spawnLoaders) {
         for (String name : sl.getCustomConfig().getConfigurationSection("").getKeys(false)) {
            MythicConfig mc = new MythicConfigImpl(name, sl.getCustomConfig());
            String file = sl.getFile().getName();

            try {
               RandomSpawner rs = new RandomSpawner(this, file, name, mc);
               if (rs.getAction() == RandomSpawnerAction.ADD) {
                  if (!rs.isValid()) {
                     MythicLogger.errorSpawnConfig(rs, mc, "No valid mob types configured");
                  } else {
                     for (String world : rs.getWorldNames()) {
                        this.randomSpawnersAdd
                           .compute(world, (w, v) -> (Map<SpawnPointType, List<RandomSpawner>>)(v == null ? Maps.newConcurrentMap() : v))
                           .compute(rs.getPositionType(), (w, v) -> (List<RandomSpawner>)(v == null ? Lists.newArrayList() : v))
                           .add(rs);
                     }
                  }
               } else if (rs.getAction() == RandomSpawnerAction.DENY) {
                  this.randomSpawnersDeny.add(rs);
               } else {
                  this.randomSpawnersReplace.add(rs);
               }
            } catch (Error | Exception e) {
               MythicLogger.error("Failed to load RandomSpawner {0} in file {1} - invalid configuration", name, file);
               e.printStackTrace();
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Sorting ADD Random Spawners...");

      for (Map<SpawnPointType, List<RandomSpawner>> worldSpawners : this.randomSpawnersAdd.values()) {
         for (List<RandomSpawner> types : worldSpawners.values()) {
            this.sortSpawners(types);
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Sorting REPLACE Random Spawners...");
      this.randomSpawnersReplace = this.sortSpawners(this.randomSpawnersReplace);
      if (this.getPlugin().getConfiguration().isRandomSpawningGenerationEnabled()) {
         if (this.generatorTask != null) {
            this.generatorTask.terminate();
         }

         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Starting up SpawnPointGenerator...");
         switch (this.getPlugin().getConfiguration().getRandomSpawningGeneratorType()) {
            case LEGACY:
               if (this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecond() > 0) {
                  this.generatorTask = new LegacyGenerator(this);
               }
               break;
            case CLUSTER:
               this.generatorTask = new ClusterGenerator(this);
               break;
            case REGIONAL:
               this.generatorTask = new RegionalClusterGenerator(this);
         }
      } else if (this.generatorTask != null) {
         this.generatorTask.terminate();
         this.generatorTask = null;
      }
   }

   public int getNumberOfSpawners() {
      int i = 0;

      for (Map<SpawnPointType, List<RandomSpawner>> worldSpawners : this.randomSpawnersAdd.values()) {
         for (List<RandomSpawner> types : worldSpawners.values()) {
            i += types.size();
         }
      }

      return i;
   }

   public AbstractEntity handleSpawnEvent(RandomSpawnPoint rsp) {
      if (this.randomSpawnersReplace.isEmpty()) {
         return null;
      } else if (rsp.getReason() == SpawnReason.NATURAL && BukkitEntityType.getIsSpawning()) {
         return null;
      } else if (rsp.getEntity() == null) {
         return null;
      } else {
         boolean obeysCap = this.getPlugin().getConfiguration().isRandomSpawningReplaceObeysCap();
         if (obeysCap) {
            Entity source = BukkitAdapter.adapt(rsp.getEntity());
            if (source != null) {
               SpawnCategory category = source.getSpawnCategory();
               if (category != null && category != SpawnCategory.MISC && source.getWorld() != null) {
                  int cap = source.getWorld().getSpawnLimit(category);
                  if (cap > 0) {
                     int count = this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getMobCountNear(rsp.getEntity());
                     if (count >= cap) {
                        return null;
                     }
                  }
               }
            }
         }

         for (RandomSpawner RS : this.randomSpawnersReplace) {
            if (RS.checkSpawn(rsp)) {
               ActiveMob mob = RS.spawn(rsp);
               if (mob != null && obeysCap) {
                  this.getPlugin().getVolatileCodeHandler().getSpawningHandler().trackForSpawnCap(rsp.getEntity());
               }

               return mob != null ? mob.getEntity() : null;
            }
         }

         return null;
      }
   }

   public void load(MythicBukkit plugin) {
      if (this.generatorTask == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Starting up SpawnPointGenerator!");
         switch (this.getPlugin().getConfiguration().getRandomSpawningGeneratorType()) {
            case LEGACY:
               if (this.getPlugin().getConfiguration().getRandomSpawningPointsPerSecond() > 0) {
                  this.generatorTask = new LegacyGenerator(this);
               }
               break;
            case CLUSTER:
               this.generatorTask = new ClusterGenerator(this);
         }
      }
   }

   @Override
   public void unload() {
      if (this.generatorTask != null) {
         this.generatorTask.terminate();
         this.generatorTask = null;
      }
   }

   public MythicSpawnState getSpawnState(AbstractWorld world) {
      return this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getSpawnState(world);
   }

   public int getMythicMobCap(AbstractWorld world) {
      return this.getMythicMobCap(this.getSpawnState(world));
   }

   public int getMythicMobCap(MythicSpawnState state) {
      int limit = this.getPlugin().getConfiguration().getRandomSpawningLimit();
      if (limit == 0) {
         return Integer.MAX_VALUE;
      } else {
         return limit < 0 ? Numbers.floor(state.getMaxMobCount() * this.getPlugin().getConfiguration().getRandomSpawningLimitMultiplier()) : limit;
      }
   }

   public boolean doSpawns(RandomSpawnPoint rsp, Collection<RandomSpawner> spawners) {
      if (rsp != null && rsp.getLocation().isLoaded() && spawners != null) {
         for (RandomSpawner spawner : spawners) {
            if (spawner.checkSpawn(rsp)) {
               ActiveMob result = spawner.spawn(rsp);
               return result != null;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private List<RandomSpawner> sortSpawners(List<RandomSpawner> list) {
      for (int i = 0; i < list.size(); i++) {
         for (int q = 1; q < list.size() - i; q++) {
            RandomSpawner o = list.get(q - 1);
            RandomSpawner n = list.get(q);
            if (o.getPriority() < n.getPriority()) {
               list.set(q - 1, n);
               list.set(q, o);
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Random Spawns Sorted to:");

      for (int i = 0; i < list.size(); i++) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "* " + list.get(i).getPriority() + " = " + list.get(i).getName());
      }

      return list;
   }

   public void spawnMobs() {
      if (this.generatorTask != null && this.generatorTask instanceof LegacyGenerator oldGenerator) {
         oldGenerator.spawnMobs();
      }
   }
}
