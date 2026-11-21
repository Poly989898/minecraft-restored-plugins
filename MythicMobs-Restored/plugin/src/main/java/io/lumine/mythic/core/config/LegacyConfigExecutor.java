package io.lumine.mythic.core.config;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.DoubleProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillString;
import java.io.File;
import java.util.HashSet;
import lombok.Generated;
import org.bukkit.Material;

public class LegacyConfigExecutor implements PropertyHolder {
   private static final DoubleProp VERSION = Property.Double(Scope.CONFIG, "Version");
   private static final BooleanProp GENERAL_ALLOW_METRICS = Property.Boolean(Scope.CONFIG, "General.AllowMetrics", true);
   private static final BooleanProp GENERAL_CHECK_UPDATES = Property.Boolean(Scope.CONFIG, "General.CheckForUpdates", true);
   private static final BooleanProp GENERAL_COMPATIB_MODE = Property.Boolean(Scope.CONFIG, "General.CompatibilityMode", false);
   private static final BooleanProp GENERAL_ERROR_LOGGING = Property.Boolean(Scope.CONFIG, "General.ErrorLogging", true);
   private static final BooleanProp CANCEL_DAMAGE_IF_0 = Property.Boolean(Scope.CONFIG, "General.CancelDamageIfZero", false);
   private static final BooleanProp GENERAL_LOAD_EXAMPLES = Property.Boolean(Scope.CONFIG, "General.LoadExampleConfigs", true);
   private static final StringProp GENERAL_LANGUAGE = Property.String(Scope.CONFIG, "General.Language", "enUS");
   private static final IntProp DEBUG_LEVEL = Property.Int(Scope.CONFIG, "General.DebugLevel", 0);
   private static final BooleanProp DEBUG_MODE = Property.Boolean(Scope.CONFIG, "General.DebugMode", false);
   private static final BooleanProp DEBUG_SPAWNERS = Property.Boolean(Scope.CONFIG, "General.DebugSpawners", false);
   private static final IntProp CLOCK_SPEED = Property.Int(Scope.CONFIG, "Clock.Main", 1);
   private static final IntProp CLOCK_RANDOM_SPAWNING = Property.Int(Scope.CONFIG, "Clock.RandomSpawning", 1);
   private static final IntProp CLOCK_SAVING = Property.Int(Scope.CONFIG, "Clock.Saving", 300);
   private static final StringProp MOBS_KILL_MESSAGE_PREFIX = Property.String(Scope.CONFIG, "Mobs.KillMessagePrefix", null);
   private IOLoader<MythicBukkit> settings;
   private MythicConfig config;
   private Boolean allowMetrics = true;
   private Boolean allowUpdateChecking = true;
   private Boolean useCompatibilityMode = false;
   private Boolean loadExampleConfigs = true;
   private Boolean fireAPIDeathEvent = true;
   private Boolean fireAPISkillEvent = true;
   private Boolean fireAPICustomSkillEvent = true;
   private int debugLevel = -1;
   private boolean debugMode = false;
   private boolean debugSpawners = false;
   private Boolean doErrorLogging = true;
   private boolean cancelDamageIfZero = false;
   private DropMethod defaultDropMethod = DropMethod.VANILLA;
   private int clockIntervalMain = 1;
   private int clockIntervalRandomSpawning = 1;
   private int clockIntervalSaving = 300;
   public int SpawningInterval = 1;
   public int timerupdate;
   public int walkupdate;
   public int ShowHealthRadius = 25;
   public int ScanInterval = 10;
   public int ClnrInterval = 600;
   private int bossBarUpdateInterval = 20;
   public boolean EnableAIModifiers = true;
   public boolean EnableThreatTables = true;
   public String killMessagePrefix = "";
   public Boolean mobsDespawnByDefault = false;
   public Boolean preventOtherDropsByDefault = false;
   public String defaultLevelModifierHealth;
   public String defaultLevelModifierDamage;
   public String defaultLevelModifierPower;
   public String defaultLevelModifierArmor;
   public String defaultLevelModifierKBR;
   public String scalingEquationDamage = null;
   public String scalingEquationHealth = null;
   public boolean EnableTimerSkills = true;
   public Boolean skillsIgnoreArmorStandsByDefault = true;
   public boolean targetSelf = false;
   public boolean targetPlayers = true;
   public boolean targetArmorStands = false;
   public boolean targetMarkers = false;
   public boolean targetCreativeMode = true;
   public boolean targetSpectatorMode = true;
   public boolean targetCitizensNPCs = false;
   public boolean targetAnimals = true;
   public boolean targetCreatures = true;
   public boolean targetMonsters = true;
   public boolean targetWaterMobs = true;
   public boolean targetFlyingMobs = true;
   public boolean targetSameFaction = true;
   public boolean targetOwner = true;
   public boolean targetNonMythic = true;
   public boolean targetVillagers = true;
   public int SpawnerRange = 40;
   public boolean rsGeneratePoints = false;
   public int rsMaxMobsPerChunk = 20;
   public int randomSpawningPlayerRadiusMax = 64;
   public int randomSpawningPlayerRadiusMin = 16;
   public int randomSpawningPlayerRadiusY = 16;
   public int rsPointsPerSecondLand = 10;
   public int rsPointsPerSecondAir = 0;
   public int rsPointsPerSecondSea = 1;
   public int rsPointsPerSecondLava = 0;
   public int rsPointsPerSecondGround = 0;
   public int rsMaxGenerationTimeMillis = 20;
   public boolean rsDespawnVanillaOutsideRadius = false;
   public boolean vanillaSpawnsDisabled = false;
   public boolean updateItems = true;
   public boolean compatHeroesXPEnable = false;
   public boolean compatHeroesShowXPMessage = true;
   public String compatHeroesXPMessageFormat = "";
   public boolean compatSkillAPIShowXPMessage = true;
   public String compatSkillAPIXPMessageFormat = "";
   public boolean compatVaultShowMoneyMessage = true;
   public String compatVaultMoneyMessageFormat = "";
   public boolean compatMcMMOShowXPMessage = true;
   public String compatMcMMOXPMessageFormat = "";
   private final MythicBukkit plugin;
   private static HashSet<Material> transparentBlocks = new HashSet<>();

   public LegacyConfigExecutor(MythicBukkit core) {
      this.plugin = core;
   }

   public File getConfigFolder() {
      File configFolder = new File(this.getPlugin().getDataFolder(), "config");
      if (!configFolder.exists()) {
         configFolder.mkdir();
      }

      return configFolder;
   }

   public void loadSettings() {
      this.settings = new IOLoader<>(this.plugin, "config.yml");
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Loading Settings...");
      File configFile = new File(this.getPlugin().getDataFolder(), "config.yml");
      if (!configFile.exists()) {
         this.getPlugin().saveResource("config.yml", false);
      }

      MythicConfig mc = new MythicConfigImpl("Configuration", this.settings.getCustomConfig());
      this.allowMetrics = GENERAL_ALLOW_METRICS.get(this);
      this.allowUpdateChecking = GENERAL_CHECK_UPDATES.get(this);
      this.useCompatibilityMode = GENERAL_COMPATIB_MODE.get(this);
      this.doErrorLogging = GENERAL_ERROR_LOGGING.get(this);
      this.cancelDamageIfZero = CANCEL_DAMAGE_IF_0.get(this);
      this.debugLevel = DEBUG_LEVEL.get(this);
      this.clockIntervalMain = CLOCK_SPEED.get(this);
      this.clockIntervalRandomSpawning = CLOCK_RANDOM_SPAWNING.get(this);
      this.clockIntervalSaving = CLOCK_SAVING.get(this);
      this.SpawningInterval = mc.getInteger("Clock.SpawnsInterval", 2);
      this.ScanInterval = mc.getInteger("Clock.ScannerInterval", 10);
      this.ClnrInterval = mc.getInteger("Clock.CleanupInterval", 600);
      this.targetSelf = mc.getBoolean("Targeters.Filters.Default.TargetSelf", false);
      this.targetPlayers = mc.getBoolean("Targeters.Filters.Default.TargetPlayers", true);
      this.targetArmorStands = mc.getBoolean("Targeters.Filters.Default.TargetArmorStands", false);
      this.targetMarkers = mc.getBoolean("Targeters.Filters.Default.TargetMarkers", false);
      this.targetCreativeMode = mc.getBoolean("Targeters.Filters.Default.TargetCreativeMode", true);
      this.targetSpectatorMode = mc.getBoolean("Targeters.Filters.Default.TargetSpectatorMode", true);
      this.targetCitizensNPCs = mc.getBoolean("Targeters.Filters.Default.TargetCitizensNPCs", false);
      this.targetAnimals = mc.getBoolean("Targeters.Filters.Default.TargetAnimals", true);
      this.targetCreatures = mc.getBoolean("Targeters.Filters.Default.TargetCreatures", true);
      this.targetMonsters = mc.getBoolean("Targeters.Filters.Default.TargetMonsters", true);
      this.targetWaterMobs = mc.getBoolean("Targeters.Filters.Default.TargetWaterMobs", true);
      this.targetFlyingMobs = mc.getBoolean("Targeters.Filters.Default.TargetFlyingMobs", true);
      this.targetSameFaction = mc.getBoolean("Targeters.Filters.Default.TargetSameFaction", true);
      this.targetOwner = mc.getBoolean("Targeters.Filters.Default.TargetOwner", true);
      this.targetNonMythic = mc.getBoolean("Targeters.Filters.Default.TargetNonMythic", true);
      this.targetVillagers = mc.getBoolean("Targeters.Filters.Default.TargetVillagers", true);
      this.mobsDespawnByDefault = mc.getBoolean("Mobs.DespawnByDefault", true);
      this.preventOtherDropsByDefault = mc.getBoolean("Mobs.PreventOtherDropsByDefault", false);
      this.EnableTimerSkills = mc.getBoolean("Mobs.EnableTimerSkills", true);
      this.EnableThreatTables = mc.getBoolean("Mobs.EnableThreatTables", true);
      this.killMessagePrefix = MOBS_KILL_MESSAGE_PREFIX.get(this);
      this.bossBarUpdateInterval = mc.getInteger("Mobs.BossBar.UpdateInterval", 20);
      this.rsGeneratePoints = mc.getBoolean("RandomSpawning.GenerateSpawnPoints", false);
      this.rsMaxMobsPerChunk = mc.getInteger("RandomSpawning.MaxMobsPerChunk", 2);
      this.randomSpawningPlayerRadiusMax = mc.getInteger("RandomSpawning.SpawnRadiusPerPlayer", 64);
      this.randomSpawningPlayerRadiusMin = mc.getInteger("RandomSpawning.SpawnRadiusPerPlayerMin", 16);
      this.randomSpawningPlayerRadiusY = mc.getInteger("RandomSpawning.SpawnRadiusPerPlayerY", 16);
      this.rsPointsPerSecondLand = mc.getInteger("RandomSpawning.PointsPerSecond.Land", 10);
      this.rsPointsPerSecondAir = mc.getInteger("RandomSpawning.PointsPerSecond.Air", 1);
      this.rsPointsPerSecondSea = mc.getInteger("RandomSpawning.PointsPerSecond.Sea", 1);
      this.rsPointsPerSecondLava = mc.getInteger("RandomSpawning.PointsPerSecond.Lava", 0);
      this.rsPointsPerSecondGround = mc.getInteger("RandomSpawning.PointsPerSecond.Ground", 0);
      this.rsDespawnVanillaOutsideRadius = mc.getBoolean("RandomSpawning.DespawnLazyRandomMobs", true);
      this.rsMaxGenerationTimeMillis = mc.getInteger("RandomSpawning.MaxGenerationTime", 20);
      this.vanillaSpawnsDisabled = mc.getBoolean("RandomSpawning.DisableVanillaSpawns", false);
      this.compatHeroesXPEnable = mc.getBoolean("Compatibility.Heroes.Enabled", this.compatHeroesXPEnable);
      this.compatHeroesShowXPMessage = mc.getBoolean("Compatibility.Heroes.ShowXPMessage", this.compatHeroesShowXPMessage);
      this.compatHeroesXPMessageFormat = mc.getString("Compatibility.Heroes.XPMessageFormat", this.compatHeroesXPMessageFormat);
      this.compatMcMMOShowXPMessage = mc.getBoolean("Compatibility.McMMO.ShowXPMessage", this.compatMcMMOShowXPMessage);
      this.compatMcMMOXPMessageFormat = mc.getString("Compatibility.McMMO.XPMessageFormat", this.compatMcMMOXPMessageFormat);
      this.compatSkillAPIShowXPMessage = mc.getBoolean("Compatibility.SkillAPI.ShowXPMessage", this.compatSkillAPIShowXPMessage);
      this.compatSkillAPIXPMessageFormat = mc.getString("Compatibility.SkillAPI.XPMessageFormat", this.compatSkillAPIXPMessageFormat);
      this.compatVaultShowMoneyMessage = mc.getBoolean("Compatibility.Vault.ShowMoneyMessage", this.compatVaultShowMoneyMessage);
      this.compatVaultMoneyMessageFormat = mc.getString("Compatibility.Vault.MoneyMessageFormat", this.compatVaultMoneyMessageFormat);
      this.compatHeroesXPMessageFormat = SkillString.convertLegacyVariables(this.compatHeroesXPMessageFormat);
      this.compatMcMMOXPMessageFormat = SkillString.convertLegacyVariables(this.compatMcMMOXPMessageFormat);
      this.compatSkillAPIXPMessageFormat = SkillString.convertLegacyVariables(this.compatSkillAPIXPMessageFormat);
      this.compatVaultMoneyMessageFormat = SkillString.convertLegacyVariables(this.compatVaultMoneyMessageFormat);
      this.updateItems = mc.getBoolean("ItemUpdating.Enabled", true);
      this.loadExampleConfigs = mc.getBoolean("LoadExampleConfigs", true);
      this.scalingEquationDamage = mc.getString("Mobs.ScalingEquations.Damage", "V * ((1.05)^(L-1))");
      this.scalingEquationHealth = mc.getString("Mobs.ScalingEquations.Health", "V * ((1.05)^(L-1))");
      if (this.settings.getCustomConfig().contains("Configuration.Mobs.Scaling")) {
         for (String world : this.settings.getCustomConfig().getConfigurationSection("Configuration.Mobs.Scaling").getKeys(false)) {
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "*** Loading WorldScaling for world " + world);
            new MythicConfigImpl("Configuration.Mobs.Scaling." + world, this.settings.getCustomConfig());
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Loading Default Level Modifiers...");
      this.defaultLevelModifierHealth = mc.getString("Mobs.DefaultLevelModifiers.Health", "0.1");
      this.defaultLevelModifierDamage = mc.getString("Mobs.DefaultLevelModifiers.Damage", "0");
      this.defaultLevelModifierPower = mc.getString("Mobs.DefaultLevelModifiers.Power", "0");
      this.defaultLevelModifierArmor = mc.getString("Mobs.DefaultLevelModifiers.Armor", "0");
      this.defaultLevelModifierKBR = mc.getString("Mobs.DefaultLevelModifiers.KnockbackResistance", "0");
   }

   public MythicConfig getConfig() {
      return this.config;
   }

   public boolean getDespawnMobsByDefault() {
      return this.mobsDespawnByDefault;
   }

   public boolean getPreventOtherDropsByDefault() {
      return this.preventOtherDropsByDefault;
   }

   public HashSet<Material> getTransparentBlocks() {
      return transparentBlocks;
   }

   public boolean shouldUpdateItems() {
      return this.updateItems;
   }

   @Override
   public String getPropertyNode() {
      return "Configuration";
   }

   @Generated
   public IOLoader<MythicBukkit> getSettings() {
      return this.settings;
   }

   @Generated
   public int getSpawningInterval() {
      return this.SpawningInterval;
   }

   @Generated
   public int getTimerupdate() {
      return this.timerupdate;
   }

   @Generated
   public int getWalkupdate() {
      return this.walkupdate;
   }

   @Generated
   public int getShowHealthRadius() {
      return this.ShowHealthRadius;
   }

   @Generated
   public int getScanInterval() {
      return this.ScanInterval;
   }

   @Generated
   public int getClnrInterval() {
      return this.ClnrInterval;
   }

   @Generated
   public boolean isEnableAIModifiers() {
      return this.EnableAIModifiers;
   }

   @Generated
   public boolean isEnableThreatTables() {
      return this.EnableThreatTables;
   }

   @Generated
   public String getDefaultLevelModifierHealth() {
      return this.defaultLevelModifierHealth;
   }

   @Generated
   public String getDefaultLevelModifierDamage() {
      return this.defaultLevelModifierDamage;
   }

   @Generated
   public String getDefaultLevelModifierPower() {
      return this.defaultLevelModifierPower;
   }

   @Generated
   public String getDefaultLevelModifierArmor() {
      return this.defaultLevelModifierArmor;
   }

   @Generated
   public String getDefaultLevelModifierKBR() {
      return this.defaultLevelModifierKBR;
   }

   @Generated
   public boolean isEnableTimerSkills() {
      return this.EnableTimerSkills;
   }

   @Generated
   public int getSpawnerRange() {
      return this.SpawnerRange;
   }

   @Generated
   public boolean isRsGeneratePoints() {
      return this.rsGeneratePoints;
   }

   @Generated
   public int getRsMaxMobsPerChunk() {
      return this.rsMaxMobsPerChunk;
   }

   @Generated
   public int getRsPointsPerSecondLand() {
      return this.rsPointsPerSecondLand;
   }

   @Generated
   public int getRsPointsPerSecondAir() {
      return this.rsPointsPerSecondAir;
   }

   @Generated
   public int getRsPointsPerSecondSea() {
      return this.rsPointsPerSecondSea;
   }

   @Generated
   public int getRsPointsPerSecondLava() {
      return this.rsPointsPerSecondLava;
   }

   @Generated
   public int getRsPointsPerSecondGround() {
      return this.rsPointsPerSecondGround;
   }

   @Generated
   public int getRsMaxGenerationTimeMillis() {
      return this.rsMaxGenerationTimeMillis;
   }

   @Generated
   public boolean isRsDespawnVanillaOutsideRadius() {
      return this.rsDespawnVanillaOutsideRadius;
   }

   @Generated
   public boolean isUpdateItems() {
      return this.updateItems;
   }

   @Generated
   public boolean isCompatHeroesXPEnable() {
      return this.compatHeroesXPEnable;
   }

   @Generated
   public boolean isCompatHeroesShowXPMessage() {
      return this.compatHeroesShowXPMessage;
   }

   @Generated
   public String getCompatHeroesXPMessageFormat() {
      return this.compatHeroesXPMessageFormat;
   }

   @Generated
   public boolean isCompatSkillAPIShowXPMessage() {
      return this.compatSkillAPIShowXPMessage;
   }

   @Generated
   public String getCompatSkillAPIXPMessageFormat() {
      return this.compatSkillAPIXPMessageFormat;
   }

   @Generated
   public boolean isCompatVaultShowMoneyMessage() {
      return this.compatVaultShowMoneyMessage;
   }

   @Generated
   public String getCompatVaultMoneyMessageFormat() {
      return this.compatVaultMoneyMessageFormat;
   }

   @Generated
   public boolean isCompatMcMMOShowXPMessage() {
      return this.compatMcMMOShowXPMessage;
   }

   @Generated
   public String getCompatMcMMOXPMessageFormat() {
      return this.compatMcMMOXPMessageFormat;
   }

   @Generated
   public void setSettings(IOLoader<MythicBukkit> settings) {
      this.settings = settings;
   }

   @Generated
   public void setConfig(MythicConfig config) {
      this.config = config;
   }

   @Generated
   public void setAllowMetrics(Boolean allowMetrics) {
      this.allowMetrics = allowMetrics;
   }

   @Generated
   public void setAllowUpdateChecking(Boolean allowUpdateChecking) {
      this.allowUpdateChecking = allowUpdateChecking;
   }

   @Generated
   public void setUseCompatibilityMode(Boolean useCompatibilityMode) {
      this.useCompatibilityMode = useCompatibilityMode;
   }

   @Generated
   public void setLoadExampleConfigs(Boolean loadExampleConfigs) {
      this.loadExampleConfigs = loadExampleConfigs;
   }

   @Generated
   public void setFireAPIDeathEvent(Boolean fireAPIDeathEvent) {
      this.fireAPIDeathEvent = fireAPIDeathEvent;
   }

   @Generated
   public void setFireAPISkillEvent(Boolean fireAPISkillEvent) {
      this.fireAPISkillEvent = fireAPISkillEvent;
   }

   @Generated
   public void setFireAPICustomSkillEvent(Boolean fireAPICustomSkillEvent) {
      this.fireAPICustomSkillEvent = fireAPICustomSkillEvent;
   }

   @Generated
   public void setDebugMode(boolean debugMode) {
      this.debugMode = debugMode;
   }

   @Generated
   public void setDebugSpawners(boolean debugSpawners) {
      this.debugSpawners = debugSpawners;
   }

   @Generated
   public void setDoErrorLogging(Boolean doErrorLogging) {
      this.doErrorLogging = doErrorLogging;
   }

   @Generated
   public void setCancelDamageIfZero(boolean cancelDamageIfZero) {
      this.cancelDamageIfZero = cancelDamageIfZero;
   }

   @Generated
   public void setDefaultDropMethod(DropMethod defaultDropMethod) {
      this.defaultDropMethod = defaultDropMethod;
   }

   @Generated
   public void setClockIntervalMain(int clockIntervalMain) {
      this.clockIntervalMain = clockIntervalMain;
   }

   @Generated
   public void setClockIntervalRandomSpawning(int clockIntervalRandomSpawning) {
      this.clockIntervalRandomSpawning = clockIntervalRandomSpawning;
   }

   @Generated
   public void setClockIntervalSaving(int clockIntervalSaving) {
      this.clockIntervalSaving = clockIntervalSaving;
   }

   @Generated
   public void setSpawningInterval(int SpawningInterval) {
      this.SpawningInterval = SpawningInterval;
   }

   @Generated
   public void setTimerupdate(int timerupdate) {
      this.timerupdate = timerupdate;
   }

   @Generated
   public void setWalkupdate(int walkupdate) {
      this.walkupdate = walkupdate;
   }

   @Generated
   public void setShowHealthRadius(int ShowHealthRadius) {
      this.ShowHealthRadius = ShowHealthRadius;
   }

   @Generated
   public void setScanInterval(int ScanInterval) {
      this.ScanInterval = ScanInterval;
   }

   @Generated
   public void setClnrInterval(int ClnrInterval) {
      this.ClnrInterval = ClnrInterval;
   }

   @Generated
   public void setBossBarUpdateInterval(int bossBarUpdateInterval) {
      this.bossBarUpdateInterval = bossBarUpdateInterval;
   }

   @Generated
   public void setEnableAIModifiers(boolean EnableAIModifiers) {
      this.EnableAIModifiers = EnableAIModifiers;
   }

   @Generated
   public void setEnableThreatTables(boolean EnableThreatTables) {
      this.EnableThreatTables = EnableThreatTables;
   }

   @Generated
   public void setKillMessagePrefix(String killMessagePrefix) {
      this.killMessagePrefix = killMessagePrefix;
   }

   @Generated
   public void setMobsDespawnByDefault(Boolean mobsDespawnByDefault) {
      this.mobsDespawnByDefault = mobsDespawnByDefault;
   }

   @Generated
   public void setPreventOtherDropsByDefault(Boolean preventOtherDropsByDefault) {
      this.preventOtherDropsByDefault = preventOtherDropsByDefault;
   }

   @Generated
   public void setDefaultLevelModifierHealth(String defaultLevelModifierHealth) {
      this.defaultLevelModifierHealth = defaultLevelModifierHealth;
   }

   @Generated
   public void setDefaultLevelModifierDamage(String defaultLevelModifierDamage) {
      this.defaultLevelModifierDamage = defaultLevelModifierDamage;
   }

   @Generated
   public void setDefaultLevelModifierPower(String defaultLevelModifierPower) {
      this.defaultLevelModifierPower = defaultLevelModifierPower;
   }

   @Generated
   public void setDefaultLevelModifierArmor(String defaultLevelModifierArmor) {
      this.defaultLevelModifierArmor = defaultLevelModifierArmor;
   }

   @Generated
   public void setDefaultLevelModifierKBR(String defaultLevelModifierKBR) {
      this.defaultLevelModifierKBR = defaultLevelModifierKBR;
   }

   @Generated
   public void setScalingEquationDamage(String scalingEquationDamage) {
      this.scalingEquationDamage = scalingEquationDamage;
   }

   @Generated
   public void setScalingEquationHealth(String scalingEquationHealth) {
      this.scalingEquationHealth = scalingEquationHealth;
   }

   @Generated
   public void setEnableTimerSkills(boolean EnableTimerSkills) {
      this.EnableTimerSkills = EnableTimerSkills;
   }

   @Generated
   public void setSkillsIgnoreArmorStandsByDefault(Boolean skillsIgnoreArmorStandsByDefault) {
      this.skillsIgnoreArmorStandsByDefault = skillsIgnoreArmorStandsByDefault;
   }

   @Generated
   public void setTargetSelf(boolean targetSelf) {
      this.targetSelf = targetSelf;
   }

   @Generated
   public void setTargetPlayers(boolean targetPlayers) {
      this.targetPlayers = targetPlayers;
   }

   @Generated
   public void setTargetArmorStands(boolean targetArmorStands) {
      this.targetArmorStands = targetArmorStands;
   }

   @Generated
   public void setTargetMarkers(boolean targetMarkers) {
      this.targetMarkers = targetMarkers;
   }

   @Generated
   public void setTargetCreativeMode(boolean targetCreativeMode) {
      this.targetCreativeMode = targetCreativeMode;
   }

   @Generated
   public void setTargetSpectatorMode(boolean targetSpectatorMode) {
      this.targetSpectatorMode = targetSpectatorMode;
   }

   @Generated
   public void setTargetCitizensNPCs(boolean targetCitizensNPCs) {
      this.targetCitizensNPCs = targetCitizensNPCs;
   }

   @Generated
   public void setTargetAnimals(boolean targetAnimals) {
      this.targetAnimals = targetAnimals;
   }

   @Generated
   public void setTargetCreatures(boolean targetCreatures) {
      this.targetCreatures = targetCreatures;
   }

   @Generated
   public void setTargetMonsters(boolean targetMonsters) {
      this.targetMonsters = targetMonsters;
   }

   @Generated
   public void setTargetWaterMobs(boolean targetWaterMobs) {
      this.targetWaterMobs = targetWaterMobs;
   }

   @Generated
   public void setTargetFlyingMobs(boolean targetFlyingMobs) {
      this.targetFlyingMobs = targetFlyingMobs;
   }

   @Generated
   public void setTargetSameFaction(boolean targetSameFaction) {
      this.targetSameFaction = targetSameFaction;
   }

   @Generated
   public void setTargetOwner(boolean targetOwner) {
      this.targetOwner = targetOwner;
   }

   @Generated
   public void setTargetNonMythic(boolean targetNonMythic) {
      this.targetNonMythic = targetNonMythic;
   }

   @Generated
   public void setTargetVillagers(boolean targetVillagers) {
      this.targetVillagers = targetVillagers;
   }

   @Generated
   public void setSpawnerRange(int SpawnerRange) {
      this.SpawnerRange = SpawnerRange;
   }

   @Generated
   public void setRsGeneratePoints(boolean rsGeneratePoints) {
      this.rsGeneratePoints = rsGeneratePoints;
   }

   @Generated
   public void setRsMaxMobsPerChunk(int rsMaxMobsPerChunk) {
      this.rsMaxMobsPerChunk = rsMaxMobsPerChunk;
   }

   @Generated
   public void setRandomSpawningPlayerRadiusMax(int randomSpawningPlayerRadiusMax) {
      this.randomSpawningPlayerRadiusMax = randomSpawningPlayerRadiusMax;
   }

   @Generated
   public void setRandomSpawningPlayerRadiusMin(int randomSpawningPlayerRadiusMin) {
      this.randomSpawningPlayerRadiusMin = randomSpawningPlayerRadiusMin;
   }

   @Generated
   public void setRandomSpawningPlayerRadiusY(int randomSpawningPlayerRadiusY) {
      this.randomSpawningPlayerRadiusY = randomSpawningPlayerRadiusY;
   }

   @Generated
   public void setRsPointsPerSecondLand(int rsPointsPerSecondLand) {
      this.rsPointsPerSecondLand = rsPointsPerSecondLand;
   }

   @Generated
   public void setRsPointsPerSecondAir(int rsPointsPerSecondAir) {
      this.rsPointsPerSecondAir = rsPointsPerSecondAir;
   }

   @Generated
   public void setRsPointsPerSecondSea(int rsPointsPerSecondSea) {
      this.rsPointsPerSecondSea = rsPointsPerSecondSea;
   }

   @Generated
   public void setRsPointsPerSecondLava(int rsPointsPerSecondLava) {
      this.rsPointsPerSecondLava = rsPointsPerSecondLava;
   }

   @Generated
   public void setRsPointsPerSecondGround(int rsPointsPerSecondGround) {
      this.rsPointsPerSecondGround = rsPointsPerSecondGround;
   }

   @Generated
   public void setRsMaxGenerationTimeMillis(int rsMaxGenerationTimeMillis) {
      this.rsMaxGenerationTimeMillis = rsMaxGenerationTimeMillis;
   }

   @Generated
   public void setRsDespawnVanillaOutsideRadius(boolean rsDespawnVanillaOutsideRadius) {
      this.rsDespawnVanillaOutsideRadius = rsDespawnVanillaOutsideRadius;
   }

   @Generated
   public void setVanillaSpawnsDisabled(boolean vanillaSpawnsDisabled) {
      this.vanillaSpawnsDisabled = vanillaSpawnsDisabled;
   }

   @Generated
   public void setUpdateItems(boolean updateItems) {
      this.updateItems = updateItems;
   }

   @Generated
   public void setCompatHeroesXPEnable(boolean compatHeroesXPEnable) {
      this.compatHeroesXPEnable = compatHeroesXPEnable;
   }

   @Generated
   public void setCompatHeroesShowXPMessage(boolean compatHeroesShowXPMessage) {
      this.compatHeroesShowXPMessage = compatHeroesShowXPMessage;
   }

   @Generated
   public void setCompatHeroesXPMessageFormat(String compatHeroesXPMessageFormat) {
      this.compatHeroesXPMessageFormat = compatHeroesXPMessageFormat;
   }

   @Generated
   public void setCompatSkillAPIShowXPMessage(boolean compatSkillAPIShowXPMessage) {
      this.compatSkillAPIShowXPMessage = compatSkillAPIShowXPMessage;
   }

   @Generated
   public void setCompatSkillAPIXPMessageFormat(String compatSkillAPIXPMessageFormat) {
      this.compatSkillAPIXPMessageFormat = compatSkillAPIXPMessageFormat;
   }

   @Generated
   public void setCompatVaultShowMoneyMessage(boolean compatVaultShowMoneyMessage) {
      this.compatVaultShowMoneyMessage = compatVaultShowMoneyMessage;
   }

   @Generated
   public void setCompatVaultMoneyMessageFormat(String compatVaultMoneyMessageFormat) {
      this.compatVaultMoneyMessageFormat = compatVaultMoneyMessageFormat;
   }

   @Generated
   public void setCompatMcMMOShowXPMessage(boolean compatMcMMOShowXPMessage) {
      this.compatMcMMOShowXPMessage = compatMcMMOShowXPMessage;
   }

   @Generated
   public void setCompatMcMMOXPMessageFormat(String compatMcMMOXPMessageFormat) {
      this.compatMcMMOXPMessageFormat = compatMcMMOXPMessageFormat;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LegacyConfigExecutor other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getDebugLevel() != other.getDebugLevel()) {
         return false;
      } else if (this.isDebugMode() != other.isDebugMode()) {
         return false;
      } else if (this.isDebugSpawners() != other.isDebugSpawners()) {
         return false;
      } else if (this.isCancelDamageIfZero() != other.isCancelDamageIfZero()) {
         return false;
      } else if (this.getClockIntervalMain() != other.getClockIntervalMain()) {
         return false;
      } else if (this.getClockIntervalRandomSpawning() != other.getClockIntervalRandomSpawning()) {
         return false;
      } else if (this.getClockIntervalSaving() != other.getClockIntervalSaving()) {
         return false;
      } else if (this.getSpawningInterval() != other.getSpawningInterval()) {
         return false;
      } else if (this.getTimerupdate() != other.getTimerupdate()) {
         return false;
      } else if (this.getWalkupdate() != other.getWalkupdate()) {
         return false;
      } else if (this.getShowHealthRadius() != other.getShowHealthRadius()) {
         return false;
      } else if (this.getScanInterval() != other.getScanInterval()) {
         return false;
      } else if (this.getClnrInterval() != other.getClnrInterval()) {
         return false;
      } else if (this.getBossBarUpdateInterval() != other.getBossBarUpdateInterval()) {
         return false;
      } else if (this.isEnableAIModifiers() != other.isEnableAIModifiers()) {
         return false;
      } else if (this.isEnableThreatTables() != other.isEnableThreatTables()) {
         return false;
      } else if (this.isEnableTimerSkills() != other.isEnableTimerSkills()) {
         return false;
      } else if (this.isTargetSelf() != other.isTargetSelf()) {
         return false;
      } else if (this.isTargetPlayers() != other.isTargetPlayers()) {
         return false;
      } else if (this.isTargetArmorStands() != other.isTargetArmorStands()) {
         return false;
      } else if (this.isTargetMarkers() != other.isTargetMarkers()) {
         return false;
      } else if (this.isTargetCreativeMode() != other.isTargetCreativeMode()) {
         return false;
      } else if (this.isTargetSpectatorMode() != other.isTargetSpectatorMode()) {
         return false;
      } else if (this.isTargetCitizensNPCs() != other.isTargetCitizensNPCs()) {
         return false;
      } else if (this.isTargetAnimals() != other.isTargetAnimals()) {
         return false;
      } else if (this.isTargetCreatures() != other.isTargetCreatures()) {
         return false;
      } else if (this.isTargetMonsters() != other.isTargetMonsters()) {
         return false;
      } else if (this.isTargetWaterMobs() != other.isTargetWaterMobs()) {
         return false;
      } else if (this.isTargetFlyingMobs() != other.isTargetFlyingMobs()) {
         return false;
      } else if (this.isTargetSameFaction() != other.isTargetSameFaction()) {
         return false;
      } else if (this.isTargetOwner() != other.isTargetOwner()) {
         return false;
      } else if (this.isTargetNonMythic() != other.isTargetNonMythic()) {
         return false;
      } else if (this.isTargetVillagers() != other.isTargetVillagers()) {
         return false;
      } else if (this.getSpawnerRange() != other.getSpawnerRange()) {
         return false;
      } else if (this.isRsGeneratePoints() != other.isRsGeneratePoints()) {
         return false;
      } else if (this.getRsMaxMobsPerChunk() != other.getRsMaxMobsPerChunk()) {
         return false;
      } else if (this.getRandomSpawningPlayerRadiusMax() != other.getRandomSpawningPlayerRadiusMax()) {
         return false;
      } else if (this.getRandomSpawningPlayerRadiusMin() != other.getRandomSpawningPlayerRadiusMin()) {
         return false;
      } else if (this.getRandomSpawningPlayerRadiusY() != other.getRandomSpawningPlayerRadiusY()) {
         return false;
      } else if (this.getRsPointsPerSecondLand() != other.getRsPointsPerSecondLand()) {
         return false;
      } else if (this.getRsPointsPerSecondAir() != other.getRsPointsPerSecondAir()) {
         return false;
      } else if (this.getRsPointsPerSecondSea() != other.getRsPointsPerSecondSea()) {
         return false;
      } else if (this.getRsPointsPerSecondLava() != other.getRsPointsPerSecondLava()) {
         return false;
      } else if (this.getRsPointsPerSecondGround() != other.getRsPointsPerSecondGround()) {
         return false;
      } else if (this.getRsMaxGenerationTimeMillis() != other.getRsMaxGenerationTimeMillis()) {
         return false;
      } else if (this.isRsDespawnVanillaOutsideRadius() != other.isRsDespawnVanillaOutsideRadius()) {
         return false;
      } else if (this.isVanillaSpawnsDisabled() != other.isVanillaSpawnsDisabled()) {
         return false;
      } else if (this.isUpdateItems() != other.isUpdateItems()) {
         return false;
      } else if (this.isCompatHeroesXPEnable() != other.isCompatHeroesXPEnable()) {
         return false;
      } else if (this.isCompatHeroesShowXPMessage() != other.isCompatHeroesShowXPMessage()) {
         return false;
      } else if (this.isCompatSkillAPIShowXPMessage() != other.isCompatSkillAPIShowXPMessage()) {
         return false;
      } else if (this.isCompatVaultShowMoneyMessage() != other.isCompatVaultShowMoneyMessage()) {
         return false;
      } else if (this.isCompatMcMMOShowXPMessage() != other.isCompatMcMMOShowXPMessage()) {
         return false;
      } else {
         Object this$allowMetrics = this.getAllowMetrics();
         Object other$allowMetrics = other.getAllowMetrics();
         if (this$allowMetrics == null ? other$allowMetrics == null : this$allowMetrics.equals(other$allowMetrics)) {
            Object this$allowUpdateChecking = this.getAllowUpdateChecking();
            Object other$allowUpdateChecking = other.getAllowUpdateChecking();
            if (this$allowUpdateChecking == null ? other$allowUpdateChecking == null : this$allowUpdateChecking.equals(other$allowUpdateChecking)) {
               Object this$useCompatibilityMode = this.getUseCompatibilityMode();
               Object other$useCompatibilityMode = other.getUseCompatibilityMode();
               if (this$useCompatibilityMode == null ? other$useCompatibilityMode == null : this$useCompatibilityMode.equals(other$useCompatibilityMode)) {
                  Object this$loadExampleConfigs = this.getLoadExampleConfigs();
                  Object other$loadExampleConfigs = other.getLoadExampleConfigs();
                  if (this$loadExampleConfigs == null ? other$loadExampleConfigs == null : this$loadExampleConfigs.equals(other$loadExampleConfigs)) {
                     Object this$fireAPIDeathEvent = this.getFireAPIDeathEvent();
                     Object other$fireAPIDeathEvent = other.getFireAPIDeathEvent();
                     if (this$fireAPIDeathEvent == null ? other$fireAPIDeathEvent == null : this$fireAPIDeathEvent.equals(other$fireAPIDeathEvent)) {
                        Object this$fireAPISkillEvent = this.getFireAPISkillEvent();
                        Object other$fireAPISkillEvent = other.getFireAPISkillEvent();
                        if (this$fireAPISkillEvent == null ? other$fireAPISkillEvent == null : this$fireAPISkillEvent.equals(other$fireAPISkillEvent)) {
                           Object this$fireAPICustomSkillEvent = this.getFireAPICustomSkillEvent();
                           Object other$fireAPICustomSkillEvent = other.getFireAPICustomSkillEvent();
                           if (this$fireAPICustomSkillEvent == null
                              ? other$fireAPICustomSkillEvent == null
                              : this$fireAPICustomSkillEvent.equals(other$fireAPICustomSkillEvent)) {
                              Object this$doErrorLogging = this.getDoErrorLogging();
                              Object other$doErrorLogging = other.getDoErrorLogging();
                              if (this$doErrorLogging == null ? other$doErrorLogging == null : this$doErrorLogging.equals(other$doErrorLogging)) {
                                 Object this$mobsDespawnByDefault = this.getMobsDespawnByDefault();
                                 Object other$mobsDespawnByDefault = other.getMobsDespawnByDefault();
                                 if (this$mobsDespawnByDefault == null
                                    ? other$mobsDespawnByDefault == null
                                    : this$mobsDespawnByDefault.equals(other$mobsDespawnByDefault)) {
                                    if (this.getPreventOtherDropsByDefault() != other.getPreventOtherDropsByDefault()) {
                                       return false;
                                    } else {
                                       Object this$skillsIgnoreArmorStandsByDefault = this.getSkillsIgnoreArmorStandsByDefault();
                                       Object other$skillsIgnoreArmorStandsByDefault = other.getSkillsIgnoreArmorStandsByDefault();
                                       if (this$skillsIgnoreArmorStandsByDefault == null
                                          ? other$skillsIgnoreArmorStandsByDefault == null
                                          : this$skillsIgnoreArmorStandsByDefault.equals(other$skillsIgnoreArmorStandsByDefault)) {
                                          Object this$settings = this.getSettings();
                                          Object other$settings = other.getSettings();
                                          if (this$settings == null ? other$settings == null : this$settings.equals(other$settings)) {
                                             Object this$config = this.getConfig();
                                             Object other$config = other.getConfig();
                                             if (this$config == null ? other$config == null : this$config.equals(other$config)) {
                                                Object this$defaultDropMethod = this.getDefaultDropMethod();
                                                Object other$defaultDropMethod = other.getDefaultDropMethod();
                                                if (this$defaultDropMethod == null
                                                   ? other$defaultDropMethod == null
                                                   : this$defaultDropMethod.equals(other$defaultDropMethod)) {
                                                   Object this$killMessagePrefix = this.getKillMessagePrefix();
                                                   Object other$killMessagePrefix = other.getKillMessagePrefix();
                                                   if (this$killMessagePrefix == null
                                                      ? other$killMessagePrefix == null
                                                      : this$killMessagePrefix.equals(other$killMessagePrefix)) {
                                                      Object this$defaultLevelModifierHealth = this.getDefaultLevelModifierHealth();
                                                      Object other$defaultLevelModifierHealth = other.getDefaultLevelModifierHealth();
                                                      if (this$defaultLevelModifierHealth == null
                                                         ? other$defaultLevelModifierHealth == null
                                                         : this$defaultLevelModifierHealth.equals(other$defaultLevelModifierHealth)) {
                                                         Object this$defaultLevelModifierDamage = this.getDefaultLevelModifierDamage();
                                                         Object other$defaultLevelModifierDamage = other.getDefaultLevelModifierDamage();
                                                         if (this$defaultLevelModifierDamage == null
                                                            ? other$defaultLevelModifierDamage == null
                                                            : this$defaultLevelModifierDamage.equals(other$defaultLevelModifierDamage)) {
                                                            Object this$defaultLevelModifierPower = this.getDefaultLevelModifierPower();
                                                            Object other$defaultLevelModifierPower = other.getDefaultLevelModifierPower();
                                                            if (this$defaultLevelModifierPower == null
                                                               ? other$defaultLevelModifierPower == null
                                                               : this$defaultLevelModifierPower.equals(other$defaultLevelModifierPower)) {
                                                               Object this$defaultLevelModifierArmor = this.getDefaultLevelModifierArmor();
                                                               Object other$defaultLevelModifierArmor = other.getDefaultLevelModifierArmor();
                                                               if (this$defaultLevelModifierArmor == null
                                                                  ? other$defaultLevelModifierArmor == null
                                                                  : this$defaultLevelModifierArmor.equals(other$defaultLevelModifierArmor)) {
                                                                  Object this$defaultLevelModifierKBR = this.getDefaultLevelModifierKBR();
                                                                  Object other$defaultLevelModifierKBR = other.getDefaultLevelModifierKBR();
                                                                  if (this$defaultLevelModifierKBR == null
                                                                     ? other$defaultLevelModifierKBR == null
                                                                     : this$defaultLevelModifierKBR.equals(other$defaultLevelModifierKBR)) {
                                                                     Object this$scalingEquationDamage = this.getScalingEquationDamage();
                                                                     Object other$scalingEquationDamage = other.getScalingEquationDamage();
                                                                     if (this$scalingEquationDamage == null
                                                                        ? other$scalingEquationDamage == null
                                                                        : this$scalingEquationDamage.equals(other$scalingEquationDamage)) {
                                                                        Object this$scalingEquationHealth = this.getScalingEquationHealth();
                                                                        Object other$scalingEquationHealth = other.getScalingEquationHealth();
                                                                        if (this$scalingEquationHealth == null
                                                                           ? other$scalingEquationHealth == null
                                                                           : this$scalingEquationHealth.equals(other$scalingEquationHealth)) {
                                                                           Object this$compatHeroesXPMessageFormat = this.getCompatHeroesXPMessageFormat();
                                                                           Object other$compatHeroesXPMessageFormat = other.getCompatHeroesXPMessageFormat();
                                                                           if (this$compatHeroesXPMessageFormat == null
                                                                              ? other$compatHeroesXPMessageFormat == null
                                                                              : this$compatHeroesXPMessageFormat.equals(other$compatHeroesXPMessageFormat)) {
                                                                              Object this$compatSkillAPIXPMessageFormat = this.getCompatSkillAPIXPMessageFormat();
                                                                              Object other$compatSkillAPIXPMessageFormat = other.getCompatSkillAPIXPMessageFormat();
                                                                              if (this$compatSkillAPIXPMessageFormat == null
                                                                                 ? other$compatSkillAPIXPMessageFormat == null
                                                                                 : this$compatSkillAPIXPMessageFormat.equals(
                                                                                    other$compatSkillAPIXPMessageFormat
                                                                                 )) {
                                                                                 Object this$compatVaultMoneyMessageFormat = this.getCompatVaultMoneyMessageFormat();
                                                                                 Object other$compatVaultMoneyMessageFormat = other.getCompatVaultMoneyMessageFormat();
                                                                                 if (this$compatVaultMoneyMessageFormat == null
                                                                                    ? other$compatVaultMoneyMessageFormat == null
                                                                                    : this$compatVaultMoneyMessageFormat.equals(
                                                                                       other$compatVaultMoneyMessageFormat
                                                                                    )) {
                                                                                    Object this$compatMcMMOXPMessageFormat = this.getCompatMcMMOXPMessageFormat();
                                                                                    Object other$compatMcMMOXPMessageFormat = other.getCompatMcMMOXPMessageFormat();
                                                                                    if (this$compatMcMMOXPMessageFormat == null
                                                                                       ? other$compatMcMMOXPMessageFormat == null
                                                                                       : this$compatMcMMOXPMessageFormat.equals(
                                                                                          other$compatMcMMOXPMessageFormat
                                                                                       )) {
                                                                                       Object this$plugin = this.getPlugin();
                                                                                       Object other$plugin = other.getPlugin();
                                                                                       return this$plugin == null
                                                                                          ? other$plugin == null
                                                                                          : this$plugin.equals(other$plugin);
                                                                                    } else {
                                                                                       return false;
                                                                                    }
                                                                                 } else {
                                                                                    return false;
                                                                                 }
                                                                              } else {
                                                                                 return false;
                                                                              }
                                                                           } else {
                                                                              return false;
                                                                           }
                                                                        } else {
                                                                           return false;
                                                                        }
                                                                     } else {
                                                                        return false;
                                                                     }
                                                                  } else {
                                                                     return false;
                                                                  }
                                                               } else {
                                                                  return false;
                                                               }
                                                            } else {
                                                               return false;
                                                            }
                                                         } else {
                                                            return false;
                                                         }
                                                      } else {
                                                         return false;
                                                      }
                                                   } else {
                                                      return false;
                                                   }
                                                } else {
                                                   return false;
                                                }
                                             } else {
                                                return false;
                                             }
                                          } else {
                                             return false;
                                          }
                                       } else {
                                          return false;
                                       }
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof LegacyConfigExecutor;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getDebugLevel();
      result = result * 59 + (this.isDebugMode() ? 79 : 97);
      result = result * 59 + (this.isDebugSpawners() ? 79 : 97);
      result = result * 59 + (this.isCancelDamageIfZero() ? 79 : 97);
      result = result * 59 + this.getClockIntervalMain();
      result = result * 59 + this.getClockIntervalRandomSpawning();
      result = result * 59 + this.getClockIntervalSaving();
      result = result * 59 + this.getSpawningInterval();
      result = result * 59 + this.getTimerupdate();
      result = result * 59 + this.getWalkupdate();
      result = result * 59 + this.getShowHealthRadius();
      result = result * 59 + this.getScanInterval();
      result = result * 59 + this.getClnrInterval();
      result = result * 59 + this.getBossBarUpdateInterval();
      result = result * 59 + (this.isEnableAIModifiers() ? 79 : 97);
      result = result * 59 + (this.isEnableThreatTables() ? 79 : 97);
      result = result * 59 + (this.isEnableTimerSkills() ? 79 : 97);
      result = result * 59 + (this.isTargetSelf() ? 79 : 97);
      result = result * 59 + (this.isTargetPlayers() ? 79 : 97);
      result = result * 59 + (this.isTargetArmorStands() ? 79 : 97);
      result = result * 59 + (this.isTargetMarkers() ? 79 : 97);
      result = result * 59 + (this.isTargetCreativeMode() ? 79 : 97);
      result = result * 59 + (this.isTargetSpectatorMode() ? 79 : 97);
      result = result * 59 + (this.isTargetCitizensNPCs() ? 79 : 97);
      result = result * 59 + (this.isTargetAnimals() ? 79 : 97);
      result = result * 59 + (this.isTargetCreatures() ? 79 : 97);
      result = result * 59 + (this.isTargetMonsters() ? 79 : 97);
      result = result * 59 + (this.isTargetWaterMobs() ? 79 : 97);
      result = result * 59 + (this.isTargetFlyingMobs() ? 79 : 97);
      result = result * 59 + (this.isTargetSameFaction() ? 79 : 97);
      result = result * 59 + (this.isTargetOwner() ? 79 : 97);
      result = result * 59 + (this.isTargetNonMythic() ? 79 : 97);
      result = result * 59 + (this.isTargetVillagers() ? 79 : 97);
      result = result * 59 + this.getSpawnerRange();
      result = result * 59 + (this.isRsGeneratePoints() ? 79 : 97);
      result = result * 59 + this.getRsMaxMobsPerChunk();
      result = result * 59 + this.getRandomSpawningPlayerRadiusMax();
      result = result * 59 + this.getRandomSpawningPlayerRadiusMin();
      result = result * 59 + this.getRandomSpawningPlayerRadiusY();
      result = result * 59 + this.getRsPointsPerSecondLand();
      result = result * 59 + this.getRsPointsPerSecondAir();
      result = result * 59 + this.getRsPointsPerSecondSea();
      result = result * 59 + this.getRsPointsPerSecondLava();
      result = result * 59 + this.getRsPointsPerSecondGround();
      result = result * 59 + this.getRsMaxGenerationTimeMillis();
      result = result * 59 + (this.isRsDespawnVanillaOutsideRadius() ? 79 : 97);
      result = result * 59 + (this.isVanillaSpawnsDisabled() ? 79 : 97);
      result = result * 59 + (this.isUpdateItems() ? 79 : 97);
      result = result * 59 + (this.isCompatHeroesXPEnable() ? 79 : 97);
      result = result * 59 + (this.isCompatHeroesShowXPMessage() ? 79 : 97);
      result = result * 59 + (this.isCompatSkillAPIShowXPMessage() ? 79 : 97);
      result = result * 59 + (this.isCompatVaultShowMoneyMessage() ? 79 : 97);
      result = result * 59 + (this.isCompatMcMMOShowXPMessage() ? 79 : 97);
      Object $allowMetrics = this.getAllowMetrics();
      result = result * 59 + ($allowMetrics == null ? 43 : $allowMetrics.hashCode());
      Object $allowUpdateChecking = this.getAllowUpdateChecking();
      result = result * 59 + ($allowUpdateChecking == null ? 43 : $allowUpdateChecking.hashCode());
      Object $useCompatibilityMode = this.getUseCompatibilityMode();
      result = result * 59 + ($useCompatibilityMode == null ? 43 : $useCompatibilityMode.hashCode());
      Object $loadExampleConfigs = this.getLoadExampleConfigs();
      result = result * 59 + ($loadExampleConfigs == null ? 43 : $loadExampleConfigs.hashCode());
      Object $fireAPIDeathEvent = this.getFireAPIDeathEvent();
      result = result * 59 + ($fireAPIDeathEvent == null ? 43 : $fireAPIDeathEvent.hashCode());
      Object $fireAPISkillEvent = this.getFireAPISkillEvent();
      result = result * 59 + ($fireAPISkillEvent == null ? 43 : $fireAPISkillEvent.hashCode());
      Object $fireAPICustomSkillEvent = this.getFireAPICustomSkillEvent();
      result = result * 59 + ($fireAPICustomSkillEvent == null ? 43 : $fireAPICustomSkillEvent.hashCode());
      Object $doErrorLogging = this.getDoErrorLogging();
      result = result * 59 + ($doErrorLogging == null ? 43 : $doErrorLogging.hashCode());
      Object $mobsDespawnByDefault = this.getMobsDespawnByDefault();
      result = result * 59 + ($mobsDespawnByDefault == null ? 43 : $mobsDespawnByDefault.hashCode());
      result = result * 59 + (this.getPreventOtherDropsByDefault() ? 79 : 97);
      Object $skillsIgnoreArmorStandsByDefault = this.getSkillsIgnoreArmorStandsByDefault();
      result = result * 59 + ($skillsIgnoreArmorStandsByDefault == null ? 43 : $skillsIgnoreArmorStandsByDefault.hashCode());
      Object $settings = this.getSettings();
      result = result * 59 + ($settings == null ? 43 : $settings.hashCode());
      Object $config = this.getConfig();
      result = result * 59 + ($config == null ? 43 : $config.hashCode());
      Object $defaultDropMethod = this.getDefaultDropMethod();
      result = result * 59 + ($defaultDropMethod == null ? 43 : $defaultDropMethod.hashCode());
      Object $killMessagePrefix = this.getKillMessagePrefix();
      result = result * 59 + ($killMessagePrefix == null ? 43 : $killMessagePrefix.hashCode());
      Object $defaultLevelModifierHealth = this.getDefaultLevelModifierHealth();
      result = result * 59 + ($defaultLevelModifierHealth == null ? 43 : $defaultLevelModifierHealth.hashCode());
      Object $defaultLevelModifierDamage = this.getDefaultLevelModifierDamage();
      result = result * 59 + ($defaultLevelModifierDamage == null ? 43 : $defaultLevelModifierDamage.hashCode());
      Object $defaultLevelModifierPower = this.getDefaultLevelModifierPower();
      result = result * 59 + ($defaultLevelModifierPower == null ? 43 : $defaultLevelModifierPower.hashCode());
      Object $defaultLevelModifierArmor = this.getDefaultLevelModifierArmor();
      result = result * 59 + ($defaultLevelModifierArmor == null ? 43 : $defaultLevelModifierArmor.hashCode());
      Object $defaultLevelModifierKBR = this.getDefaultLevelModifierKBR();
      result = result * 59 + ($defaultLevelModifierKBR == null ? 43 : $defaultLevelModifierKBR.hashCode());
      Object $scalingEquationDamage = this.getScalingEquationDamage();
      result = result * 59 + ($scalingEquationDamage == null ? 43 : $scalingEquationDamage.hashCode());
      Object $scalingEquationHealth = this.getScalingEquationHealth();
      result = result * 59 + ($scalingEquationHealth == null ? 43 : $scalingEquationHealth.hashCode());
      Object $compatHeroesXPMessageFormat = this.getCompatHeroesXPMessageFormat();
      result = result * 59 + ($compatHeroesXPMessageFormat == null ? 43 : $compatHeroesXPMessageFormat.hashCode());
      Object $compatSkillAPIXPMessageFormat = this.getCompatSkillAPIXPMessageFormat();
      result = result * 59 + ($compatSkillAPIXPMessageFormat == null ? 43 : $compatSkillAPIXPMessageFormat.hashCode());
      Object $compatVaultMoneyMessageFormat = this.getCompatVaultMoneyMessageFormat();
      result = result * 59 + ($compatVaultMoneyMessageFormat == null ? 43 : $compatVaultMoneyMessageFormat.hashCode());
      Object $compatMcMMOXPMessageFormat = this.getCompatMcMMOXPMessageFormat();
      result = result * 59 + ($compatMcMMOXPMessageFormat == null ? 43 : $compatMcMMOXPMessageFormat.hashCode());
      Object $plugin = this.getPlugin();
      return result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "LegacyConfigExecutor(settings="
         + this.getSettings()
         + ", config="
         + this.getConfig()
         + ", allowMetrics="
         + this.getAllowMetrics()
         + ", allowUpdateChecking="
         + this.getAllowUpdateChecking()
         + ", useCompatibilityMode="
         + this.getUseCompatibilityMode()
         + ", loadExampleConfigs="
         + this.getLoadExampleConfigs()
         + ", fireAPIDeathEvent="
         + this.getFireAPIDeathEvent()
         + ", fireAPISkillEvent="
         + this.getFireAPISkillEvent()
         + ", fireAPICustomSkillEvent="
         + this.getFireAPICustomSkillEvent()
         + ", debugLevel="
         + this.getDebugLevel()
         + ", debugMode="
         + this.isDebugMode()
         + ", debugSpawners="
         + this.isDebugSpawners()
         + ", doErrorLogging="
         + this.getDoErrorLogging()
         + ", cancelDamageIfZero="
         + this.isCancelDamageIfZero()
         + ", defaultDropMethod="
         + this.getDefaultDropMethod()
         + ", clockIntervalMain="
         + this.getClockIntervalMain()
         + ", clockIntervalRandomSpawning="
         + this.getClockIntervalRandomSpawning()
         + ", clockIntervalSaving="
         + this.getClockIntervalSaving()
         + ", SpawningInterval="
         + this.getSpawningInterval()
         + ", timerupdate="
         + this.getTimerupdate()
         + ", walkupdate="
         + this.getWalkupdate()
         + ", ShowHealthRadius="
         + this.getShowHealthRadius()
         + ", ScanInterval="
         + this.getScanInterval()
         + ", ClnrInterval="
         + this.getClnrInterval()
         + ", bossBarUpdateInterval="
         + this.getBossBarUpdateInterval()
         + ", EnableAIModifiers="
         + this.isEnableAIModifiers()
         + ", EnableThreatTables="
         + this.isEnableThreatTables()
         + ", killMessagePrefix="
         + this.getKillMessagePrefix()
         + ", mobsDespawnByDefault="
         + this.getMobsDespawnByDefault()
         + ", preventOtherDropsByDefault="
         + this.getPreventOtherDropsByDefault()
         + ", defaultLevelModifierHealth="
         + this.getDefaultLevelModifierHealth()
         + ", defaultLevelModifierDamage="
         + this.getDefaultLevelModifierDamage()
         + ", defaultLevelModifierPower="
         + this.getDefaultLevelModifierPower()
         + ", defaultLevelModifierArmor="
         + this.getDefaultLevelModifierArmor()
         + ", defaultLevelModifierKBR="
         + this.getDefaultLevelModifierKBR()
         + ", scalingEquationDamage="
         + this.getScalingEquationDamage()
         + ", scalingEquationHealth="
         + this.getScalingEquationHealth()
         + ", EnableTimerSkills="
         + this.isEnableTimerSkills()
         + ", skillsIgnoreArmorStandsByDefault="
         + this.getSkillsIgnoreArmorStandsByDefault()
         + ", targetSelf="
         + this.isTargetSelf()
         + ", targetPlayers="
         + this.isTargetPlayers()
         + ", targetArmorStands="
         + this.isTargetArmorStands()
         + ", targetMarkers="
         + this.isTargetMarkers()
         + ", targetCreativeMode="
         + this.isTargetCreativeMode()
         + ", targetSpectatorMode="
         + this.isTargetSpectatorMode()
         + ", targetCitizensNPCs="
         + this.isTargetCitizensNPCs()
         + ", targetAnimals="
         + this.isTargetAnimals()
         + ", targetCreatures="
         + this.isTargetCreatures()
         + ", targetMonsters="
         + this.isTargetMonsters()
         + ", targetWaterMobs="
         + this.isTargetWaterMobs()
         + ", targetFlyingMobs="
         + this.isTargetFlyingMobs()
         + ", targetSameFaction="
         + this.isTargetSameFaction()
         + ", targetOwner="
         + this.isTargetOwner()
         + ", targetNonMythic="
         + this.isTargetNonMythic()
         + ", targetVillagers="
         + this.isTargetVillagers()
         + ", SpawnerRange="
         + this.getSpawnerRange()
         + ", rsGeneratePoints="
         + this.isRsGeneratePoints()
         + ", rsMaxMobsPerChunk="
         + this.getRsMaxMobsPerChunk()
         + ", randomSpawningPlayerRadiusMax="
         + this.getRandomSpawningPlayerRadiusMax()
         + ", randomSpawningPlayerRadiusMin="
         + this.getRandomSpawningPlayerRadiusMin()
         + ", randomSpawningPlayerRadiusY="
         + this.getRandomSpawningPlayerRadiusY()
         + ", rsPointsPerSecondLand="
         + this.getRsPointsPerSecondLand()
         + ", rsPointsPerSecondAir="
         + this.getRsPointsPerSecondAir()
         + ", rsPointsPerSecondSea="
         + this.getRsPointsPerSecondSea()
         + ", rsPointsPerSecondLava="
         + this.getRsPointsPerSecondLava()
         + ", rsPointsPerSecondGround="
         + this.getRsPointsPerSecondGround()
         + ", rsMaxGenerationTimeMillis="
         + this.getRsMaxGenerationTimeMillis()
         + ", rsDespawnVanillaOutsideRadius="
         + this.isRsDespawnVanillaOutsideRadius()
         + ", vanillaSpawnsDisabled="
         + this.isVanillaSpawnsDisabled()
         + ", updateItems="
         + this.isUpdateItems()
         + ", compatHeroesXPEnable="
         + this.isCompatHeroesXPEnable()
         + ", compatHeroesShowXPMessage="
         + this.isCompatHeroesShowXPMessage()
         + ", compatHeroesXPMessageFormat="
         + this.getCompatHeroesXPMessageFormat()
         + ", compatSkillAPIShowXPMessage="
         + this.isCompatSkillAPIShowXPMessage()
         + ", compatSkillAPIXPMessageFormat="
         + this.getCompatSkillAPIXPMessageFormat()
         + ", compatVaultShowMoneyMessage="
         + this.isCompatVaultShowMoneyMessage()
         + ", compatVaultMoneyMessageFormat="
         + this.getCompatVaultMoneyMessageFormat()
         + ", compatMcMMOShowXPMessage="
         + this.isCompatMcMMOShowXPMessage()
         + ", compatMcMMOXPMessageFormat="
         + this.getCompatMcMMOXPMessageFormat()
         + ", plugin="
         + this.getPlugin()
         + ")";
   }

   @Generated
   public Boolean getAllowMetrics() {
      return this.allowMetrics;
   }

   @Generated
   public Boolean getAllowUpdateChecking() {
      return this.allowUpdateChecking;
   }

   @Generated
   public Boolean getUseCompatibilityMode() {
      return this.useCompatibilityMode;
   }

   @Generated
   public Boolean getLoadExampleConfigs() {
      return this.loadExampleConfigs;
   }

   @Generated
   public Boolean getFireAPIDeathEvent() {
      return this.fireAPIDeathEvent;
   }

   @Generated
   public Boolean getFireAPISkillEvent() {
      return this.fireAPISkillEvent;
   }

   @Generated
   public Boolean getFireAPICustomSkillEvent() {
      return this.fireAPICustomSkillEvent;
   }

   @Generated
   public int getDebugLevel() {
      return this.debugLevel;
   }

   @Generated
   public void setDebugLevel(int debugLevel) {
      this.debugLevel = debugLevel;
   }

   @Generated
   public boolean isDebugMode() {
      return this.debugMode;
   }

   @Generated
   public boolean isDebugSpawners() {
      return this.debugSpawners;
   }

   @Generated
   public Boolean getDoErrorLogging() {
      return this.doErrorLogging;
   }

   @Generated
   public boolean isCancelDamageIfZero() {
      return this.cancelDamageIfZero;
   }

   @Generated
   public DropMethod getDefaultDropMethod() {
      return this.defaultDropMethod;
   }

   @Generated
   public int getClockIntervalMain() {
      return this.clockIntervalMain;
   }

   @Generated
   public int getClockIntervalRandomSpawning() {
      return this.clockIntervalRandomSpawning;
   }

   @Generated
   public int getClockIntervalSaving() {
      return this.clockIntervalSaving;
   }

   @Generated
   public int getBossBarUpdateInterval() {
      return this.bossBarUpdateInterval;
   }

   @Generated
   public String getKillMessagePrefix() {
      return this.killMessagePrefix;
   }

   @Generated
   public Boolean getMobsDespawnByDefault() {
      return this.mobsDespawnByDefault;
   }

   @Generated
   public String getScalingEquationDamage() {
      return this.scalingEquationDamage;
   }

   @Generated
   public String getScalingEquationHealth() {
      return this.scalingEquationHealth;
   }

   @Generated
   public Boolean getSkillsIgnoreArmorStandsByDefault() {
      return this.skillsIgnoreArmorStandsByDefault;
   }

   @Generated
   public boolean isTargetSelf() {
      return this.targetSelf;
   }

   @Generated
   public boolean isTargetPlayers() {
      return this.targetPlayers;
   }

   @Generated
   public boolean isTargetArmorStands() {
      return this.targetArmorStands;
   }

   @Generated
   public boolean isTargetMarkers() {
      return this.targetMarkers;
   }

   @Generated
   public boolean isTargetCreativeMode() {
      return this.targetCreativeMode;
   }

   @Generated
   public boolean isTargetSpectatorMode() {
      return this.targetSpectatorMode;
   }

   @Generated
   public boolean isTargetCitizensNPCs() {
      return this.targetCitizensNPCs;
   }

   @Generated
   public boolean isTargetAnimals() {
      return this.targetAnimals;
   }

   @Generated
   public boolean isTargetCreatures() {
      return this.targetCreatures;
   }

   @Generated
   public boolean isTargetMonsters() {
      return this.targetMonsters;
   }

   @Generated
   public boolean isTargetWaterMobs() {
      return this.targetWaterMobs;
   }

   @Generated
   public boolean isTargetFlyingMobs() {
      return this.targetFlyingMobs;
   }

   @Generated
   public boolean isTargetSameFaction() {
      return this.targetSameFaction;
   }

   @Generated
   public boolean isTargetOwner() {
      return this.targetOwner;
   }

   @Generated
   public boolean isTargetNonMythic() {
      return this.targetNonMythic;
   }

   @Generated
   public boolean isTargetVillagers() {
      return this.targetVillagers;
   }

   @Generated
   public int getRandomSpawningPlayerRadiusMax() {
      return this.randomSpawningPlayerRadiusMax;
   }

   @Generated
   public int getRandomSpawningPlayerRadiusMin() {
      return this.randomSpawningPlayerRadiusMin;
   }

   @Generated
   public int getRandomSpawningPlayerRadiusY() {
      return this.randomSpawningPlayerRadiusY;
   }

   @Generated
   public boolean isVanillaSpawnsDisabled() {
      return this.vanillaSpawnsDisabled;
   }

   @Generated
   public MythicBukkit getPlugin() {
      return this.plugin;
   }

   static {
      for (Material m : Material.values()) {
         if (m.isTransparent() || !m.isSolid() || !m.isOccluding()) {
            transparentBlocks.add(m);
         }
      }
   }
}
