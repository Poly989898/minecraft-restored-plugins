package io.lumine.mythic.core.mobs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.stats.StatManager;
import io.lumine.mythic.api.volatilecode.VolatileEnchantment;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.clock.TimingsHandler;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.compatibility.WorldGuardSupport;
import io.lumine.mythic.bukkit.events.MythicMobPreSpawnConfigureEvent;
import io.lumine.mythic.bukkit.events.MythicMobPreSpawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.LineConfig;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.config.DropMethod;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.menus.mobs.MobEditorMenuContext;
import io.lumine.mythic.core.menus.mobs.MobMenuContext;
import io.lumine.mythic.core.mobs.model.MPetsModel;
import io.lumine.mythic.core.mobs.model.MobModel;
import io.lumine.mythic.core.mobs.totems.TotemConfig;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.audience.CustomAudience;
import io.lumine.mythic.core.skills.bounds.BoundsSpec;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.Stats;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableUtils;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.objecthunter.exp4j.Expression;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobType implements MythicMob, SkillHolder, MenuData<MobMenuContext> {
   private static final Pattern TIMER_PATTERN = Pattern.compile("~onTimer:([0-9]+)");
   private static final Pattern SIGNAL_PATTERN = Pattern.compile("~onSignal:([a-zA-Z0-9_-]*)");
   private static final Pattern BOUNDS_TRIGGER_PATTERN = Pattern.compile(
      "~on(Enter_?Bounds|Exit_?Bounds|Leave_?Bounds|Bounds_?Enter|Bounds_?Exit|Player_?Enter(?:_?Bounds)?|Player_?(?:Exit|Leave)(?:_?Bounds)?)(?=\\{|\\s|$)", 2
   );
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final MobExecutor manager;
   @Getter
   private Pack pack;
   @Getter
   private File file;
   @Getter
   private MythicConfig config;
   @Getter
   private String internalName;
   @Getter
   private ItemStack cachedMenuItem;
   @Getter
   private boolean isHidden;
   protected String entityTypeString;
   @Getter
   protected MythicEntityType entityType;
   @Getter
   protected BukkitEntityType entityBaseSpawner;
   @Getter @Setter
   protected PlaceholderString displayName;
   @Getter @Setter
   protected String faction = null;
   private AbstractItemStack eggItem = null;
   @Getter
   private PlaceholderString eggDisplay = null;
   @Getter
   private PlaceholderString eggModel = null;
   @Getter
   private PlaceholderString eggMobName = null;
   @Getter
   private List<PlaceholderString> eggLore = null;
   @Getter
   private TotemConfig totemConfig = null;
   @Getter
   private MobModel model;
   @Getter
   private DropTable dropTable;
   @Getter
   private DropTable equipmentTable;
   @Getter @Setter
   protected double optionReviveHealth;
   protected double lvlModDamage;
   protected double lvlModHealth;
   protected double lvlModArmor;
   protected double lvlModKBR;
   protected double lvlModPower;
   protected double lvlModSpeed;
   protected double lvlModAttackSpeed;
   protected DespawnMode despawnMode = DespawnMode.NORMAL;
   protected boolean optionShowHealthInChat = false;
   protected boolean optionSilent = false;
   protected boolean optionNoAI = false;
   @Getter
   protected boolean optionAsyncPathfinding = true;
   protected boolean optionAware = true;
   protected boolean optionGlowing = false;
   protected boolean optionInvincible = false;
   protected boolean optionCollidable = true;
   protected boolean optionNoGravity = true;
   protected boolean optionInteractable = true;
   protected boolean optionHealOnReload = false;
   @Getter
   protected Boolean optionLockPitch = false;
   protected boolean useBossBar = false;
   protected int bossBarRange;
   protected int bossBarRangeSq;
   protected PlaceholderString bossBarTitle;
   protected AbstractBossBar.BarColor bossBarColor;
   protected AbstractBossBar.BarStyle bossBarStyle;
   protected boolean bossBarCreateFog;
   protected boolean bossBarDarkenSky;
   protected boolean bossBarPlayMusic;
   protected Optional<String> mount = Optional.empty();
   protected Optional<String> rider = Optional.empty();
   @Getter
   private Map<StatType, PlaceholderDouble> stats;
   @Getter
   private Map<StatType, Double> statLevelModifiers;
   @Getter
   private RandomInt defaultLevel = new RandomInt(1);
   @Getter
   private boolean levelConfigured = false;
   @Getter
   private VariableRegistry defaultVariables = null;
   @Getter
   private VariableRegistry defaultSkillVariables = null;
   @Getter
   private Map<String, Double> damageModifiers;
   @Getter
   private Map<String, Double> entityDamageModifiers;
   private List<String> aiGoalSelectors = Lists.newArrayList();
   private boolean aiGoalSelectorsClear = false;
   private List<String> aiTargetSelectors = Lists.newArrayList();
   private Map<String, Float> aiPathfindingMalus = Maps.newConcurrentMap();
   @Getter
   private String aiNavigator;
   private boolean hasCombatSkills = false;
   private Map<SkillTrigger, Queue<SkillMechanic>> skills = Maps.newConcurrentMap();
   private Queue<SkillMechanic> timerSkills = Queues.newConcurrentLinkedQueue();
   private Queue<MobType.BoundsTriggerSkill> boundsTriggerSkills = Queues.newConcurrentLinkedQueue();
   private Map<String, List<SkillMechanic>> signalSkills = Maps.newConcurrentMap();
   @Getter
   private boolean usingTimers = false;
   @Getter
   private SkillAudience executionAudience = null;
   @Getter
   private boolean executionAudienceIsEmpty = false;
   private boolean warnedUnresolvedAudience = false;
   @Getter
   private int noDamageTicks;
   @Getter
   private int maxAttackRange;
   @Getter
   private int maxAttackableRange;
   @Getter
   private int maxThreatDistance;
   private boolean alwaysShowName = true;
   private boolean showNameOnDamage = true;
   private boolean useThreatTable;
   private boolean useImmunityTable;
   private boolean useHearing;
   @Getter
   private boolean useCustomNameplate;
   private boolean optionTTFromDamage = true;
   private boolean optionTTDecayUnreachable = true;
   private int optionTTDropUnreachableTicks = 10;
   @Getter
   private Boolean repeatAllSkills = false;
   @Getter
   private Boolean preventVanillaDamage = false;
   @Getter
   private Boolean preventOtherDrops = false;
   @Getter
   private Boolean preventRandomEquipment = false;
   @Getter
   private Boolean preventLeashing = false;
   @Getter
   private Boolean preventKnockback = false;
   @Getter
   private Boolean preventRename = true;
   @Getter
   private Boolean preventEndermanTeleport = true;
   @Getter
   private Boolean preventItemPickup = true;
   @Getter
   private Boolean preventSilverfishInfection = true;
   @Getter
   private Boolean preventSunburn = false;
   @Getter
   private Boolean preventExploding = false;
   @Getter
   private Boolean preventMobKillDrops = false;
   @Getter
   private Boolean preventTransformation = true;
   @Getter
   private Boolean preventMounts = false;
   @Getter
   private Boolean passthroughDamage = false;
   @Getter
   private Boolean applyInvisibility = false;
   @Getter
   private Boolean visibleByDefault = false;
   @Getter
   private Boolean digOutOfGround = false;
   @Getter
   private Boolean randomizeProperties = true;
   @Getter
   private Boolean usesHealthBar = false;
   @Getter
   private float speechBubbleOffset = 0.4F;
   protected double spawnVelocityX = 0.0;
   @Getter
   protected double spawnVelocityXMax = 0.0;
   protected double spawnVelocityY = 0.0;
   @Getter
   protected double spawnVelocityYMax = 0.0;
   protected double spawnVelocityZ = 0.0;
   @Getter
   protected double spawnVelocityZMax = 0.0;
   @Getter
   protected Boolean spawnVelocityXRange = false;
   @Getter
   protected Boolean spawnVelocityYRange = false;
   @Getter
   protected Boolean spawnVelocityZRange = false;
   protected List<PlaceholderString> killMessages;
   @Getter
   private PlaceholderString disguise = null;
   @Getter
   private DropMethod dropMethod = DropMethod.VANILLA;
   @Getter
   private boolean dropsPerPlayer = false;
   @Getter
   private Boolean dropsDoLootsplosion = false;
   @Getter
   private Boolean dropsAreClientsideByDefault = false;
   @Getter
   private Boolean dropsShowNameByDefault = false;
   @Getter
   private Boolean dropsGlowByDefault = false;
   @Getter
   private Boolean dropsHaveBeamByDefault = false;
   @Getter
   private Boolean dropsHaveVFXByDefault = false;
   @Getter
   private Material dropsItemVFXMaterial;
   @Getter
   private int dropsItemVFXModel;
   @Getter
   private double dropsPerPlayerRequiredDamagePercent = 0.0;
   @Getter
   private Boolean damageLeaderboard = false;
   @Getter
   private Boolean showChatLeaderboardOnDeath = false;
   @Getter
   private Boolean showHologramLeaderboardOnDeath = false;
   @Getter
   private Integer hologramLeaderboardTimeout = 6000;
   @Getter
   private List<String> fancyDropHologramMessage = Lists.newArrayList();
   @Getter
   private List<String> fancyDropChatMessage = Lists.newArrayList();
   private final transient ReentrantLock skillLock = new ReentrantLock();
   private static final Queue<SkillMechanic> EMPTY_SKILLS = new LinkedList<>();

   public MobType(MobExecutor manager, Pack pack, File file, String internalName, MythicConfig mc) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.pack = pack;
      this.config = mc;
      this.file = file;
      this.internalName = internalName;
      ConfigExecutor configManager = this.plugin.getConfiguration();
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loading MythicMob type '{0}'...", this.internalName);
      this.entityTypeString = mc.getString(new String[]{"Type", "MobType", "Mobtype"}, this.entityTypeString);
      if (this.entityTypeString == null) {
         BukkitEntityType me = BukkitEntityType.getMythicEntity(internalName);
         if (me == null) {
            MythicLogger.errorMobConfig(this, mc, "Could not load MythicMob " + this.internalName + "! No Type specified.");
            this.entityTypeString = "NULL";
            this.entityType = MythicEntityType.COD;
            this.entityBaseSpawner = BukkitEntityType.getMythicEntity(MythicEntityType.COD);
            this.displayName = PlaceholderString.of("ERROR: MOB TYPE FOR '" + this.internalName + "' IS NOT OPTIONAL");
            return;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "+ EntityType is vanilla override for {0}", this.entityTypeString);
         this.entityType = MythicEntityType.get(internalName);
         this.entityBaseSpawner = me;
      } else {
         this.entityType = MythicEntityType.get(this.entityTypeString);
         this.entityBaseSpawner = BukkitEntityType.getMythicEntity(this.entityType);
      }

      this.isHidden = mc.getBoolean("Hidden", false);
      this.levelConfigured = mc.isSet("Level") || mc.isSet("MobLevel") || mc.isSet("Options.Level");

      try {
         this.defaultLevel = new RandomInt(mc.getString(new String[]{"Level", "MobLevel", "Options.Level"}, "1"));
      } catch (IllegalArgumentException e) {
         MythicLogger.errorMobConfig(this, mc, "Invalid Level value; must be a number or range (e.g. 5 or 3to7). Defaulting to 1.");
         this.defaultLevel = new RandomInt(1);
      }

      if (this.entityBaseSpawner != null) {
         this.entityBaseSpawner.instantiate(mc);
         String strDisplayName = mc.getString("Display", this.displayName == null ? null : this.displayName.toString());
         strDisplayName = mc.getString("DisplayName", strDisplayName);
         if (strDisplayName != null) {
            this.displayName = PlaceholderString.of(strDisplayName);
         }

         if (mc.isSet("Model")) {
            Schedulers.sync().runLater(() -> {
               if (mc.isSet("Model.Type")) {
                  String modelType = mc.getString("Model.Type").toUpperCase();
                  if (!modelType.startsWith("MPE") && !modelType.equalsIgnoreCase("MINIATUREPE")) {
                     if (manager.getPlugin().getCompatibility().getModelEngine().isPresent()) {
                        this.model = manager.getPlugin().getCompatibility().getModelEngine().get().createMobModel(this, mc);
                        this.applyInvisibility = true;
                     }
                  } else if (manager.getPlugin().getCompatibility().getMiniaturePets().isPresent()) {
                     this.model = new MPetsModel(this, mc);
                  }
               } else if (manager.getPlugin().getCompatibility().getModelEngine().isPresent()) {
                  this.model = manager.getPlugin().getCompatibility().getModelEngine().get().createMobModel(this, mc);
                  this.applyInvisibility = true;
               }
            }, 1L);
         } else {
            this.model = null;
         }

         List<String> lstStats = mc.getStringList("Stats");
         this.stats = Maps.newConcurrentMap();
         if (!lstStats.isEmpty()) {
            for (String stat : lstStats) {
               try {
                  String[] split = stat.split(" ");
                  String typeName = split[0];
                  PlaceholderDouble value = PlaceholderDouble.of(split[1]);
                  StatType statType = manager.getPlugin().getStatManager().getStatOrRegisterStatic(typeName);
                  this.stats.put(statType, value);
               } catch (Exception ex) {
                  MythicLogger.errorMobConfig(this, mc, "Invalid syntax for Stat line: " + stat);
               }
            }
         }

         PlaceholderDouble attrHealth = mc.getPlaceholderDouble("Health", null);
         if (attrHealth != null) {
            this.stats.put(Stats.HEALTH, attrHealth);
         }

         PlaceholderDouble attrDamage = mc.getPlaceholderDouble("Damage", null);
         if (attrDamage != null) {
            this.stats.put(Stats.ATTACK_DAMAGE, attrDamage);
         }

         PlaceholderDouble attrArmor = mc.getPlaceholderDouble(new String[]{"Armor", "Armour"}, null);
         if (attrArmor != null) {
            this.stats.put(Stats.ARMOR, attrArmor);
         }

         PlaceholderDouble attrAttackSpeed = mc.getPlaceholderDouble("Options.AttackSpeed", null);
         if (attrAttackSpeed != null) {
            this.stats.put(Stats.ATTACK_SPEED, attrAttackSpeed);
         }

         PlaceholderDouble attrMovementSpeed = mc.getPlaceholderDouble("Options.MovementSpeed", null);
         if (attrMovementSpeed != null) {
            this.stats.put(Stats.MOVEMENT_SPEED, attrMovementSpeed);
         }

         PlaceholderDouble attrFlyingSpeed = mc.getPlaceholderDouble("Options.FlyingSpeed", null);
         if (attrFlyingSpeed != null) {
            this.stats.put(Stats.FLYING_SPEED, attrFlyingSpeed);
         }

         PlaceholderDouble attrKnockbackResist = mc.getPlaceholderDouble("Options.KnockbackResistance", null);
         if (attrKnockbackResist != null) {
            this.stats.put(Stats.KNOCKBACK_RESISTANCE, attrKnockbackResist);
         }

         PlaceholderDouble attrFollowRange = mc.getPlaceholderDouble("Options.FollowRange", null);
         if (attrFollowRange != null) {
            this.stats.put(Stats.FOLLOW_RANGE, attrFollowRange);
         }

         PlaceholderDouble attrStepHeight = mc.getPlaceholderDouble("StepHeight", null);
         if (attrStepHeight != null) {
            this.stats.put(Stats.STEP_HEIGHT, attrStepHeight);
         }

         PlaceholderDouble attrScale = mc.getPlaceholderDouble("Options.Scale", null);
         if (attrScale != null) {
            this.stats.put(Stats.SCALE, attrScale);
         }

         if (mc.isSet("Variables") && mc.isConfigurationSection("Variables")) {
            this.setupDefaultVariableValues();
         }

         if (mc.isSet("SkillVariables") && mc.isConfigurationSection("SkillVariables")) {
            this.setupDefaultSkillVariableValues();
         }

         AbstractItemStack eggItem = configManager.getDefaultMobEggItem();
         this.eggDisplay = mc.getPlaceholderString("Egg.Display", null);
         this.eggModel = mc.getPlaceholderString("Egg.Model", null);
         String eggMobNameAaaa = mc.getString("MythicEggDisplay", null);
         this.eggMobName = mc.getPlaceholderString("Egg.MobName", eggMobNameAaaa);
         if (mc.isSet("Egg.Lore")) {
            this.eggLore = mc.getPlaceholderStringListAlt("Egg.Lore", configManager.getDefaultMobEggLore());
         }

         if (mc.isSet("Totem")) {
            this.totemConfig = new TotemConfig(this, mc.getNestedConfig("Totem"));
            this.getPlugin().getMobManager().getTotemManager().registerTotem(this);
         }

         this.optionReviveHealth = mc.getDouble("Options.ReviveHealth", 10.0);
         this.optionInvincible = mc.getBoolean("Options.Invincible", false);
         this.optionInvincible = mc.getBoolean("Options.Invulnerable", this.optionInvincible);
         this.faction = mc.getString("Faction", null);
         String mount = mc.getString("Mount", null);
         mount = mc.getString("Riding", mount);
         this.mount = Optional.ofNullable(mount);
         String rider = mc.getString("Rider", null);
         rider = mc.getString("Passenger", rider);
         this.rider = Optional.ofNullable(rider);
         boolean despawnByDefault = this.getPlugin().getConfiguration().getMobsDespawnByDefault();
         String shouldDespawn = mc.getString("Despawn", despawnByDefault ? "TRUE" : "FALSE");
         shouldDespawn = mc.getString("Options.Despawn", shouldDespawn);
         boolean isPersistent = mc.getBoolean("Persistent", false);
         isPersistent = mc.getBoolean("Options.Persistent", isPersistent);
         if (isPersistent) {
            shouldDespawn = DespawnMode.PERSISTENT.toString();
         }

         this.despawnMode = DespawnMode.get(shouldDespawn);
         this.optionGlowing = mc.getBoolean("Options.Glowing", false);
         this.optionCollidable = mc.getBoolean("Options.Collidable", true);
         this.optionNoGravity = mc.getBoolean("Options.NoGravity", false);
         this.optionInteractable = mc.getBoolean("Options.Interactable", this.optionInteractable);
         this.optionSilent = mc.getBoolean("Options.Silent", this.optionSilent);
         this.optionNoAI = mc.getBoolean("Options.NoAI", this.optionNoAI);
         this.optionAsyncPathfinding = mc.getBoolean("Options.AsyncPathfinding", this.getPlugin().getConfiguration().isAsyncPathfindingEnabled());
         this.optionAware = mc.getBoolean("Options.Aware", this.optionAware);
         this.optionHealOnReload = mc.getBoolean("Options.HealOnReload", this.optionHealOnReload);
         this.noDamageTicks = mc.getInteger("Options.NoDamageTicks", this.getPlugin().getConfiguration().getNoDamageTicksByDefault()) * 2;
         this.optionLockPitch = mc.getBoolean("Options.LockPitch", this.optionLockPitch);
         this.useBossBar = mc.getBoolean("BossBar.Enabled", false);
         this.bossBarTitle = mc.getPlaceholderString("BossBar.Title", this.displayName == null ? "" : this.displayName.toString());
         this.bossBarRange = mc.getInteger("BossBar.Range", 64);
         this.bossBarRangeSq = (int)Math.pow(this.bossBarRange, 2.0);
         String bossBarColor = mc.getString("BossBar.Color", "WHITE");
         String bossBarStyle = mc.getString("BossBar.Style", "SOLID");

         try {
            this.bossBarColor = AbstractBossBar.BarColor.valueOf(bossBarColor);
         } catch (Exception ex2) {
            this.bossBarColor = AbstractBossBar.BarColor.WHITE;
         }

         try {
            this.bossBarStyle = AbstractBossBar.BarStyle.valueOf(bossBarStyle);
         } catch (Exception ex3) {
            this.bossBarStyle = AbstractBossBar.BarStyle.SOLID;
         }

         this.bossBarCreateFog = mc.getBoolean("BossBar.CreateFog", false);
         this.bossBarDarkenSky = mc.getBoolean("BossBar.DarkenSky", false);
         this.bossBarPlayMusic = mc.getBoolean("BossBar.PlayMusic", false);
         this.usesHealthBar = mc.getBoolean("HealthBar.Enabled", false);
         this.speechBubbleOffset = mc.getFloat("Speech.Offset", 0.4F);
         String executionAudienceStr = mc.getString(new String[]{"ExecutionAudience", "Options.ExecutionAudience"}, null);
         if (executionAudienceStr != null) {
            if (!executionAudienceStr.equalsIgnoreCase("NONE") && !executionAudienceStr.equalsIgnoreCase("EMPTY")) {
               this.executionAudience = this.getPlugin().getSkillManager().getAudience(executionAudienceStr);
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Mob {0} has ExecutionAudience: {1}", this.internalName, executionAudienceStr);
            } else {
               this.executionAudienceIsEmpty = true;
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Mob {0} has ExecutionAudience set to NONE/EMPTY", this.internalName);
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Loading mob skills... ");
         List<String> nSkills = mc.getStringList("Skills");
         int interval = 1;

         for (String s : nSkills) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Loading mechanic line: {0}", s);
            s = MythicLineConfigImpl.unparseBlock(s);
            if (s.startsWith("selfskill:")) {
               s = s.replace("selfskill:", "skill:mob:" + internalName + ":");
            } else if (s.contains("self:")) {
               s = s.replace("self:", "skill:mob:" + internalName + ":");
            }

            SkillMechanic ms = this.getPlugin().getSkillManager().getMechanic(pack, mc.getFile(), this, s);
            if (ms == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "-! Mechanic was not found. Skipping.");
            } else {
               ms.setParent(this);
               if (s.contains("~onTimer")) {
                  Matcher Rmatcher = TIMER_PATTERN.matcher(s);
                  Rmatcher.find();

                  try {
                     interval = Integer.parseInt(Rmatcher.group(1));
                  } catch (Exception ex4) {
                     MythicLogger.errorMobConfig(this, mc, "Error parsing Timer skill, invalid interval specified (must be an integer). AbstractSkill=" + s);
                     continue;
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Mechanic set on timer with interval {0}", interval);
                  ms.setTimerInterval(interval);
                  this.timerSkills.add(ms);
               } else if (this.isBoundsTriggerSkill(ms)) {
                  Matcher Rmatcher = BOUNDS_TRIGGER_PATTERN.matcher(s);
                  if (!Rmatcher.find()) {
                     MythicLogger.errorMobConfig(this, mc, "Error parsing Bounds trigger, invalid trigger syntax. AbstractSkill=" + s);
                  } else {
                     try {
                        String boundsArgs = extractBoundsArguments(s, Rmatcher.end());
                        MythicLineConfigImpl triggerConfig = new MythicLineConfigImpl("bounds" + boundsArgs);
                        BoundsSpec bounds = new BoundsSpec(triggerConfig);
                        String stateKey = "bounds:" + this.boundsTriggerSkills.size();
                        this.boundsTriggerSkills.add(new MobType.BoundsTriggerSkill(ms, bounds, ms.getTrigger(), stateKey));
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Mechanic set on Bounds trigger {0}", ms.getTrigger());
                     } catch (Exception ex5) {
                        MythicLogger.errorMobConfig(this, mc, "Error parsing Bounds trigger bounds: " + ex5.getMessage() + ". AbstractSkill=" + s);
                        MythicLogger.handleMinorError(ex5);
                     }
                  }
               } else if (s.contains("~onSignal:")) {
                  Matcher Rmatcher = SIGNAL_PATTERN.matcher(s);
                  Rmatcher.find();

                  String signal;
                  try {
                     signal = Rmatcher.group(1);
                  } catch (Exception ex6) {
                     MythicLogger.errorMobConfig(
                        this, mc, "Error parsing Signal skill, invalid signal specified (contains invalid characters). AbstractSkill=" + s
                     );
                     continue;
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Mechanic set on Signal with key '{0}'", signal);
                  this.signalSkills.computeIfAbsent(signal, k -> new ArrayList<>()).add(ms);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Loading mechanic to base skill tree...");
                  SkillTrigger trigger = ms.getTrigger();
                  if (trigger.equals(SkillTriggers.SPAWN_OR_LOAD)) {
                     this.skills.computeIfAbsent(SkillTriggers.SPAWN, k -> Queues.newConcurrentLinkedQueue()).add(ms);
                     this.skills.computeIfAbsent(SkillTriggers.LOAD, k -> Queues.newConcurrentLinkedQueue()).add(ms);
                  } else {
                     this.skills.computeIfAbsent(trigger, k -> Queues.newConcurrentLinkedQueue()).add(ms);
                  }
               }
            }
         }

         if (!this.timerSkills.isEmpty() || !this.boundsTriggerSkills.isEmpty()) {
            this.usingTimers = true;
         }

         if (this.hasSkills(SkillTriggers.COMBAT)) {
            this.hasCombatSkills = true;
         }

         this.optionShowHealthInChat = mc.getBoolean("ShowHealth", false);
         this.useThreatTable = mc.getBoolean("Modules.ThreatTable", false);
         this.useImmunityTable = mc.getBoolean("Modules.ImmunityTable", false);
         this.useCustomNameplate = mc.getBoolean("Nameplate.Enabled", strDisplayName != null && strDisplayName.contains("\\n"));
         this.useThreatTable = mc.getBoolean("Options.UseThreatTable", this.useThreatTable);
         this.useImmunityTable = mc.getBoolean("Options.UseImmunityTable", this.useImmunityTable);
         this.useThreatTable = mc.getBoolean("ThreatTable.Enabled", this.useThreatTable);
         this.optionTTFromDamage = mc.getBoolean("ThreatTable.UseDamageTaken", true);
         this.optionTTDecayUnreachable = mc.getBoolean("ThreatTable.DecayUnreachable", true);
         this.optionTTDropUnreachableTicks = mc.getInteger("ThreatTable.DropUnreachableSeconds", this.optionTTDropUnreachableTicks);
         this.useThreatTable = mc.getBoolean("ThreatTables.Enabled", this.useThreatTable);
         this.optionTTFromDamage = mc.getBoolean("ThreatTables.UseDamageTaken", this.optionTTFromDamage);
         this.optionTTDecayUnreachable = mc.getBoolean("ThreatTables.DecayUnreachable", this.optionTTDecayUnreachable);
         this.optionTTDropUnreachableTicks = mc.getInteger("ThreatTables.DropUnreachableSeconds", this.optionTTDropUnreachableTicks);
         this.useHearing = mc.getBoolean("Hearing.Enabled", false);
         this.maxAttackRange = mc.getInteger("Options.MaxAttackRange", 64);
         this.maxAttackableRange = mc.getInteger("Options.MaxCombatDistance", 256);
         this.maxAttackableRange = mc.getInteger("Options.MaxAttackableRange", this.maxAttackableRange);
         this.maxThreatDistance = mc.getInteger("Options.MaxThreatDistance", 40);
         this.alwaysShowName = mc.getBoolean("Options.AlwaysShowName", false);
         this.showNameOnDamage = mc.getBoolean("Options.ShowNameOnDamaged", false);
         this.repeatAllSkills = mc.getBoolean("Options.RepeatAllSkills", false);
         this.preventVanillaDamage = mc.getBoolean("Options.PreventVanillaDamage", this.getPlugin().getConfiguration().getPreventVanillaDamageByDefault());
         this.preventOtherDrops = mc.getBoolean("Options.PreventOtherDrops", this.getPlugin().getConfiguration().getPreventOtherDropsByDefault());
         this.preventRandomEquipment = mc.getBoolean("Options.PreventRandomEquipment", false);
         this.preventLeashing = mc.getBoolean("Options.PreventLeashing", this.getPlugin().getConfiguration().getPreventLeashingByDefault());
         this.preventKnockback = mc.getBoolean("Options.PreventKnockback", false);
         this.preventRename = mc.getBoolean("Options.PreventRenaming", true);
         this.preventSunburn = mc.getBoolean("Options.PreventSunburn", false);
         this.preventEndermanTeleport = mc.getBoolean("Options.PreventTeleport", false);
         this.preventEndermanTeleport = mc.getBoolean("Options.PreventTeleporting", this.preventEndermanTeleport);
         this.preventTransformation = mc.getBoolean("Options.PreventTransformation", true);
         this.preventTransformation = mc.getBoolean("Options.PreventTransforming", this.preventTransformation);
         this.preventTransformation = mc.getBoolean("Options.PreventTransform", this.preventTransformation);
         this.preventMounts = mc.getBoolean("Options.PreventMounts", this.getPlugin().getConfiguration().getPreventJockeyMountsByDefault());
         this.preventMounts = mc.getBoolean("Options.PreventJockey", this.preventMounts);
         this.preventMounts = mc.getBoolean("Options.PreventJockeyMounts", this.preventMounts);
         this.preventItemPickup = mc.getBoolean("Options.PreventItemPickup", this.getPlugin().getConfiguration().getPreventItemPickupByDefault());
         this.preventMobKillDrops = mc.getBoolean("Options.PreventMobKillDrops", false);
         this.passthroughDamage = mc.getBoolean("Options.PassthroughDamage", false);
         this.applyInvisibility = mc.getBoolean("Options.ApplyInvisibility", false);
         this.applyInvisibility = mc.getBoolean("Options.Invisibility", this.applyInvisibility);
         this.applyInvisibility = mc.getBoolean("Options.Invisible", this.applyInvisibility);
         this.randomizeProperties = mc.getBoolean("Options.RandomizeProperties", true);
         this.aiNavigator = mc.getString("AINavigator", null);
         if (this.aiNavigator != null) {
            this.aiNavigator = this.aiNavigator.trim();
            if (this.aiNavigator.isEmpty()
               || this.aiNavigator.equalsIgnoreCase("default")
               || this.aiNavigator.equalsIgnoreCase("vanilla")
               || this.aiNavigator.equalsIgnoreCase("none")) {
               this.aiNavigator = null;
            }
         }

         List<String> lstAIGoalSelectors = mc.getStringList("AIGoalSelectors", mc.getStringList("AIGoals"));
         if (lstAIGoalSelectors != null) {
            for (String s : lstAIGoalSelectors) {
               s = MythicLineConfigImpl.unparseBlock(s);
               this.aiGoalSelectors.add(s);
               if (isClearGoalSelector(s)) {
                  this.aiGoalSelectorsClear = true;
               }
            }
         }

         List<String> lstAITargetSelectors = mc.getStringList("AITargetSelectors", mc.getStringList("AITargets"));
         if (lstAITargetSelectors != null) {
            for (String sx : lstAITargetSelectors) {
               sx = MythicLineConfigImpl.unparseBlock(sx);
               this.aiTargetSelectors.add(sx);
            }
         }

         List<String> lstAIPathfindingMalus = mc.getStringList("AIPathfindingMalus");
         if (lstAIPathfindingMalus != null) {
            for (String sx : lstAIPathfindingMalus) {
               try {
                  String[] split = sx.split(" ");
                  this.aiPathfindingMalus.put(split[0], Float.parseFloat(split[1]));
               } catch (Error | Exception ex7) {
                  MythicLogger.errorMobConfig(this, mc, "Invalid format for PathfindingMalus line " + sx);
                  ex7.printStackTrace();
               }
            }
         }

         this.visibleByDefault = mc.getBoolean("Options.VisibleByDefault", true);
         List<String> drops = mc.getStringList("Drops");
         this.dropTable = new DropTable(this.file.getAbsolutePath(), "Mob:" + this.internalName, drops, true);
         this.dropMethod = DropMethod.valueOf(mc.getString("DropOptions.DropMethod", "VANILLA").toUpperCase());
         this.showChatLeaderboardOnDeath = mc.getBoolean("DropOptions.ShowDeathChatMessage", false);
         this.showHologramLeaderboardOnDeath = mc.getBoolean("DropOptions.ShowDeathHologram", false);
         this.fancyDropHologramMessage = mc.getStringList("DropOptions.HologramMessage", configManager.getMobDrosDefaultDeathHologram());
         this.fancyDropChatMessage = mc.getStringList("DropOptions.ChatMessage", configManager.getMobDropsDefaultChatMessage());
         if (!this.fancyDropChatMessage.isEmpty()) {
            this.fancyDropChatMessage.replaceAll(line -> line != null && !line.isEmpty() && !line.equals(" ") ? line : "<red> ");
         }

         this.dropsPerPlayer = mc.getBoolean("DropOptions.PerPlayerDrops", configManager.isMobDropsPerPlayer());
         this.dropsDoLootsplosion = mc.getBoolean("DropOptions.Lootsplosion", configManager.isMobDropsDoLoopslosion());
         this.dropsAreClientsideByDefault = mc.getBoolean("DropOptions.ClientSideDrops", configManager.isMobDropsAreClientside());
         this.dropsShowNameByDefault = mc.getBoolean("DropOptions.HologramItemNames", configManager.isMobDropsDoNameHologram());
         this.dropsGlowByDefault = mc.getBoolean("DropOptions.ItemGlowByDefault", configManager.isMobDropsDoItemGlow());
         this.dropsHaveBeamByDefault = mc.getBoolean("DropOptions.ItemBeamByDefault", configManager.isMobDropsDoItemBeam());
         this.dropsHaveVFXByDefault = mc.getBoolean("DropOptions.ItemVFXByDefault", configManager.isMobDropsDoItemVFX());
         this.hologramLeaderboardTimeout = mc.getInt("DropOptions.HologramTimeout", configManager.getHologramLeaderboardTimeout());

         try {
            this.dropsItemVFXMaterial = Material.valueOf(mc.getString("DropOptions.ItemVFX.Material", configManager.getMobDropsItemVFXMaterial().toString()));
            this.dropsItemVFXModel = mc.getInt("DropOptions.ItemVFX.Model", configManager.getMobDropsItemVFXModel());
         } catch (Throwable ex8) {
            MythicLogger.errorMobConfig(this, mc, "Error in DropOptions.ItemVFX configuration");
         }

         double requiredDamagePercent = configManager.getMobDropsPerPlayerRequiredDamagePercent();
         String requiredDamageString = mc.getString("DropOptions.RequiredDamagePercent", null);
         if (requiredDamageString != null) {
            String trimmed = requiredDamageString.trim();

            try {
               if (trimmed.endsWith("%")) {
                  String number = trimmed.substring(0, trimmed.length() - 1).trim();
                  requiredDamagePercent = Double.parseDouble(number) / 100.0;
               } else {
                  requiredDamagePercent = Double.parseDouble(trimmed);
                  if (requiredDamagePercent > 1.0) {
                     requiredDamagePercent /= 100.0;
                  }
               }
            } catch (Exception ex9) {
            }
         }

         this.dropsPerPlayerRequiredDamagePercent = requiredDamagePercent;
         if (this.dropMethod == DropMethod.VANILLA
            && (
               this.dropsDoLootsplosion
                  || this.dropsAreClientsideByDefault
                  || this.dropsShowNameByDefault
                  || this.dropsGlowByDefault
                  || this.dropsHaveBeamByDefault
            )) {
            this.dropMethod = DropMethod.FANCY;
         }

         if (this.dropMethod == DropMethod.FANCY || this.dropsPerPlayer || this.showChatLeaderboardOnDeath || this.showHologramLeaderboardOnDeath) {
            this.damageLeaderboard = true;
         }

         List<String> equipment = mc.getStringList("Equipment");
         this.equipmentTable = new DropTable(this.file.getAbsolutePath(), "MobEquipment:" + this.internalName, equipment, true);
         List<String> lstDamageMod = mc.getStringList("DamageModifiers");
         this.damageModifiers = new HashMap<>();
         if (lstDamageMod != null && !lstDamageMod.isEmpty()) {
            for (String dm : lstDamageMod) {
               try {
                  String[] split = dm.split(" ");
                  String type = split[0];
                  Double mod = Double.valueOf(split[1]);
                  this.damageModifiers.put(type, mod);
               } catch (Exception ex10) {
                  MythicLogger.errorMobConfig(this, mc, "Invalid syntax for DamageModifier line: " + dm);
               }
            }
         }

         List<String> lstEntDamageMod = mc.getStringList("EntityDamageModifiers");
         this.entityDamageModifiers = new HashMap<>();
         if (lstEntDamageMod != null && lstEntDamageMod.size() > 0) {
            for (String dm : lstEntDamageMod) {
               try {
                  String[] split = dm.split(" ");
                  String type = split[0];
                  double mod = Double.valueOf(split[1]);
                  this.entityDamageModifiers.put(type, mod);
               } catch (Exception ex11) {
                  MythicLogger.errorMobConfig(this, mc, "Invalid syntax for DamageModifier");
               }
            }
         }

         List<String> killMessages = mc.getStringList("KillMessages");
         if (killMessages != null && !killMessages.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Loading mob kill messages...");
            if (this.killMessages == null) {
               this.killMessages = new ArrayList<>();
            }

            killMessages.forEach(message -> this.killMessages.add(PlaceholderString.of(message)));
         }

         if (mc.isSet("LevelModifiers")) {
            this.statLevelModifiers = new ConcurrentHashMap<>();
            if (mc.isConfigurationSection("LevelModifiers")) {
               for (String node : mc.getKeys("LevelModifiers")) {
                  String statName = StatManager.translateLegacyStatAliases(node);
                  Optional<StatType> maybeStat = this.getPlugin().getStatManager().getStat(statName);
                  if (maybeStat.isPresent()) {
                     StatType statType = maybeStat.get();
                     double value = mc.getDouble("LevelModifiers." + node, 0.0);
                     this.statLevelModifiers.put(statType, value);
                  }
               }
            } else if (mc.isList("LevelModifiers")) {
               for (String line : mc.getStringList("LevelModifiers")) {
                  try {
                     String[] split = line.split(" ");
                     String statName = StatManager.translateLegacyStatAliases(split[0]);
                     Optional<StatType> maybeStat = this.getPlugin().getStatManager().getStat(statName);
                     if (maybeStat.isPresent()) {
                        StatType statType = maybeStat.get();
                        Double value = Double.valueOf(split[1]);
                        this.statLevelModifiers.put(statType, value);
                     }
                  } catch (Exception ex12) {
                  }
               }
            }
         }

         for (Entry<String, String> entry : configManager.getStatDefaultLevelModifiers().entrySet()) {
            if (this.statLevelModifiers == null) {
               this.statLevelModifiers = new ConcurrentHashMap<>();
            }

            String statName = StatManager.translateLegacyStatAliases(entry.getKey());
            Optional<StatType> maybeStat = this.getPlugin().getStatManager().getStat(statName);
            if (!maybeStat.isEmpty()) {
               StatType statType = maybeStat.get();
               if (!this.statLevelModifiers.containsKey(statType)) {
                  PlaceholderDouble baseValue = this.stats.get(statType);
                  if (baseValue != null && baseValue.isStatic()) {
                     String strDefaultModifier = entry.getValue();

                     try {
                        double modifier;
                        if (strDefaultModifier.startsWith("+")) {
                           modifier = Double.valueOf(strDefaultModifier.substring(1));
                        } else if (strDefaultModifier.startsWith("*")) {
                           modifier = baseValue.get() * Double.valueOf(strDefaultModifier.substring(1));
                        } else {
                           modifier = baseValue.get() * Double.valueOf(strDefaultModifier);
                        }

                        this.statLevelModifiers.put(statType, modifier);
                     } catch (Exception ex13) {
                        MythicLogger.error("Error calculating Damage Level Modifier: Default configuration is bad.", ex13);
                     }
                  }
               }
            }
         }

         this.digOutOfGround = mc.getBoolean("Options.DigOutOfGround", false);
         String strSpawnVelocityX = mc.getString("SpawnModifiers.VelocityX", "0");
         String strSpawnVelocityY = mc.getString("SpawnModifiers.VelocityY", "0");
         String strSpawnVelocityZ = mc.getString("SpawnModifiers.VelocityZ", "0");
         if (strSpawnVelocityX.contains("to")) {
            String[] split = strSpawnVelocityX.split("to");

            try {
               this.spawnVelocityX = Double.valueOf(split[0]);
               this.spawnVelocityXMax = Double.valueOf(split[1]);
               this.spawnVelocityXRange = true;
            } catch (Exception ex14) {
               this.spawnVelocityX = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityX.", this.internalName);
            }
         } else {
            try {
               this.spawnVelocityX = Double.valueOf(strSpawnVelocityX);
            } catch (Exception ex15) {
               this.spawnVelocityX = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityX.", this.internalName);
            }
         }

         if (strSpawnVelocityY.contains("to")) {
            String[] split = strSpawnVelocityY.split("to");

            try {
               this.spawnVelocityY = Double.valueOf(split[0]);
               this.spawnVelocityYMax = Double.valueOf(split[1]);
               this.spawnVelocityYRange = true;
            } catch (Exception ex16) {
               this.spawnVelocityY = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityY.", this.internalName);
            }
         } else {
            try {
               this.spawnVelocityY = Double.valueOf(strSpawnVelocityY);
            } catch (Exception ex17) {
               this.spawnVelocityY = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityY.", this.internalName);
            }
         }

         if (strSpawnVelocityZ.contains("to")) {
            String[] split = strSpawnVelocityZ.split("to");

            try {
               this.spawnVelocityZ = Double.valueOf(split[0]);
               this.spawnVelocityZMax = Double.valueOf(split[1]);
               this.spawnVelocityZRange = true;
            } catch (Exception ex18) {
               this.spawnVelocityZ = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityZ.", this.internalName);
            }
         } else {
            try {
               this.spawnVelocityZ = Double.valueOf(strSpawnVelocityZ);
            } catch (Exception ex19) {
               this.spawnVelocityZ = 0.0;
               MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityZ.", this.internalName);
            }
         }

         if (mc.isSet("Disguise")) {
            this.disguise = mc.getPlaceholderString("Disguise", null);
         }

         Map<String, MythicConfig> mobSpecificMetaSkills = mc.getNestedConfigs("MetaSkills");

         for (Entry<String, MythicConfig> entry : mobSpecificMetaSkills.entrySet()) {
            String key = entry.getKey();
            MythicConfig metaConfig = entry.getValue();
            String metaSkillName = "mob:" + this.getInternalName() + ":" + key;
            MetaSkill metaSkill = new MetaSkill(this.getPlugin().getSkillManager(), this, pack, file, metaSkillName, metaConfig);
            this.getPlugin().getSkillManager().registerSkill(metaSkillName, metaSkill);
         }

         this.buildCache(false);
      } else {
         MythicLogger.error("Could not load MythicMob {0}! Invalid type specified.", this.internalName);
         this.entityTypeString = "NULL";
         this.entityType = MythicEntityType.COD;
         this.entityBaseSpawner = BukkitEntityType.getMythicEntity(MythicEntityType.COD);
         this.displayName = PlaceholderString.of("ERROR: MOB TYPE FOR '" + this.internalName + "' IS INVALID");
      }
   }

   @Override
   public void buildCache() {
      this.buildCache(true);
   }

   public void buildCache(boolean refreshMobs) {
      this.cachedMenuItem = this.entityBaseSpawner.getHead();
      this.cachedMenuItem = ItemFactory.of(this.cachedMenuItem)
         .hideAttributes()
         .lore("<red> ")
         .lore("<rainbow>\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583")
         .lore("<red> ")
         .lore("<gray>\u22b3 <white>Left-Click to get mob egg")
         .lore("<gray>\u22b3 <white>Right-Click to Edit <red>(ALPHA FEATURE)")
         .build();
      if (refreshMobs) {
         this.getManager().getActiveMobs(am -> am.getType().equals(this)).forEach(mob -> mob.remountType());
      }
   }

   @Override
   public ActiveMob spawn(AbstractLocation location, double level) {
      return this.spawn(location, level, SpawnReason.OTHER);
   }

   @Override
   public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason) {
      return this.spawn(location, level, reason, null);
   }

   @Override
   public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      return this.spawn(location, level, reason, prespawnFunc, null);
   }

   @Override
   public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc, MythicSpawner spawner) {
      return this.spawn(location, level, reason, prespawnFunc, spawner, null);
   }

   @Override
   public ActiveMob spawn(
      AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc, MythicSpawner spawner, Set<UUID> executionAudience
   ) {
      return this.spawn(location, level, reason, prespawnFunc, spawner, executionAudience, null);
   }

   @Override
   public ActiveMob spawn(
      AbstractLocation location,
      double level,
      SpawnReason reason,
      Consumer<Entity> prespawnFunc,
      MythicSpawner spawner,
      Set<UUID> executionAudience,
      SkillCaster parent
   ) {
      if (ServerVersion.isFolia()) {
         Location regionLoc = BukkitAdapter.adapt(location);
         if (!FoliaHelper.isOwnedByCurrentRegion(regionLoc)) {
            Schedulers.of(regionLoc).run(() -> this.spawn0(location, level, reason, prespawnFunc, spawner, executionAudience, parent));
            return null;
         }
      }

      return this.spawn0(location, level, reason, prespawnFunc, spawner, executionAudience, parent);
   }

   private ActiveMob spawn0(
      AbstractLocation location,
      double level,
      SpawnReason reason,
      Consumer<Entity> prespawnFunc,
      MythicSpawner spawner,
      Set<UUID> executionAudience,
      SkillCaster parent
   ) {
      MobExecutor.spawnflag = true;
      UUID[] spawningId = new UUID[1];

      try {
         if (Double.isNaN(level)) {
            level = this.defaultLevel.get();
         }

         MythicMobPreSpawnEvent preEvent = Events.callAndReturn(new MythicMobPreSpawnEvent(this, location, level, reason));
         if (preEvent.isCancelled()) {
            return null;
         } else {
            double spawnLevel = Math.max(1.0, preEvent.getMobLevel());
            if (this.getPlugin().getCompatibility().getWorldGuard().isPresent()) {
               WorldGuardSupport worldGuardSupport = this.getPlugin().getCompatibility().getWorldGuard().get();
               if (!worldGuardSupport.getLocationAllowsMobSpawning(BukkitAdapter.adapt(location))) {
                  return null;
               }
            }

            Entity e;
            try {
               if (this.entityBaseSpawner == null) {
                  return null;
               }

               e = BukkitAdapter.adapt(this.entityBaseSpawner.spawn(location, reason.withRandomizeSpawnProperties(this.randomizeProperties), entity -> {
                  entity.getPersistentDataContainer().set(MobKeys.TYPE, PersistentDataType.STRING, this.internalName);
                  entity.getPersistentDataContainer().set(MobKeys.LEVEL, PersistentDataType.DOUBLE, spawnLevel);
                  spawningId[0] = entity.getUniqueId();
                  this.getManager().markSpawning(spawningId[0]);
                  if (this.applyInvisibility && !ServerVersion.isFolia()) {
                     if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
                     }

                     entity.setInvisible(true);
                  }

                  if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20) && !ServerVersion.isFolia()) {
                     entity.setVisibleByDefault(this.hasExecutionAudienceRestriction(executionAudience) ? false : this.visibleByDefault);
                  }

                  if (this.disguise != null && CompatibilityManager.LibsDisguises != null) {
                     CompatibilityManager.LibsDisguises.preDisguise(entity, this.disguise.get());
                  }

                  if (prespawnFunc != null) {
                     prespawnFunc.accept(entity);
                  }

                  Events.call(new MythicMobPreSpawnConfigureEvent(this, BukkitAdapter.adapt(entity), spawnLevel, reason));
               }));
            } catch (Exception ex) {
               MythicLogger.errorMobConfig(
                  this, this.config, "Mob type may not be supported on this version of Minecraft. Enable debugging for more information."
               );
               MythicLogger.handleMinorError(ex);
               return null;
            } catch (Error ex2) {
               MythicLogger.errorMobConfig(
                  this, this.config, "Mob type may not be supported on this version of Minecraft. Enable debugging for more information."
               );
               MythicLogger.handleMinorError(ex2);
               return null;
            }

            if (ServerVersion.isFolia()) {
               Schedulers.of(e).run(() -> {
                  if (this.applyInvisibility && e instanceof LivingEntity livingEntity) {
                     livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
                     e.setInvisible(true);
                  }

                  if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20)) {
                     e.setVisibleByDefault(this.hasExecutionAudienceRestriction(executionAudience) ? false : this.visibleByDefault);
                  }
               });
            }

            ActiveMob am = new ActiveMob(BukkitAdapter.adapt(e), this, spawnLevel);
            if (spawner != null) {
               am.setSpawner(spawner);
            }

            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Calling MythicMobSpawnEvent for " + this.getInternalName() + " (level: " + spawnLevel + ")");
            MythicMobSpawnEvent event = new MythicMobSpawnEvent(am, spawnLevel, reason);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               this.getManager().registerActiveMob(am);
               this.applyMobOptions(am, spawnLevel, true);
               this.applyMobVolatileOptions(am);
               am = this.applySpawnModifiers(am);
               this.applyExecutionAudienceVisibility(am, executionAudience);
               if (parent != null) {
                  am.setParent(parent);
               }

               if (this.hasSkills(SkillTriggers.SPAWN)) {
                  SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.SPAWN, am, null, null, false);
                  this.getPlugin().getSkillManager().getEventBus().processTriggerMechanics(data);
               }

               return am;
            } else {
               e.remove();
               return null;
            }
         }
      } finally {
         if (spawningId[0] != null) {
            this.getManager().unmarkSpawning(spawningId[0]);
         }

         MobExecutor.spawnflag = false;
      }
   }

   @Override
   public ActiveMob applyMobOptions(ActiveMob am, double level) {
      return this.applyMobOptions(am, level, false);
   }

   public ActiveMob applyMobOptions(ActiveMob am, double level, boolean firstSpawn) {
      AbstractEntity aEntity = am.getEntity();
      if (aEntity.isLiving() && am.isDead()) {
         aEntity.remove();
         am.unregister(true);
         return am;
      } else {
         aEntity.setRemoveWhenFarAway(am.getDespawnMode().getDespawnWithoutNearbyPlayers());
         aEntity.setPersistent(am.getDespawnMode().getSavesToDisk());
         aEntity.setAlwaysShowName(this.alwaysShowName);
         Entity bukkitEntity = aEntity.getBukkitEntity();
         if (aEntity.isLiving()) {
            LivingEntity asLiving = (LivingEntity)bukkitEntity;
            if (this.preventItemPickup) {
               asLiving.setCanPickupItems(false);
            }

            if (this.applyInvisibility) {
               asLiving.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
            }

            double health = this.getHealth(am);
            if (health > 0.0) {
               if (this.optionHealOnReload) {
                  aEntity.setHealthAndMax(health);
               } else {
                  double hp = aEntity.getHealth();
                  if (hp < aEntity.getMaxHealth()) {
                     double ratio = hp / aEntity.getMaxHealth();
                     aEntity.setMaxHealth(health);
                     aEntity.setHealth(health * ratio);
                  } else {
                     aEntity.setHealthAndMax(health);
                  }
               }
            }

            if (!this.optionCollidable) {
               asLiving.setCollidable(false);
            }

            asLiving.setMaximumNoDamageTicks(this.noDamageTicks);
            Runnable equipPass = () -> {
               if (am.getEntity() == null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "MythicMob {0} failed to spawn - was despawned by other plugin", this.internalName);
               } else {
                  if (this.preventOtherDrops && asLiving instanceof Mob) {
                     EntityEquipment ee = asLiving.getEquipment();
                     ee.setHelmetDropChance(0.0F);
                     ee.setChestplateDropChance(0.0F);
                     ee.setLeggingsDropChance(0.0F);
                     ee.setBootsDropChance(0.0F);
                  }

                  if (this.preventRandomEquipment && !am.getEntity().getDataContainer().has(MobKeys.EQUIPPED, PersistentDataType.BYTE)) {
                     EntityEquipment ee = asLiving.getEquipment();

                     assert ee != null;

                     ee.clear();
                     ee.setArmorContents(
                        new ItemStack[]{
                           new ItemStack(Material.AIR),
                           new ItemStack(Material.AIR),
                           new ItemStack(Material.AIR),
                           new ItemStack(Material.AIR),
                           new ItemStack(Material.AIR),
                           new ItemStack(Material.AIR)
                        }
                     );
                  }

                  this.equipmentTable.generate(new DropMetadataImpl(am, am.getEntity())).equip(am.getEntity());
                  am.checkEquipment();
                  this.reapplyAIGoalSelectors(am);
                  am.getEntity().getDataContainer().set(MobKeys.EQUIPPED, PersistentDataType.BYTE, (byte)1);
                  EntityEquipment ee = asLiving.getEquipment();

                  assert ee != null;

                  if (this.preventOtherDrops && asLiving instanceof Creature creature) {
                     ee.setItemInMainHandDropChance(0.0F);
                     ee.setItemInOffHandDropChance(0.0F);
                     ee.setHelmetDropChance(0.0F);
                     ee.setChestplateDropChance(0.0F);
                     ee.setLeggingsDropChance(0.0F);
                     ee.setBootsDropChance(0.0F);
                  }
               }
            };
            if (ServerVersion.isFolia() ? !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity) : !Bukkit.isPrimaryThread()) {
               Schedulers.of(bukkitEntity).run(equipPass);
            } else {
               equipPass.run();
            }

            if (this.useCustomNameplate) {
               Schedulers.of(bukkitEntity).runLater(() -> {
                  if (am.getEntity() != null) {
                     am.setShowCustomNameplate(true);
                  }
               }, 5L);
            }

            if (this.getDisplayName() != null) {
               if (this.useCustomNameplate) {
                  aEntity.setCustomName("");
               } else {
                  aEntity.setCustomName(am.getDisplayName());
               }
            }
         }

         if (this.optionInvincible) {
            bukkitEntity.setInvulnerable(true);
         }

         if (this.optionGlowing) {
            bukkitEntity.setGlowing(true);
         }

         if (this.optionNoGravity) {
            bukkitEntity.setGravity(false);
         }

         if (this.optionSilent) {
            bukkitEntity.setSilent(true);
         }

         if (this.model != null) {
            this.model.apply(am.getEntity());
         }

         if (firstSpawn && this.mount.isPresent() && !MobExecutor.mountflag && this.getManager().getMythicMob(this.mount.get()) != null) {
            this.getManager();
            MobExecutor.mountflag = true;
            if (bukkitEntity.getVehicle() != null) {
               bukkitEntity.getVehicle().remove();
            }

            AbstractEntity mount = this.getManager().getMythicMob(this.mount.get()).get().spawn(am.getLocation(), level, SpawnReason.SUMMON).getEntity();
            this.getManager();
            MobExecutor.mountflag = false;
            mount.setPassenger(bukkitEntity);
            ActiveMob mountInstance = this.getManager().getMythicMobInstance(mount);
            am.setMount(mountInstance);
            mountInstance.setParent(am);
         }

         if (this.rider.isPresent()) {
            this.getManager();
            if (!MobExecutor.mountflag && this.getManager().getMythicMob(this.rider.get()) != null) {
               this.getManager();
               MobExecutor.mountflag = true;
               AbstractEntity rider = this.getManager().getMythicMob(this.rider.get()).get().spawn(am.getLocation(), level, SpawnReason.SUMMON).getEntity();
               this.getManager();
               MobExecutor.mountflag = false;
               rider.setPassenger(bukkitEntity);
               ActiveMob riderInstance = this.getManager().getMythicMobInstance(rider);
               riderInstance.setMount(am);
               riderInstance.setParent(am);
            }
         }

         return am;
      }
   }

   @Override
   public ActiveMob applyMobVolatileOptions(ActiveMob am) {
      Entity e = am.getEntity().getBukkitEntity();
      if (am.getEntity().isMob()) {
         if (this.optionNoAI) {
            am.getEntity().setAI(false);
         }

         if (!this.optionAware) {
            am.getEntity().setAware(false);
         }

         if (this.aiNavigator != null) {
            this.getPlugin().getVolatileCodeHandler().getAIHandler().setNavigationController(am.getEntity(), this.aiNavigator);
         }

         if (this.optionAsyncPathfinding) {
            this.getPlugin().getVolatileCodeHandler().getAIHandler().enableAsyncPathfinding(am.getEntity());
         }

         if (!this.aiGoalSelectors.isEmpty()) {
            this.getPlugin().getVolatileCodeHandler().getAIHandler().addPathfinderGoals((LivingEntity)e, this.getAIGoalSelectors());
         }

         if (!this.aiTargetSelectors.isEmpty()) {
            this.getPlugin().getVolatileCodeHandler().getAIHandler().addTargetGoals((LivingEntity)e, this.getAITargetSelectors());
         }

         if (!this.aiPathfindingMalus.isEmpty()) {
            for (Entry<String, Float> entry : this.aiPathfindingMalus.entrySet()) {
               this.getPlugin().getVolatileCodeHandler().getAIHandler().setPathfindingMalus(am.getEntity(), entry.getKey(), entry.getValue());
            }
         }
      }

      if (this.disguise != null && CompatibilityManager.LibsDisguises != null) {
         Schedulers.of(e).run(() -> CompatibilityManager.LibsDisguises.setDisguise(am, this.disguise.get(am)));
      }

      return am;
   }

   @Override
   public void reapplyAIGoalSelectors(ActiveMob am) {
      if (this.aiGoalSelectorsClear) {
         AbstractEntity entity = am.getEntity();
         if (entity != null && entity.isMob()) {
            this.getPlugin().getVolatileCodeHandler().getAIHandler().addPathfinderGoals((LivingEntity)entity.getBukkitEntity(), this.getAIGoalSelectors());
         }
      }
   }

   static boolean isClearGoalSelector(String line) {
      String[] split = line.split(" ");
      String goal = split.length > 1 && split[0].matches("[0-9]*") ? split[1] : split[0];
      return "clear".equalsIgnoreCase(LineConfig.getKey(goal));
   }

   @Override
   public ActiveMob applySpawnModifiers(ActiveMob am) {
      AbstractEntity e = am.getEntity();
      AbstractVector v = e.getVelocity();
      if (this.spawnVelocityXRange) {
         double vl = Numbers.randomDouble() * (this.spawnVelocityXMax - this.spawnVelocityX) + this.spawnVelocityX;
         v.setX(vl);
      } else {
         v.setX(this.spawnVelocityX);
      }

      if (this.spawnVelocityYRange) {
         double vl = Numbers.randomDouble() * (this.spawnVelocityYMax - this.spawnVelocityY) + this.spawnVelocityY;
         v.setY(vl);
      } else {
         v.setY(this.spawnVelocityY);
      }

      if (this.spawnVelocityZRange) {
         double vl = Numbers.randomDouble() * (this.spawnVelocityZMax - this.spawnVelocityZ) + this.spawnVelocityZ;
         v.setZ(vl);
      } else {
         v.setZ(this.spawnVelocityZ);
      }

      e.setVelocity(v);
      return am;
   }

   @Override
   public void executeSkills(SkillTrigger cause, SkillMetadata data) {
      try {
         MythicLogger.debug(
            MythicLogger.DebugLevel.SKILL, "Running Mechanics for ActiveMob {0} (uuid: {1})", this.internalName, data.getCaster().getEntity().getUniqueId()
         );
         if (data.getCaster().getEntity() == null || !data.getCaster().getEntity().getWorld().isLoaded()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "! Mob is not loaded. Ignoring skills.");
            return;
         }

         if (this.defaultSkillVariables != null) {
            data.setVariables(this.defaultSkillVariables.clone());
         }

         this.applyExecutionAudience(data);
         Optional<Set<UUID>> audience = data.getExecutionAudience();
         if (audience.isPresent()) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.SKILL,
               "+ Running Mechanics with audience '{0}' for ActiveMob '{1}' (uuid: {2})",
               audience.get(),
               this.internalName,
               data.getCaster().getEntity().getUniqueId()
            );
         }

         Queue<SkillMechanic> mechanicsForCause = this.getSkills(cause);
         if (data.getIsAsync()) {
            for (SkillMechanic ms : mechanicsForCause) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.SKILL,
                  "+ Running Mechanic '{0}' for ActiveMob '{1}' (uuid: {2})",
                  ms.getInternalName(),
                  this.internalName,
                  data.getCaster().getEntity().getUniqueId()
               );
               if (!ms.getRunAsync() && ms.isUsableFromCaster(data)) {
                  ms.execute(data.deepClone().setIsAsync(false));
               }
            }

            Schedulers.async().run(() -> {
               for (SkillMechanic msx : mechanicsForCause) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Evaluating SkillMechanic {0}", msx.getConfigLine());
                  if (msx.getRunAsync() && msx.isUsableFromCaster(data)) {
                     msx.execute(data);
                  }
               }
            });
         } else {
            for (SkillMechanic msx : mechanicsForCause) {
               if (msx.isUsableFromCaster(data)) {
                  msx.execute(data);
               }
            }
         }
      } catch (Error | Exception e) {
         MythicLogger.errorMobConfig(
            this,
            this.config,
            "Error processing skills for mob "
               + this.internalName
               + ". Mob type may no longer exist, or may have become corrupted by a server crash, and will be removed."
         );
         MythicLogger.handleMinorError(e);
         if (data.getCaster() != null && data.getCaster() instanceof ActiveMob) {
            this.getPlugin().getMobManager().unregisterActiveMob((ActiveMob)data.getCaster());
         }
      }
   }

   private void applyExecutionAudience(SkillMetadata data) {
      if (!data.hasExecutionAudience()) {
         if (data.getCaster() instanceof ActiveMob am) {
            if (am.getExecutionAudience() != null) {
               data.setExecutionAudience(am.getExecutionAudience());
               return;
            }

            if (am.isExecutionAudienceMutatedByMechanic()) {
               return;
            }
         }

         if (this.executionAudienceIsEmpty) {
            data.setExecutionAudience(Collections.emptySet());
         } else {
            if (this.executionAudience != null) {
               if (this.executionAudience instanceof CustomAudience ca && ca.getAudience().isEmpty()) {
                  this.warnUnresolvedAudience(ca);
                  return;
               }

               Collection<AbstractPlayer> resolved = this.executionAudience.get(data, data.getCaster().getEntity());
               HashSet<UUID> uuids = new HashSet<>();

               for (AbstractPlayer player : resolved) {
                  uuids.add(player.getUniqueId());
               }

               data.setExecutionAudience(uuids);
            }
         }
      }
   }

   private boolean hasExecutionAudienceRestriction(Set<UUID> passedAudience) {
      if (passedAudience != null) {
         return true;
      } else if (this.executionAudienceIsEmpty) {
         return true;
      } else {
         return this.executionAudience == null ? false : !(this.executionAudience instanceof CustomAudience ca && ca.getAudience().isEmpty());
      }
   }

   private void warnUnresolvedAudience(CustomAudience ca) {
      if (!this.warnedUnresolvedAudience) {
         this.warnedUnresolvedAudience = true;
         MythicLogger.error(
            "Mob {0}: ExecutionAudience {1} did not resolve to a known audience; treating as unrestricted", this.internalName, ca.getAudienceName()
         );
      }
   }

   void applyExecutionAudienceVisibility(ActiveMob am, Set<UUID> passedAudience) {
      Set<UUID> audienceUuids;
      if (passedAudience != null) {
         audienceUuids = new HashSet<>(passedAudience);
      } else {
         if (this.executionAudience == null && !this.executionAudienceIsEmpty) {
            return;
         }

         AbstractEntity entity = am.getEntity();
         if (entity == null) {
            return;
         }

         if (this.executionAudienceIsEmpty) {
            audienceUuids = Collections.emptySet();
         } else {
            if (this.executionAudience instanceof CustomAudience ca && ca.getAudience().isEmpty()) {
               this.warnUnresolvedAudience(ca);
               return;
            }

            SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.SPAWN, am, null, null, false);
            Collection<AbstractPlayer> resolved = this.executionAudience.get(data, entity);
            audienceUuids = new HashSet<>();

            for (AbstractPlayer player : resolved) {
               audienceUuids.add(player.getUniqueId());
            }
         }
      }

      am.setExecutionAudience(audienceUuids);
      AbstractEntity entityx = am.getEntity();
      if (entityx != null) {
         entityx.getDataContainer().set(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY, audienceUuids.toArray(new UUID[0]));
         this.getPlugin().getMobManager().registerAudienceTrackedEntity(entityx.getUniqueId());
         if (passedAudience != null) {
            am.setExecutionAudienceMutatedByMechanic(true);
            entityx.getDataContainer().set(MobKeys.EXECUTION_AUDIENCE_MUTATED, PersistentDataType.BYTE, (byte)1);
         }

         this.getPlugin().getMobManager().applyAudienceVisibility(entityx, audienceUuids);
      }
   }

   public void refreshExecutionAudienceForJoin(ActiveMob am, UUID joiningPlayerUuid) {
      if (this.executionAudience != null && !this.executionAudienceIsEmpty) {
         if (!(this.executionAudience instanceof CustomAudience ca && ca.getAudience().isEmpty())) {
            if (!am.isExecutionAudienceMutatedByMechanic()) {
               AbstractEntity entity = am.getEntity();
               if (entity != null) {
                  Set<UUID> current = am.getExecutionAudience();
                  if (current == null || !current.contains(joiningPlayerUuid)) {
                     SkillMetadata data = this.getPlugin().getSkillManager().getEventBus().buildSkillMetadata(SkillTriggers.SPAWN, am, null, null, false);
                     Collection<AbstractPlayer> resolved = this.executionAudience.get(data, entity);
                     boolean stillQualifies = false;

                     for (AbstractPlayer player : resolved) {
                        if (player.getUniqueId().equals(joiningPlayerUuid)) {
                           stillQualifies = true;
                           break;
                        }
                     }

                     if (stillQualifies) {
                        HashSet<UUID> updated = current == null ? new HashSet<>() : new HashSet<>(current);
                        updated.add(joiningPlayerUuid);
                        Runnable apply = () -> {
                           am.setExecutionAudience(updated);
                           entity.getDataContainer().set(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY, updated.toArray(new UUID[0]));
                        };
                        if (ServerVersion.isFolia()) {
                           Schedulers.of(BukkitAdapter.adapt(entity)).run(apply);
                        } else {
                           apply.run();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void executeTimerSkills(SkillMetadata data, long timer) {
      if (this.defaultSkillVariables != null) {
         try {
            data.setVariables(this.defaultSkillVariables.clone());
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }

      this.applyExecutionAudience(data);
      String mobName = this.getInternalName();
      TimingsHandler timings = this.getPlugin().getTimingsHandler();
      double clockInterval = this.getPlugin().getConfiguration().getClockIntervalMain();

      for (SkillMechanic skill : this.getTimerSkills()) {
         String timingKey = mobName + ":" + skill.getConfigLine();
         timings.markSkillNew(timingKey);
         if (timer % (skill.getTimerInterval() / clockInterval) == 0.0 && skill.isUsableFromCaster(data)) {
            skill.execute(data);
         }

         timings.markSkillComplete(timingKey);
      }

      this.executeBoundsTriggerSkills(data);
   }

   private boolean isBoundsTriggerSkill(SkillMechanic skill) {
      return SkillTriggers.ENTER_BOUNDS.equals(skill.getTrigger())
         || SkillTriggers.EXIT_BOUNDS.equals(skill.getTrigger())
         || isPlayerBoundsTriggerSkill(skill.getTrigger());
   }

   private static boolean isPlayerBoundsTriggerSkill(SkillTrigger trigger) {
      return SkillTriggers.PLAYER_ENTER_BOUNDS.equals(trigger) || SkillTriggers.PLAYER_EXIT_BOUNDS.equals(trigger);
   }

   private static String extractBoundsArguments(String line, int triggerEnd) {
      int open = triggerEnd;

      while (open < line.length() && Character.isWhitespace(line.charAt(open))) {
         open++;
      }

      if (open < line.length() && line.charAt(open) == '{') {
         int depth = 0;
         char quote = 0;

         for (int i = open; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != 0) {
               if (c == quote && (i == 0 || line.charAt(i - 1) != '\\')) {
                  quote = 0;
               }
            } else if (c == '"' || c == '\'') {
               quote = c;
            } else if (c == '{') {
               depth++;
            } else if (c == '}') {
               if (--depth == 0) {
                  return line.substring(open, i + 1);
               }
            }
         }

         throw new IllegalArgumentException("Unbalanced braces in bounds trigger arguments");
      } else {
         return "{}";
      }
   }

   private void executeBoundsTriggerSkills(SkillMetadata baseData) {
      if (!this.boundsTriggerSkills.isEmpty() && baseData.getCaster() instanceof ActiveMob am) {
         if (am.getEntity() != null && am.getEntity().isValid() && !am.getEntity().isDead()) {
            AbstractEntity entity = am.getEntity();
            AbstractLocation location = entity.getLocation();
            PlaceholderContext context = null;
            List<AbstractPlayer> worldPlayers = null;
            Set<UUID> seenInWorld = null;
            Iterator iterator = this.boundsTriggerSkills.iterator();

            while (true) {
               MobType.BoundsTriggerSkill boundsSkill;
               whileLoop:
               while (true) {
                  if (!iterator.hasNext()) {
                     return;
                  }

                  boundsSkill = (MobType.BoundsTriggerSkill)iterator.next();
                  if (!boundsSkill.bounds.isStatic() && context == null) {
                     context = PlaceholderContext.builder().meta(baseData).entity(entity).build();
                  }

                  if (isPlayerBoundsTriggerSkill(boundsSkill.trigger)) {
                     if (worldPlayers != null) {
                        break;
                     }

                     AbstractWorld world = entity.getWorld();
                     if (world != null) {
                        worldPlayers = new ArrayList<>();
                        seenInWorld = new HashSet<>();
                        Iterator iterator2 = world.getPlayers().iterator();

                        while (true) {
                           if (!iterator2.hasNext()) {
                              break whileLoop;
                           }

                           AbstractPlayer player = (AbstractPlayer)iterator2.next();
                           if (player.isValid() && !player.isDead()) {
                              worldPlayers.add(player);
                              seenInWorld.add(player.getUniqueId());
                           }
                        }
                     }
                  } else {
                     boolean inBounds;
                     if (boundsSkill.bounds.isStatic()) {
                        inBounds = boundsSkill.bounds.contains(location);
                     } else {
                        if (context == null) {
                           context = PlaceholderContext.builder().meta(baseData).entity(entity).build();
                        }

                        inBounds = boundsSkill.bounds.contains(location, context);
                     }

                     Boolean wasInBounds = am.updateBoundsTriggerState(boundsSkill.stateKey, inBounds);
                     if ((wasInBounds != null && wasInBounds) != inBounds) {
                        SkillTrigger firedTrigger = inBounds ? SkillTriggers.ENTER_BOUNDS : SkillTriggers.EXIT_BOUNDS;
                        if (boundsSkill.trigger.equals(firedTrigger)) {
                           this.fireBoundsTriggerSkill(boundsSkill.skill, firedTrigger, am, entity, location);
                        }
                     }
                  }
               }

               this.executePlayerBoundsTriggerSkill(boundsSkill, am, worldPlayers, seenInWorld, context);
            }
         }
      }
   }

   private void executePlayerBoundsTriggerSkill(
      MobType.BoundsTriggerSkill boundsSkill, ActiveMob am, List<AbstractPlayer> players, Set<UUID> seenInWorld, PlaceholderContext context
   ) {
      Set<UUID> wasInside = am.getPlayerBoundsTriggerState(boundsSkill.stateKey);
      boolean fireOnEnter = SkillTriggers.PLAYER_ENTER_BOUNDS.equals(boundsSkill.trigger);

      for (AbstractPlayer player : players) {
         AbstractLocation playerLocation = player.getLocation();
         boolean inBounds = boundsSkill.bounds.isStatic() ? boundsSkill.bounds.contains(playerLocation) : boundsSkill.bounds.contains(playerLocation, context);
         boolean changed = inBounds ? wasInside.add(player.getUniqueId()) : wasInside.remove(player.getUniqueId());
         if (changed && inBounds == fireOnEnter) {
            this.fireBoundsTriggerSkill(boundsSkill.skill, boundsSkill.trigger, am, player, playerLocation);
         }
      }

      wasInside.retainAll(seenInWorld);
   }

   private void fireBoundsTriggerSkill(SkillMechanic skill, SkillTrigger trigger, ActiveMob am, AbstractEntity triggerEntity, AbstractLocation origin) {
      SkillMetadataImpl triggerData = new SkillMetadataImpl(trigger, am, triggerEntity, origin, null, null, am.getPower());
      if (this.defaultSkillVariables != null) {
         try {
            triggerData.setVariables(this.defaultSkillVariables.clone());
         } catch (Throwable e) {
            MythicLogger.handleMinorError(e);
         }
      }

      this.applyExecutionAudience(triggerData);
      if (skill.isUsableFromCaster(triggerData)) {
         skill.execute(triggerData);
      }
   }

   @Override
   public void executeSignalSkill(String signal, SkillMetadata data) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Executing signal skills...");
      this.applyExecutionAudience(data);
      List<SkillMechanic> signalSkills = this.signalSkills.get(signal);
      if (signalSkills != null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Found {0} skills for signal {1}", signalSkills.size(), signal);

         for (SkillMechanic ms : signalSkills) {
            if (ms.isUsableFromCaster(data)) {
               ms.execute(data);
            }
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Signal skills for signal {0} not found", signal);
      }

      Queue<SkillMechanic> genericSignalSkills = this.getSkills(SkillTriggers.SIGNAL);
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Found {0} generic signal skills for signal {1}", genericSignalSkills.size(), signal);

      for (SkillMechanic msx : this.getSkills(SkillTriggers.SIGNAL)) {
         if (msx.isUsableFromCaster(data)) {
            msx.execute(data);
         }
      }
   }

   @Override
   public Queue<SkillMechanic> getSkills(SkillTrigger trigger) {
      if (!this.hasCombatSkills
         || trigger != SkillTriggers.SPAWN && trigger != SkillTriggers.ATTACK && trigger != SkillTriggers.DAMAGED && trigger != SkillTriggers.DEATH) {
         if (trigger != SkillTriggers.SPAWN && trigger != SkillTriggers.LOAD) {
            return this.skills.getOrDefault(trigger, EMPTY_SKILLS);
         } else {
            Queue<SkillMechanic> skills = Queues.newConcurrentLinkedQueue(this.skills.getOrDefault(trigger, EMPTY_SKILLS));
            Queue<SkillMechanic> spawnOrLoad = this.skills.get(SkillTriggers.SPAWN_OR_LOAD);
            if (spawnOrLoad != null) {
               skills.addAll(spawnOrLoad);
            }

            return skills;
         }
      } else {
         Queue<SkillMechanic> skills = Queues.newConcurrentLinkedQueue(this.skills.getOrDefault(trigger, EMPTY_SKILLS));
         skills.addAll(this.skills.get(SkillTriggers.COMBAT));
         return skills;
      }
   }

   @Override
   public boolean hasSkills(SkillTrigger trigger) {
      return this.skills.containsKey(trigger);
   }

   @Override
   public Queue<SkillMechanic> getTimerSkills() {
      return this.timerSkills;
   }

   @Override
   public void setEntityType(MythicEntityType type) {
      this.entityTypeString = type.toString();
      this.entityType = type;
      this.entityBaseSpawner = BukkitEntityType.getMythicEntity(this.entityType);
      this.entityBaseSpawner.instantiate(this.config);
      this.config.set("Type", this.entityTypeString);
   }

   @Override
   public String getEntityTypeString() {
      return this.entityTypeString;
   }

   @Override
   public BukkitEntityType getMythicEntity() {
      return this.entityBaseSpawner;
   }

   @Override
   public DespawnMode getDespawnMode() {
      return this.despawnMode;
   }

   @Override
   public double getPerLevelHealth() {
      return this.statLevelModifiers == null ? 0.0 : this.statLevelModifiers.getOrDefault(Stats.HEALTH, 0.0);
   }

   @Override
   public double getPerLevelDamage() {
      return this.statLevelModifiers == null ? 0.0 : this.statLevelModifiers.getOrDefault(Stats.ATTACK_DAMAGE, 0.0);
   }

   @Override
   public double getPerLevelPower() {
      return this.statLevelModifiers == null ? 0.0 : this.statLevelModifiers.getOrDefault(Stats.SKILL_POWER, 0.0);
   }

   @Override
   public Optional<Double> getBaseStatValue(ActiveMob am, StatType statType) {
      PlaceholderDouble value = this.stats.get(statType);
      if (value == null) {
         return Optional.empty();
      } else {
         double level = am.getLevel();
         double attr = value.get(am);
         if (level > 1.0) {
            double levelMod = this.statLevelModifiers == null ? 0.0 : this.statLevelModifiers.getOrDefault(statType, 0.0);
            if (levelMod != 0.0) {
               attr += levelMod * (level - 1.0);
            } else {
               Optional<Expression> maybeEq = this.getPlugin().getConfiguration().getStatScalingEquation(statType);
               if (maybeEq.isPresent()) {
                  attr = maybeEq.get().setVariable("v", attr).setVariable("l", am.getLevel()).evaluate();
               }
            }
         }

         return Optional.of(attr);
      }
   }

   @Override
   public PlaceholderDouble getArmor() {
      return this.getStats().getOrDefault(Stats.ARMOR, PlaceholderDouble.of("0"));
   }

   @Override
   public double getPerLevelArmor() {
      return this.statLevelModifiers == null ? 0.0 : this.statLevelModifiers.getOrDefault(Stats.ARMOR, 0.0);
   }

   @Override
   public double getArmor(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.ARMOR).orElse(-1.0);
   }

   @Override
   public PlaceholderDouble getDamage() {
      return this.getStats().getOrDefault(Stats.ATTACK_DAMAGE, PlaceholderDouble.of("0"));
   }

   @Override
   public PlaceholderDouble getHealth() {
      return this.getStats().getOrDefault(Stats.HEALTH, PlaceholderDouble.of("0"));
   }

   @Override
   public double getHealth(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.HEALTH).orElse(-1.0);
   }

   @Override
   public double getDamage(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.ATTACK_DAMAGE).orElse(-1.0);
   }

   @Override
   public double getAttackSpeed(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.ATTACK_SPEED).orElse(-1.0);
   }

   @Override
   public double getMovementSpeed(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.MOVEMENT_SPEED).orElse(-1.0);
   }

   @Override
   public double getFlyingSpeed(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.FLYING_SPEED).orElse(-1.0);
   }

   @Override
   public double getKnockbackResistance(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.KNOCKBACK_RESISTANCE).orElse(0.0);
   }

   public double getFollowRange(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.FOLLOW_RANGE).orElse(0.0);
   }

   public double AttackSpeed(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.ATTACK_SPEED).orElse(0.0);
   }

   public double getScale(ActiveMob am) {
      return this.getBaseStatValue(am, Stats.SCALE).orElse(-1.0);
   }

   @Override
   public double getReviveHealth(ActiveMob am) {
      return this.optionReviveHealth <= 0.0 ? this.getHealth(am) : this.optionReviveHealth;
   }

   @Override
   public boolean hasFaction() {
      return this.faction != null;
   }

   @Override
   public boolean getIsInvincible() {
      return this.optionInvincible;
   }

   @Override
   public boolean usesThreatTable() {
      return this.useThreatTable;
   }

   @Override
   public boolean usesImmunityTable() {
      return this.useImmunityTable;
   }

   @Override
   public boolean usesHearing() {
      return this.useHearing;
   }

   @Override
   public boolean getThreatTableUseDamageTaken() {
      return this.optionTTFromDamage;
   }

   @Override
   public boolean getThreatTableDecaysUnreachable() {
      return this.optionTTDecayUnreachable;
   }

   @Override
   public int getThreatTableDropUnreachableTicks() {
      return this.optionTTDropUnreachableTicks;
   }

   @Override
   public List<String> getAIGoalSelectors() {
      return this.aiGoalSelectors;
   }

   @Override
   public List<String> getAITargetSelectors() {
      return this.aiTargetSelectors;
   }

   @Override
   public boolean hasKillMessages() {
      return this.killMessages != null && !this.killMessages.isEmpty();
   }

   @Override
   public PlaceholderString getKillMessage() {
      return !this.hasKillMessages() ? null : this.killMessages.get(Numbers.randomInt(this.killMessages.size()));
   }

   @Override
   public double getSpawnVelocityX() {
      return this.spawnVelocityX;
   }

   @Override
   public double getSpawnVelocityY() {
      return this.spawnVelocityY;
   }

   @Override
   public double getSpawnVelocityZ() {
      return this.spawnVelocityZ;
   }

   @Override
   public boolean getIsInteractable() {
      return this.optionInteractable;
   }

   @Override
   public boolean usesBossBar() {
      return this.useBossBar;
   }

   @Override
   public int getBossBarRangeSquared() {
      return this.bossBarRangeSq;
   }

   @Override
   public Optional<AbstractBossBar> getBossBar() {
      if (!this.useBossBar) {
         return Optional.empty();
      } else {
         AbstractBossBar bar = this.getPlugin().getBootstrap().createBossBar(" ", this.bossBarColor, this.bossBarStyle);
         bar.setProgress(1.0);
         if (this.bossBarCreateFog) {
            bar.setCreateFog(true);
         }

         if (this.bossBarDarkenSky) {
            bar.setDarkenSky(true);
         }

         if (this.bossBarPlayMusic) {
            bar.setPlayBossMusic(true);
         }

         return Optional.of(bar);
      }
   }

   @Override
   public PlaceholderString getBossBarTitle() {
      return this.bossBarTitle;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof MobType ? ((MobType)o).getInternalName().equals(this.internalName) : false;
   }

   @Override
   public String toString() {
      return "MythicMob{" + this.internalName + "}";
   }

   public int compareTo(MythicMob mm) {
      return this.internalName.compareTo(mm.getInternalName());
   }

   @Override
   public boolean getShowHealthInChat() {
      return this.optionShowHealthInChat;
   }

   @Override
   public boolean getShowNameOnDamaged() {
      return this.showNameOnDamage;
   }

   @Override
   public AbstractItemStack getEgg() {
      BukkitItemStack egg;
      if (this.eggItem != null) {
         egg = (BukkitItemStack)this.eggItem;
      } else {
         egg = (BukkitItemStack)this.getPlugin().getConfiguration().getDefaultMobEggItem();
      }

      PlaceholderString eggDisplay;
      if (this.eggDisplay != null) {
         eggDisplay = this.eggDisplay;
      } else {
         eggDisplay = this.getPlugin().getConfiguration().getDefaultMobEggDisplay();
      }

      PlaceholderString eggModel;
      if (this.eggModel != null) {
         eggModel = this.eggModel;
      } else {
         eggModel = this.getPlugin().getConfiguration().getDefaultMobEggModel();
      }

      List<PlaceholderString> eggLore;
      if (this.eggLore != null) {
         eggLore = this.eggLore;
      } else {
         eggLore = this.getPlugin().getConfiguration().getDefaultMobEggLore();
      }

      egg.enchant(VolatileEnchantment.UNBREAKING, 10).flags(AbstractItemFlag.HIDE_ATTRIBUTES);
      String displayName = eggDisplay.get();
      if (this.eggMobName != null) {
         displayName = displayName.replace("<caster.name>", this.eggMobName.get());
      } else if (this.displayName != null) {
         displayName = displayName.replace("<caster.name>", this.displayName.get());
      } else {
         displayName = displayName.replace("<caster.name>", this.entityType.name());
      }

      egg.customData("MYTHIC_EGG", new StringTag(this.getInternalName()));
      egg.name(Text.parse(displayName));
      List<Component> loreList = Lists.newArrayList();

      for (PlaceholderString line : eggLore) {
         String lore = line.get();
         lore = lore.replace("<caster.name>", displayName);
         loreList.add(Text.parse(lore));
      }

      egg.lore(loreList);
      if (eggModel != null) {
         String model = eggModel.get();
         if (!model.contains(":")) {
            model = "minecraft:" + model;
         }

         egg.itemModel(NamespacedKey.fromString(model));
      }

      return egg;
   }

   @Override
   public Icon<MobMenuContext> getIcon() {
      return IconBuilder.<MobMenuContext>create()
         .itemStack(this.cachedMenuItem)
         .hideFlags(true)
         .name(this.internalName)
         .lore(
            a -> {
               String display = this.displayName == null ? "<red>None" : this.displayName.get();
               return Lists.newArrayList(
                  new String[]{
                     "<red>",
                     Text.colorizeLegacy("<gold>Type<white>: " + this.entityType.toString()),
                     Text.colorizeLegacy("<gold>Display<white>: " + display),
                     Text.colorizeLegacy("<red> "),
                     Text.colorizeLegacy("<gray>\u22b3 <yellow>Left-Click to get Mob Egg"),
                     Text.colorizeLegacy("<gray>\u22b3 <yellow>Right-Click to Edit")
                  }
               );
            }
         )
         .click((context, player) -> {
            player.performCommand("mythicmobs egg get " + this.internalName);
            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
         })
         .rightClick((context, player) -> {
            MobEditorMenuContext ctx = new MobEditorMenuContext(this, context);
            this.getPlugin().getMenuManager().getMobEditorMenu().open(player, ctx);
         })
         .build();
   }

   private void setupDefaultVariableValues() {
      MythicConfig config = this.getConfig();
      this.defaultVariables = new VariableRegistry();

      for (String node : config.getKeys("Variables")) {
         String innerNode = "Variables." + node;
         String value = config.getString(innerNode);
         this.defaultVariables.put(node, VariableUtils.createVariableFromPrefixedValue(value));
      }
   }

   private void setupDefaultSkillVariableValues() {
      MythicConfig config = this.getConfig();
      this.defaultSkillVariables = new VariableRegistry();

      for (String node : config.getKeys("SkillVariables")) {
         String innerNode = "SkillVariables." + node;
         String value = config.getString(innerNode);
         this.defaultSkillVariables.put(node, VariableUtils.createVariableFromPrefixedValue(value));
      }
   }

   private record BoundsTriggerSkill(SkillMechanic skill, BoundsSpec bounds, SkillTrigger trigger, String stateKey) {
   }
}
