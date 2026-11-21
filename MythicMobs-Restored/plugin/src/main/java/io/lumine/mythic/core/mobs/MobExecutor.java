package io.lumine.mythic.core.mobs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.npcs.NPCExecutor;
import io.lumine.mythic.core.mobs.totems.TotemExecutor;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.mechanics.HideMechanic;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.TemplateUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

public class MobExecutor extends ReloadableModule<MythicBukkit> implements MobManager, CasterRegistry {
   private static final String FOLDER_MOBS = "Mobs";
   @Getter
   private EggExecutor eggManager;
   @Getter
   private NPCExecutor npcManager;
   @Getter
   private TotemExecutor totemManager;
   private Map<String, MythicMob> mmList = Maps.newConcurrentMap();
   private Map<String, MythicMob> mmDisplayLookup = Maps.newConcurrentMap();
   private List<MobStack> listMobStacks = Lists.newArrayList();
   private Map<MythicEntityType, MythicMob> mmDefaultList = Maps.newConcurrentMap();
   @Getter
   private MobRegistry mobRegistry;
   private transient Map<UUID, ActiveMob> activeMobsInCombat = Maps.newConcurrentMap();
   private final transient Set<UUID> audienceTrackedEntities = Collections.newSetFromMap(Maps.newConcurrentMap());
   private final transient Set<UUID> spawningEntities = Collections.newSetFromMap(Maps.newConcurrentMap());
   private transient Cache<UUID, Boolean> mmVoidList = CacheBuilder.newBuilder()
      .maximumSize(1000L)
      .expireAfterWrite(10L, TimeUnit.MINUTES)
      .build(new CacheLoader<UUID, Boolean>() {
         public Boolean load(UUID uuid) {
            return true;
         }
      });
   public static boolean spawnflag = false;
   public static boolean mountflag = false;

   public MobExecutor(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      this.eggManager = new EggExecutor(plugin, this);
      this.npcManager = new NPCExecutor(plugin, this);
      this.totemManager = new TotemExecutor(plugin, this);
      if (this.mobRegistry == null) {
         this.mobRegistry = new MobRegistry();
      }
   }

   @Override
   public void unload() {
   }

   public void loadMobs() {
      new IOLoader<>(MythicBukkit.inst(), "VanillaMobs.yml", "Mobs");
      IOLoader<MythicBukkit> defaultMobs = new IOLoader<>(MythicBukkit.inst(), "ExampleMobs.yml", "Mobs");
      List<File> mobFiles = IOHandler.getAllFiles(defaultMobs.getFile().getParent());
      List<IOLoader<MythicBukkit>> mobLoaders = IOHandler.getSaveLoad(MythicBukkit.inst(), mobFiles, "Mobs");
      this.mmList.clear();
      this.mmDefaultList.clear();
      this.mmDisplayLookup.clear();
      this.listMobStacks.clear();
      ArrayList<TemplateUtil.TemplateMob> mobsUsingTemplates = Lists.newArrayList();

      for (PackExecutor.ConfigFile configFile : this.plugin.getPackManager().getConfigFiles(ConfigFileType.MOB)) {
         MythicConfigImpl fileConfig = new MythicConfigImpl(configFile.file());

         for (String node : NodeListProp.getNodes(configFile.file(), "")) {
            try {
               MythicConfig config = fileConfig.getNestedConfig(node);
               if (!configFile.file().getParentFile().getName().equals("mono")
                  || !config.isSet("Id") && (!config.isSet("Skills") || config.isSet("Type") || config.isSet("Id"))) {
                  if (BukkitEntityType.getMythicEntity(node) != null) {
                     MythicEntityType met = MythicEntityType.get(node);
                     MythicMob mm = new MobType(this, configFile.pack(), configFile.file(), node, config);
                     this.mmDefaultList.put(met, mm);
                     this.mmList.put(met.toString(), mm);
                  } else if (config.getString("MobStack") != null) {
                     String stack = config.getString("MobStack");
                     this.listMobStacks.add(new MobStack(node, stack, configFile.file().getAbsolutePath()));
                  } else {
                     String display = config.getString(new String[]{"Display", "DisplayName"});
                     String maybeTemplate = config.getString("Template");
                     if (maybeTemplate == null) {
                        maybeTemplate = config.getString("Templates");
                     }

                     if (maybeTemplate != null) {
                        TemplateUtil.TemplateMob tmpl = new TemplateUtil.TemplateMob(configFile.file(), configFile.pack(), node, config, maybeTemplate);
                        mobsUsingTemplates.add(tmpl);
                     } else {
                        MythicMob mm = new MobType(this, configFile.pack(), configFile.file(), node, config);
                        this.mmList.put(node, mm);
                        if (display != null) {
                           this.mmDisplayLookup.put(display, mm);
                        }
                     }
                  }
               }
            } catch (Exception e) {
               MythicLogger.error("Error loading mob '" + node + "'. Enable debugging for a stack trace.");
               MythicLogger.handleMinorError(e);
            }
         }
      }

      TemplateUtil.handleTemplates(mobsUsingTemplates, this.mmList);
   }

   @Override
   public Collection<MythicMob> getMobTypes() {
      return this.mmList.values();
   }

   @Override
   public Collection<String> getMobNames() {
      return this.mmList.keySet();
   }

   @Override
   public Collection<MythicMob> getVanillaTypes() {
      return this.mmDefaultList.values();
   }

   @Override
   public Optional<MythicMob> getVanillaType(MythicEntityType type) {
      return Optional.ofNullable(this.mmDefaultList.getOrDefault(type, null));
   }

   @Override
   public Collection<MobStack> getMobStacks() {
      return this.listMobStacks;
   }

   @Override
   public Optional<SkillCaster> getSkillCaster(UUID uuid) {
      return Optional.ofNullable(this.mobRegistry.get(uuid));
   }

   @Override
   public Collection<ActiveMob> getActiveMobs() {
      return this.mobRegistry.values();
   }

   public Collection<ActiveMob> getActiveMobs(Predicate<ActiveMob> predicate) {
      Collection<ActiveMob> mobs = Lists.newArrayList();

      for (ActiveMob mob : this.mobRegistry.values()) {
         if (predicate.test(mob)) {
            mobs.add(mob);
         }
      }

      return mobs;
   }

   @Override
   public Collection<ActiveMob> getActiveMobsInCombat() {
      return this.activeMobsInCombat.values();
   }

   @Override
   public void registerAudienceTrackedEntity(UUID entityUuid) {
      if (entityUuid != null) {
         this.audienceTrackedEntities.add(entityUuid);
      }
   }

   @Override
   public void unregisterAudienceTrackedEntity(UUID entityUuid) {
      if (entityUuid != null) {
         this.audienceTrackedEntities.remove(entityUuid);
      }
   }

   @Override
   public Set<UUID> getAudienceTrackedEntities() {
      return Collections.unmodifiableSet(this.audienceTrackedEntities);
   }

   @Override
   public void applyMobAudience(ActiveMob am, Set<UUID> audience) {
      AbstractEntity entity = am.getEntity();
      if (entity != null) {
         Set<UUID> audienceCopy = audience == null ? null : new HashSet<>(audience);
         Runnable apply = () -> {
            am.setExecutionAudience(audienceCopy);
            am.setExecutionAudienceMutatedByMechanic(true);
            PersistentDataContainer pdc = entity.getDataContainer();
            pdc.set(MobKeys.EXECUTION_AUDIENCE_MUTATED, PersistentDataType.BYTE, (byte)1);
            if (audienceCopy == null) {
               pdc.remove(MobKeys.EXECUTION_AUDIENCE);
               this.audienceTrackedEntities.remove(entity.getUniqueId());
            } else {
               pdc.set(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY, audienceCopy.toArray(new UUID[0]));
               this.audienceTrackedEntities.add(entity.getUniqueId());
            }

            this.applyAudienceVisibility(entity, audienceCopy);
         };
         if (ServerVersion.isFolia()) {
            Schedulers.of(BukkitAdapter.adapt(entity)).run(apply);
         } else {
            Schedulers.sync().run(apply);
         }
      }
   }

   @Override
   public void applyAudienceVisibility(AbstractEntity entity, Set<UUID> audience) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
         Set<UUID> hiddenFrom = HideMechanic.getHiddenPlayers(entity.getDataContainer());
         if (audience == null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_20)) {
            Entity bukkitEntity = entity.getBukkitEntity();
            if (bukkitEntity != null && !bukkitEntity.isVisibleByDefault()) {
               bukkitEntity.setVisibleByDefault(true);
            }
         }

         for (AbstractPlayer player : this.getPlugin().getBootstrap().getOnlinePlayers()) {
            if (!hiddenFrom.contains(player.getUniqueId())) {
               if (audience != null && !audience.contains(player.getUniqueId())) {
                  player.hideEntity(entity);
               } else {
                  player.showEntity(entity);
               }
            }
         }
      }
   }

   @Deprecated
   public boolean isLoadedMob(AbstractEntity entity) {
      return this.isLoadedMob(entity.getUniqueId());
   }

   @Deprecated
   public boolean isLoadedMob(UUID uuid) {
      return this.isActiveMob(uuid);
   }

   public boolean isMythicMob(Entity entity) {
      return entity.getPersistentDataContainer().has(MobKeys.TYPE, PersistentDataType.STRING);
   }

   public boolean isMythicMob(AbstractEntity entity) {
      PersistentDataContainer pdc = entity.getDataContainer();
      return pdc != null && pdc.has(MobKeys.TYPE, PersistentDataType.STRING);
   }

   public Optional<String> getMythicType(Entity entity) {
      return Optional.ofNullable(entity.getPersistentDataContainer().getOrDefault(MobKeys.TYPE, PersistentDataType.STRING, null));
   }

   public Optional<String> getMythicType(AbstractEntity entity) {
      return this.getMythicType(entity.getBukkitEntity());
   }

   public boolean isActiveMob(AbstractEntity entity) {
      return this.isActiveMob(entity.getUniqueId());
   }

   @Override
   public boolean isActiveMob(UUID uuid) {
      return this.getActiveMob(uuid).isPresent();
   }

   public Optional<ActiveMob> getActiveMob(UUID uuid) {
      return this.mobRegistry.getActiveMob(uuid).or(() -> {
         Optional<AbstractModelEngineSupport> megSupport = this.getPlugin().getCompatibility().getModelEngine();
         if (megSupport.isEmpty()) {
            return Optional.empty();
         } else {
            AbstractModelEngineSupport meg = megSupport.get();
            return !meg.isSubHitbox(uuid) ? Optional.empty() : this.getActiveMob(meg.getParentUUID(uuid));
         }
      });
   }

   public Set<UUID> getVoidList() {
      return this.mmVoidList.asMap().keySet();
   }

   public void setIgnoreEntity(UUID uuid) {
      this.mmVoidList.put(uuid, true);
   }

   public boolean isIgnoredEntity(UUID uuid) {
      return this.mmVoidList.asMap().keySet().contains(uuid);
   }

   public int removeAllMobs() {
      int amount = 0;

      for (ActiveMob am : this.mobRegistry.values()) {
         if (am.getType() == null || am.getDespawnMode().getObeysKillCommand()) {
            if (ServerVersion.isFolia()) {
               Schedulers.of(BukkitAdapter.adapt(am.getEntity().getLocation())).run(() -> {
                  am.setDespawned();
                  this.unregisterActiveMob(am);
                  am.getEntity().remove();
               });
            } else {
               am.setDespawned();
               this.unregisterActiveMob(am);
               am.getEntity().remove();
            }

            amount++;
         }
      }

      return amount;
   }

   public int removeAllAllMobs() {
      int amount = 0;

      for (ActiveMob am : this.mobRegistry.values()) {
         if (ServerVersion.isFolia()) {
            Schedulers.of(BukkitAdapter.adapt(am.getEntity().getLocation())).run(() -> {
               am.setDespawned();
               this.unregisterActiveMob(am);
               am.getEntity().remove();
            });
         } else {
            am.setDespawned();
            this.unregisterActiveMob(am);
            am.getEntity().remove();
         }

         amount++;
      }

      return amount;
   }

   public int despawnAllMobs() {
      int amount = 0;

      for (ActiveMob am : this.mobRegistry.values()) {
         try {
            if (am.getDespawnMode().getDespawnsOnUnload()) {
               am.setDespawnedSync();
               MythicBukkit.inst().getMobManager().unregisterActiveMob(am);
               if (am.getEntity() != null) {
                  try {
                     am.getEntity().remove();
                  } catch (Exception e) {
                  }
               }

               amount++;
            } else {
               am.save();
            }
         } catch (Error | Exception ex) {
            MythicLogger.error("Failed to unload mob {0}: {1}", am.getUniqueId(), ex.toString());
         }
      }

      return amount;
   }

   public void setEntityDespawned(UUID uniqueId) {
      this.mobRegistry.getActiveMob(uniqueId).ifPresent(mob -> mob.setDespawned());
   }

   public void ScanWorld() {
      Schedulers.async().run(() -> {
         for (ActiveMob am : this.getActiveMobs()) {
            if (am.isDead()) {
               am.setDespawned();
            }
         }
      });
   }

   public ActiveMob registerActiveMob(ActiveMob am) {
      this.mobRegistry.put(am.getUniqueId(), am);
      return am;
   }

   public ActiveMob registerActiveMob(AbstractEntity l, MythicMob mm, double level) {
      if (this.isActiveMob(l)) {
         return this.getMythicMobInstance(l);
      } else {
         level = Math.max(1.0, level);
         ActiveMob am = new ActiveMob(l, mm, level);
         this.mobRegistry.put(l.getUniqueId(), am);
         mm.applyMobVolatileOptions(am);
         if (mm instanceof MobType mobType) {
            mobType.applyExecutionAudienceVisibility(am, null);
         }

         SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.SPAWN, am, null, null, false);
         this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
         return am;
      }
   }

   void markSpawning(UUID uuid) {
      this.spawningEntities.add(uuid);
   }

   void unmarkSpawning(UUID uuid) {
      this.spawningEntities.remove(uuid);
   }

   public Optional<ActiveMob> loadMythicMob(Entity entity) {
      return this.loadMythicMob(BukkitAdapter.adapt(entity));
   }

   public Optional<ActiveMob> loadMythicMob(AbstractEntity entity) {
      ActiveMob am = new ActiveMob(entity);
      if (am.validateLoadedMob()) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loaded Mob {0} from PersistentData", entity.getUniqueId());
         this.registerActiveMob(am);
         am.loadSaved();
         if (am.getExecutionAudience() != null) {
            this.audienceTrackedEntities.add(am.getUniqueId());
         }

         return Optional.of(am);
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Failed to load {0} from PersistentData", entity.getUniqueId());
         this.mmVoidList.put(entity.getUniqueId(), true);
         return Optional.empty();
      }
   }

   public Optional<ActiveMob> reconstructForDeath(AbstractEntity entity) {
      try {
         ActiveMob am = new ActiveMob(entity);
         if (am.validateLoadedMob(true)) {
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Reconstructed Mob {0} from PersistentData for death", entity.getUniqueId());
            this.registerActiveMob(am);
            return Optional.of(am);
         } else {
            return Optional.empty();
         }
      } catch (IllegalArgumentException e) {
         return Optional.empty();
      }
   }

   public void unregisterActiveMob(UUID u) {
      this.mobRegistry.removeMob(u);
      this.activeMobsInCombat.remove(u);
      this.audienceTrackedEntities.remove(u);
   }

   public void unregisterActiveMob(ActiveMob am) {
      this.unregisterActiveMob(am.getUniqueId());
   }

   public ActiveMob getMythicMobInstance(Entity target) {
      return this.getMythicMobInstance(BukkitAdapter.adapt(target));
   }

   @Nullable
   public ActiveMob getMythicMobInstance(AbstractEntity target) {
      ActiveMob am = this.getActiveMob(target.getUniqueId()).orElse(null);
      if (am != null) {
         this.resetActiveMobEntity(am, target);
         return am;
      } else {
         if (this.isMythicMob(target)) {
            if (this.spawningEntities.contains(target.getUniqueId())) {
               return null;
            }

            Optional<ActiveMob> maybeMob = this.loadMythicMob(target);
            if (maybeMob.isPresent()) {
               return maybeMob.get();
            }
         }

         return null;
      }
   }

   public void verifyMythicMobInstance(AbstractEntity e) {
      ActiveMob am = this.mobRegistry.get(e.getUniqueId());
      if (am != null) {
         this.resetActiveMobEntity(am, e);
      }
   }

   private void resetActiveMobEntity(ActiveMob am, AbstractEntity e) {
      if ((am.getEntity() == null || !am.getEntity().isValid()) && e != null && e.isValid()) {
         am.setEntity(e);
      }
   }

   @Override
   public boolean isMythicMobSpawning() {
      return spawnflag;
   }

   public ActiveMob spawnMob(String type, Location loc) {
      return this.spawnMob(type, BukkitAdapter.adapt(loc), SpawnReason.OTHER, Double.NaN);
   }

   public ActiveMob spawnMob(String type, Location loc, double level) {
      return this.spawnMob(type, BukkitAdapter.adapt(loc), SpawnReason.OTHER, level);
   }

   public ActiveMob spawnMob(String type, AbstractLocation loc) {
      return this.spawnMob(type, loc, SpawnReason.OTHER, Double.NaN);
   }

   public ActiveMob spawnMob(String type, AbstractLocation loc, double level) {
      return this.spawnMob(type, loc, SpawnReason.OTHER, level);
   }

   public ActiveMob spawnMob(String type, AbstractLocation loc, SpawnReason reason, double level) {
      return this.spawnMob(type, loc, reason, level, null);
   }

   public ActiveMob spawnMob(String type, AbstractLocation loc, SpawnReason reason, double level, MythicSpawner spawner) {
      MythicMob mm = this.getMythicMob(type).orElseGet(() -> null);
      if (mm != null) {
         return mm.spawn(loc, level, reason, null, spawner);
      } else {
         MobStack ms = this.getMythicMobStack(type);
         return ms != null ? ms.spawn(loc, level, reason, spawner) : null;
      }
   }

   public void SetupMythicMobCompat(LivingEntity l, MythicMob mm) {
      if (!spawnflag) {
         if (mm.getMythicEntity() != null) {
            l = (LivingEntity)mm.getMythicEntity().applySpawnOptions(l);
         }

         AbstractEntity abstractEntity = BukkitAdapter.adapt(l);
         this.getPlugin().getMobManager();
         ActiveMob am = new ActiveMob(abstractEntity, mm, getMythicMobLevel(mm, BukkitAdapter.adapt(l)));
         this.mobRegistry.put(l.getUniqueId(), am);
         mm.applyMobOptions(am, am.getLevel());
         mm.applyMobVolatileOptions(am);
         SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.SPAWN, am, null, null, false);
         this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
      }
   }

   public static AbstractLocation findSafeSpawnLocation(AbstractLocation b, double radiusXZ, double radiusY, int mob_height, boolean Ymod, boolean onSurface) {
      Location base = BukkitAdapter.adapt(b);
      if (radiusXZ <= 0.0) {
         radiusXZ = 1.0;
      }

      if (radiusY < 0.0) {
         radiusY = 0.0;
      }

      double x = base.getX() - radiusXZ + Numbers.randomDouble() * (radiusXZ * 2.0);
      double z = base.getZ() - radiusXZ + Numbers.randomDouble() * (radiusXZ * 2.0);
      double y;
      if (radiusY == 0.0) {
         y = base.getY();
      } else if (Ymod) {
         y = base.getY() - radiusY + Numbers.randomDouble() * (radiusY * 2.0);
      } else {
         y = base.getY() + Numbers.randomDouble() * radiusY;
      }

      Location loc = new Location(base.getWorld(), x, y, z, base.getYaw(), base.getPitch());
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(loc)) {
         return BukkitAdapter.adapt(loc);
      } else {
         if (loc.getBlock().getType().isSolid()) {
            int j = 10;

            while (loc.getBlock().getType().isSolid()) {
               x = base.getX() - radiusXZ + Numbers.randomDouble() * (radiusXZ * 2.0);
               z = base.getZ() - radiusXZ + Numbers.randomDouble() * (radiusXZ * 2.0);
               if (radiusY == 0.0) {
                  y = base.getY();
               } else if (Ymod) {
                  y = base.getY() - radiusY + Numbers.randomDouble() * (radiusY * 2.0);
               } else {
                  y = base.getY() + Numbers.randomDouble() * radiusY;
               }

               loc = new Location(base.getWorld(), x, y, z, base.getYaw(), base.getPitch());
               if (--j == 0) {
                  loc = new Location(base.getWorld(), base.getX(), base.getY() + 1.0, base.getZ(), base.getYaw(), base.getPitch());
                  break;
               }
            }
         }

         if (onSurface && !loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            int highestY = loc.getWorld().getHighestBlockYAt(loc);
            if (highestY <= loc.getY()) {
               loc.setY(highestY + 1);
            } else {
               for (int j = 10; !loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid(); j--) {
                  if (j == 0) {
                     loc = new Location(base.getWorld(), base.getX(), base.getY() + 1.0, base.getZ(), base.getYaw(), base.getPitch());
                     break;
                  }

                  loc.setY(loc.getY() - 1.0);
               }
            }
         }

         return BukkitAdapter.adapt(loc);
      }
   }

   public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, double radiusXZ, double radiusY, int mob_height, boolean yMod) {
      return findSafeSpawnLocation(base, radiusXZ, radiusY, mob_height, yMod, false);
   }

   public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, double radiusXZ, double radiusY, int mob_height) {
      return findSafeSpawnLocation(base, radiusXZ, radiusY, mob_height, true, false);
   }

   public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, double radius, int mob_height) {
      return findSafeSpawnLocation(base, radius, radius, mob_height, true, false);
   }

   public Collection<ActiveMob> getMobsInCombat() {
      return this.activeMobsInCombat.values();
   }

   public boolean getInCombat(ActiveMob am) {
      return this.activeMobsInCombat.containsKey(am.getUniqueId());
   }

   public void setInCombat(ActiveMob am, boolean b) {
      if (b) {
         this.activeMobsInCombat.put(am.getUniqueId(), am);
      } else {
         this.activeMobsInCombat.remove(am.getUniqueId());
      }
   }

   public void saveCachedActiveMobs() {
      if (ServerVersion.isFolia()) {
         for (ActiveMob am : this.mobRegistry.values()) {
            AbstractEntity entity = am.getEntity();
            if (entity != null) {
               Schedulers.of(entity.getBukkitEntity()).run(() -> this.saveActiveMob(am));
            }
         }
      } else {
         Schedulers.ensureSync(() -> {
            for (ActiveMob amx : this.mobRegistry.values()) {
               this.saveActiveMob(amx);
            }
         });
      }
   }

   private void saveActiveMob(ActiveMob am) {
      try {
         AbstractEntity entity = am.getEntity();
         if (entity == null || !entity.isValid()) {
            return;
         }

         am.save();
      } catch (Error | Exception e) {
         MythicLogger.error("Failed to save mob data for {0}: {1}", am.getUniqueId(), e.toString());
      }
   }

   public List<LivingEntity> getAllMythicEntities() {
      List<LivingEntity> list = new ArrayList<>();

      for (World w : Bukkit.getWorlds()) {
         for (LivingEntity e : w.getLivingEntities()) {
            if (this.mobRegistry.isActiveMob(e.getUniqueId())) {
               list.add(e);
            }
         }
      }

      return list;
   }

   @Override
   public Optional<MythicMob> getMythicMob(String type) {
      return Optional.ofNullable(this.mmList.getOrDefault(type, null));
   }

   public MobStack getMythicMobStack(String s) {
      for (MobStack ML : this.listMobStacks) {
         if (ML.getName().equals(s)) {
            return ML;
         }
      }

      return null;
   }

   public static int getMythicMobLevel(MythicMob mm, AbstractEntity l) {
      double pdcLevel = l.getDataContainer().getOrDefault(MobKeys.LEVEL, PersistentDataType.DOUBLE, -1.0);
      if (pdcLevel > 0.0) {
         return (int)pdcLevel;
      } else {
         Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
         Objective o = sb.getObjective("MythicMobLevel");
         if (o != null) {
            int si = o.getScore(l.getUniqueId().toString()).getScore();
            if (si > 0) {
               return si;
            }
         }

         int level = 1;
         if (mm.getPerLevelHealth() > 0.0) {
            double health = l.getMaxHealth() - mm.getHealth().get();
            return level + (int)(health / mm.getPerLevelHealth());
         } else {
            return 1;
         }
      }
   }

   public static void setMetaData(AbstractEntity abstractEntity, String s, String key) {
      BukkitAdapter.adapt(abstractEntity).setMetadata(key, new FixedMetadataValue(MythicBukkit.inst(), s));
   }

   public MythicMob determineMobType(AbstractEntity l) {
      List<MetadataValue> list = l.getBukkitEntity().getMetadata("mobname");
      MythicMob mm = null;

      for (MetadataValue mv : list) {
         mm = this.mmList.get(mv);
         if (mm != null) {
            return mm;
         }
      }

      if (l.getCustomName() == null) {
         return null;
      } else {
         mm = this.mmDisplayLookup.get(l.getCustomName());
         if (mm != null) {
            return mm;
         } else {
            for (MythicMob MM : this.mmList.values()) {
               if (MM.getDisplayName() != null) {
                  String search = MM.getDisplayName().toString();
                  search = search.replace("[", "\\[");
                  search = search.replace("]", "\\]");
                  search = search.replace("?", "\\?");
                  search = search.replace("$level", "[0-9]*");
                  search = search.replace("<mob.level>", "[0-9]*");
                  search = ChatColor.translateAlternateColorCodes('&', search);

                  try {
                     if (l.getCustomName().toString().matches(search) && MM.getHealth().get() <= l.getMaxHealth()) {
                        l.setHealth(l.getMaxHealth());
                        return MM;
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }

            return null;
         }
      }
   }

   public MythicMob getMythicMobByDisplayCompat(AbstractEntity l) {
      if (l.getCustomName() == null) {
         return null;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "-- Attempting to match mob for compatibility: " + l.getCustomName().toString());
         MythicMob mm = this.mmDisplayLookup.get(l.getCustomName());
         if (mm != null) {
            return mm;
         } else {
            mm = this.determineMobType(l);
            if (mm != null) {
               return mm;
            } else {
               for (MythicMob MM : this.mmList.values()) {
                  if (MM.getDisplayName() != null) {
                     String search = MM.getDisplayName().toString();
                     search = search.replace("?", "\\?");
                     search = search.replace("$level", "[0-9]*");
                     search = ChatColor.translateAlternateColorCodes('&', search);

                     try {
                        String name = ChatColor.translateAlternateColorCodes('&', l.getCustomName().toString());
                        if (name.matches(search)) {
                           l.setHealth(l.getMaxHealth());
                           return MM;
                        }
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
               }

               return null;
            }
         }
      }
   }

   public boolean registerMob(String internalName, MythicMob mob) {
      if (this.mmList.containsKey(internalName)) {
         return false;
      } else {
         this.mmList.put(internalName, mob);
         return true;
      }
   }

   public void deleteMob(MobType mob) {
      MythicLogger.log("Deleting MythicMob: {0}", mob.getInternalName());
      this.mmList.remove(mob.getInternalName());
      this.mmDisplayLookup.values().removeIf(entry -> entry == mob);
      this.mmDefaultList.values().removeIf(entry -> entry == mob);
      mob.getConfig().deleteNodeAndSave();
   }

   public Collection<MobType> filterMobs(Collection<MobType> mobs, String ffilter) {
      String filter = ffilter.toUpperCase();
      mobs.removeIf(mi -> {
         boolean remove = true;
         if (mi.getInternalName().toUpperCase().contains(filter)) {
            remove = false;
         } else if (mi.getDisplayName() != null && mi.getDisplayName().toString().toUpperCase().contains(filter)) {
            remove = false;
         } else if (mi.getEntityType().toString() != null && mi.getEntityType().toString().contains(filter)) {
            remove = false;
         }

         return remove;
      });
      return mobs;
   }
}
