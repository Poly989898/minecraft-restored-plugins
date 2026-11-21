package io.lumine.mythic.core.spawning.spawners;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.listeners.SpawnerListeners;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class SpawnerManager extends ReloadableModule<MythicBukkit> {
   private final SpawnerListeners listener;
   private List<File> spawnerFiles;
   private List<IOLoader<MythicBukkit>> spawnerLoaders;
   public Map<String, MythicSpawner> spawners = Maps.newConcurrentMap();
   public Map<AbstractLocation, MythicSpawner> mmSpawners = Maps.newConcurrentMap();
   public Map<String, Collection<MythicSpawner>> mmChunkSpawnerLookup = Maps.newConcurrentMap();
   public Map<Integer, MythicSpawner> mmSpawnerHashcodeLookup = Maps.newConcurrentMap();
   private Map<String, BiConsumer<MythicSpawner, String>> spawnerAttributes = Maps.newConcurrentMap();

   public SpawnerManager(MythicBukkit plugin) {
      super(plugin, false);
      this.listener = new SpawnerListeners(this);
      Bukkit.getPluginManager().registerEvents(this.listener, plugin);
   }

   public void load(MythicBukkit plugin) {
      this.loadSpawners();
   }

   public void loadSpawners() {
      MythicLogger.log("Loading Spawn Blocks...");
      this.spawners.clear();
      this.mmSpawners.clear();
      this.mmSpawnerHashcodeLookup.clear();
      this.mmChunkSpawnerLookup.clear();

      for (File file : this.getPlugin().getPackManager().getFiles(ConfigFileType.SPAWNER)) {
         for (String node : NodeListProp.getNodes(file, "")) {
            try {
               MythicSpawner ms = new MythicSpawner(this, file, node);
               this.registerSpawner(ms);
               break;
            } catch (Exception e) {
               MythicLogger.error("Error loading MythicSpawner {0}: enable debugging for stacktrace.", node);
               MythicLogger.handleMinorError(e);
            }
         }
      }

      this.buildSpawnerChunkLookupTable();
   }

   @Override
   public void unload() {
      for (MythicSpawner ms : this.spawners.values()) {
         ms.unloadSpawner();
      }

      this.saveSpawners();
      this.spawners.clear();
      this.mmSpawners.clear();
      this.mmSpawnerHashcodeLookup.clear();
      this.mmChunkSpawnerLookup.clear();
   }

   public File getSpawnerFolder() {
      return this.getPlugin().getPackManager().getBasePack().getPackFolder("Spawners");
   }

   private void registerSpawner(MythicSpawner spawner) {
      this.spawners.put(spawner.getPropertyNode(), spawner);
      this.mmSpawners.put(spawner.getLocation(), spawner);
      if (!this.mmSpawnerHashcodeLookup.containsKey(spawner.hashCode())) {
         this.mmSpawnerHashcodeLookup.put(spawner.hashCode(), spawner);
      } else {
         MythicSpawner conflict = this.mmSpawnerHashcodeLookup.get(spawner.hashCode());
         MythicLogger.error("WARNING: HashCode collision detected when loading spawners.");
         MythicLogger.error("Spawner 1 Hash: " + spawner.hashCode() + " Type: " + this.mmSpawnerHashcodeLookup.get(spawner.hashCode()).getInternalName());
         MythicLogger.error("Spawner 2 Hash: " + conflict.hashCode() + " Type: " + conflict.getInternalName());
         MythicLogger.error("We recommend changing one of these spawners' names to avoid issues resolving mob type.");
      }
   }

   public void saveSpawners() {
      this.spawners.values().forEach(ms -> ms.saveSpawnerData());
   }

   public Collection<MythicSpawner> getSpawners() {
      return this.spawners.values();
   }

   public void tickSpawnerClocks() {
      this.spawners.values().forEach(spawner -> {
         if (ServerVersion.isFolia()) {
            AbstractLocation aLoc = spawner.getLocation();
            if (aLoc == null || aLoc.getWorld() == null) {
               this.tickSpawnerSafely(spawner);
               return;
            }

            if (!spawner.tryBeginTick()) {
               return;
            }

            try {
               Schedulers.ensureSync(BukkitAdapter.adapt(aLoc), () -> {
                  try {
                     this.tickSpawnerSafely(spawner);
                  } finally {
                     spawner.endTick();
                  }
               });
            } catch (Throwable e) {
               spawner.endTick();
               throw e;
            }
         } else {
            this.tickSpawnerSafely(spawner);
         }
      });
   }

   private void tickSpawnerSafely(MythicSpawner spawner) {
      MythicBukkit.inst().getTimingsHandler().markSpawnerNew(spawner.getName());

      try {
         spawner.tickSpawnerClock();
      } catch (Error | Exception e) {
         MythicLogger.error("Error ticking spawner {0}", spawner.getInternalName());
         e.printStackTrace();
         spawner.setInternalCooldown(5);
      }

      MythicBukkit.inst().getTimingsHandler().markSpawnerComplete(spawner.getName());
   }

   public void unloadAllSpawners() {
      for (MythicSpawner ms : this.spawners.values()) {
         ms.unloadSpawner();
      }
   }

   public Collection<MythicSpawner> getSpawnersByString(String string) {
      return this.getSpawnersByString(null, string);
   }

   public Collection<MythicSpawner> getSpawnersByString(AbstractLocation abstractLocation, String string) {
      if (string == null) {
         return null;
      } else {
         ArrayList<MythicSpawner> msl = new ArrayList<>();
         if (string.startsWith("g:")) {
            String group = string.substring(2);
            msl = this.getSpawnersByGroup(group);
         } else if (string.startsWith("r:")) {
            if (abstractLocation == null) {
               return null;
            }

            String sradius = string.substring(2);
            double radius = 0.0;

            try {
               radius = Double.parseDouble(sradius);
            } catch (Exception e) {
            }

            for (MythicSpawner ms : this.getSpawners()) {
               if (ms.getLocation().getWorld() != null
                  && ms.getLocation().getWorld().equals(abstractLocation.getWorld())
                  && ms.distanceTo(abstractLocation) <= radius) {
                  msl.add(ms);
               }
            }
         } else if (string.equals("*")) {
            for (MythicSpawner msx : this.getSpawners()) {
               msl.add(msx);
            }
         } else if (!string.contains("*") && !string.contains("?")) {
            MythicSpawner msx = this.getSpawnerByName(string);
            if (msx != null) {
               msl.add(msx);
            }
         } else {
            for (MythicSpawner msx : this.getSpawners()) {
               if (msx.getName().matches(string.replace("?", ".?").replace("*", ".*?"))) {
                  msl.add(msx);
               }
            }
         }

         return msl;
      }
   }

   public MythicSpawner getSpawnerByName(String name) {
      return this.spawners.get(name);
   }

   public ArrayList<MythicSpawner> getSpawnersByGroup(String group) {
      ArrayList<MythicSpawner> msl = new ArrayList<>();

      for (MythicSpawner s : this.spawners.values()) {
         try {
            if (s.getGroup().equals(group)) {
               msl.add(s);
            }
         } catch (Exception e) {
         }
      }

      return msl;
   }

   public MythicSpawner getSpawnerAtLocation(AbstractLocation location) {
      return this.mmSpawners.getOrDefault(location, null);
   }

   public Optional<MythicSpawner> getSpawnerByHashcode(int hashcode) {
      return Optional.ofNullable(this.mmSpawnerHashcodeLookup.getOrDefault(hashcode, null));
   }

   public Optional<Collection<MythicSpawner>> getSpawnersByChunk(String lookup) {
      return Optional.ofNullable(this.mmChunkSpawnerLookup.getOrDefault(lookup, null));
   }

   public boolean hasBreakableSpawner(AbstractLocation location) {
      return this.mmSpawners.containsKey(location) ? this.mmSpawners.get(location).isBreakable() : false;
   }

   public MythicSpawner createSpawner(String name, Location location, String mobName) {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Creating New Spawner at " + location.getX() + "," + location.getY() + "," + location.getZ());
      if (this.getSpawnerByName(name) != null) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "-- Spawner creation failed due to bad name.");
         return null;
      } else if (this.getPlugin().getMobManager().getMythicMob(mobName) == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "-- Spawner creation failed due to bad mob name.");
         return null;
      } else {
         MythicSpawner newspawner = new MythicSpawner(this, name, BukkitAdapter.adapt(location), mobName);
         this.spawners.put(name, newspawner);
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "New spawner created successfully!");
         return newspawner;
      }
   }

   public boolean copySpawner(String name, String nameNew, AbstractLocation location) {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Creating Copy of Spawner at " + location.getX() + "," + location.getY() + "," + location.getZ());
      MythicSpawner ms = this.getSpawnerByName(name);

      MythicSpawner msNew;
      try {
         msNew = ms.clone();
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return false;
      }

      msNew.setName(nameNew);
      msNew.setLocation(location);
      msNew.setAssociatedMobs(new ArrayList<>());
      msNew.getAssociatedMobs().clear();
      msNew.setBreakable(false);
      msNew.setBreakable(ms.isBreakable());
      msNew.setType(ms.getTypeName());
      this.addSpawnerToChunkLookupTable(msNew);
      msNew.save();
      this.spawners.put(nameNew, msNew);
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "New spawner cloned successfully!");
      return true;
   }

   public boolean moveSpawner(String name, AbstractLocation location) {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Moving spawner to " + location.getX() + "," + location.getY() + "," + location.getZ());
      MythicSpawner ms = this.getSpawnerByName(name);
      return ms == null ? false : this.moveSpawner(ms, location);
   }

   public boolean moveSpawner(MythicSpawner ms, AbstractLocation location) {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Moving spawner to " + location.getX() + "," + location.getY() + "," + location.getZ());
      this.removeSpawnerFromChunkLookupTable(ms);
      ms.setLocation(location);
      this.addSpawnerToChunkLookupTable(ms);
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Spawner moved successfully!");
      return true;
   }

   public static synchronized boolean mobIsValid(Location location, UUID u) {
      if (location.getWorld() == null) {
         return true;
      } else {
         for (LivingEntity l : location.getWorld().getLivingEntities()) {
            if (l.getUniqueId().equals(u)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean playerWithinSpawnerRange(int range, AbstractLocation location) {
      int rangeSquared = (int)Math.pow(range, 2.0);

      for (AbstractPlayer player : location.getWorld().getPlayers()) {
         if (location.getWorld().equals(player.getWorld()) && location.distanceSquared(player.getLocation()) <= rangeSquared) {
            return true;
         }
      }

      return false;
   }

   public static void RemoveMobFromSpawners(ActiveMob am) {
      if (am.getSpawner() != null) {
         am.getSpawner().markMobDead(am);
      }
   }

   public boolean setSpawnerAttribute(MythicSpawner ms, String attr, String value) {
      SpawnerAttribute attribute;
      try {
         attribute = SpawnerAttribute.get(attr);
      } catch (IllegalArgumentException e) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "The attribute " + attr + " does not exist!");
         return false;
      }

      value = value.trim();
      switch (attribute) {
         case GROUP:
            ms.setGroup(value);
            break;
         case RADIUS:
            ms.setSpawnRadius(PlaceholderDouble.of(value));
            break;
         case RADIUS_Y:
            ms.setSpawnRadiusY(PlaceholderDouble.of(value));
            break;
         case COOLDOWN:
            ms.setCooldown(PlaceholderInt.of(value));
            break;
         case WARMUP:
            ms.setWarmup(PlaceholderInt.of(value));
            break;
         case MAX_MOBS:
            ms.setMaxMobs(PlaceholderInt.of(value));
            break;
         case MOB_LEVEL:
            ms.setMobLevel(PlaceholderInt.of(value));
            break;
         case MOBS_PER_SPAWN:
            ms.setMobsPerSpawn(PlaceholderInt.of(value));
            break;
         case USE_TIMER:
            ms.setUseTimer(Boolean.parseBoolean(value));
            break;
         case SHOW_FLAMES:
            ms.setShowFlames(Boolean.parseBoolean(value));
            break;
         case ACTIVATION_RANGE:
            ms.setActivationRange(PlaceholderDouble.of(value));
            break;
         case SCALING_RANGE:
            ms.setScalingRange(PlaceholderDouble.of(value));
            break;
         case LEASH_RANGE:
            ms.setLeashRange(PlaceholderDouble.of(value));
            break;
         case HEAL_ON_LEASH:
            ms.setHealOnLeash(Boolean.parseBoolean(value));
            break;
         case RESET_THREAT_ON_LEASH:
            ms.setLeashResetsThreat(Boolean.parseBoolean(value));
            break;
         case MOB_TYPE:
            if (!ms.setType(value)) {
               return false;
            }
            break;
         case BREAKABLE:
            ms.setBreakable(Boolean.parseBoolean(value));
            break;
         case CHECK_FOR_PLAYERS:
            ms.setCheckForPlayers(Boolean.parseBoolean(value));
            break;
         case YAW:
            ms.setYaw(Float.parseFloat(value));
            break;
         case PITCH:
            ms.setPitch(Float.parseFloat(value));
            break;
         case CHANCE:
            ms.setChance(PlaceholderDouble.of(value));
            break;
         case RANDOM_ROTATION:
            ms.setRandomRotation(Boolean.parseBoolean(value));
            break;
         case USE_WORLD_SCALING:
            ms.setUseWorldScaling(Boolean.parseBoolean(value));
            break;
         default:
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Unhandled attribute: " + attr);
            return false;
      }

      return true;
   }

   public boolean addSpawnerCondition(MythicSpawner ms, String condition) {
      return ms.addCondition(condition);
   }

   public boolean removeSpawnerCondition(MythicSpawner ms, String condition) {
      return ms.removeCondition(condition);
   }

   public boolean addSpawnerTargetCondition(MythicSpawner ms, String condition) {
      return ms.addTargetCondition(condition);
   }

   public boolean removeSpawnerTargetCondition(MythicSpawner ms, String condition) {
      return ms.removeTargetCondition(condition);
   }

   public boolean removeSpawner(MythicSpawner ms) {
      this.spawners.remove(ms.getPropertyNode());
      this.mmSpawners.remove(ms.getLocation());
      this.mmSpawnerHashcodeLookup.remove(ms.hashCode());
      if (this.mmChunkSpawnerLookup.containsKey(ms.getChunkString())) {
         this.mmChunkSpawnerLookup.get(ms.getChunkString()).remove(ms);
      }

      File spawnerFile = new File(MythicBukkit.inst().getDataFolder() + System.getProperty("file.separator") + "spawners", ms.getName() + ".yml");
      spawnerFile.delete();
      return true;
   }

   public Location findSpawningLocation(Location location, int radius) {
      int tries = 0;

      while (tries < 64) {
         tries++;
         Location spawnLoc = randomizeSpawnLocation(location, radius);
         if (this.areaIsEmpty(spawnLoc)) {
            return spawnLoc;
         }
      }

      return null;
   }

   public static Location randomizeSpawnLocation(Location location, int r) {
      double sX = location.getX();
      double sY = location.getY();
      double sZ = location.getZ();
      double rSq = (double)r * r;

      double x;
      double y;
      double z;
      do {
         x = randomRange(sX - r, sX + r);
         y = randomRange(sY - r, sY + r);
         z = randomRange(sZ - r, sZ + r);
      } while ((x - sX) * (x - sX) + (y - sY) * (y - sY) + (z - sZ) * (z - sZ) > rSq);

      return new Location(location.getWorld(), x, y, z);
   }

   public static double randomRange(double arg0, double arg1) {
      double range = arg0 < arg1 ? arg1 - arg0 : arg0 - arg1;
      if (range < 1.0) {
         return Math.floor(arg0) + 0.5;
      } else {
         double min = arg0 < arg1 ? arg0 : arg1;
         return Math.floor(min + ThreadLocalRandom.current().nextDouble() * range) + 0.5;
      }
   }

   public boolean areaIsEmpty(Location loc) {
      return true;
   }

   public void addSpawnerToChunkLookupTable(MythicSpawner ms) {
      String cS = ms.getChunkString();
      if (this.mmChunkSpawnerLookup.containsKey(cS)) {
         this.mmChunkSpawnerLookup.get(cS).add(ms);
      } else {
         HashSet<MythicSpawner> hs = new HashSet<>();
         hs.add(ms);
         this.mmChunkSpawnerLookup.put(cS, hs);
      }
   }

   public void removeSpawnerFromChunkLookupTable(MythicSpawner ms) {
      String cS = ms.getChunkString();
      if (this.mmChunkSpawnerLookup.containsKey(cS)) {
         this.mmChunkSpawnerLookup.get(cS).remove(ms);
      }
   }

   public void buildSpawnerChunkLookupTable() {
      this.mmChunkSpawnerLookup.clear();

      for (MythicSpawner ms : this.spawners.values()) {
         this.addSpawnerToChunkLookupTable(ms);
      }
   }

   public String getSpawnerCopyName(String name) {
      return "";
   }

   public Optional<ItemStack> getSpawnerItem(String type) {
      new ItemStack(Material.SPAWNER);
      return Optional.ofNullable(null);
   }

   public void filterSpawners(List<MythicSpawner> spawners, String filter) {
      if (filter != null) {
         if (filter.startsWith("g:")) {
            String group = filter.substring(2);
            spawners.removeIf(spawner -> !spawner.getGroup().equals(group));
         } else if (filter.contains("*") || filter.contains("?")) {
            spawners.removeIf(spawner -> {
               String regex = filter.replace("?", ".?").replace("*", ".*?");
               return !spawner.getName().matches(regex);
            });
         }
      }
   }
}
