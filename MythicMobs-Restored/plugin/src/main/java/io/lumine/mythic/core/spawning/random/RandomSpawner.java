package io.lumine.mythic.core.spawning.random;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.collections.WeightedCollection;
import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class RandomSpawner {
   public static Set<RandomSpawnPoint> spawnPoints = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static final UUID SHARED_COOLDOWN = new UUID(0L, 0L);
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final RandomSpawnerManager manager;
   @Getter
   private String name;
   private Collection<String> configuredMobTypes = null;
   private WeightedCollection<RandomSpawnMob> mobTypes = new WeightedCollection<>();
   @Getter
   private RandomSpawnerAction action;
   @Getter
   private int priority;
   @Getter
   private int maxSpawnerMobCount = -1;
   @Getter
   private int maxLocalMobCount = -1;
   @Getter
   private PlaceholderDouble level;
   private float chance;
   private PlaceholderDouble cooldown;
   private final Map<UUID, Cooldown> cooldowns = new ConcurrentHashMap<>();
   @Getter
   private Collection<String> worldNames = Lists.newArrayList();
   @Getter
   private Collection<String> regionTags = Lists.newArrayList();
   @Getter
   private Collection<AbstractWorld> worlds = Lists.newArrayList();
   @Getter
   private Collection<AbstractBiome> biomes = Sets.newConcurrentHashSet();
   @Getter
   private Collection<AbstractStructure> structures = Sets.newConcurrentHashSet();
   @Getter
   private Collection<SpawnReason> spawnReasons = Lists.newArrayList();
   @Getter
   private List<SkillCondition> conditions;
   @Getter
   private SpawnPointType positionType = SpawnPointType.LAND;
   private boolean hasConditions = false;
   private boolean useWorldScaling = true;
   @Getter
   private boolean randomRotation = false;

   public RandomSpawner(RandomSpawnerManager manager, String file, String name, MythicConfig mc) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.name = name;
      String strAction = mc.getString("SpawnMethod", "ADD");
      strAction = mc.getString("Action", strAction);
      this.action = RandomSpawnerAction.valueOf(strAction.toUpperCase());
      if (mc.isList("Types")) {
         for (String line : mc.getStringList("Types")) {
            try {
               String[] split = line.split(" ");
               String strMobType = split[0];
               double weight = 1.0;
               if (split.length > 1) {
                  weight = Double.valueOf(split[1]);
               }

               Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getMythicMob(strMobType);
               if (maybeMob.isPresent()) {
                  RandomSpawnMob spawn = new RandomSpawnMob(maybeMob.get(), weight);
                  this.mobTypes.add(spawn);
               }
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }
      } else {
         String strMobName = mc.getString("Mobname", "");
         strMobName = mc.getString("MobName", strMobName);
         strMobName = mc.getString("MobType", strMobName);
         strMobName = mc.getString("Type", strMobName);
         strMobName = mc.getString("Types", strMobName);
         if (!strMobName.equalsIgnoreCase("GROUP")) {
            String[] types = strMobName.split(",");

            for (String s : types) {
               this.getPlugin().getMobManager().getMythicMob(s).ifPresent(mm -> this.mobTypes.add(new RandomSpawnMob(mm, 1.0)));
            }
         }
      }

      this.level = mc.getPlaceholderDouble("Level", "1");
      this.chance = (float)mc.getDouble("Chance", 1.0);
      this.priority = mc.getInteger("Priority", 1);
      this.maxSpawnerMobCount = mc.getInteger("MaxPerCluster", -1);
      this.maxLocalMobCount = mc.getInteger("MaxLocalMobs", MythicBukkit.inst().getConfiguration().getRandomSpawningLocalLimit());
      this.useWorldScaling = mc.getBoolean("UseWorldScaling", true);
      this.randomRotation = mc.getBoolean("RandomRotation", false);
      this.cooldown = mc.getPlaceholderDouble("Cooldown", "0");
      String strPositionType = "LAND";

      try {
         strPositionType = mc.getString("PositionType", "LAND");
         this.positionType = SpawnPointType.valueOf(strPositionType.toUpperCase());
      } catch (Exception ex) {
         MythicLogger.errorSpawnConfig(this, mc, "'" + strPositionType + "' is not a valid value for PositionType");
         this.positionType = SpawnPointType.LAND;
      }

      for (String s : mc.getStringList("Conditions")) {
         if (s.contains("\"")) {
            String[] splitx = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : splitx) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditions == null) {
               this.conditions = new ArrayList<>();
            }

            this.conditions.add(sc);
         }
      }

      if (this.conditions != null && this.conditions.size() > 0) {
         this.hasConditions = true;
      }

      String world = mc.getString("Worlds");
      String reasons = mc.getString("Reason");
      reasons = mc.getString("Reasons", reasons);
      if (world != null) {
         String[] worldName = world.split(",");
         World w = null;
         if (worldName != null) {
            for (String wName : worldName) {
               this.worldNames.add(wName);
               w = Bukkit.getWorld(wName);
               if (w != null) {
                  this.worlds.add(BukkitAdapter.adapt(w));
               }
            }
         }
      }

      this.regionTags = mc.getStringList("RegionTags");
      List<String> biomes = null;
      if (mc.isSet("Biomes")) {
         if (mc.isList("Biomes")) {
            biomes = mc.getStringList("Biomes");
         } else {
            biomes = Lists.newArrayList(mc.getString("Biomes").split(","));
         }
      } else if (mc.isSet("Biome")) {
         biomes = Lists.newArrayList(new String[]{mc.getString("Biome")});
      }

      if (biomes != null) {
         for (String biomeName : biomes) {
            try {
               Collection<AbstractBiome> maybeBiomes = this.getPlugin().getBootstrap().getBiomes(biomeName);
               this.biomes.addAll(maybeBiomes);
            } catch (Error | Exception ex2) {
               MythicLogger.errorSpawnConfig(this, mc, "Biome '" + biomeName + "' not found");
               ex2.printStackTrace();
            }
         }
      }

      List<String> lstStructures = Lists.newArrayList();
      if (mc.isList("Structures")) {
         lstStructures.addAll(mc.getStringList("Structures"));
      } else if (mc.isSet("Structure")) {
         lstStructures.add(mc.getString("Structure"));
      }

      for (String structureName : lstStructures) {
         Optional<AbstractStructure> structure = MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getStructure(structureName);
         if (structure.isPresent()) {
            this.structures.add(structure.get());
         }
      }

      if (reasons != null) {
         String[] r = reasons.split(",");

         for (String rr : r) {
            SpawnReason fr = SpawnReason.valueOf(rr);
            if (fr != null) {
               this.spawnReasons.add(fr);
            }
         }
      }
   }

   public Collection<String> getConfiguredMobTypes() {
      if (this.configuredMobTypes != null) {
         return this.configuredMobTypes;
      } else {
         this.configuredMobTypes = new ArrayList<>();

         for (RandomSpawnMob t : this.mobTypes.getView()) {
            this.configuredMobTypes.add(t.getMob().getInternalName());
         }

         return this.configuredMobTypes;
      }
   }

   public float getChance() {
      return this.chance;
   }

   public double getLevel(AbstractLocation location) {
      double level = this.getBaseLevel();
      if (this.useWorldScaling) {
         level += WorldScaling.getLevelBonus(location);
      }

      return level;
   }

   public double getBaseLevel() {
      return this.level.get();
   }

   public Collection<SpawnReason> getReasons() {
      return this.spawnReasons;
   }

   public boolean isValid() {
      return !this.mobTypes.getView().isEmpty();
   }

   public boolean hasConditions() {
      return this.hasConditions;
   }

   public boolean checkSpawn(RandomSpawnPoint rsp) {
      UUID cooldownKey = this.resolveCooldownKey(rsp);
      Cooldown cd = this.cooldowns.get(cooldownKey);
      if (cd != null && !cd.testSilently()) {
         return false;
      } else if (this.chance < Numbers.randomDouble()) {
         return false;
      } else {
         if (this.maxLocalMobCount > 0) {
            double localCap = this.maxLocalMobCount;
            if (rsp.getNearbyPlayers() > 1) {
               double mod = (rsp.getNearbyPlayers() - 1) * MythicBukkit.inst().getConfiguration().getRandomSpawningGroupMultiplier();
               localCap *= mod;
            }

            if (rsp.getLocalMobCount() > localCap) {
               return false;
            }
         }

         if (!this.spawnReasons.isEmpty()) {
            if (!this.spawnReasons.contains(rsp.getReason())) {
               return false;
            }
         } else {
            switch (rsp.getReason()) {
               case NATURAL:
               case REINFORCEMENTS:
               case VILLAGE_INVASION:
                  break;
               default:
                  return false;
            }
         }

         if (this.worlds != null && !this.worlds.isEmpty() && !this.worlds.contains(rsp.getLocation().getWorld())) {
            return false;
         } else if (!this.biomes.isEmpty() && !this.biomes.contains(rsp.getBiome())) {
            return false;
         } else {
            if (!this.structures.isEmpty()) {
               Collection<AbstractStructure> structures = MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getStructures(rsp.getLocation());
               boolean match = false;

               for (AbstractStructure struct : structures) {
                  if (this.structures.contains(struct)) {
                     match = true;
                     break;
                  }
               }

               if (!match) {
                  return false;
               }
            }

            if (this.hasConditions()) {
               for (SkillCondition condition : this.conditions) {
                  if (!condition.evaluateRandomSpawnPoint(rsp)) {
                     return false;
                  }
               }
            }

            long cooldownMillis = TimeUnit.SECONDS.toMillis((long)this.cooldown.get());
            if (cooldownMillis > 0L) {
               Cooldown next = Cooldown.of(cooldownMillis, TimeUnit.MILLISECONDS);
               next.reset();
               this.cooldowns.put(cooldownKey, next);
            }

            return true;
         }
      }
   }

   private UUID resolveCooldownKey(RandomSpawnPoint rsp) {
      if (MythicBukkit.inst().getConfiguration().isRandomSpawningSharedCooldown()) {
         return SHARED_COOLDOWN;
      } else {
         AbstractPlayer player = rsp.getTriggeringPlayer();
         return player == null ? SHARED_COOLDOWN : player.getUniqueId();
      }
   }

   public ActiveMob spawn(RandomSpawnPoint rsp) {
      if (this.mobTypes.getView().isEmpty()) {
         return null;
      } else {
         MythicMob mm = this.mobTypes.get().getMob();
         AbstractLocation spawnLocation = rsp.getLocation();
         if (this.randomRotation) {
            float randomYaw = (float)(ThreadLocalRandom.current().nextDouble() * 360.0);
            spawnLocation = spawnLocation.clone();
            spawnLocation.setYaw(randomYaw);
            spawnLocation.setPitch(0.0F);
         }

         return MythicBukkit.inst()
            .getMobManager()
            .spawnMob(
               mm.getInternalName(),
               spawnLocation,
               new io.lumine.mythic.api.mobs.entities.SpawnReason(rsp.getReason()),
               this.getLevel(rsp.getLocation()) * rsp.getLevelMod()
            );
      }
   }
}
