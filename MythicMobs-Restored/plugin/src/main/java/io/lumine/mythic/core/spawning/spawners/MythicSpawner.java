package io.lumine.mythic.core.spawning.spawners;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.collections.WeightedCollection;
import io.lumine.mythic.bukkit.utils.collections.WeightedEntry;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.DoubleProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;

public class MythicSpawner implements Cloneable, PropertyHolder {
   private static final StringProp GROUP = Property.String(Scope.CONFIG, "SpawnerGroup");
   private static final StringProp MOB_TYPE = Property.String(Scope.CONFIG, "MobName");
   private static final StringProp WORLD = Property.String(Scope.CONFIG, "World", "world");
   private static final IntProp POS_X = Property.Int(Scope.CONFIG, "X", 0);
   private static final IntProp POS_Y = Property.Int(Scope.CONFIG, "Y", 0);
   private static final IntProp POS_Z = Property.Int(Scope.CONFIG, "Z", 0);
   private static final DoubleProp YAW = Property.Double(Scope.CONFIG, "Yaw", 0.0);
   private static final DoubleProp PITCH = Property.Double(Scope.CONFIG, "Pitch", 0.0);
   private static final StringProp SPAWN_RADIUS_H = Property.String(Scope.CONFIG, "Radius", "0");
   private static final StringProp SPAWN_RADIUS_V = Property.String(Scope.CONFIG, "RadiusY", "0");
   private static final BooleanProp USE_TIMER = Property.Boolean(Scope.CONFIG, "UseTimer", true);
   private static final StringProp MAX_MOBS = Property.String(Scope.CONFIG, "MaxMobs", "1");
   private static final StringProp MOB_LEVEL = Property.String(Scope.CONFIG, "MobLevel", "0");
   private static final StringProp MOBS_PER_SPAWN = Property.String(Scope.CONFIG, "MobsPerSpawn", "1");
   private static final StringProp COOLDOWN = Property.String(Scope.CONFIG, "Cooldown", "0");
   private static final IntProp COOLDOWN_TIMER = Property.Int(Scope.CONFIG, "CooldownTimer", 0);
   private static final StringProp WARMUP = Property.String(Scope.CONFIG, "Warmup", "0");
   private static final IntProp WARMUP_TIMER = Property.Int(Scope.CONFIG, "WarmupTimer", 0);
   private static final StringProp ACTIVATION_RANGE = Property.String(Scope.CONFIG, "ActivationRange", "32");
   private static final StringProp SCALING_RANGE = Property.String(Scope.CONFIG, "ScalingRange", "32");
   private static final StringProp LEASH_RANGE = Property.String(Scope.CONFIG, "LeashRange", "32");
   private static final BooleanProp LEASH_HEAL = Property.Boolean(Scope.CONFIG, "HealOnLeash", true);
   private static final BooleanProp LEASH_RESET_THREAT = Property.Boolean(Scope.CONFIG, "ResetThreatOnLeash", true);
   private static final BooleanProp BREAKABLE = Property.Boolean(Scope.CONFIG, "Breakable", false);
   private static final BooleanProp CHECK_FOR_PLAYERS = Property.Boolean(Scope.CONFIG, "CheckForPlayers", true);
   private static final BooleanProp SHOW_FLAMES = Property.Boolean(Scope.CONFIG, "ShowFlames", false);
   private static final StringProp CHANCE = Property.String(Scope.CONFIG, "Chance", "1");
   private static final BooleanProp RANDOM_ROTATION = Property.Boolean(Scope.CONFIG, "RandomRotation", false);
   private static final BooleanProp USE_WORLD_SCALING = Property.Boolean(Scope.CONFIG, "UseWorldScaling", false);
   private static final StringListProp CONDITIONS_ACTIVATE = Property.StringList(Scope.CONFIG, "Conditions");
   private static final StringListProp CONDITIONS_TARGET = Property.StringList(Scope.CONFIG, "SpawnConditions");
   private static final IntProp ACTIVE_MOBS = Property.Int(Scope.CONFIG, "ActiveMobs", 0);
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final SpawnerManager manager;
   private boolean active = true;
   private volatile boolean ready = true;
   private File file;
   private String name;
   private String group;
   private String strMobTypes;
   @Getter
   private AbstractLocation location;
   private String world;
   @Getter @Setter
   private int blockX;
   @Getter @Setter
   private int blockY;
   @Getter @Setter
   private int blockZ;
   @Getter
   private double yaw;
   @Getter
   private double pitch;
   private boolean useTimer = true;
   private PlaceholderInt cooldown = PlaceholderInt.of("0");
   private long spawnCooldownMillis = 0L;
   private volatile long spawnCooldownTimer = 0L;
   private PlaceholderInt warmup = PlaceholderInt.of("0");
   private long spawnWarmupMillis = 4L;
   private volatile long spawnWarmupTimer = 0L;
   private PlaceholderDouble spawnRadius = PlaceholderDouble.of("0");
   private PlaceholderDouble spawnRadiusY = PlaceholderDouble.of("0");
   @Getter
   private PlaceholderInt maxMobs = PlaceholderInt.of("1");
   private PlaceholderInt mobsPerSpawn = PlaceholderInt.of("1");
   private PlaceholderInt mobLevel = PlaceholderInt.of("1");
   @Getter
   private boolean showFlames = false;
   @Getter
   private boolean breakable = false;
   @Getter
   private boolean checkForPlayers = true;
   private PlaceholderDouble activationRange = PlaceholderDouble.of("32");
   private PlaceholderDouble scalingRange = PlaceholderDouble.of("25");
   private PlaceholderDouble leashRange = PlaceholderDouble.of("32");
   @Getter
   private boolean healOnLeash = false;
   @Getter
   private boolean leashResetsThreat = false;
   private PlaceholderDouble chance = PlaceholderDouble.of("1");
   @Getter
   private boolean randomRotation = false;
   @Getter
   private boolean useWorldScaling = false;
   @Getter
   private Collection<SkillCondition> conditions = Lists.newArrayList();
   @Getter
   private Collection<SkillCondition> conditionsTarget = Lists.newArrayList();
   protected volatile int cachedActiveMobs = 0;
   private Collection<UUID> mobs = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private ReentrantLock mobsLock = new ReentrantLock();
   @Setter
   private transient boolean save;
   private transient WeightedCollection<WeightedEntry<String>> mobTypes = new WeightedCollection<>();
   private transient volatile int leashTimer = 0;
   private transient volatile int internalCooldown = 8;
   private transient int mobHeight = 0;
   private final transient AtomicBoolean ticking = new AtomicBoolean(false);

   public boolean tryBeginTick() {
      return this.ticking.compareAndSet(false, true);
   }

   public void endTick() {
      this.ticking.set(false);
   }

   public MythicSpawner(SpawnerManager manager, String name, AbstractLocation location, String mobName) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.name = name;
      this.file = new File(manager.getSpawnerFolder(), name + ".yml");
      this.save = !manager.getPlugin().getConfiguration().isSpawnersDisableSaving();
      this.strMobTypes = mobName;
      if (!this.setType(this.strMobTypes)) {
         MythicLogger.error("Error enabling spawner '{0}' - MobType '{1}' is invalid! Spawner cannot initialize. Disabling.", this.name, this.strMobTypes);
      }

      MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(this.strMobTypes).orElseGet(() -> null);
      if (mm != null && mm.getMythicEntity() != null) {
         this.mobHeight = mm.getMythicEntity().getHeight();
      } else {
         this.mobHeight = 2;
      }

      this.location = location;
      this.world = location.getWorld().getName();
      this.blockX = location.getBlockX();
      this.blockY = location.getBlockY();
      this.blockZ = location.getBlockZ();
      this.yaw = location.getYaw();
      this.pitch = location.getPitch();
      if (this.location == null) {
         MythicLogger.error("Location data for spawner '{0}' is invalid! Spawner cannot be attached to world. Disabling.", this.name);
         this.Disable();
      }

      this.spawnCooldownTimer = System.currentTimeMillis();
      this.spawnWarmupTimer = System.currentTimeMillis();
      this.save();
   }

   public MythicSpawner(SpawnerManager manager, File file, String name) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.file = file;
      this.name = name;
      this.save = !manager.getPlugin().getConfiguration().isSpawnersDisableSaving();
      this.group = GROUP.fget(file, this);
      this.strMobTypes = MOB_TYPE.fget(file, this);
      this.setType(this.strMobTypes);

      for (WeightedEntry<String> weighted : this.mobTypes.getView()) {
         Optional<MythicMob> mob = this.getPlugin().getMobManager().getMythicMob(weighted.getValue());
         if (!mob.isEmpty()) {
            int mobHeight = mob.get().getMythicEntity().getHeight();
            if (mobHeight > this.mobHeight) {
               this.mobHeight = mobHeight;
            }
         }
      }

      this.world = WORLD.fget(file, this);
      this.blockX = POS_X.fget(file, this);
      this.blockY = POS_Y.fget(file, this);
      this.blockZ = POS_Z.fget(file, this);
      this.yaw = YAW.fget(file, this);
      this.pitch = PITCH.fget(file, this);

      AbstractWorld w;
      try {
         w = new BukkitWorld(this.world);
      } catch (IllegalArgumentException e) {
         w = BukkitAdapter.adapt(Bukkit.getWorlds().get(0));
      }

      this.location = new AbstractLocation(w, (double)this.blockX, (double)this.blockY, (double)this.blockZ);
      this.spawnRadius = PlaceholderDouble.of(SPAWN_RADIUS_H.fget(file, this));
      this.spawnRadiusY = PlaceholderDouble.of(SPAWN_RADIUS_V.fget(file, this));
      this.useTimer = USE_TIMER.fget(file, this);
      this.maxMobs = PlaceholderInt.of(MAX_MOBS.fget(file, this));
      this.mobsPerSpawn = PlaceholderInt.of(MOBS_PER_SPAWN.fget(file, this));
      this.mobLevel = PlaceholderInt.of(MOB_LEVEL.fget(file, this));
      this.cooldown = PlaceholderInt.of(COOLDOWN.fget(file, this));
      this.spawnCooldownMillis = TimeUnit.SECONDS.toMillis(this.cooldown.get());
      this.setRemainingCooldownSeconds(COOLDOWN_TIMER.fget(file, this).intValue());
      this.warmup = PlaceholderInt.of(WARMUP.fget(file, this));
      this.spawnWarmupMillis = TimeUnit.SECONDS.toMillis(this.warmup.get());
      this.setRemainingWarmupSeconds(WARMUP_TIMER.fget(file, this).intValue());
      this.showFlames = SHOW_FLAMES.fget(file, this);
      this.activationRange = PlaceholderDouble.of(ACTIVATION_RANGE.fget(file, this));
      this.scalingRange = PlaceholderDouble.of(SCALING_RANGE.fget(file, this));
      this.leashRange = PlaceholderDouble.of(LEASH_RANGE.fget(file, this));
      this.healOnLeash = LEASH_HEAL.fget(file, this);
      this.leashResetsThreat = LEASH_RESET_THREAT.fget(file, this);
      String mobLevel = MOB_LEVEL.fget(file, this);
      this.breakable = BREAKABLE.fget(file, this);
      this.checkForPlayers = CHECK_FOR_PLAYERS.fget(file, this);
      this.chance = PlaceholderDouble.of(CHANCE.fget(file, this));
      this.randomRotation = RANDOM_ROTATION.fget(file, this);
      this.useWorldScaling = USE_WORLD_SCALING.fget(file, this);
      this.cachedActiveMobs = ACTIVE_MOBS.fget(file, this);

      for (String line : CONDITIONS_ACTIVATE.fget(file, this)) {
         SkillCondition condition = this.getPlugin().getSkillManager().getCondition(line);
         if (condition != null) {
            this.conditions.add(condition);
         }
      }

      for (String linex : CONDITIONS_TARGET.fget(file, this)) {
         SkillCondition condition = this.getPlugin().getSkillManager().getCondition(linex);
         if (condition != null) {
            this.conditionsTarget.add(condition);
         }
      }

      if (this.getRemainingWarmupSeconds() > 0 || this.getRemainingCooldownSeconds() > 0) {
         this.ready = false;
      }
   }

   public void unloadSpawner() {
      this.mobsLock.lock();

      try {
         this.mobs.removeIf(uuid -> {
            Optional<ActiveMob> maybeMob = MythicBukkit.inst().getMobManager().getActiveMob(uuid);
            if (maybeMob.isPresent()) {
               ActiveMob am = maybeMob.get();
               if (!am.getType().getDespawnMode().getDespawnsOnUnload()) {
                  return false;
               }

               if (!am.isDead() && am.getEntity() != null && am.getEntity().isValid()) {
                  this.cachedActiveMobs++;
                  am.despawn();
               }
            }

            return true;
         });
      } finally {
         this.mobsLock.unlock();
      }

      this.internalCooldown = 10;
   }

   public void saveSpawnerData() {
      if (this.save && this.file != null && this != null) {
         int mobCount = this.getNumberOfCachedMobs();
         int cd = this.getRemainingCooldownSeconds();
         int wu = this.getRemainingWarmupSeconds();
         COOLDOWN_TIMER.fset(this.file, this, cd);
         WARMUP_TIMER.fset(this.file, this, wu);
         ACTIVE_MOBS.fset(this.file, this, mobCount);
      }
   }

   public void save() {
      if (!this.save) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Spawner saving is disabled - skipping saving.");
      } else if (this.getInternalName() == null) {
         throw new IllegalStateException("Spawner Internal Name cannot be null");
      } else {
         this.mobsLock.lock();
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "SPAWNER SAVE CALLED");

         try {
            MOB_TYPE.fsetStaged(this.file, this, this.strMobTypes);
            GROUP.fsetStaged(this.file, this, this.group);
            WORLD.fsetStaged(this.file, this, this.world);
            POS_X.fsetStaged(this.file, this, this.blockX);
            POS_Y.fsetStaged(this.file, this, this.blockY);
            POS_Z.fsetStaged(this.file, this, this.blockZ);
            YAW.fsetStaged(this.file, this, this.yaw);
            PITCH.fsetStaged(this.file, this, this.pitch);
            SPAWN_RADIUS_H.fsetStaged(this.file, this, this.spawnRadius.serialize());
            SPAWN_RADIUS_V.fsetStaged(this.file, this, this.spawnRadiusY.serialize());
            USE_TIMER.fsetStaged(this.file, this, this.useTimer);
            MAX_MOBS.fsetStaged(this.file, this, this.maxMobs.serialize());
            MOB_LEVEL.fsetStaged(this.file, this, this.mobLevel.serialize());
            MOBS_PER_SPAWN.fsetStaged(this.file, this, this.mobsPerSpawn.serialize());
            COOLDOWN.fset(this.file, this, this.cooldown.serialize());
            WARMUP.fset(this.file, this, this.warmup.serialize());
            ACTIVATION_RANGE.fsetStaged(this.file, this, this.activationRange.serialize());
            SCALING_RANGE.fsetStaged(this.file, this, this.scalingRange.serialize());
            LEASH_RANGE.fsetStaged(this.file, this, this.leashRange.serialize());
            LEASH_HEAL.fsetStaged(this.file, this, this.healOnLeash);
            LEASH_RESET_THREAT.fsetStaged(this.file, this, this.leashResetsThreat);
            BREAKABLE.fsetStaged(this.file, this, this.breakable);
            CHECK_FOR_PLAYERS.fsetStaged(this.file, this, this.checkForPlayers);
            SHOW_FLAMES.fsetStaged(this.file, this, this.showFlames);
            CHANCE.fsetStaged(this.file, this, this.chance.serialize());
            RANDOM_ROTATION.fsetStaged(this.file, this, this.randomRotation);
            USE_WORLD_SCALING.fsetStaged(this.file, this, this.useWorldScaling);
            CONDITIONS_ACTIVATE.fsetStaged(this.file, this, this.conditions.stream().map(condition -> condition.getLine()).toList());
            CONDITIONS_TARGET.fset(this.file, this, this.conditionsTarget.stream().map(condition -> condition.getLine()).toList());
         } finally {
            this.mobsLock.unlock();
         }
      }
   }

   private File getFile() {
      return new File(this.manager.getSpawnerFolder(), this.name + ".yml");
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
      this.file = this.getFile();
   }

   public String getGroup() {
      return this.group;
   }

   public void setGroup(String group) {
      this.group = group;
      if (this.save) {
         GROUP.fset(this.file, this, group);
      }
   }

   public String getTypeName() {
      return this.strMobTypes;
   }

   public boolean setType(String type) {
      this.mobTypes.clear();
      String[] split = type.split(",");

      try {
         for (String s : split) {
            String mobType;
            double weight;
            if (s.contains("%")) {
               String[] split2 = s.split("%");
               mobType = split2[1];
               weight = Double.valueOf(split2[0]) / 100.0;
            } else if (s.contains(" ")) {
               String[] split2 = s.split(" ");
               mobType = split2[0];
               weight = Double.valueOf(split2[1]);
            } else {
               mobType = s;
               weight = 1.0;
            }

            this.mobTypes.add(new WeightedEntry<>(mobType, weight));
         }
      } catch (Exception e) {
         MythicLogger.handleMinorError(e);
         return false;
      }

      this.strMobTypes = type;
      if (this.save) {
         MOB_TYPE.fset(this.file, this, this.strMobTypes);
      }

      return true;
   }

   public void setType(MythicMob type) {
      this.mobTypes.clear();
      this.mobTypes.add(new WeightedEntry<>(type.getInternalName(), 1.0));
      if (this.save) {
         MOB_TYPE.fset(this.file, this, type.getInternalName());
      }
   }

   public void setTypes(Collection<MythicMob> types) {
      this.mobTypes.clear();
      StringBuilder sb = new StringBuilder();

      for (MythicMob mm : types) {
         this.mobTypes.add(new WeightedEntry<>(mm.getInternalName(), 1.0));
         sb.append(mm.getInternalName());
         sb.append(",");
      }

      if (sb.length() > 0) {
         sb.setLength(sb.length() - 1);
      }

      this.strMobTypes = sb.toString();
      if (this.save) {
         MOB_TYPE.fset(this.file, this, this.strMobTypes);
      }
   }

   public void setTypes(WeightedCollection types) {
      this.mobTypes = types;
   }

   public void setYaw(double yaw) {
      this.location.setYaw((float)yaw);
      if (this.save) {
         YAW.fset(this.file, this, yaw);
      }
   }

   public void setPitch(double yaw) {
      this.location.setPitch((float)yaw);
      if (this.save) {
         PITCH.fset(this.file, this, this.pitch);
      }
   }

   public void setLocation(AbstractLocation location) {
      this.location = location;
      this.blockX = location.getBlockX();
      this.blockY = location.getBlockY();
      this.blockZ = location.getBlockZ();
      this.yaw = location.getYaw();
      this.pitch = location.getPitch();
      this.world = location.getWorld().getName();
      if (this.save) {
         WORLD.fset(this.file, this, this.world);
         POS_X.fset(this.file, this, this.blockX);
         POS_Y.fset(this.file, this, this.blockY);
         POS_Z.fset(this.file, this, this.blockZ);
         YAW.fset(this.file, this, this.yaw);
         PITCH.fset(this.file, this, this.pitch);
      }
   }

   public String getWorldName() {
      return this.world;
   }

   public void setWorld(String worldName) {
      this.world = worldName;
   }

   public void setOnCooldown() {
      this.spawnCooldownMillis = TimeUnit.SECONDS.toMillis(this.cooldown.get());
      this.spawnCooldownTimer = System.currentTimeMillis();
   }

   public boolean isOnCooldown() {
      return this.spawnCooldownMillis + this.spawnCooldownTimer - System.currentTimeMillis() > 0L;
   }

   public int getCooldownSeconds() {
      return (int)TimeUnit.MILLISECONDS.toSeconds(this.spawnCooldownMillis);
   }

   public void setCooldownSeconds(int cooldown) {
      this.setCooldown(PlaceholderInt.of(String.valueOf(cooldown)));
   }

   public void setCooldown(PlaceholderInt cooldown) {
      this.cooldown = cooldown;
      this.spawnCooldownMillis = TimeUnit.SECONDS.toMillis(cooldown.get());
      if (this.save) {
         COOLDOWN.fset(this.file, this, cooldown.serialize());
      }
   }

   public int getRemainingCooldownSeconds() {
      return this.isOnCooldown()
         ? (int)Math.abs(TimeUnit.MILLISECONDS.toSeconds(this.spawnCooldownMillis + this.spawnCooldownTimer - System.currentTimeMillis()))
         : 0;
   }

   public void setRemainingCooldownSeconds(long seconds) {
      this.spawnCooldownTimer = System.currentTimeMillis() - this.spawnCooldownMillis + TimeUnit.SECONDS.toMillis(seconds);
   }

   public void setOnWarmup() {
      this.spawnWarmupMillis = TimeUnit.SECONDS.toMillis(this.warmup.get());
      this.spawnWarmupTimer = System.currentTimeMillis();
   }

   public boolean isOnWarmup() {
      return this.spawnWarmupMillis + this.spawnWarmupTimer - System.currentTimeMillis() > 0L;
   }

   public int getWarmupSeconds() {
      return (int)TimeUnit.MILLISECONDS.toSeconds(this.spawnWarmupMillis);
   }

   public void setWarmupSeconds(int warmup) {
      this.setWarmup(PlaceholderInt.of(String.valueOf(warmup)));
   }

   public void setWarmup(PlaceholderInt warmup) {
      this.warmup = warmup;
      this.spawnWarmupMillis = TimeUnit.SECONDS.toMillis(warmup.get());
      if (this.save) {
         WARMUP.fset(this.file, this, warmup.serialize());
      }
   }

   public int getRemainingWarmupSeconds() {
      return this.isOnWarmup()
         ? (int)Math.abs(TimeUnit.MILLISECONDS.toSeconds(this.spawnWarmupMillis + this.spawnWarmupTimer - System.currentTimeMillis()))
         : 0;
   }

   public void setRemainingWarmupSeconds(long seconds) {
      this.spawnWarmupTimer = System.currentTimeMillis() - this.spawnWarmupMillis + TimeUnit.SECONDS.toMillis(seconds);
   }

   public boolean getUseTimer() {
      return this.useTimer;
   }

   public void setUseTimer(boolean bool) {
      this.useTimer = bool;
      if (this.save) {
         USE_TIMER.fset(this.file, this, bool);
      }
   }

   public int getInternalCooldown() {
      return this.internalCooldown;
   }

   public void setInternalCooldown(int ic) {
      this.internalCooldown = ic;
   }

   public void tickSpawnerClock() {
      this.internalCooldown--;
      double leashRange = this.leashRange.get(this.placeholderContext());
      boolean spawn = true;
      if (this.isOnCooldown() || this.isOnWarmup()) {
         spawn = false;
         this.ready = false;
      } else if (!this.useTimer) {
         spawn = false;
      }

      if (this.active) {
         if (this.internalCooldown <= 1) {
            if ((this.location == null || Bukkit.getWorld(this.world) == null) && !this.reattachToWorld()) {
               this.internalCooldown = 8;
               return;
            }

            if (!this.location.isLoaded()) {
               this.internalCooldown = 5;
               return;
            }

            if (this.checkForPlayers && !SpawnerManager.playerWithinSpawnerRange((int)this.activationRange.get(this.placeholderContext()), this.location)) {
               this.internalCooldown = 4;
               return;
            }

            if (this.maxMobs.get(this) <= this.getNumberOfMobs()) {
               this.cleanMobList();
               if (this.isOnWarmup()) {
                  return;
               }
            }
         } else if (this.internalCooldown > 1) {
            if (leashRange > 0.0 && this.getNumberOfMobs() > 0 && this.leashTimer-- <= 0) {
               this.Leash();
               this.leashTimer = 2;
            }

            return;
         }

         if (leashRange > 0.0 && leashRange > 0.0 && this.leashTimer-- <= 0) {
            this.Leash();
            this.leashTimer = 2;
         }

         if (this.showFlames) {
            BukkitAdapter.adapt(this.location).getWorld().playEffect(BukkitAdapter.adapt(this.location), Effect.MOBSPAWNER_FLAMES, 0);
         }

         if (this.cachedActiveMobs > 0) {
            while (this.cachedActiveMobs > 0) {
               this.Spawn();
               this.cachedActiveMobs--;
            }
         } else if (this.maxMobs.get(this) > this.getNumberOfMobs()) {
            if (spawn && this.useTimer && !this.getPlugin().getConfiguration().isDebugMode()) {
               for (SkillCondition condition : this.conditions) {
                  if (!condition.evaluateSpawner(this, this.getLocation())) {
                     return;
                  }
               }

               if (!(this.chance.get(this.placeholderContext()) < Numbers.randomDouble())) {
                  for (ActiveMob am : this.Spawn()) {
                     SkillMetadata data = MythicBukkit.inst().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.READY, am, null, null, false);
                     this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
                  }
               }
            }
         }
      }
   }

   public void ActivateSpawner() {
      MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Activating spawner {0}", this.getInternalName());
      if (this.location != null && this.location.getWorld() != null) {
         if (ServerVersion.isFolia()) {
            Schedulers.runOnRegion(BukkitAdapter.adapt(this.location), this::activateSpawner0);
         } else {
            this.activateSpawner0();
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Spawner world is unloaded.");
      }
   }

   private void activateSpawner0() {
      if (this.getPlugin().getConfiguration().getDebugLevel() > 0 || this.showFlames) {
         BukkitAdapter.adapt(this.location).getWorld().playEffect(BukkitAdapter.adapt(this.location), Effect.MOBSPAWNER_FLAMES, 0);
      }

      if (this.active && !this.isOnCooldown() && !this.isOnWarmup()) {
         if (this.maxMobs.get(this) <= this.getNumberOfMobs()) {
            if (!this.cleanMobList()) {
               return;
            }

            if (this.spawnWarmupMillis > 0L) {
               this.setOnWarmup();
               return;
            }
         }

         for (SkillCondition condition : this.conditions) {
            if (!condition.evaluateSpawner(this, this.getLocation())) {
               return;
            }
         }

         if (!(this.chance.get(this.placeholderContext()) < Numbers.randomDouble())) {
            for (ActiveMob am : this.Spawn()) {
               SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.READY, am, null, null, false);
               this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
            }
         }
      }
   }

   public boolean checkSpawnConditions(AbstractLocation spawnLocation) {
      for (SkillCondition condition : this.conditionsTarget) {
         if (!condition.evaluateSpawner(this, spawnLocation)) {
            return false;
         }
      }

      return true;
   }

   private void registerMob(ActiveMob mob) {
      this.mobsLock.lock();

      try {
         this.mobs.add(mob.getUniqueId());
      } finally {
         this.mobsLock.unlock();
      }
   }

   private void unregisterMob(ActiveMob mob) {
      this.mobsLock.lock();

      try {
         this.mobs.remove(mob.getUniqueId());
      } finally {
         this.mobsLock.unlock();
      }
   }

   public Collection<UUID> getAssociatedMobs() {
      Collection<UUID> mobs = Lists.newArrayList();
      this.mobsLock.lock();

      try {
         mobs.addAll(this.mobs);
      } finally {
         this.mobsLock.unlock();
      }

      return mobs;
   }

   public void setAssociatedMobs(Collection<UUID> collection) {
      this.mobsLock.lock();

      try {
         this.mobs = collection;
      } finally {
         this.mobsLock.unlock();
      }
   }

   public Collection<ActiveMob> Spawn() {
      if (!this.active) {
         return null;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Spawner " + this.name + " Spawn() called. Spawning mobs...");
         if (this.cachedActiveMobs == 0) {
            this.setOnCooldown();
         }

         new AbstractLocation(
            this.location.getWorld(), this.location.getX() + 0.5, this.location.getY() + 1.0, this.location.getZ() + 0.5, (float)this.yaw, (float)this.pitch
         );
         double spawnRadius = this.getSpawnRadius();
         double spawnRadiusY = this.getSpawnRadiusY();
         boolean good = false;
         int j = 25;
         List<ActiveMob> mobs = new ArrayList<>();

         forLoop:
         for (int i = this.mobsPerSpawn.get(this); i > 0 && this.maxMobs.get(this) > this.getNumberOfMobs(); i--) {
            AbstractLocation spawningLocation = null;
            if (!(spawnRadius > 0.0) && !(spawnRadiusY > 1.0)) {
               spawningLocation = new AbstractLocation(
                  this.location.getWorld(),
                  this.location.getBlockX(),
                  this.location.getBlockY() + 1,
                  this.location.getBlockZ(),
                  (float)this.yaw,
                  (float)this.pitch
               );

               for (SkillCondition condition : this.conditionsTarget) {
                  if (!condition.evaluateSpawner(this, spawningLocation)) {
                     continue forLoop;
                  }
               }
            } else {
               block: {
                  j = 11;

                  whileLoop:
                  while (--j != 0) {
                     int nx = (int)(this.location.getBlockX() - spawnRadius + Numbers.randomInt((int)(spawnRadius * 2.0)));
                     int nz = (int)(this.location.getBlockZ() - spawnRadius + Numbers.randomInt((int)(spawnRadius * 2.0)));
                     int ny;
                     if (spawnRadiusY > 0.0) {
                        ny = (int)(this.location.getBlockY() - spawnRadiusY + Numbers.randomInt((int)(spawnRadiusY * 2.0))) + 1;
                     } else {
                        ny = this.location.getBlockY() + 1;
                     }

                     AbstractLocation loc = new AbstractLocation(this.location.getWorld(), nx, ny, nz, (float)this.yaw, (float)this.pitch);
                     if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(BukkitAdapter.adapt(loc))) {
                        for (int k = 0; k < this.mobHeight; k++) {
                           AbstractLocation check = new AbstractLocation(this.location.getWorld(), nx, ny + k, nz, (float)this.yaw, (float)this.pitch);
                           if (BukkitAdapter.adapt(check).getBlock().getType().isSolid()) {
                              continue whileLoop;
                           }
                        }

                        for (SkillCondition conditionx : this.conditionsTarget) {
                           if (!conditionx.evaluateSpawner(this, loc)) {
                              continue whileLoop;
                           }
                        }

                        spawningLocation = loc;
                        break block;
                     }
                  }

                  spawningLocation = this.location.clone().add(0.0, 1.0, 0.0);

                  for (SkillCondition conditionxx : this.conditionsTarget) {
                     if (!conditionxx.evaluateSpawner(this, spawningLocation)) {
                        continue forLoop;
                     }
                  }
               }
            }

            if (spawningLocation == null) {
               spawningLocation = this.location.clone().add(0.0, 1.0, 0.0);
            }

            ActiveMob am;
            try {
               String nextType = this.mobTypes.get().getValue();
               int rolledLevel = this.mobLevel.get();
               double useLevel;
               if (rolledLevel <= 0) {
                  useLevel = Double.NaN;
               } else if (this.useWorldScaling) {
                  useLevel = rolledLevel + WorldScaling.getLevelBonus(spawningLocation);
               } else {
                  useLevel = rolledLevel;
               }

               if (this.randomRotation) {
                  spawningLocation.setYaw((float)(Numbers.randomDouble() * 360.0));
                  spawningLocation.setPitch(0.0F);
               }

               am = MythicBukkit.inst().getMobManager().spawnMob(nextType, spawningLocation.add(0.5, 0.0, 0.5), SpawnReason.SPAWNER, useLevel, this);
            } catch (Error | Exception e) {
               am = null;
               MythicLogger.errorGenericConfig("Failed to spawn mob for Spawner '" + this.name + "'. Enable debugging for more info.");
               this.setRemainingCooldownSeconds(5L);
               MythicLogger.handleMinorError(e);
               return mobs;
            }

            if (am == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "-- Spawner " + this.name + " failed to spawn mob: SpawnMythicMob returned null.");
               return mobs;
            }

            if (!am.getType().getDespawnMode().getSavesToDisk()) {
               am.getEntity().setSavesToDisk(false);
            }

            this.registerMob(am);
            mobs.add(am);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "-- Spawner spawned mob(s). Number of mobs tied to spawner now: {0}", this.getNumberOfMobs());
         return mobs;
      }
   }

   public void Leash() {
      double leashRange = this.leashRange.get(this.placeholderContext());
      if (!(leashRange < 1.0)) {
         this.mobsLock.lock();

         try {
            for (UUID uuid : this.mobs) {
               Optional<ActiveMob> maybeMob = MythicBukkit.inst().getMobManager().getActiveMob(uuid);
               if (maybeMob.isPresent() && maybeMob.get().getEntity() != null) {
                  ActiveMob am = maybeMob.get();
                  if (am.getLocation().getWorld().equals(this.location.getWorld())) {
                     if (this.location.distanceSquared(am.getEntity().getLocation()) > Numbers.square(leashRange)) {
                        if (am.getEntity().getVehicle() != null) {
                           for (AbstractEntity l = am.getEntity(); l.getVehicle() != null; l = l.getVehicle()) {
                              if (this.healOnLeash && l.isLiving()) {
                                 l.setHealth(l.getMaxHealth());
                              }

                              if (this.leashResetsThreat) {
                                 MythicBukkit.inst().getMobManager().getMythicMobInstance(l).resetTarget();
                              }
                           }
                        }

                        this.doLeash(am);
                     }
                  } else {
                     this.doLeash(am);
                  }
               }
            }
         } finally {
            this.mobsLock.unlock();
         }
      }
   }

   public void doLeash(ActiveMob am) {
      AbstractLocation spawnLocation = this.location.clone().add(0.5, 1.0, 0.5);
      am.getEntity().teleport(spawnLocation);
      if (this.healOnLeash && am.getEntity().isLiving()) {
         am.getEntity().setHealth(am.getEntity().getMaxHealth());
      }

      if (this.leashResetsThreat) {
         am.resetTarget();
      }
   }

   private boolean cleanMobList() {
      this.mobsLock.lock();

      boolean flag;
      try {
         flag = this.mobs.removeIf(uuid -> {
            if (!MythicBukkit.inst().getMobManager().isActiveMob(uuid)) {
               return true;
            } else {
               ActiveMob am = MythicBukkit.inst().getMobManager().getActiveMob(uuid).get();
               if (!am.isDead() && am.getEntity() != null && am.getEntity().isValid()) {
                  return false;
               } else {
                  if (this.getNumberOfMobs() >= this.maxMobs.get(this)) {
                     this.setOnWarmup();
                  }

                  return true;
               }
            }
         });
      } finally {
         this.mobsLock.unlock();
      }

      return flag;
   }

   public boolean trackMob(ActiveMob am) {
      if (am.getDespawnMode().getDespawnWithoutNearbyPlayers() && this.mobs.size() >= this.maxMobs.get(this)) {
         return false;
      } else {
         this.registerMob(am);
         return true;
      }
   }

   public void markMobDespawned(ActiveMob am) {
      this.unregisterMob(am);
   }

   public void markMobDead(ActiveMob am) {
      this.mobsLock.lock();

      try {
         boolean tracked = this.mobs.contains(am.getUniqueId());
         if (tracked || am.getSpawner() == this) {
            if (this.getNumberOfMobs() + (tracked ? 0 : 1) >= this.maxMobs.get(this)) {
               this.setOnWarmup();
            }

            this.mobs.remove(am.getUniqueId());
            return;
         }
      } finally {
         this.mobsLock.unlock();
      }
   }

   public boolean reattachToWorld() {
      if (Bukkit.getWorld(this.world) == null) {
         return false;
      } else {
         this.location = new AbstractLocation(BukkitAdapter.adapt(Bukkit.getWorld(this.world)), (double)this.blockX, (double)this.blockY, (double)this.blockZ);
         if (this.location == null) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.EVENT,
               "Spawner {0} has an invalid location and could not be reattached to world. Perhaps the world is unloaded?",
               this.name
            );
            return false;
         } else {
            return true;
         }
      }
   }

   public String getInternalName() {
      return this.name;
   }

   public int getNumberOfMobs() {
      return this.mobs.size() + this.cachedActiveMobs;
   }

   public void Disable() {
      this.active = false;
   }

   public void Enable() {
      this.active = true;
   }

   public double distanceTo(AbstractLocation abstractLocation) {
      return abstractLocation.distance(this.location);
   }

   public MythicSpawner clone() throws CloneNotSupportedException {
      return (MythicSpawner)super.clone();
   }

   public void resetTimers() {
      this.spawnWarmupTimer = System.currentTimeMillis() - this.spawnWarmupMillis;
      this.spawnCooldownTimer = System.currentTimeMillis() - this.spawnCooldownMillis;
   }

   public int getChunkX() {
      return this.blockX >> 4;
   }

   public int getChunkZ() {
      return this.blockZ >> 4;
   }

   public String getChunkString() {
      return this.world + "-" + this.getChunkX() + "-" + this.getChunkZ();
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   public int getNumberOfCachedMobs() {
      return this.cachedActiveMobs;
   }

   private PlaceholderContext placeholderContext() {
      return PlaceholderContext.builder().spawner(this).build();
   }

   public PlaceholderInt getMobLevel() {
      return this.mobLevel;
   }

   public int getMobsPerSpawn() {
      return this.mobsPerSpawn.get(this);
   }

   public double getSpawnRadius() {
      return Math.max(0.0, this.spawnRadius.get(this.placeholderContext()));
   }

   public double getSpawnRadiusY() {
      return Math.max(1.0, this.spawnRadiusY.get(this.placeholderContext()));
   }

   public double getActivationRange() {
      return this.activationRange.get(this.placeholderContext());
   }

   public double getScalingRange() {
      return this.scalingRange.get(this.placeholderContext());
   }

   public double getLeashRange() {
      return this.leashRange.get(this.placeholderContext());
   }

   public double getChance() {
      return this.chance.get(this.placeholderContext());
   }

   @Override
   public String getPropertyNode() {
      return this.name;
   }

   public void setSpawnRadius(double opt) {
      this.setSpawnRadius(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setSpawnRadius(PlaceholderDouble opt) {
      this.spawnRadius = opt;
      if (this.save) {
         SPAWN_RADIUS_H.fset(this.file, this, opt.serialize());
      }
   }

   public void setSpawnRadiusY(double opt) {
      this.setSpawnRadiusY(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setSpawnRadiusY(PlaceholderDouble opt) {
      this.spawnRadiusY = opt;
      if (this.save) {
         SPAWN_RADIUS_V.fset(this.file, this, opt.serialize());
      }
   }

   public void setMaxMobs(PlaceholderInt opt) {
      this.maxMobs = opt;
      if (this.save) {
         MAX_MOBS.fset(this.file, this, opt.serialize());
      }
   }

   public void setMobsPerSpawn(int opt) {
      this.setMobsPerSpawn(PlaceholderInt.of(String.valueOf(opt)));
   }

   public void setMobsPerSpawn(PlaceholderInt opt) {
      this.mobsPerSpawn = opt;
      if (this.save) {
         MOBS_PER_SPAWN.fset(this.file, this, opt.serialize());
      }
   }

   public void setMobLevel(RandomInt opt) {
      this.setMobLevel(PlaceholderInt.of(opt.toString()));
   }

   public void setMobLevel(PlaceholderInt opt) {
      this.mobLevel = opt;
      if (this.save) {
         MOB_LEVEL.fset(this.file, this, opt.serialize());
      }
   }

   public void setShowFlames(boolean opt) {
      this.showFlames = opt;
      if (this.save) {
         SHOW_FLAMES.fset(this.file, this, opt);
      }
   }

   public void setBreakable(boolean opt) {
      this.breakable = opt;
      if (this.save) {
         BREAKABLE.fset(this.file, this, opt);
      }
   }

   public void setCheckForPlayers(boolean opt) {
      this.checkForPlayers = opt;
      if (this.save) {
         CHECK_FOR_PLAYERS.fset(this.file, this, opt);
      }
   }

   public void setActivationRange(double opt) {
      this.setActivationRange(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setActivationRange(PlaceholderDouble opt) {
      this.activationRange = opt;
      if (this.save) {
         ACTIVATION_RANGE.fset(this.file, this, opt.serialize());
      }
   }

   public void setScalingRange(double opt) {
      this.setScalingRange(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setScalingRange(PlaceholderDouble opt) {
      this.scalingRange = opt;
      if (this.save) {
         SCALING_RANGE.fset(this.file, this, opt.serialize());
      }
   }

   public void setLeashRange(double opt) {
      this.setLeashRange(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setLeashRange(PlaceholderDouble opt) {
      this.leashRange = opt;
      if (this.save) {
         LEASH_RANGE.fset(this.file, this, opt.serialize());
      }
   }

   public void setHealOnLeash(boolean opt) {
      this.healOnLeash = opt;
      if (this.save) {
         LEASH_HEAL.fset(this.file, this, opt);
      }
   }

   public void setLeashResetsThreat(boolean opt) {
      this.leashResetsThreat = opt;
      if (this.save) {
         LEASH_RESET_THREAT.fset(this.file, this, opt);
      }
   }

   public void setChance(double opt) {
      this.setChance(PlaceholderDouble.of(String.valueOf(opt)));
   }

   public void setChance(PlaceholderDouble opt) {
      this.chance = opt;
      if (this.save) {
         CHANCE.fset(this.file, this, opt.serialize());
      }
   }

   public void setRandomRotation(boolean opt) {
      this.randomRotation = opt;
      if (this.save) {
         RANDOM_ROTATION.fset(this.file, this, opt);
      }
   }

   public void setUseWorldScaling(boolean opt) {
      this.useWorldScaling = opt;
      if (this.save) {
         USE_WORLD_SCALING.fset(this.file, this, opt);
      }
   }

   public boolean addCondition(String line) {
      SkillCondition condition = this.manager.getPlugin().getSkillManager().getCondition(line);
      if (condition == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "The condition " + line + " does not exist!");
         return false;
      } else {
         this.conditions.add(condition);
         if (this.save) {
            this.saveConditions();
         }

         return true;
      }
   }

   public boolean removeCondition(String line) {
      this.conditions.removeIf(condition -> condition.getLine().toLowerCase().startsWith(line.toLowerCase()));
      if (this.save) {
         this.saveConditions();
      }

      return true;
   }

   private void saveConditions() {
      CONDITIONS_ACTIVATE.fset(this.file, this, this.conditions.stream().map(condition -> condition.getLine()).toList());
   }

   public boolean addTargetCondition(String line) {
      SkillCondition condition = this.manager.getPlugin().getSkillManager().getCondition(line);
      if (condition == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "The condition " + line + " does not exist!");
         return false;
      } else {
         this.conditionsTarget.add(condition);
         if (this.save) {
            this.saveTargetConditions();
         }

         return true;
      }
   }

   public boolean removeTargetCondition(String line) {
      this.conditionsTarget.removeIf(condition -> condition.getLine().toLowerCase().startsWith(line.toLowerCase()));
      if (this.save) {
         this.saveTargetConditions();
      }

      return true;
   }

   private void saveTargetConditions() {
      CONDITIONS_TARGET.fset(this.file, this, this.conditionsTarget.stream().map(condition -> condition.getLine()).toList());
   }
}
