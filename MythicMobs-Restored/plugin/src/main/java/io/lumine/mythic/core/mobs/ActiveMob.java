package io.lumine.mythic.core.mobs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractTransientEntity;
import io.lumine.mythic.api.mobs.MobEars;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.entities.BukkitWolf;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.promise.PromiseRetiredException;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.holograms.types.HealthBar;
import io.lumine.mythic.core.holograms.types.Nameplate;
import io.lumine.mythic.core.holograms.types.SpeechBubble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.equipment.MobEquipment;
import io.lumine.mythic.core.mobs.tracker.DamageRecord;
import io.lumine.mythic.core.mobs.tracker.DamageSnapshot;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.core.skills.cooldowns.ChargeRestoreOrder;
import io.lumine.mythic.core.skills.cooldowns.CooldownRegistry;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta;
import io.lumine.mythic.core.skills.stats.AttributeMappedStat;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableHolder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.MythicGson;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.IllegalPluginAccessException;

public class ActiveMob implements SkillCaster, StatSource, VariableHolder {
   private UUID uuid;
   @Getter
   private String mobType;
   @Setter
   private transient AbstractEntity entity;
   private transient MythicMob type;
   @Getter
   private long spawnTime = System.currentTimeMillis();
   @Getter
   private long aliveTime = 0L;
   @Getter
   private long lastSeen = System.currentTimeMillis();
   @Getter
   private AbstractLocation spawnLocation;
   @Getter
   private VariableRegistry variables = new VariableRegistry();
   private PlaceholderString displayName;
   @Getter
   private double level;
   private float power = 1.0F;
   @Getter @Setter
   private int globalCooldown = 0;
   private DespawnMode despawnMode = null;
   @Getter
   private String stance;
   @Getter
   private String lastSignal = null;
   @Getter
   private int playerKills = 0;
   @Getter
   private Optl<UUID> ownerUUID = Optl.empty();
   @Getter
   private Optl<UUID> parentUUID = Optl.empty();
   private Collection<UUID> children = Sets.newConcurrentHashSet();
   private Collection<UUID> persistentChildren = Sets.newConcurrentHashSet();
   private String spawnerName = null;
   private Optl<String> faction = Optl.empty();
   private transient CooldownRegistry skillCooldowns = new CooldownRegistry();
   @Getter
   private transient AbstractTransientEntity cachedOwner = null;
   @Getter
   private transient AbstractTransientEntity cachedParent = null;
   @Getter
   private transient Optional<ActiveMob> mount = Optional.empty();
   @Getter
   private transient MythicSpawner spawner = null;
   private transient Map<EquipSlot, MobEquipment> equipment = Maps.newConcurrentMap();
   @Getter
   private transient StatRegistry statRegistry;
   @Getter
   private transient DamageRecord damageRecord;
   private transient ActiveMob.ThreatTable threatTable;
   @Getter
   private transient ActiveMob.ImmunityTable immunityTable;
   @Getter
   private transient MobEars ears;
   private transient Optional<AbstractBossBar> bossBar = Optional.empty();
   private transient Map<String, AbstractBossBar> bossBars = null;
   protected transient HealthBar healthbar = null;
   protected transient Nameplate nameplate = null;
   @Getter
   protected transient SpeechBubble speechBubble = null;
   @Getter @Setter
   private AbstractLocation trackedLocation = null;
   private transient Map<String, Boolean> boundsTriggerStates = new ConcurrentHashMap<>();
   private transient Map<String, Set<UUID>> playerBoundsTriggerStates = new ConcurrentHashMap<>();
   private transient AbstractEntity newTarget = null;
   private transient double lastDamageSkillAmount = 0.0;
   private transient boolean damageSkillRunning = false;
   @Getter
   private transient volatile boolean dead = false;
   @Getter
   private transient int noDamageTicks;
   @Getter
   protected transient AbstractEntity lastAggroCause;
   @Getter @Setter
   private transient Set<UUID> executionAudience = null;
   @Getter @Setter
   private transient boolean executionAudienceMutatedByMechanic = false;
   private boolean changingTarget = false;
   private transient int usingDamageSkill = 0;
   private Collection<String> triggeredMechanicHealthConditionals = ConcurrentHashMap.newKeySet();

   private static final MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   public ActiveMob() {
   }

   public ActiveMob(AbstractEntity entity, MythicMob type, double level) {
      this.uuid = entity.getUniqueId();
      this.entity = entity;
      this.type = type;
      this.mobType = type.getInternalName();
      this.spawnLocation = entity.getLocation();
      this.level = level;
      this.faction = Optl.of(type.getFaction());
      this.globalCooldown = 0;
      this.stance = "default";
      this.noDamageTicks = type.getNoDamageTicks();
      this.statRegistry = new StatRegistry(MythicProvider.get().getStatManager(), this);
      if (type.getStats() != null) {
         for (Entry<StatType, PlaceholderDouble> entry : type.getStats().entrySet()) {
            StatType stat = entry.getKey();
            Optional<Double> value = type.getBaseStatValue(this, stat);
            this.statRegistry.putBaseValueSilently(stat, value.get());
            if (stat instanceof AttributeMappedStat mappedStat) {
               mappedStat.applyAttribute(entity, value.get());
            }
         }
      }

      entity.setHealth(entity.getMaxHealth());
      if (type.getDefaultVariables() != null) {
         try {
            this.variables = type.getDefaultVariables().clone();
         } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
         }
      }

      Schedulers.of(BukkitAdapter.adapt(entity)).runLater(() -> this.setupNew(), 5L);
      PersistentDataContainer data = entity.getDataContainer();
      data.set(MobKeys.SPAWN_TIME, PersistentDataType.STRING, String.valueOf(this.spawnTime));
      data.set(MobKeys.SPAWN_LOCATION, PersistentDataType.STRING, entity.getLocation().serializeShortForm());
      data.set(MobKeys.TYPE, PersistentDataType.STRING, this.mobType);
      data.set(MobKeys.LEVEL, PersistentDataType.DOUBLE, this.level);
      if (this.faction.isPresent()) {
         data.set(MobKeys.FACTION, PersistentDataType.STRING, this.faction.get());
      }

      if (!this.statRegistry.isEmpty()) {
         HashMap<String, Double> statMap = this.statRegistry.getBaseStats();
         data.set(MobKeys.STATS, MobKeys.STAT_FIELD_TYPE, statMap);
      }
   }

   public ActiveMob(AbstractEntity entity) {
      PersistentDataContainer data = entity.getDataContainer();
      this.uuid = entity.getUniqueId();
      this.entity = entity;
      this.statRegistry = new StatRegistry(MythicBukkit.inst().getStatManager(), this);
      this.spawnTime = Long.valueOf(data.getOrDefault(MobKeys.SPAWN_TIME, PersistentDataType.STRING, "0"));
      this.spawnLocation = AbstractLocation.deserialize(data.getOrDefault(MobKeys.SPAWN_LOCATION, PersistentDataType.STRING, "0,0,0,world"));
      this.mobType = data.getOrDefault(MobKeys.TYPE, PersistentDataType.STRING, null);
      this.level = data.getOrDefault(MobKeys.LEVEL, PersistentDataType.DOUBLE, 1.0);
      this.faction = Optl.of(data.getOrDefault(MobKeys.FACTION, PersistentDataType.STRING, null));
      this.spawnerName = data.getOrDefault(MobKeys.SPAWNER, PersistentDataType.STRING, null);
      this.displayName = PlaceholderString.of(data.getOrDefault(MobKeys.DISPLAY_NAME, PersistentDataType.STRING, null));
      this.stance = data.getOrDefault(MobKeys.STANCE, PersistentDataType.STRING, "default");
      this.lastSignal = data.getOrDefault(MobKeys.LAST_SIGNAL, PersistentDataType.STRING, null);
      if (data.has(MobKeys.DESPAWN_MODE, DataType.asEnum(DespawnMode.class))) {
         this.despawnMode = data.get(MobKeys.DESPAWN_MODE, DataType.asEnum(DespawnMode.class));
      }

      if (data.has(MobKeys.OWNER, PersistentDataType.STRING)) {
         this.ownerUUID = Optl.of(UUID.fromString(data.get(MobKeys.OWNER, PersistentDataType.STRING)));
      }

      if (data.has(MobKeys.PARENT, PersistentDataType.STRING)) {
         this.parentUUID = Optl.of(UUID.fromString(data.get(MobKeys.PARENT, PersistentDataType.STRING)));
      }

      if (data.has(MobKeys.CHILDREN, DataType.UUID_ARRAY)) {
         for (UUID child : data.get(MobKeys.CHILDREN, DataType.UUID_ARRAY)) {
            this.children.add(child);
            this.persistentChildren.add(child);
         }
      }

      if (data.has(MobKeys.STATS, MobKeys.STAT_FIELD_TYPE)) {
         for (Entry<String, Double> entry : (data.get(MobKeys.STATS, MobKeys.STAT_FIELD_TYPE)).entrySet()) {
            String stat = entry.getKey();
            Double value = entry.getValue();
            StatType statType = getPlugin().getStatManager().getStatOrRegisterStatic(stat);
            this.statRegistry.putBaseValue(statType, value);
         }
      }

      if (data.has(MobKeys.VARIABLES, PersistentDataType.STRING)) {
         String serializedVariables = data.get(MobKeys.VARIABLES, PersistentDataType.STRING);
         this.variables = MythicGson.get().fromJson(serializedVariables, VariableRegistry.class);
      }

      if (data.has(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY)) {
         UUID[] arr = data.get(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY);
         if (arr != null) {
            this.executionAudience = new HashSet<>(Arrays.asList(arr));
         }
      }

      if (data.has(MobKeys.EXECUTION_AUDIENCE_MUTATED, PersistentDataType.BYTE)) {
         this.executionAudienceMutatedByMechanic = true;
      }

      if (this.mobType == null) {
         throw new IllegalArgumentException("Supplied entity is not a valid MythicMob");
      } else {
         this.checkEquipment();
      }
   }

   private CooldownRegistry getSkillCooldownRegistry() {
      if (this.skillCooldowns == null) {
         this.skillCooldowns = new CooldownRegistry();
      }

      return this.skillCooldowns;
   }

   public void setupNew() {
      if (this.entity != null && this.entity.isValid() && !this.entity.isDead() && this.getType() != null) {
         if (this.type.usesBossBar()) {
            this.bossBar = this.type.getBossBar();
         }

         if (this.type.usesThreatTable()) {
            this.threatTable = new ActiveMob.ThreatTable();
         } else {
            this.threatTable = null;
         }

         if (this.type.usesImmunityTable()) {
            this.immunityTable = new ActiveMob.ImmunityTable();
         } else {
            this.immunityTable = null;
         }

         if (this.type.usesHearing()) {
            this.ears = getPlugin().getVolatileCodeHandler().getAIHandler().createEars(this);
         } else {
            this.ears = null;
         }
      } else {
         this.unregister(true);
      }
   }

   public boolean validateLoadedMob() {
      return this.validateLoadedMob(false);
   }

   public boolean validateLoadedMob(boolean allowDead) {
      if (this.entity == null) {
         return false;
      } else {
         this.type = getPlugin().getMobManager().getMythicMob(this.mobType).orElseGet(() -> null);
         if (this.type == null) {
            return false;
         } else {
            if (this.type.getDefaultVariables() != null) {
               UnmodifiableIterator unmodifiableIterator = this.type.getDefaultVariables().asMap().entrySet().iterator();

               while (unmodifiableIterator.hasNext()) {
                  Entry<String, Variable> entry = (Entry<String, Variable>)unmodifiableIterator.next();
                  if (!this.variables.has(entry.getKey())) {
                     this.variables.put(entry.getKey(), entry.getValue().clone());
                  }
               }
            }

            return this.entity == null ? false : allowDead || !this.entity.isDead();
         }
      }
   }

   public boolean loadSaved() {
      this.lastSeen = System.currentTimeMillis();
      if (ServerVersion.isFolia() ? !FoliaHelper.isOwnedByCurrentRegion(BukkitAdapter.adapt(this.entity)) : !Bukkit.isPrimaryThread()) {
         Schedulers.of(BukkitAdapter.adapt(this.entity)).run(() -> {
            this.getType().applyMobOptions(this, this.level);
            this.getType().applyMobVolatileOptions(this);
            this.initLoadedModules();
         });
      } else {
         this.getType().applyMobOptions(this, this.level);
         this.getType().applyMobVolatileOptions(this);
         this.initLoadedModules();
      }

      if (this.type.usesBossBar()) {
         this.bossBar = this.type.getBossBar();
      }

      if (this.spawnerName != null) {
         this.remountSpawner();
      }

      getPlugin().getSkillManager().getAuraManager().restoreUnloadedAuras(this.entity);
      SkillMetadata data = getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.LOAD, this, null, null, false);
      getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
      return true;
   }

   private void initLoadedModules() {
      if (this.entity != null && this.entity.isValid() && !this.entity.isDead() && this.getType() != null) {
         this.noDamageTicks = this.type.getNoDamageTicks();
         if (this.type.usesThreatTable()) {
            this.threatTable = new ActiveMob.ThreatTable();
         }

         if (this.type.usesImmunityTable()) {
            this.immunityTable = new ActiveMob.ImmunityTable();
         }

         if (this.type.usesHearing()) {
            this.ears = getPlugin().getVolatileCodeHandler().getAIHandler().createEars(this);
         }
      }
   }

   public void tick(long timer, int c) {
      if (!this.dead) {
         if (this.entity == null) {
            this.remove();
         } else if (!this.entity.isDead()) {
            if (!this.entity.isValid()) {
               this.setUnloaded();
            } else {
               this.aliveTime += c;
               if (this.globalCooldown > 0) {
                  this.globalCooldown -= c;
               }

               if (timer % ((double)getPlugin().getConfiguration().getClockBarUpdating() / c) == 0.0) {
                  this.updateBossBar();
               }

               if (this.ears != null) {
                  this.ears.tick();
               }
            }
         } else {
            if (!this.entity.isValid() && (!this.entity.isLiving() || this.entity.getHealth() > 0.0)) {
               this.setDespawnedSync();
            }
         }
      }
   }

   @Override
   public AbstractEntity getEntity() {
      if (this.entity == null) {
         if (!Bukkit.isPrimaryThread()) {
            Schedulers.sync().run(() -> {
               this.entity = getPlugin().getBootstrap().getEntity(this.uuid);
               if (this.entity == null) {
                  this.unregister(true);
               }
            });
            return null;
         }

         this.entity = getPlugin().getBootstrap().getEntity(this.uuid);
      }

      return this.entity;
   }

   public MythicMob getType() {
      if (this.type == null) {
         this.type = getPlugin().getMobManager().getMythicMob(this.mobType).orElseGet(() -> null);
      }

      if (this.type == null) {
         if (this.entity != null) {
            MythicLogger.error("Mob type {0} not found - removing mob {1}", this.mobType, this.uuid);
            this.entity.remove();
            this.entity = null;
         }

         this.remove();
      }

      return this.type;
   }

   public DespawnMode getDespawnMode() {
      return this.despawnMode == null ? this.getType().getDespawnMode() : this.despawnMode;
   }

   public void setDespawnMode(DespawnMode mode) {
      this.despawnMode = mode;
      this.entity.getDataContainer().set(MobKeys.DESPAWN_MODE, DataType.asEnum(DespawnMode.class), mode);
      this.entity.setRemoveWhenFarAway(mode.getDespawnWithoutNearbyPlayers());
      this.entity.setSaveToDisk(mode.getSavesToDisk());
      if (this.parentUUID.isPresent()) {
         getPlugin().getMobManager().getActiveMob(this.parentUUID.get()).ifPresent(parent -> parent.setChildPersistent(this.uuid, mode.getSavesToDisk()));
      }
   }

   public String getDisplayName() {
      if (this.displayName == null) {
         PlaceholderString display = this.type.getDisplayName();
         return display == null ? null : display.get(this);
      } else {
         return this.displayName.get(this);
      }
   }

   public void setDisplayName(String name) {
      this.displayName = PlaceholderString.of(name);
      this.entity.getDataContainer().set(MobKeys.DISPLAY_NAME, PersistentDataType.STRING, name);
      if (this.nameplate != null) {
         this.nameplate.terminate();
      }

      if (this.getType().getModel() != null || !this.getType().isUseCustomNameplate() && !name.contains("\\n")) {
         this.getEntity().setCustomName(this.getDisplayName());
      } else {
         this.nameplate = getPlugin().getHologramManager().createNameplate(this);
         this.getEntity().setCustomName("");
      }
   }

   @Override
   public AbstractLocation getLocation() {
      return this.entity.getLocation();
   }

   public void setSpawnLocation(AbstractLocation location) {
      this.spawnLocation = location;
      PersistentDataContainer data = this.entity.getDataContainer();
      data.set(MobKeys.SPAWN_LOCATION, PersistentDataType.STRING, location.serializeShortForm());
   }

   public void setParent(SkillCaster am) {
      if (am == null) {
         this.parentUUID = Optl.empty();
         this.cachedParent = null;
      } else {
         this.parentUUID = Optl.of(am.getEntity().getUniqueId());
         this.cachedParent = new AbstractTransientEntity(am.getEntity().getUniqueId(), am.getEntity());
         this.entity.getDataContainer().set(MobKeys.PARENT, PersistentDataType.STRING, am.getEntity().getUniqueId().toString());
         am.addChild(this.getEntity());
      }
   }

   public Optl<AbstractEntity> getParent() {
      if (this.cachedParent != null) {
         return this.cachedParent.get();
      } else if (this.parentUUID.isPresent()) {
         this.cachedParent = new AbstractTransientEntity(this.parentUUID.get());
         return this.cachedParent.get();
      } else {
         return Optl.empty();
      }
   }

   @Override
   public void addChild(AbstractEntity entity) {
      this.children.add(entity.getUniqueId());
      Entity bukkitEntity = entity.getBukkitEntity();
      if (bukkitEntity != null && bukkitEntity.isPersistent() && this.persistentChildren.add(entity.getUniqueId())) {
         this.saveChildren();
      }
   }

   @Override
   public void removeChild(AbstractEntity entity) {
      this.removeChild(entity.getUniqueId());
   }

   public void removeChild(UUID uuid) {
      this.children.remove(uuid);
      if (this.persistentChildren.remove(uuid)) {
         this.saveChildren();
      }
   }

   private void setChildPersistent(UUID uuid, boolean persistent) {
      if (this.children.contains(uuid)) {
         if (persistent ? this.persistentChildren.add(uuid) : this.persistentChildren.remove(uuid)) {
            this.saveChildren();
         }
      }
   }

   private void saveChildren() {
      Schedulers.runOnRegion(this.entity.getBukkitEntity(), () -> {
         UUID[] children = this.persistentChildren.toArray(new UUID[0]);
         this.entity.getDataContainer().set(MobKeys.CHILDREN, DataType.UUID_ARRAY, children);
      });
   }

   @Override
   public Collection<AbstractEntity> getChildren() {
      return getPlugin().getBootstrap().getEntities(this.children);
   }

   public Collection<UUID> getChildrenUUID() {
      return Collections.unmodifiableCollection(this.children);
   }

   public Optl<AbstractEntity> getOwner() {
      if (this.cachedOwner != null) {
         return this.cachedOwner.get();
      } else if (this.ownerUUID.isPresent()) {
         this.cachedOwner = new AbstractTransientEntity(this.ownerUUID.get());
         return this.cachedOwner.get();
      } else {
         return Optl.empty();
      }
   }

   public void setOwnerUUID(UUID uuid) {
      this.ownerUUID = Optl.of(uuid);
      if (uuid != null) {
         this.cachedOwner = new AbstractTransientEntity(uuid);
         this.entity.getDataContainer().set(MobKeys.OWNER, PersistentDataType.STRING, uuid.toString());
      } else {
         this.cachedOwner = null;
      }

      if (this.getType().getMythicEntity() instanceof BukkitWolf) {
         Wolf wolf = (Wolf)this.getEntity().getBukkitEntity();
         AttributeInstance maxHealthAttribute = wolf.getAttribute(Attribute.MAX_HEALTH);
         double previousMaxHealth = maxHealthAttribute != null ? maxHealthAttribute.getBaseValue() : wolf.getMaxHealth();
         double previousHealth = wolf.getHealth();
         wolf.setOwner(Bukkit.getPlayer(uuid));
         if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(previousMaxHealth);
         }

         wolf.setHealth(Math.min(previousHealth, wolf.getMaxHealth()));
      }
   }

   public void removeOwner() {
      this.ownerUUID = Optl.empty();
      this.cachedOwner = null;
   }

   public void setMount(ActiveMob am) {
      this.mount = Optional.of(am);
   }

   public void remountType() {
      if (this.mobType == null) {
         this.unregister();
      } else {
         MythicMob mm = getPlugin().getMobManager().getMythicMob(this.mobType).orElseGet(() -> null);
         if (mm == null) {
            this.unregister();
         } else {
            this.type = mm;
            if (this.getEntity() != null
               && (this.getEntity().isLiving() && this.getEntity().getHealth() == this.getEntity().getMaxHealth() || !this.getEntity().isLiving())) {
               this.type.applyMobOptions(this, this.level);
               this.type.applyMobVolatileOptions(this);
            }
         }
      }
   }

   public double getDamage() {
      return this.getType().getDamage(this);
   }

   public double getArmor() {
      return this.getType().getArmor(this);
   }

   public void setLevel(double level) {
      this.level = level;
      if (this.getType() != null && this.getType().getStats() != null) {
         double oldMaxHealth = this.entity.getMaxHealth();
         double healthRatio = oldMaxHealth > 0.0 ? this.entity.getHealth() / oldMaxHealth : 0.0;

         for (Entry<StatType, PlaceholderDouble> entry : this.getType().getStats().entrySet()) {
            StatType stat = entry.getKey();
            Optional<Double> value = this.getType().getBaseStatValue(this, stat);
            if (value.isPresent()) {
               this.statRegistry.putBaseValue(stat, value.get());
            }
         }

         if (healthRatio > 0.0 && this.entity.getMaxHealth() != oldMaxHealth) {
            this.entity.setHealth(this.entity.getMaxHealth() * healthRatio);
         }
      }

      this.getType().applyMobOptions(this, level);
      this.getType().applyMobVolatileOptions(this);
      this.entity.getDataContainer().set(MobKeys.LEVEL, PersistentDataType.DOUBLE, this.level);
   }

   public void setPower(float power) {
      this.power = power;
   }

   @Override
   public float getPower() {
      return this.getType() == null ? this.power : (float)(this.power + (this.getLevel() - 1.0) * this.getType().getPerLevelPower());
   }

   public UUID getUniqueId() {
      return this.uuid;
   }

   public Boolean updateBoundsTriggerState(String key, boolean inBounds) {
      return this.boundsTriggerStates.put(key, inBounds);
   }

   public Set<UUID> getPlayerBoundsTriggerState(String key) {
      return this.playerBoundsTriggerStates.computeIfAbsent(key, k -> new HashSet<>());
   }

   public void setStance(String stance) {
      this.stance = stance;
   }

   public boolean hasFaction() {
      return this.faction.isPresent() ? true : this.type.hasFaction();
   }

   public String getFaction() {
      return this.faction.isPresent() ? this.faction.get() : this.type.getFaction();
   }

   public ActiveMob setFaction(String faction) {
      this.faction = Optl.of(faction);
      if (faction != null) {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_14)) {
            this.entity.getDataContainer().set(MobKeys.FACTION, PersistentDataType.STRING, faction);
         }
      } else if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_14)) {
         this.entity.getDataContainer().remove(MobKeys.FACTION);
      }

      return this;
   }

   public void incrementPlayerKills() {
      this.playerKills++;
   }

   public void importPlayerKills(int pk) {
      this.playerKills = pk;
   }

   public void setLastAggroCause(AbstractEntity aggro) {
      this.lastAggroCause = aggro;
   }

   public void checkEquipment() {
      if (this.entity.isLiving()) {
         if (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(this.entity.getBukkitEntity())) {
            LivingEntity livingEntity = (LivingEntity)this.entity.getBukkitEntity();
            EntityEquipment ee = livingEntity.getEquipment();
            if (ee != null) {
               this.parseEquipmentStats(ee.getHelmet(), EquipSlot.HEAD);
               this.parseEquipmentStats(ee.getChestplate(), EquipSlot.CHEST);
               this.parseEquipmentStats(ee.getLeggings(), EquipSlot.LEGS);
               this.parseEquipmentStats(ee.getBoots(), EquipSlot.FEET);
               this.parseEquipmentStats(ee.getItemInMainHand(), EquipSlot.HAND);
               this.parseEquipmentStats(ee.getItemInOffHand(), EquipSlot.OFFHAND);
               this.statRegistry.updateDirtyStats();
            }
         }
      }
   }

   private void parseEquipmentStats(ItemStack item, EquipSlot slot) {
      MobEquipment currentItem = this.equipment.get(slot);
      if (currentItem != null) {
         if (currentItem.equals(item)) {
            currentItem.setItemStack(item);
            return;
         }

         this.equipment.remove(slot);
      }

      if (item != null && item.hasItemMeta()) {
         MobEquipment newItem = new MobEquipment(this, item, slot, currentItem);
         this.equipment.put(slot, newItem);
      } else if (currentItem != null && currentItem.hasStats()) {
         for (StatType stat : currentItem.getStats().keySet()) {
            this.statRegistry.removeValueSilently(stat, currentItem);
         }
      }
   }

   public boolean hasTarget() {
      return this.hasThreatTable() ? this.threatTable.getTopThreatHolder() == null : this.entity.isCreature() && this.entity.getTarget() != null;
   }

   public void setTarget(AbstractEntity l) {
      this.newTarget = l;
      if (this.entity.isLiving() && !this.changingTarget) {
         this.changingTarget = true;
         this.entity.setTarget(l);
         this.changingTarget = false;
      }
   }

   public boolean changingTarget() {
      return this.newTarget != null;
   }

   public AbstractEntity getNewTarget() {
      return this.newTarget;
   }

   public void voidTargetChange() {
      this.newTarget = null;
   }

   public void resetTarget() {
      if (this.hasThreatTable()) {
         this.threatTable.dropCombat();
      } else {
         this.entity.setTarget(null);
      }
   }

   public void recordPlayerDamage(UUID playerUUID, double damage) {
      if (this.damageRecord == null) {
         this.damageRecord = new DamageRecord(this);
      }

      DamageSnapshot snapshot = DamageSnapshot.create(this.getUniqueId(), playerUUID, damage);
      this.damageRecord.registerSnapshot(snapshot);
   }

   public ActiveMob.ThreatTable getThreatTable() {
      if (this.threatTable == null && this.type.usesThreatTable()) {
         this.threatTable = new ActiveMob.ThreatTable();
      }

      return this.threatTable;
   }

   public boolean hasThreatTable() {
      return this.threatTable != null;
   }

   public void importThreatTable(ActiveMob.ThreatTable tt) {
      this.threatTable = tt;
   }

   public boolean hasImmunityTable() {
      return this.immunityTable != null;
   }

   public void save() {
      if (this.entity != null) {
         String serializeVariables = MythicGson.get().toJson(this.variables);
         PersistentDataContainer data = this.entity.getDataContainer();
         if (this.statRegistry != null) {
            HashMap<String, Double> statMap = this.statRegistry.getBaseStats();
            data.set(MobKeys.STATS, MobKeys.STAT_FIELD_TYPE, statMap);
         }

         data.set(MobKeys.VARIABLES, PersistentDataType.STRING, serializeVariables);
         data.set(MobKeys.STANCE, PersistentDataType.STRING, this.stance);
      }
   }

   public void despawn() {
      if (getPlugin().isShuttingDown()) {
         this.setDespawnedSync();
      } else {
         this.setDespawned();
      }

      if (this.entity != null) {
         this.entity.remove();
      }
   }

   public void remove() {
      this.dead = true;
      this.unregister();
      if (this.entity != null) {
         this.entity.remove();
      }
   }

   public void setDespawned() {
      if (!this.dead) {
         this.dead = true;

         try {
            SkillMetadata data = getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.DESPAWNED, this, this.getEntity(), null, false);
            getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
         } catch (IllegalPluginAccessException e) {
         }

         Schedulers.of(BukkitAdapter.adapt(this.entity)).run(() -> getPlugin().getBootstrap().handleMobDespawnEvent(this));
         if (this.getSpawner() != null) {
            this.getSpawner().markMobDespawned(this);
         }
      }

      this.unregister();
   }

   public void setDespawnedSync() {
      if (!this.dead) {
         this.dead = true;

         try {
            SkillMetadata data = getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.DESPAWNED, this, this.getEntity(), null, false);
            getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
         } catch (IllegalPluginAccessException e) {
         }

         getPlugin().getBootstrap().handleMobDespawnEvent(this);
         if (this.getSpawner() != null) {
            this.getSpawner().markMobDespawned(this);
         }
      }

      this.unregister();
   }

   public void setDespawnedOnOwningRegion() {
      Location location = BukkitAdapter.adapt(this.getEntity().getLocation());
      Bukkit.getRegionScheduler()
         .execute(getPlugin(), location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, () -> this.setDespawnedSync());
   }

   public void setDead() {
      if (!this.dead) {
         this.dead = true;
         if (this.getSpawner() != null) {
            this.getSpawner().markMobDead(this);
         }

         this.unregister();
      }
   }

   public void setUnloaded() {
      if (!this.dead) {
         if (this.type == null) {
            this.remove();
         } else if (this.getDespawnMode().getDespawnsOnUnload()) {
            try {
               SkillMetadata data = getPlugin()
                  .getSkillManager()
                  .getEventBus()
                  .buildSkillMetadata(SkillTriggers.DESPAWNED, this, this.getEntity(), null, false);
               getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
            } catch (IllegalPluginAccessException e) {
            }

            getPlugin().getBootstrap().handleMobDespawnEvent(this);
            if (this.getSpawner() != null) {
               this.getSpawner().markMobDespawned(this);
            }

            this.remove();
         } else {
            this.unloadBossBars();
            this.save();
            getPlugin().getSkillManager().getAuraManager().persistAurasThroughUnload(this.entity);
            getPlugin().getMobManager().getMobRegistry().setInactive(this.uuid);
         }
      }
   }

   public void unregister() {
      this.unregister(false);
   }

   public void unregister(boolean sync) {
      getPlugin().getMobManager().setIgnoreEntity(this.uuid);
      this.unloadBossBars();
      if (this.parentUUID.isPresent() && this.getEntity() != null) {
         AbstractEntity parentEntity = getPlugin().getBootstrap().getEntity(this.parentUUID.get());
         if (parentEntity != null) {
            SkillCaster parentCaster = getPlugin().getSkillManager().getCaster(parentEntity);
            if (parentCaster != null) {
               parentCaster.removeChild(this.getEntity());
            }
         }
      }

      if (this.getEntity() != null && this.getType() != null && this.getType().getModel() != null) {
         this.getType().getModel().remove(this.getEntity());
      }

      if (sync) {
         getPlugin().getMobManager().unregisterActiveMob(this);
      } else {
         Schedulers.of(BukkitAdapter.adapt(this.entity)).runLater(() -> getPlugin().getMobManager().unregisterActiveMob(this), 5L).exceptionallyAsync(t -> {
            if (t instanceof PromiseRetiredException) {
               getPlugin().getMobManager().unregisterActiveMob(this);
            }

            return null;
         });
      }
   }

   @Override
   public void setUsingDamageSkill(boolean bool) {
      if (bool) {
         this.usingDamageSkill = this.usingDamageSkill >= 3 ? 3 : this.usingDamageSkill + 1;
      } else {
         this.usingDamageSkill = this.usingDamageSkill <= 0 ? 0 : this.usingDamageSkill - 1;
      }
   }

   @Override
   public boolean isUsingDamageSkill() {
      return this.usingDamageSkill >= 3;
   }

   public void setLastDamageSkillAmount(double d) {
      this.lastDamageSkillAmount = d;
   }

   public double getLastDamageSkillAmount() {
      return this.lastDamageSkillAmount;
   }

   public void setSpawner(MythicSpawner ms) {
      this.spawnerName = ms.getInternalName();
      this.spawner = ms;
      this.entity.getDataContainer().set(MobKeys.SPAWNER, PersistentDataType.STRING, ms.getInternalName());
   }

   public void remountSpawner() {
      if (this.spawnerName != null) {
         MythicSpawner maybeSpawner = getPlugin().getSpawnerManager().getSpawnerByName(this.spawnerName);
         if (maybeSpawner != null) {
            this.spawner = maybeSpawner;
            if (!this.spawner.trackMob(this)) {
               this.remove();
            }
         }
      }
   }

   public void signalMob(AbstractEntity trigger, String signal) {
      SkillMetadata data = new SkillMetadataImpl(SkillTriggers.SIGNAL, this, trigger);
      if (trigger != null) {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Received signal {0} from {1}", signal, trigger.getUniqueId());
         HashSet<AbstractEntity> eTargets = new HashSet<>();
         eTargets.add(data.getTrigger());
         data.setEntityTargets(eTargets);
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Received signal {0}", signal);
      }

      data.setPower(this.getPower());
      Schedulers.runOnRegion(this.entity.getBukkitEntity(), () -> {
         this.lastSignal = signal;
         this.entity.getDataContainer().set(MobKeys.LAST_SIGNAL, PersistentDataType.STRING, signal);
         this.getType().executeSignalSkill(signal, data);
      });
   }

   public void signalDamaged() {
      if (this.healthbar != null) {
         this.healthbar.update();
      } else if (this.getType().getUsesHealthBar()) {
         this.healthbar = getPlugin().getHologramManager().createHealthBar(this);
      }
   }

   @Override
   public void setSkillCooldownMillis(AbstractSkill skill, double millis) {
      if (millis > 0.0) {
         this.getSkillCooldownRegistry().setCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), (long)millis);
      } else {
         this.getSkillCooldownRegistry().clearCooldown(skill.getUuid());
      }
   }

   @Override
   public double getSkillCooldownRemainingMillis(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getRemainingCooldownMillis(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   @Override
   public boolean isSkillOnCooldown(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().isOnCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public boolean isSkillRecharging(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().isRecharging(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public int getAvailableSkillCharges(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getAvailableCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public int getOverflowSkillCharges(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getOverflowCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public boolean consumeSkillCharge(AbstractSkill skill, long cooldownMillis) {
      return this.getSkillCooldownRegistry().consumeCharge(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), cooldownMillis);
   }

   public void addSkillCharges(AbstractSkill skill, int amount, ChargeRestoreOrder order, boolean overflow) {
      this.getSkillCooldownRegistry().addCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), amount, order, overflow);
   }

   public void clearSkillCooldowns() {
      this.getSkillCooldownRegistry().purgeExpired();
      this.skillCooldowns = new CooldownRegistry();
   }

   public boolean hasMechanicUsedHealthCheck(String id) {
      return this.triggeredMechanicHealthConditionals.contains(id);
   }

   public void markMechanicUsedHealthCheck(String id) {
      this.triggeredMechanicHealthConditionals.add(id);
   }

   @Override
   public AuraRegistry getAuraRegistry() {
      return getPlugin().getSkillManager().getAuraManager().getAuraRegistry(this.entity);
   }

   public void addBar(String key, AbstractBossBar bar) {
      if (this.bossBars == null) {
         this.bossBars = new ConcurrentHashMap<>();
      }

      this.bossBars.put(key, bar);
   }

   public AbstractBossBar getBar(String key) {
      return this.bossBars == null ? null : this.bossBars.getOrDefault(key, null);
   }

   public void removeBar(String key) {
      if (this.bossBars != null && this.bossBars.containsKey(key)) {
         this.bossBars.remove(key).terminate();
      }
   }

   public void updateBossBar() {
      boolean hasPrimary = this.bossBar.isPresent();
      boolean hasAux = this.bossBars != null && !this.bossBars.isEmpty();
      if (hasPrimary || hasAux) {
         AbstractLocation loc = this.getLocation();
         Collection<AbstractPlayer> inRange = loc.getWorld().getPlayersInRadius(loc, this.getType().getBossBarRangeSquared());
         HashSet<UUID> inRangeIds = new HashSet<>(inRange.size() * 2);

         for (AbstractPlayer p : inRange) {
            inRangeIds.add(p.getUniqueId());
         }

         if (hasPrimary) {
            AbstractBossBar bar = this.bossBar.get();
            Collection<AbstractPlayer> current = bar.getPlayers();
            double progress = this.getEntity().getHealth() / this.getEntity().getMaxHealth();
            String title = this.type.getBossBarTitle().get(new GenericPlaceholderMeta(this, this.getEntity()));
            bar.setTitle(title);
            if (progress >= 0.0 || progress <= 1.0) {
               bar.setProgress(progress);
            }

            for (AbstractPlayer player : current) {
               if (!inRangeIds.contains(player.getUniqueId())) {
                  bar.removePlayer(player);
               }
            }

            for (AbstractPlayer playerx : inRange) {
               if (!bar.isViewing(playerx)) {
                  bar.addPlayer(playerx);
               }
            }
         }

         if (hasAux) {
            for (AbstractBossBar barx : this.bossBars.values()) {
               barx.getPlayers().removeIf(playerxx -> !inRangeIds.contains(playerxx.getUniqueId()));
               Collection<AbstractPlayer> barPlayers = barx.getPlayers();

               for (AbstractPlayer playerxx : inRange) {
                  if (!barPlayers.contains(playerxx)) {
                     barx.addPlayer(playerxx);
                  }
               }
            }
         }
      }
   }

   public void unloadBossBars() {
      if (this.bossBar.isPresent()) {
         this.bossBar.get().removeAll();
         this.bossBar = Optional.empty();
      }

      if (this.bossBars != null) {
         this.bossBars.values().forEach(bar -> bar.terminate());
         this.bossBars.clear();
      }
   }

   public boolean getShowCustomNameplate() {
      return this.nameplate == null;
   }

   public void setShowCustomNameplate(boolean b) {
      if (b) {
         if (this.nameplate == null && this.getType().getModel() == null) {
            this.nameplate = getPlugin().getHologramManager().createNameplate(this);
         }
      } else if (this.nameplate != null) {
         this.nameplate.terminate();
         this.nameplate = null;
      }
   }

   @Override
   public int hashCode() {
      return this.getUniqueId().toString().hashCode();
   }

   public class ImmunityTable {
      private long otherSource;
      private Map<UUID, Long> immunityTable = Maps.newConcurrentMap();

      public ImmunityTable() {
         this.immunityTable = new ConcurrentHashMap<>();
      }

      public void setCooldown(AbstractEntity e) {
         if (e == null) {
            this.otherSource = ActiveMob.this.aliveTime;
         } else {
            this.immunityTable.put(e.getUniqueId(), ActiveMob.this.aliveTime);
         }
      }

      public void clearCooldown(AbstractEntity e) {
         if (e == null) {
            this.otherSource = -ActiveMob.this.getNoDamageTicks();
         } else {
            this.immunityTable.put(e.getUniqueId(), (long)(-(ActiveMob.this.getNoDamageTicks() + 1)));
         }
      }

      public boolean onCooldown(AbstractEntity e) {
         if (e == null) {
            return ActiveMob.this.aliveTime - this.otherSource <= ActiveMob.this.getNoDamageTicks() / 2;
         } else if (this.immunityTable.containsKey(e.getUniqueId())) {
            long last = this.immunityTable.get(e.getUniqueId());
            return ActiveMob.this.aliveTime - last <= ActiveMob.this.getNoDamageTicks() / 2;
         } else {
            return false;
         }
      }
   }

   public class ThreatTable implements Cloneable {
      private WeakReference<ActiveMob> mob;
      private double totalThreat = 0.0;
      private double targetThreat = 0.0;
      private int targetUnreachableTicks = 0;
      private AbstractEntity target = null;
      @Getter
      private final Map<UUID, Double> threatTable = new ConcurrentHashMap<>();
      private final Map<UUID, AbstractEntity> threatEntities = new ConcurrentHashMap<>();

      public void setParent(ActiveMob am) {
         this.mob = new WeakReference<>(am);
      }

      public void tickThreat() {
         try {
            if (ActiveMob.this.entity == null) {
               return;
            }

            if (this.target == null) {
               this.clearTarget();
               this.targetHighestThreat();
               return;
            }

            if (this.target.isDead()) {
               this.targetDeath();
               return;
            }

            if (this.threatTable.isEmpty()) {
               this.dropCombat();
               return;
            }

            if (ActiveMob.this.getFaction() != null) {
               String targetFaction = this.target.getFaction().orElse(null);
               if (targetFaction != null && targetFaction.equals(ActiveMob.this.getFaction())) {
                  this.clearTarget();
                  this.targetHighestThreat();
                  return;
               }
            }

            if (this.target.isPlayer() && !this.target.asPlayer().isOnline()) {
               this.clearTarget();
               this.targetHighestThreat();
            }

            AbstractLocation mobLocation = ActiveMob.this.getEntity().getLocation();
            AbstractLocation targetLocation = this.target.getLocation();
            boolean decayUnreachable = ActiveMob.this.getType().getThreatTableDecaysUnreachable();
            int unreachableDropTicks = ActiveMob.this.getType().getThreatTableDropUnreachableTicks();
            if (targetLocation.getWorld().equals(mobLocation.getWorld())
               && (!this.target.isPlayer() || !this.target.asPlayer().isInCreativeMode() && !this.target.asPlayer().isInSpectatorMode())) {
               boolean targetOutOfSight = decayUnreachable && !this.target.hasLineOfSight(ActiveMob.this.getEntity());
               if (targetOutOfSight) {
                  this.decayTargetThreat();
               }

               boolean targetOutOfRange = decayUnreachable
                  && targetLocation.distanceSquared(mobLocation)
                     > ActiveMob.this.getType().getMaxThreatDistance() * ActiveMob.this.getType().getMaxThreatDistance();
               if (!targetOutOfRange && !targetOutOfSight) {
                  this.targetUnreachableTicks = 0;
               } else {
                  this.targetUnreachableTicks++;
                  if (this.targetUnreachableTicks >= unreachableDropTicks) {
                     this.clearTarget();
                     this.targetHighestThreat();
                     this.targetUnreachableTicks = 0;
                  }
               }
            } else {
               this.clearTarget();
               this.targetHighestThreat();
               this.targetUnreachableTicks = 0;
            }
         } catch (Error | Exception e) {
            this.clearTarget();
            this.targetHighestThreat();
            MythicLogger.handleMinorError(e);
         }
      }

      public boolean inCombat() {
         return !this.threatTable.isEmpty();
      }

      public boolean targetEvent(AbstractEntity entity) {
         if (entity == null) {
            return false;
         } else {
            UUID uuid = entity.getUniqueId();
            if (this.target == null) {
               this.target = entity;
               this.targetUnreachableTicks = 0;
               ActiveMob.this.setTarget(entity);
               if (this.threatTable.containsKey(uuid)) {
                  this.targetThreat = this.threatTable.get(uuid);
               } else {
                  if (!this.inCombat()) {
                     ActiveMob.getPlugin().getMobManager().setInCombat(ActiveMob.this, true);
                     SkillMetadata data = ActiveMob.getPlugin()
                        .getSkillManager()
                        .getEventBus()
                        .buildSkillMetadata(SkillTriggers.ENTERCOMBAT, ActiveMob.this, entity, null, false);
                     ActiveMob.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
                  }

                  this.targetThreat = 1.0;
                  this.threatTable.put(uuid, 1.0);
                  this.threatEntities.put(uuid, entity);
               }

               return true;
            } else if (this.target.equals(entity)) {
               return true;
            } else if (!this.target.isDead()) {
               return false;
            } else {
               this.targetDeath();
               return this.target != null && this.target.equals(entity);
            }
         }
      }

      public boolean Taunt(AbstractEntity target) {
         if (target.equals(this.target)) {
            return false;
         } else {
            this.threatTable.put(target.getUniqueId(), this.targetThreat * 1.1);
            this.threatEntities.put(target.getUniqueId(), target);
            this.targetThreateningEntity(target);
            return true;
         }
      }

      public void dropCombat() {
         this.target = null;
         this.targetThreat = 0.0;
         this.totalThreat = 0.0;
         this.targetUnreachableTicks = 0;
         this.threatTable.clear();
         this.threatEntities.clear();
         ActiveMob.getPlugin().getMobManager().setInCombat(ActiveMob.this, false);
         ActiveMob.this.entity.setTarget(null);
         SkillMetadata data = ActiveMob.getPlugin()
            .getSkillManager()
            .getEventBus()
            .buildSkillMetadata(SkillTriggers.DROPCOMBAT, ActiveMob.this, null, null, false);
         ActiveMob.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
      }

      public void observeDeath(AbstractEntity entity) {
         if (this.target != null) {
            if (this.target.equals(entity)) {
               this.targetDeath();
            }

            this.threatTable.remove(entity.getUniqueId());
            this.threatEntities.remove(entity.getUniqueId());
         }
      }

      public void observeChangeWorld(AbstractEntity entity) {
         if (this.target != null) {
            if (this.target.equals(entity)) {
               this.clearTarget();
               if (this.inCombat()) {
                  this.targetHighestThreat();
               }
            }

            this.threatTable.remove(entity.getUniqueId());
            this.threatEntities.remove(entity.getUniqueId());
         }
      }

      public boolean targetThreateningEntity(AbstractEntity entity) {
         this.target = entity;
         this.targetThreat = this.threatTable.get(entity.getUniqueId());
         this.targetUnreachableTicks = 0;
         ActiveMob.this.setTarget(this.target);
         return true;
      }

      public void targetHighestThreat() {
         if (this.threatTable.isEmpty()) {
            this.dropCombat();
         } else {
            this.targetUnreachableTicks = 0;
            this.targetThreat = 0.0;

            for (Entry<UUID, Double> pairs : this.threatTable.entrySet()) {
               AbstractEntity checkedTarget = this.threatEntities.get(pairs.getKey());
               if (checkedTarget != null && checkedTarget.isValid() && pairs.getValue() > this.targetThreat) {
                  if (ActiveMob.this.getFaction() != null) {
                     String targetFaction = checkedTarget.getFaction().orElse(null);
                     if (targetFaction != null && targetFaction.equals(ActiveMob.this.getFaction())) {
                        continue;
                     }
                  }

                  this.targetThreat = pairs.getValue();
                  this.target = checkedTarget;
               }
            }

            ActiveMob.this.setTarget(this.target);
         }
      }

      public void clearTarget() {
         if (this.target != null) {
            this.threatTable.remove(this.target.getUniqueId());
            this.threatEntities.remove(this.target.getUniqueId());
         }

         if (this.threatTable.isEmpty()) {
            this.dropCombat();
         }
      }

      public void targetDeath() {
         if (this.target.isDead()) {
            this.clearTarget();
            this.targetHighestThreat();
         }
      }

      public void threatSet(AbstractEntity entity, double amount) {
         if (entity != null) {
            if (!entity.getUniqueId().equals(ActiveMob.this.getUniqueId())) {
               if (!ActiveMob.this.ownerUUID.isPresent() || !ActiveMob.this.ownerUUID.get().equals(entity.getUniqueId())) {
                  if (!entity.isPlayer() || !entity.asPlayer().isInCreativeMode() && !entity.asPlayer().isInSpectatorMode()) {
                     this.totalThreat = this.totalThreat - this.getThreat(entity);
                     if (this.target == null) {
                        this.targetEvent(entity);
                        this.targetThreat = amount;
                     } else if (this.target.equals(entity)) {
                        this.targetThreat = amount;
                     }

                     this.threatTable.put(entity.getUniqueId(), amount);
                     this.threatEntities.put(entity.getUniqueId(), entity);
                     if (amount > this.targetThreat * 1.1) {
                        this.targetThreateningEntity(entity);
                     }

                     this.totalThreat += amount;
                  }
               }
            }
         }
      }

      public void threatGain(AbstractEntity entity, double amount) {
         if (entity != null) {
            UUID uuid = entity.getUniqueId();
            if (!entity.getUniqueId().equals(ActiveMob.this.getUniqueId())) {
               if (!ActiveMob.this.ownerUUID.isPresent() || !ActiveMob.this.ownerUUID.get().equals(entity.getUniqueId())) {
                  if (!entity.isPlayer() || !entity.asPlayer().isInCreativeMode() && !entity.asPlayer().isInSpectatorMode()) {
                     if (this.target == null) {
                        this.targetEvent(entity);
                        this.targetThreat = amount;
                     } else if (this.target.equals(entity)) {
                        this.targetThreat += amount;
                        amount = this.targetThreat;
                     } else if (this.threatTable.containsKey(uuid)) {
                        amount += this.threatTable.get(uuid);
                     }

                     this.threatTable.put(uuid, amount);
                     this.threatEntities.put(uuid, entity);
                     if (amount > this.targetThreat * 1.1) {
                        this.targetThreateningEntity(entity);
                     }

                     this.totalThreat += amount;
                  }
               }
            }
         }
      }

      public void threatLoss(AbstractEntity entity, double amount) {
         UUID uuid = entity.getUniqueId();
         if (this.threatTable.containsKey(uuid)) {
            double nT = this.threatTable.get(uuid) - amount;
            if (nT < 0.0) {
               amount -= 0.0 - nT;
               nT = 0.0;
            }

            this.threatTable.put(uuid, nT);
            this.totalThreat -= amount;
            if (this.target.equals(entity)) {
               this.targetHighestThreat();
            }
         }
      }

      public void decayTargetThreat() {
         double dt = this.targetThreat * 0.96;
         this.totalThreat = this.totalThreat - (this.targetThreat - dt);
         this.targetThreat = dt;
         this.threatTable.put(this.target.getUniqueId(), dt);
      }

      public AbstractEntity getTopThreatHolder() {
         return this.target;
      }

      public double getTopTargetThreat() {
         return this.targetThreat;
      }

      public double getThreat(AbstractEntity entity) {
         return this.threatTable.getOrDefault(entity.getUniqueId(), 0.0);
      }

      public double getTotalThreat() {
         return this.totalThreat;
      }

      public Set<AbstractEntity> getAllThreatTargets() {
         return this.threatTable.keySet().stream().map(uuid -> ActiveMob.getPlugin().getBootstrap().getPlayer(uuid)).collect(Collectors.toSet());
      }

      public int size() {
         return this.threatTable.size();
      }

      public ImmutableMap<UUID, Double> asMap() {
         return ImmutableMap.copyOf(this.threatTable);
      }

      public ActiveMob.ThreatTable clone() throws CloneNotSupportedException {
         return (ActiveMob.ThreatTable)super.clone();
      }
   }
}
