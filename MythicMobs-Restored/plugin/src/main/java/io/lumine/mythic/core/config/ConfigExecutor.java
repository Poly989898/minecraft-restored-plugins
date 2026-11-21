package io.lumine.mythic.core.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.ConfigManager;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.IPlaceholder;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.api.skills.stats.StatManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.CrucibleSupport;
import io.lumine.mythic.bukkit.utils.config.ConfigMerger;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ChromaProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.DoubleProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.EnumProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.MaterialProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.Node2StringProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.files.Folders;
import io.lumine.mythic.bukkit.utils.logging.ConsoleColor;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.tasks.LumineAsyncExecutor;
import io.lumine.mythic.bukkit.utils.tasks.LumineExecutors;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderAngleImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderBooleanImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderDoubleImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderDurationImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderFloatImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderIntImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderLongImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderTimeImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderVectorImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PolymorphicPlaceholderImpl;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.spawning.random.GeneratorType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display.Billboard;

public class ConfigExecutor extends ReloadableModule<MythicBukkit> implements ConfigManager, PropertyHolder {
   private static final DoubleProp VERSION = Property.Double(Scope.CONFIG_GENERAL, "Version");
   private static final BooleanProp GENERAL_ALLOW_METRICS = Property.Boolean(Scope.CONFIG_GENERAL, "General.AllowMetrics", true);
   private static final BooleanProp GENERAL_CHECK_UPDATES = Property.Boolean(Scope.CONFIG_GENERAL, "General.CheckForUpdates", true);
   private static final BooleanProp GENERAL_COMPATIB_MODE = Property.Boolean(Scope.CONFIG_GENERAL, "General.CompatibilityMode", false);
   private static final BooleanProp GENERAL_ERROR_LOGGING = Property.Boolean(Scope.CONFIG_GENERAL, "General.ErrorLogging", true);
   private static final BooleanProp GENERAL_LOAD_EXAMPLES = Property.Boolean(Scope.CONFIG_GENERAL, "General.LoadExampleConfigs", true);
   private static final StringProp GENERAL_LANGUAGE = Property.String(Scope.CONFIG_GENERAL, "General.Language", "en-us");
   private static final IntProp DEBUG_LEVEL = Property.Int(Scope.CONFIG_GENERAL, "General.DebugLevel", 0);
   private static final IntProp THREAD_POOL = Property.Int(Scope.CONFIG_GENERAL, "General.ThreadPoolSize", -1);
   private static final BooleanProp USE_VIRTUAL_THREADS = Property.Boolean(Scope.CONFIG_GENERAL, "General.UseVirtualThreads", false);
   private static final BooleanProp ASYNC_PATHFINDING_ENABLED = Property.Boolean(Scope.CONFIG_GENERAL, "General.AsyncPathfinding.Enabled", true);
   private static final IntProp ASYNC_PATHFINDING_THREADS = Property.Int(Scope.CONFIG_GENERAL, "General.AsyncPathfinding.Threads", 0);
   private static final BooleanProp DEBUG_MODE = Property.Boolean(Scope.CONFIG_GENERAL, "General.DebugMode", false);
   private static final BooleanProp DEBUG_SPAWNERS = Property.Boolean(Scope.CONFIG_GENERAL, "General.DebugSpawners", false);
   private static final BooleanProp ANNOUNCE_OP_RELOAD = Property.Boolean(Scope.CONFIG_GENERAL, "General.AnnounceOpReload", false);
   private static final IntProp CLOCK_SPEED = Property.Int(Scope.CONFIG_GENERAL, "Clock.Main", 1);
   private static final IntProp CLOCK_BARS = Property.Int(Scope.CONFIG_GENERAL, "Clock.Bars", 20);
   private static final IntProp CLOCK_SAVING = Property.Int(Scope.CONFIG_GENERAL, "Clock.Saving", 300);
   private static final IntProp CLOCK_SPAWNS = Property.Int(Scope.CONFIG_GENERAL, "Clock.Spawners", 2);
   private static final IntProp CLOCK_RANDOM_SPAWNING = Property.Int(Scope.CONFIG_GENERAL, "Clock.RandomSpawning", 1);
   private static final IntProp CLOCK_SCANNER = Property.Int(Scope.CONFIG_GENERAL, "Clock.Scanner", 10);
   private static final IntProp CLOCK_CLEANUP = Property.Int(Scope.CONFIG_GENERAL, "Clock.Cleanup", 600);
   private static final BooleanProp COMPAT_HEROES_XP_ENABLE = Property.Boolean(Scope.CONFIG_GENERAL, "Compatibility.Heroes.Enabled", false);
   private static final BooleanProp COMPAT_HEROES_SHOW_XP_MESSAGE = Property.Boolean(Scope.CONFIG_GENERAL, "Compatibility.Heroes.ShowXPMessage", false);
   private static final StringProp COMPAT_HEROES_XP_MESSAGE_FORMAT = Property.String(Scope.CONFIG_GENERAL, "Compatibility.Heroes.XPMessageFormat", "");
   private static final BooleanProp COMPAT_MCMMO_SHOW_XP_MESSAGE = Property.Boolean(Scope.CONFIG_GENERAL, "Compatibility.McMMO.ShowXPMessage", false);
   private static final StringProp COMPAT_MCMMO_XP_MESSAGE_FORMAT = Property.String(Scope.CONFIG_GENERAL, "Compatibility.McMMO.XPMessageFormat", "");
   private static final BooleanProp COMPAT_SKILLAPI_SHOW_XP_MESSAGE = Property.Boolean(Scope.CONFIG_GENERAL, "Compatibility.SkillAPI.ShowXPMessage", false);
   private static final StringProp COMPAT_SKILLAPI_XP_MESSAGE_FORMAT = Property.String(Scope.CONFIG_GENERAL, "Compatibility.SkillAPI.XPMessageFormat", "");
   private static final BooleanProp COMPAT_VAULT_SHOW_MONEY_MESSAGE = Property.Boolean(Scope.CONFIG_GENERAL, "Compatibility.Vault.ShowMoneyMessage", false);
   private static final StringProp COMPAT_VAULT_MONEY_MESSAGE_FORMAT = Property.String(Scope.CONFIG_GENERAL, "Compatibility.Vault.MoneyMessageFormat", "");
   private static final DoubleProp PACKET_MIN_POS_DELTA = Property.Double(Scope.CONFIG_GENERAL, "Packet.MinimumPositionDelta", 7.6293945E-6F);
   private static final BooleanProp PACKET_PARTICLE_BUNDLE_ENABLED = Property.Boolean(Scope.CONFIG_GENERAL, "Packet.ParticleBundleEnabled", true);
   private static final IntProp PACKET_PARTICLE_BUNDLE_SIZE = Property.Int(Scope.CONFIG_GENERAL, "Packet.ParticleBundleSize", 512);
   private static final BooleanProp PACKET_PARTICLE_TICK_BATCH_ENABLED = Property.Boolean(Scope.CONFIG_GENERAL, "Packet.ParticleTickBatchEnabled", true);
   private static final BooleanProp PACKET_RESPECT_CLIENT_PARTICLE_VIS = Property.Boolean(Scope.CONFIG_GENERAL, "Packet.RespectClientParticleVisibility", false);
   private static final IntProp PACKET_PARTICLE_VIS_ALL = Property.Int(Scope.CONFIG_GENERAL, "Packet.ClientParticleVisibility.All", 100);
   private static final IntProp PACKET_PARTICLE_VIS_DECREASED = Property.Int(Scope.CONFIG_GENERAL, "Packet.ClientParticleVisibility.Decreased", 50);
   private static final IntProp PACKET_PARTICLE_VIS_MINIMAL = Property.Int(Scope.CONFIG_GENERAL, "Packet.ClientParticleVisibility.Minimal", 10);
   private static final BooleanProp CANCEL_DAMAGE_IF_0 = Property.Boolean(Scope.CONFIG_MOBS, "Mobs.CancelDamageIfZero", false);
   private static final StringProp MOBS_KILL_MESSAGE_PREFIX = Property.String(Scope.CONFIG_MOBS, "Mobs.KillMessagePrefix", null);
   private static final StringProp DROPS_DEFAULT_DROPMETHOD = Property.String(Scope.CONFIG_MOBS, "MobDrops.DefaultDropMethod", "VANILLA");
   private static final BooleanProp DROPS_DEFAULT_PERPLAYER = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoPerPlayerDropsByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_LOOTSPLOSION = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoLootsplosionByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_CLIENTSIDE = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoClientsideDropsByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_NAME_HOLOGRAM = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoHologramNameByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_ITEM_GLOW = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoItemGlowByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_ITEM_BEAM = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoItemBeamByDefault", false);
   private static final BooleanProp DROPS_DEFAULT_ITEM_VFX = Property.Boolean(Scope.CONFIG_MOBS, "MobDrops.DoItemVFXByDefault", false);
   private static final MaterialProp DROPS_DEFAULT_ITEM_VFX_MATERIAL = Property.Material(Scope.CONFIG_MOBS, "MobDrops.DefaultItemVFX.Material", Material.POTION);
   private static final IntProp DROPS_DEFAULT_ITEM_VFX_MODEL = Property.Int(Scope.CONFIG_MOBS, "MobDrops.DefaultItemVFX.Model", 1);
   private static final StringProp DROPS_DEFAULT_ITEM_VFX_GENERATION = Property.String(Scope.CONFIG_MOBS, "MobDrops.DefaultItemVFX.Generation", null);
   private static final StringListProp DROPS_DEFAULT_DEATH_HOLOGRAM = Property.StringList(Scope.CONFIG_MOBS, "MobDrops.DefaultDeathHologram");
   private static final IntProp DROPS_DEFAULT_LEADERBOARD_TIMEOUT = Property.Int(Scope.CONFIG_MOBS, "MobDrops.DefaultLeaderboardTimeout", 6000);
   private static final StringListProp DROPS_DEFAULT_CHAT_MESSAGE = Property.StringList(Scope.CONFIG_MOBS, "MobDrops.DefaultDeathChatMessage");
   private static final StringProp DROPS_PER_PLAYER_PERCENT = Property.String(Scope.CONFIG_MOBS, "MobDrops.MinimumDamagePercentForDrops", "1%");
   private static final BooleanProp MOBS_DESPAWN_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.Despawn", true);
   private static final BooleanProp PREVENT_OTHER_DROPS_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventOtherDrops", false);
   private static final BooleanProp PREVENT_VANILLA_DAMAGE_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventVanillaDamage", false);
   private static final IntProp NO_DAMAGE_TICKS_BY_DEFAULT = Property.Int(Scope.CONFIG_MOBS, "DefaultMobOptions.NoDamageTicks", 10);
   private static final BooleanProp PREVENT_JOCKEY_MOUNTS_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventJockeyMounts", false);
   private static final BooleanProp PREVENT_CONVERSION_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventConversion", false);
   private static final BooleanProp PREVENT_LEASHING_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventLeashing", true);
   private static final BooleanProp PREVENT_ITEM_PICKUP_BY_DEFAULT = Property.Boolean(Scope.CONFIG_MOBS, "DefaultMobOptions.PreventItemPickup", true);
   private static final StringProp EGG_DEFAULT_ITEM_DISPLAY = Property.String(Scope.CONFIG_MOBS, "MobEggs.DefaultDisplay", "<mob.name> Spawn Egg");
   private static final MaterialProp EGG_DEFAULT_ITEM_MATERIAL = Property.Material(Scope.CONFIG_MOBS, "MobEggs.Material", Material.PIG_SPAWN_EGG);
   private static final StringProp EGG_DEFAULT_ITEM_MODEL = Property.String(Scope.CONFIG_MOBS, "MobEggs.Model", null);
   private static final StringListProp EGG_DEFAULT_ITEM_LORE = Property.StringList(
      Scope.CONFIG_MOBS,
      "MobEggs.DefaultLore",
      Lists.newArrayList(
         new String[]{"<dark_gray>A Mythical Egg that can</dark_gray>", "<dark_gray>be used to resurrect a</dark_gray>", "<dark_gray><mob.name></dark_gray>"}
      )
   );
   private static final DoubleProp HOLOGRAM_GLOBAL_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.GlobalOffset", 0.25);
   private static final ChromaProp HOLOGRAM_CASTBAR_INFO_BACKGROUND = Property.Chroma(
      Scope.CONFIG_MOBS, "Holograms.CastBar.InfoBackground", Chroma.of(0, 0, 0, 0)
   );
   private static final StringProp HOLOGRAM_CASTBAR_INFO_BILLBOARDING = Property.String(Scope.CONFIG_MOBS, "Holograms.CastBar.InfoBillboarding", "VERTICAL");
   private static final DoubleProp HOLOGRAM_CASTBAR_INFO_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.CastBar.InfoOffset", 1.0);
   private static final ChromaProp HOLOGRAM_CASTBAR_CAST_BACKGROUND = Property.Chroma(
      Scope.CONFIG_MOBS, "Holograms.CastBar.CastBackground", Chroma.of(0, 0, 0, 0)
   );
   private static final StringProp HOLOGRAM_CASTBAR_CAST_BILLBOARDING = Property.String(Scope.CONFIG_MOBS, "Holograms.CastBar.CastBillboarding", "VERTICAL");
   private static final DoubleProp HOLOGRAM_CASTBAR_CAST_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.CastBar.CastOffset", 1.0);
   private static final ChromaProp HOLOGRAM_HEALTHBAR_BACKGROUND = Property.Chroma(Scope.CONFIG_MOBS, "Holograms.HealthBar.Background", Chroma.of(0, 0, 0, 0));
   private static final StringProp HOLOGRAM_HEALTHBAR_BILLBOARDING = Property.String(Scope.CONFIG_MOBS, "Holograms.HealthBar.Billboarding", "VERTICAL");
   private static final DoubleProp HOLOGRAM_HEALTHBAR_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.HealthBar.Offset", -0.05);
   private static final IntProp HOLOGRAM_HEALTHBAR_LENGTH = Property.Int(Scope.CONFIG_MOBS, "Holograms.HealthBar.Length", 50);
   private static final StringProp HOLOGRAM_HEALTHBAR_SCALE = Property.String(Scope.CONFIG_MOBS, "Holograms.HealthBar.Scale", "0.4,0.4,0.4");
   private static final ChromaProp HOLOGRAM_NAMEPLATE_BACKGROUND = Property.Chroma(Scope.CONFIG_MOBS, "Holograms.Nameplate.Background", Chroma.of(0, 0, 0, 60));
   private static final StringProp HOLOGRAM_NAMEPLATE_BILLBOARDING = Property.String(Scope.CONFIG_MOBS, "Holograms.Nameplate.Billboarding", "VERTICAL");
   private static final DoubleProp HOLOGRAM_NAMEPLATE_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.Nameplate.Offset", 0.0);
   private static final StringProp HOLOGRAM_NAMEPLATE_SCALE = Property.String(Scope.CONFIG_MOBS, "Holograms.Nameplate.Scale", "1,1,1");
   private static final ChromaProp HOLOGRAM_SPEECH_BACKGROUND = Property.Chroma(Scope.CONFIG_MOBS, "Holograms.Speech.Background", Chroma.of(255, 255, 255, 100));
   private static final StringProp HOLOGRAM_SPEECH_BILLBOARDING = Property.String(Scope.CONFIG_MOBS, "Holograms.Speech.Billboarding", "VERTICAL");
   private static final DoubleProp HOLOGRAM_SPEECH_OFFSET = Property.Double(Scope.CONFIG_MOBS, "Holograms.Speech.Offset", 0.4);
   private static final StringProp HOLOGRAM_SPEECH_PREFIX = Property.String(Scope.CONFIG_MOBS, "Holograms.Speech.LinePrefix", "<white>");
   private static final StringProp SCALING_EQUATION_DAMAGE = Property.String(Scope.CONFIG_MOBS, "MobLeveling.ScalingEquations.Damage", null);
   private static final StringProp SCALING_EQUATION_HEALTH = Property.String(Scope.CONFIG_MOBS, "MobLeveling.ScalingEquations.Health", null);
   private static final StringProp SCALING_EQUATION_SCALE = Property.String(Scope.CONFIG_MOBS, "MobLeveling.ScalingEquations.Scale", null);
   private static final Node2StringProp SCALING_EQUATIONS = Property.Node2String(Scope.CONFIG_MOBS, "MobLeveling.ScalingEquations");
   private static final Node2StringProp DEFAULT_LEVEL_MODIFIERS = Property.Node2String(Scope.CONFIG_MOBS, "MobLeveling.DefaultLevelModifiers");
   private static final NodeListProp WORLD_SCALING_NODES = Property.NodeList(Scope.CONFIG_MOBS, "MobLeveling.WorldScaling");
   private static final BooleanProp UPDATE_ITEMS = Property.Boolean(Scope.CONFIG_ITEMS, "ItemUpdating.Enabled", true);
   private static final StringProp ITEM_DEFAULT_MATERIAL = Property.String(Scope.CONFIG_ITEMS, "DefaultItemOptions.Material", "STONE");
   private static final StringProp ITEM_DEFAULT_PREVENTENCHANTING = Property.String(Scope.CONFIG_ITEMS, "DefaultItemOptions.PreventEnchanting", "false");
   private static final StringProp ITEM_DEFAULT_PREVENTCRAFTING = Property.String(Scope.CONFIG_ITEMS, "DefaultItemOptions.PreventCrafting", "modeled");
   private static final StringProp ITEM_DEFAULT_PREVENTANVILING = Property.String(Scope.CONFIG_ITEMS, "DefaultItemOptions.PreventAnviling", "default");
   private static final StringProp ITEM_DEFAULT_PREVENTSMITHING = Property.String(Scope.CONFIG_ITEMS, "DefaultItemOptions.PreventSmithing", "default");
   private static final BooleanProp TARGET_SELF = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetSelf", false);
   private static final BooleanProp TARGET_PLAYERS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetPlayers", true);
   private static final BooleanProp TARGET_ARMOR_STANDS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetArmorStands", false);
   private static final BooleanProp TARGET_MARKERS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetMarkers", false);
   private static final BooleanProp TARGET_TEXT_DISPLAYS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetTextDisplays", true);
   private static final BooleanProp TARGET_ITEM_DISPLAYS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetItemDisplays", true);
   private static final BooleanProp TARGET_BLOCK_DISPLAYS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetBlockDisplays", true);
   private static final BooleanProp TARGET_INTERACTIONS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetInteractions", true);
   private static final BooleanProp TARGET_CREATIVE_MODE = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetCreativeMode", true);
   private static final BooleanProp TARGET_SPECTATOR_MODE = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetSpectatorMode", true);
   private static final BooleanProp TARGET_INVULNERABLE = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetInvulnerable", true);
   private static final BooleanProp TARGET_CITIZENS_NPCS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetCitizensNPCs", false);
   private static final BooleanProp TARGET_ANIMALS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetAnimals", true);
   private static final BooleanProp TARGET_CREATURES = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetCreatures", true);
   private static final BooleanProp TARGET_MONSTERS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetMonsters", true);
   private static final BooleanProp TARGET_WATER_MOBS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetWaterMobs", true);
   private static final BooleanProp TARGET_FLYING_MOBS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetFlyingMobs", true);
   private static final BooleanProp TARGET_SAME_FACTION = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetSameFaction", true);
   private static final BooleanProp TARGET_OWNER = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetOwner", true);
   private static final BooleanProp TARGET_NON_MYTHIC = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetNonMythic", true);
   private static final BooleanProp TARGET_VILLAGERS = Property.Boolean(Scope.CONFIG_SKILLS, "Targeters.DefaultEntityFilters.TargetVillagers", true);
   private static final BooleanProp SPAWNERS_DISABLE_SAVING = Property.Boolean(Scope.CONFIG_SPAWNING, "Spawners.DisableCommandSaving", false);
   private static final EnumProp<GeneratorType> RS_GENERATOR = Property.Enum(
      Scope.CONFIG_SPAWNING, GeneratorType.class, "RandomSpawning.Generator", GeneratorType.NONE
   );
   private static final BooleanProp RS_GENERATE_POINTS = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.GenerateSpawnPoints", false);
   private static final IntProp RANDOM_SPAWNING_PLAYER_RADIUS_MAX = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.SpawnRadiusPerPlayer", 64);
   private static final IntProp RANDOM_SPAWNING_PLAYER_RADIUS_MIN = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.SpawnRadiusPerPlayerMin", 12);
   private static final IntProp RANDOM_SPAWNING_PLAYER_RADIUS_Y = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.SpawnRadiusPerPlayerY", 16);
   private static final DoubleProp RS_GROUP_DISTANCE = Property.Double(Scope.CONFIG_SPAWNING, "RandomSpawning.PlayerClusterDistance", 24.0);
   private static final DoubleProp RS_LIMIT_MULTIPLIER = Property.Double(Scope.CONFIG_SPAWNING, "RandomSpawning.LimitMultiplier", 1.2);
   private static final DoubleProp RS_GROUP_MULTIPLIER = Property.Double(Scope.CONFIG_SPAWNING, "RandomSpawning.LocalGroupMultiplier", 0.1);
   private static final IntProp RS_LIMIT = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.SpawningLimit", -1);
   private static final IntProp RS_LOCAL_LIMIT = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.LocalSpawningLimit", -1);
   private static final IntProp RS_POINTS_PER_SECOND_LAND = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.PointsPerSecond.Land", 10);
   private static final IntProp RS_POINTS_PER_SECOND_AIR = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.PointsPerSecond.Air", 1);
   private static final IntProp RS_POINTS_PER_SECOND_SEA = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.PointsPerSecond.Sea", 1);
   private static final IntProp RS_POINTS_PER_SECOND_LAVA = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.PointsPerSecond.Lava", 0);
   private static final IntProp RS_POINTS_PER_SECOND_GROUND = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.PointsPerSecond.Ground", 0);
   private static final BooleanProp RS_DESPAWN_VANILLA_OUTSIDE_RADIUS = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.DespawnLazyRandomMobs", true);
   private static final IntProp RS_MAX_GENERATION_TIME_MILLIS = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.MaxGenerationTime", 5);
   private static final IntProp RS_MAX_GENERATION_ATTEMPTS = Property.Int(Scope.CONFIG_SPAWNING, "RandomSpawning.MaxGenerationAttempts", 10);
   private static final BooleanProp VANILLA_SPAWNS_DISABLED = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.DisableVanillaSpawns", false);
   private static final BooleanProp REPLACE_OBEYS_CAP = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.ReplaceObeysCap", false);
   private static final BooleanProp RS_SHARED_COOLDOWN = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.SharedCooldown", true);
   private static final BooleanProp REPLACE_IGNORES_VANILLA = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.IgnoreVanillaMobs", false);
   private static final BooleanProp REPLACE_IGNORES_UNNATURAL = Property.Boolean(Scope.CONFIG_SPAWNING, "RandomSpawning.IgnoreUnnaturalMobs", false);
   private static final BooleanProp ITEMS_ENABLE_FANCY_DROPS = Property.Boolean(Scope.CONFIG_ITEMS, "EnableFancyDropEffectsEverywhere", false);
   private final String propertyNode = "Configuration";
   @Getter
   private Boolean allowMetrics = true;
   @Getter
   private Boolean allowUpdateChecking = true;
   @Getter
   private Boolean useCompatibilityMode = false;
   @Getter
   private Boolean loadExampleConfigs = true;
   @Getter
   private String language = "en-us";
   @Getter
   private boolean cacheComponents = false;
   @Getter
   private boolean asyncPathfindingEnabled = true;
   @Getter
   private int asyncPathfindingThreads = 0;
   @Getter
   private Boolean fireAPIDeathEvent = true;
   @Getter
   private Boolean fireAPISkillEvent = true;
   @Getter
   private Boolean fireAPICustomSkillEvent = true;
   @Getter @Setter
   private int debugLevel = -1;
   @Getter
   private boolean debugMode = false;
   @Getter
   private boolean debugSpawners = false;
   @Getter
   private Boolean doErrorLogging = true;
   @Getter
   private boolean cancelDamageIfZero = false;
   @Getter
   private DropMethod defaultDropMethod = DropMethod.VANILLA;
   @Getter
   private Boolean announceOpReload = false;
   @Getter
   private double packetMinimumPositionDelta = 7.6293945E-6F;
   @Getter
   private boolean particleBundleEnabled = true;
   @Getter
   private int particleBundleSize = 512;
   @Getter
   private boolean particleTickBatchEnabled = true;
   @Getter
   private boolean respectClientParticleVisibility = false;
   @Getter
   private int particleVisibilityAll = 100;
   @Getter
   private int particleVisibilityDecreased = 50;
   @Getter
   private int particleVisibilityMinimal = 10;
   @Getter
   private Material menuIconGenerationMaterial = Material.PAPER;
   private int menuIconGenerationStartingModel = 88000000;
   private int menuIconGenerationModel = 88000000;
   @Getter
   private int clockIntervalMain = 1;
   @Getter
   private int clockBarUpdating = 20;
   @Getter
   private int clockIntervalRandomSpawning = 1;
   @Getter
   private int clockIntervalSaving = 300;
   @Getter
   private int clockSpawningInterval = 2;
   @Getter
   private int clockScanningInterval = 10;
   @Getter
   private int clockCleaningInterval = 600;
   @Getter
   private String killMessagePrefix = "";
   @Getter
   private Boolean mobsDespawnByDefault = false;
   @Getter
   private Boolean preventOtherDropsByDefault = false;
   @Getter
   private Boolean preventVanillaDamageByDefault = false;
   @Getter
   private int noDamageTicksByDefault = 10;
   @Getter
   private Boolean preventJockeyMountsByDefault = false;
   @Getter
   private Boolean preventConversionByDefault = false;
   @Getter
   private Boolean preventLeashingByDefault = true;
   @Getter
   private Boolean preventItemPickupByDefault = true;
   @Getter
   private Map<String, Expression> statScalingEquations = Maps.newConcurrentMap();
   @Getter
   private Map<String, String> statDefaultLevelModifiers = Maps.newConcurrentMap();
   @Getter
   private Expression scalingEquationDamage = null;
   @Getter
   private Expression scalingEquationHealth = null;
   @Getter
   private Expression scalingEquationScale = null;
   @Getter
   private boolean mobDropsPerPlayer;
   @Getter
   private boolean mobDropsDoLoopslosion;
   @Getter
   private boolean mobDropsAreClientside;
   @Getter
   private boolean mobDropsDoNameHologram;
   @Getter
   private boolean mobDropsDoItemGlow;
   @Getter
   private boolean mobDropsDoItemBeam;
   @Getter
   private boolean mobDropsDoItemVFX;
   @Getter
   private Material mobDropsItemVFXMaterial;
   @Getter
   private int mobDropsItemVFXModel;
   @Getter
   private String mobDropsItemVFXGeneration;
   @Getter
   private List<String> mobDrosDefaultDeathHologram;
   @Getter
   private Integer hologramLeaderboardTimeout;
   @Getter
   private List<String> mobDropsDefaultChatMessage;
   @Getter
   private double mobDropsPerPlayerRequiredDamagePercent;
   @Getter
   private AbstractItemStack defaultMobEggItem;
   @Getter
   private PlaceholderString defaultMobEggDisplay;
   @Getter
   private PlaceholderString defaultMobEggModel;
   @Getter
   private List<PlaceholderString> defaultMobEggLore;
   @Getter
   private float globalMountedHologramOffset = 0.0F;
   @Getter
   private Color hologramCastingBarBackgroundColor;
   @Getter
   private Billboard hologramCastingBarBillboarding;
   @Getter
   private float hologramCastingBarOffset;
   @Getter
   private Color hologramCastingInfoBackgroundColor;
   @Getter
   private Billboard hologramCastingInfoBillboarding;
   @Getter
   private float hologramCastingInfoOffset;
   @Getter
   private Color hologramHealthBarBackgroundColor;
   @Getter
   private Billboard hologramHealthBarBillboarding;
   @Getter
   private float hologramHealthBarOffset;
   @Getter
   private int hologramHealthBarLength;
   @Getter
   private AbstractVector hologramHealthBarScale;
   @Getter
   private Color hologramNameplateBackgroundColor;
   @Getter
   private Billboard hologramNameplateBillboarding;
   @Getter
   private float hologramNameplateOffset;
   @Getter
   private String hologramNameplateScale;
   @Getter
   private Color hologramSpeechBackgroundColor;
   @Getter
   private Billboard hologramSpeechBillboarding;
   @Getter
   private float hologramSpeechOffset;
   @Getter
   private String hologramSpeechLinePrefix;
   @Getter
   private static boolean EnableTimerSkills = true;
   @Getter
   private Boolean skillsIgnoreArmorStandsByDefault = true;
   @Getter
   public static boolean targetSelf = false;
   @Getter
   public static boolean targetPlayers = true;
   @Getter
   public static boolean targetArmorStands = false;
   @Getter
   public static boolean targetMarkers = false;
   @Getter
   public static boolean targetTextDisplays = true;
   @Getter
   public static boolean targetItemDisplays = true;
   @Getter
   public static boolean targetBlockDisplays = true;
   @Getter
   public static boolean targetInteractions = true;
   @Getter
   public static boolean targetCreativeMode = true;
   @Getter
   public static boolean targetSpectatorMode = true;
   @Getter
   public static boolean targetInvulnerable = true;
   @Getter
   public static boolean targetCitizensNPCs = false;
   @Getter
   public static boolean targetAnimals = true;
   @Getter
   public static boolean targetCreatures = true;
   @Getter
   public static boolean targetMonsters = true;
   @Getter
   public static boolean targetWaterMobs = true;
   @Getter
   public static boolean targetFlyingMobs = true;
   @Getter
   public static boolean targetSameFaction = true;
   @Getter
   public static boolean targetOwner = true;
   @Getter
   public static boolean targetNonMythic = true;
   @Getter
   public static boolean targetVillagers = true;
   public static int SpawnerRange = 40;
   @Getter
   private boolean spawnersDisableSaving = false;
   @Getter
   private GeneratorType randomSpawningGeneratorType;
   @Getter
   private boolean randomSpawningGenerationEnabled = false;
   @Getter
   private int randomSpawningPlayerRadiusMax = 64;
   @Getter
   private int randomSpawningPlayerRadiusMin = 16;
   @Getter
   private int randomSpawningPlayerRadiusY = 16;
   @Getter
   private double randomSpawningGroupDistance = 24.0;
   @Getter
   private double randomSpawningLimitMultiplier = 1.2;
   @Getter
   private double randomSpawningGroupMultiplier = 0.1;
   @Getter
   private int randomSpawningLimit = -1;
   @Getter
   private int randomSpawningLocalLimit = -1;
   @Getter
   private boolean randomSpawningIgnoresVanilla = false;
   @Getter
   private boolean randomSpawningIgnoresUnnatural = false;
   @Getter
   private boolean randomSpawningReplaceObeysCap = false;
   @Getter
   private boolean randomSpawningSharedCooldown = true;
   @Getter
   private double randomSpawningTargetPercent = 0.5;
   @Getter
   private int randomSpawningPointsPerSecond = 10;
   @Getter
   private int randomSpawningPointsPerSecondAir = 0;
   @Getter
   private int randomSpawningPointsPerSecondSea = 1;
   @Getter
   private int randomSpawningPointsPerSecondLava = 0;
   @Getter
   private int randomSpawningPointsPerSecondGround = 0;
   @Getter
   private int randomSpawningMaxGenerationTimeMillis = 5;
   @Getter
   private int randomSpawningMaxGenerationAttempts = 10;
   @Getter
   private boolean randomSpawningDespawnVanillaOutsideRadius = false;
   @Getter
   private boolean vanillaSpawnsDisabled = false;
   public static boolean updateItems = true;
   @Getter
   public static String itemDefaultMaterial = "STONE";
   @Getter
   public static String itemDefaultPreventCrafting = "modeled";
   @Getter
   public boolean fancyDropsEnabledEverywhere = false;
   public static boolean compatHeroesXPEnable = false;
   public static boolean compatHeroesShowXPMessage = true;
   public static String compatHeroesXPMessageFormat = "";
   public static boolean compatSkillAPIShowXPMessage = true;
   public static String compatSkillAPIXPMessageFormat = "";
   public static boolean compatVaultShowMoneyMessage = true;
   public static String compatVaultMoneyMessageFormat = "";
   public static boolean compatMcMMOShowXPMessage = true;
   public static String compatMcMMOXPMessageFormat = "";
   private final MythicLang languageManager;
   private final Map<Class<? extends IPlaceholder>, Map<String, IPlaceholder>> placeholderStringCache = new ConcurrentHashMap<>();
   private static HashSet<Material> transparentBlocks = new HashSet<>();

   public ConfigExecutor(MythicBukkit core) {
      super(core, false);
      this.languageManager = new MythicLang(core);
   }

   public void firstLoad() {
      this.getConfigFolder();
      this.generateConfigFiles();
      this.debugLevel = DEBUG_LEVEL.get(this);
      this.loadConfigSettings();
   }

   public void load(MythicBukkit plugin) {
      plugin.getClock().setReloadingPause(true);
      plugin.getClock().deferPostReload();
      this.getConfigFolder();
      this.generateConfigFiles();
      this.loadConfigSettings();

      try {
         plugin.reloadConfiguration();
         plugin.getPackManager().loadPacks();
         plugin.getPlaceholderManager().load(plugin);
         plugin.getPinManager().load(plugin);
         plugin.getStatManager().load(plugin);
         plugin.getItemManager().load(plugin);
         plugin.getSkillManager().loadSkills();
         plugin.getMobManager().loadMobs();
         plugin.getDropManager().loadDropTables();
         plugin.getRandomSpawningManager().loadRandomSpawns();
         plugin.getSpawnerManager().load(plugin);
         plugin.getMenuManager().load(plugin);
         plugin.getDialogManager().load(plugin);
      } finally {
         plugin.getClock().setReloadingPause(false);
      }

      long startMillis = System.currentTimeMillis();
      plugin.getClock().runSecondPass();
      long endMillis = System.currentTimeMillis();
      MythicLogger.log("Second passes loaded after " + (endMillis - startMillis) + "ms!");
      MythicLogger.log(
         "" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + " Loaded " + plugin.getMobManager().getMobTypes().size() + " mobs."
      );
      MythicLogger.log(
         ""
            + ConsoleColor.GREEN
            + ConsoleColor.CHECK_MARK
            + ConsoleColor.WHITE
            + " Loaded "
            + plugin.getMobManager().getVanillaTypes().size()
            + " vanilla mob overrides."
      );
      MythicLogger.log(
         "" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + " Loaded " + plugin.getMobManager().getMobStacks().size() + " mob stacks."
      );
      MythicLogger.log(
         "" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + " Loaded " + plugin.getSkillManager().getSkills().size() + " skills."
      );
      MythicLogger.log(
         ""
            + ConsoleColor.GREEN
            + ConsoleColor.CHECK_MARK
            + ConsoleColor.WHITE
            + " Loaded "
            + plugin.getRandomSpawningManager().getNumberOfSpawners()
            + " random spawns."
      );
      MythicLogger.log(
         "" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + " Loaded " + plugin.getItemManager().getItems().size() + " mythic items."
      );
      MythicLogger.log(
         "" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + " Loaded " + plugin.getDropManager().getDropTables().size() + " drop tables."
      );
      MythicLogger.log(
         ""
            + ConsoleColor.GREEN
            + ConsoleColor.CHECK_MARK
            + ConsoleColor.WHITE
            + " Loaded "
            + plugin.getSpawnerManager().getSpawners().size()
            + " mob spawners."
      );
   }

   public void save() {
      MythicLogger.log("Saving plugin data...");

      try {
         this.getPlugin().getMobManager().saveCachedActiveMobs();
         this.getPlugin().getPlayerManager().saveAll();
         this.getPlugin().getWorldManager().saveAll();
         this.getPlugin().getVariableManager().save();
         MythicLogger.log("" + ConsoleColor.GREEN + ConsoleColor.CHECK_MARK + ConsoleColor.WHITE + "Saving Finished");
      } catch (Error | Exception e) {
         MythicLogger.log("" + ConsoleColor.RED + ConsoleColor.ERROR_MARK + ConsoleColor.WHITE + "An error occurred while saving data");
         e.printStackTrace();
      }
   }

   public void doClockSave() {
      try {
         this.getPlugin().getMobManager().saveCachedActiveMobs();
         this.getPlugin().getPlayerManager().saveAll();
         this.getPlugin().getWorldManager().saveAll();
         this.getPlugin().getVariableManager().save();
         this.getPlugin().getSpawnerManager().saveSpawners();
         this.getPlugin().getSkillManager().purgeExpiredCooldowns();
      } catch (Error | Exception e) {
         MythicLogger.log("" + ConsoleColor.RED + ConsoleColor.ERROR_MARK + ConsoleColor.WHITE + "An error occurred while saving data");
         e.printStackTrace();
      }
   }

   @Override
   public void unload() {
      this.plugin.getClock().setReloadingPause(true);
      this.save();
      this.placeholderStringCache.clear();
      this.plugin.getDropManager().unload();
      this.plugin.getItemManager().unload();
      this.plugin.getPinManager().unload();
      this.plugin.getStatManager().unload();
      this.plugin.getSpawnerManager().unload();
      this.plugin.getPlaceholderManager().unload();
      this.plugin.getMobManager().getTotemManager().unload();
   }

   @Override
   public File getConfigFolder() {
      File configFolder = new File(this.getPlugin().getDataFolder(), "config");
      if (!configFolder.exists()) {
         configFolder.mkdir();
      }

      return configFolder;
   }

   private void generateConfigFiles() {
      File mythicFolder = this.getPlugin().getDataFolder();
      File mythicConfigFolder = this.getConfigFolder();
      File mythicLanguageFolder = new File(mythicConfigFolder, "language");
      File mythicMenusFolder = new File(mythicConfigFolder, "menus");
      File packFolder = new File(mythicFolder, "packs");
      File internalFolder = new File(packFolder, ".internal");
      if (!mythicLanguageFolder.exists()) {
         mythicLanguageFolder.mkdir();
      }

      if (!mythicMenusFolder.exists()) {
         mythicMenusFolder.mkdir();
      }

      File configGeneralFile = new File(mythicConfigFolder, "config-general.yml");
      if (!configGeneralFile.exists() || configGeneralFile.length() == 0L) {
         this.getPlugin().saveResource("config-general.yml", configGeneralFile, true);
      }

      ConfigMerger.mergeDefaults(this.getPlugin(), "config-general.yml", configGeneralFile);
      File configMobsFile = new File(mythicConfigFolder, "config-mobs.yml");
      if (!configMobsFile.exists() || configMobsFile.length() == 0L) {
         this.getPlugin().saveResource("config-mobs.yml", configMobsFile, true);
      }

      ConfigMerger.mergeDefaults(this.getPlugin(), "config-mobs.yml", configMobsFile);
      File configItemsFile = new File(mythicConfigFolder, "config-items.yml");
      if (!configItemsFile.exists() || configItemsFile.length() == 0L) {
         this.getPlugin().saveResource("config-items.yml", configItemsFile, true);
      }

      ConfigMerger.mergeDefaults(this.getPlugin(), "config-items.yml", configItemsFile);
      File configSkillsFile = new File(mythicConfigFolder, "config-skills.yml");
      if (!configSkillsFile.exists() || configSkillsFile.length() == 0L) {
         this.getPlugin().saveResource("config-skills.yml", configSkillsFile, true);
      }

      ConfigMerger.mergeDefaults(this.getPlugin(), "config-skills.yml", configSkillsFile);
      File configSpawningFile = new File(mythicConfigFolder, "config-spawning.yml");
      if (!configSpawningFile.exists() || configSpawningFile.length() == 0L) {
         this.getPlugin().saveResource("config-spawning.yml", configSpawningFile, true);
      }

      ConfigMerger.mergeDefaults(this.getPlugin(), "config-spawning.yml", configSpawningFile);
      File statsFile = new File(this.getPlugin().getDataFolder(), "stats.yml");
      if (!statsFile.exists()) {
         this.getPlugin().saveResource("stats.yml", false);
      }

      File oldConfigFile = new File(mythicFolder, "config.yml");
      if (oldConfigFile.exists() && oldConfigFile.length() > 0L) {
         this.plugin.reloadConfiguration();
         this.convertLegacyConfigValues();
      }

      if (Bukkit.getPluginManager().getPlugin("MythicCrucible") != null) {
         this.copyResourcesIfMissing("config/internal", internalFolder, "plugin assets");
      }

      if (this.shouldLoadExampleConfigs(configGeneralFile)) {
         this.generateDefaultPackFiles(mythicFolder);
      }
   }

   private boolean shouldLoadExampleConfigs(File configGeneralFile) {
      return YamlConfiguration.loadConfiguration(configGeneralFile).getBoolean("Configuration.General.LoadExampleConfigs", true);
   }

   private void generateDefaultPackFiles(File mythicFolder) {
      this.saveDefaultPackFile(mythicFolder, "Mobs", "VanillaMobs.yml");
      this.saveDefaultPackFile(mythicFolder, "Mobs", "ExampleMobs.yml");
      this.saveDefaultPackFile(mythicFolder, "Items", "ExampleItems.yml");
      this.saveDefaultPackFile(mythicFolder, "Skills", "ExampleSkills.yml");
      this.saveDefaultPackFile(mythicFolder, "DropTables", "ExampleDropTables.yml");
      this.saveDefaultPackFile(mythicFolder, "RandomSpawns", "ExampleRandomSpawns.yml");
      this.saveDefaultPackFile(mythicFolder, "Dialogs", "ExampleDialogs.yml");
   }

   private void saveDefaultPackFile(File mythicFolder, String folderName, String resourceName) {
      File folder = this.getDefaultPackFolder(mythicFolder, folderName);
      if (!folder.exists() && !folder.mkdirs()) {
         MythicLogger.error("Could not create default pack folder " + folder.getAbsolutePath());
      } else {
         File file = new File(folder, resourceName);
         if (!file.exists()) {
            try {
               this.getPlugin().saveResource(resourceName, file, true);
            } catch (Throwable e) {
               MythicLogger.error("Could not load default pack file " + resourceName + ".");
               e.printStackTrace();
            }
         }
      }
   }

   private File getDefaultPackFolder(File mythicFolder, String folderName) {
      File lowerCaseFolder = new File(mythicFolder, folderName.toLowerCase(Locale.ROOT));
      if (lowerCaseFolder.exists()) {
         return lowerCaseFolder;
      } else {
         File originalCaseFolder = new File(mythicFolder, folderName);
         return originalCaseFolder.exists() ? originalCaseFolder : lowerCaseFolder;
      }
   }

   private void copyResourcesIfMissing(String sourceFolder, File targetFolder, String label) {
      String prefix = sourceFolder.endsWith("/") ? sourceFolder : sourceFolder + "/";

      try (JarFile jarFile = new JarFile(this.getPlugin().getJarFile())) {
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if (name.startsWith(prefix) && name.length() != prefix.length() && !name.endsWith("/")) {
               String relativePath = name.substring(prefix.length());
               File targetFile = new File(targetFolder, relativePath);
               if (!targetFile.exists()) {
                  File parent = targetFile.getParentFile();
                  if (parent != null && !parent.exists()) {
                     parent.mkdirs();
                  }

                  try (InputStream input = this.getPlugin().getResource(name)) {
                     if (input != null) {
                        Files.copy(input, targetFile.toPath());
                     }
                  }
               }
            }
         }
      } catch (IOException e) {
         MythicLogger.error("Could not load " + label + ".");
         e.printStackTrace();
      }
   }

   public void loadConfigSettings() {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Loading Settings...");
      File mythicFolder = this.getPlugin().getDataFolder();
      File mythicConfigFolder = this.getConfigFolder();
      File configMobsFile = new File(mythicConfigFolder, "config-mobs.yml");
      new File(mythicConfigFolder, "config-spawning.yml");
      MythicConfig generalConfig = Scope.CONFIG_GENERAL.getConfig().getNestedConfig("Configuration");
      this.allowMetrics = generalConfig.getBoolean("General.AllowMetrics", true);
      this.allowUpdateChecking = GENERAL_CHECK_UPDATES.get(this);
      this.useCompatibilityMode = GENERAL_COMPATIB_MODE.get(this);
      this.doErrorLogging = GENERAL_ERROR_LOGGING.get(this);
      this.loadExampleConfigs = GENERAL_LOAD_EXAMPLES.get(this);
      this.language = GENERAL_LANGUAGE.getset(this);
      this.languageManager.setLanguage(this.language);
      this.languageManager.loadLanguageFile();
      Integer threadLimit = THREAD_POOL.getset(this);
      Boolean useVirtualThreads = USE_VIRTUAL_THREADS.getset(this);
      if (threadLimit > 0) {
         ((LumineAsyncExecutor)LumineExecutors.asyncLumine()).setThreadLimit(threadLimit);
      } else if (useVirtualThreads) {
         ((LumineAsyncExecutor)LumineExecutors.asyncLumine()).useVirtualThreads();
      }

      this.asyncPathfindingEnabled = ASYNC_PATHFINDING_ENABLED.getset(this);
      this.asyncPathfindingThreads = ASYNC_PATHFINDING_THREADS.getset(this);
      this.cacheComponents = generalConfig.getBoolean("General.ShareComponents", false);
      this.announceOpReload = ANNOUNCE_OP_RELOAD.get(this);
      this.clockIntervalMain = CLOCK_SPEED.get(this);
      this.clockIntervalRandomSpawning = CLOCK_RANDOM_SPAWNING.get(this);
      this.clockIntervalSaving = CLOCK_SAVING.get(this);
      this.clockBarUpdating = CLOCK_BARS.get(this);
      this.clockCleaningInterval = CLOCK_CLEANUP.get(this);
      this.clockSpawningInterval = CLOCK_SPAWNS.get(this);
      this.clockScanningInterval = CLOCK_SCANNER.get(this);
      compatHeroesXPEnable = COMPAT_HEROES_XP_ENABLE.get(this);
      compatHeroesShowXPMessage = COMPAT_HEROES_SHOW_XP_MESSAGE.get(this);
      compatHeroesXPMessageFormat = COMPAT_HEROES_XP_MESSAGE_FORMAT.get(this);
      compatMcMMOShowXPMessage = COMPAT_MCMMO_SHOW_XP_MESSAGE.get(this);
      compatMcMMOXPMessageFormat = COMPAT_MCMMO_XP_MESSAGE_FORMAT.get(this);
      compatSkillAPIShowXPMessage = COMPAT_SKILLAPI_SHOW_XP_MESSAGE.get(this);
      compatSkillAPIXPMessageFormat = COMPAT_SKILLAPI_XP_MESSAGE_FORMAT.get(this);
      compatVaultShowMoneyMessage = COMPAT_VAULT_SHOW_MONEY_MESSAGE.get(this);
      compatVaultMoneyMessageFormat = COMPAT_VAULT_MONEY_MESSAGE_FORMAT.get(this);
      compatHeroesXPMessageFormat = SkillString.convertLegacyVariables(compatHeroesXPMessageFormat);
      compatMcMMOXPMessageFormat = SkillString.convertLegacyVariables(compatMcMMOXPMessageFormat);
      compatSkillAPIXPMessageFormat = SkillString.convertLegacyVariables(compatSkillAPIXPMessageFormat);
      compatVaultMoneyMessageFormat = SkillString.convertLegacyVariables(compatVaultMoneyMessageFormat);
      this.packetMinimumPositionDelta = Math.pow(PACKET_MIN_POS_DELTA.get(this), 2.0);
      this.particleBundleEnabled = PACKET_PARTICLE_BUNDLE_ENABLED.get(this);
      int bundleSize = PACKET_PARTICLE_BUNDLE_SIZE.get(this);
      this.particleBundleSize = bundleSize < 1 ? 1 : Math.min(bundleSize, 4096);
      this.particleTickBatchEnabled = PACKET_PARTICLE_TICK_BATCH_ENABLED.get(this);
      this.respectClientParticleVisibility = PACKET_RESPECT_CLIENT_PARTICLE_VIS.get(this);
      this.particleVisibilityAll = Math.min(Math.max(PACKET_PARTICLE_VIS_ALL.get(this), 0), 100);
      this.particleVisibilityDecreased = Math.min(Math.max(PACKET_PARTICLE_VIS_DECREASED.get(this), 0), 100);
      this.particleVisibilityMinimal = Math.min(Math.max(PACKET_PARTICLE_VIS_MINIMAL.get(this), 0), 100);
      this.cancelDamageIfZero = CANCEL_DAMAGE_IF_0.get(this);
      this.mobsDespawnByDefault = MOBS_DESPAWN_BY_DEFAULT.get(this);
      this.preventOtherDropsByDefault = PREVENT_OTHER_DROPS_BY_DEFAULT.get(this);
      this.preventVanillaDamageByDefault = PREVENT_VANILLA_DAMAGE_BY_DEFAULT.get(this);
      this.noDamageTicksByDefault = NO_DAMAGE_TICKS_BY_DEFAULT.get(this);
      this.preventJockeyMountsByDefault = PREVENT_JOCKEY_MOUNTS_BY_DEFAULT.get(this);
      this.preventConversionByDefault = PREVENT_CONVERSION_BY_DEFAULT.get(this);
      this.preventLeashingByDefault = PREVENT_LEASHING_BY_DEFAULT.get(this);
      this.preventItemPickupByDefault = PREVENT_ITEM_PICKUP_BY_DEFAULT.get(this);
      this.killMessagePrefix = MOBS_KILL_MESSAGE_PREFIX.get(this);
      this.killMessagePrefix = this.killMessagePrefix != null && !this.killMessagePrefix.isEmpty()
         ? SkillString.parseMessageSpecialChars(this.killMessagePrefix)
         : null;
      this.defaultDropMethod = DropMethod.valueOf(DROPS_DEFAULT_DROPMETHOD.getset(this).toUpperCase());
      this.mobDropsDoLoopslosion = DROPS_DEFAULT_LOOTSPLOSION.getset(this);
      this.mobDropsDoNameHologram = DROPS_DEFAULT_NAME_HOLOGRAM.getset(this);
      this.mobDropsDoItemGlow = DROPS_DEFAULT_ITEM_GLOW.getset(this);
      this.mobDropsDoItemBeam = DROPS_DEFAULT_ITEM_BEAM.getset(this);
      this.mobDropsDoItemVFX = DROPS_DEFAULT_ITEM_VFX.getset(this);
      this.mobDropsItemVFXMaterial = DROPS_DEFAULT_ITEM_VFX_MATERIAL.getset(this);
      this.mobDropsItemVFXModel = DROPS_DEFAULT_ITEM_VFX_MODEL.getset(this);
      this.mobDropsItemVFXGeneration = DROPS_DEFAULT_ITEM_VFX_GENERATION.getset(this);
      this.getPlugin()
         .getClock()
         .queueAfterLoad(
            () -> {
               if (this.mobDropsItemVFXGeneration != null
                  && !this.mobDropsItemVFXGeneration.isEmpty()
                  && this.getPlugin().getCompatibility().getCrucible().isPresent()) {
                  CrucibleSupport crucible = this.getPlugin().getCompatibility().getCrucible().get();
                  crucible.addItemToGeneration(
                     this.getPlugin().getPackManager().getBasePack(), this.mobDropsItemVFXMaterial, this.mobDropsItemVFXModel, this.mobDropsItemVFXGeneration
                  );
               }
            }
         );
      this.mobDropsPerPlayer = DROPS_DEFAULT_PERPLAYER.getset(this);
      this.mobDropsAreClientside = DROPS_DEFAULT_CLIENTSIDE.getset(this);
      String mobDropsPerPlayerRequiredDamagePercent = DROPS_PER_PLAYER_PERCENT.getset(this);
      if (mobDropsPerPlayerRequiredDamagePercent.endsWith("%")) {
         String number = mobDropsPerPlayerRequiredDamagePercent.replace("%", "");
         this.mobDropsPerPlayerRequiredDamagePercent = Double.parseDouble(number) / 100.0;
      } else {
         this.mobDropsPerPlayerRequiredDamagePercent = Double.parseDouble(mobDropsPerPlayerRequiredDamagePercent);
         if (this.mobDropsPerPlayerRequiredDamagePercent > 1.0) {
            this.mobDropsPerPlayerRequiredDamagePercent /= 100.0;
         }
      }

      this.mobDrosDefaultDeathHologram = DROPS_DEFAULT_DEATH_HOLOGRAM.getset(this);
      this.hologramLeaderboardTimeout = DROPS_DEFAULT_LEADERBOARD_TIMEOUT.getset(this);
      this.mobDropsDefaultChatMessage = DROPS_DEFAULT_CHAT_MESSAGE.getset(this);
      Material eggMaterial = EGG_DEFAULT_ITEM_MATERIAL.getset(this);
      String eggModel = EGG_DEFAULT_ITEM_MODEL.getset(this);
      String eggDisplay = EGG_DEFAULT_ITEM_DISPLAY.getset(this);
      List<String> eggLore = EGG_DEFAULT_ITEM_LORE.getset(this);
      this.getPlugin().getClock().queueAfterLoad(() -> {
         this.defaultMobEggDisplay = PlaceholderString.of(eggDisplay);
         this.defaultMobEggModel = PlaceholderString.of(eggModel);
         this.defaultMobEggItem = BukkitAdapter.adapt(eggMaterial);
         OptionalInt maybeNumber = Numbers.parseInteger(eggModel);
         if (maybeNumber.isPresent()) {
            this.defaultMobEggItem.modelData(maybeNumber.getAsInt());
            this.defaultMobEggModel = null;
         } else if (ServerVersion.isBefore(MinecraftVersions.v1_21_3)) {
            this.defaultMobEggModel = null;
         }

         this.defaultMobEggLore = Lists.newArrayList();

         for (String line : eggLore) {
            this.defaultMobEggLore.add(PlaceholderString.of(line));
         }
      });
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         this.globalMountedHologramOffset = HOLOGRAM_GLOBAL_OFFSET.get(this).floatValue();
         this.hologramCastingBarBackgroundColor = HOLOGRAM_CASTBAR_CAST_BACKGROUND.get(this).toBukkitColor();

         try {
            this.hologramCastingBarBillboarding = Billboard.valueOf(HOLOGRAM_CASTBAR_CAST_BILLBOARDING.get(this).toUpperCase());
         } catch (Throwable e) {
            MythicLogger.errorGenericConfig("Invalid Billboarding Option for Holograms.CastBar.CastBillboard");
         }

         this.hologramCastingBarOffset = HOLOGRAM_CASTBAR_CAST_OFFSET.get(this).floatValue();
         this.hologramCastingInfoBackgroundColor = HOLOGRAM_CASTBAR_INFO_BACKGROUND.get(this).toBukkitColor();

         try {
            this.hologramCastingInfoBillboarding = Billboard.valueOf(HOLOGRAM_CASTBAR_INFO_BILLBOARDING.get(this).toUpperCase());
         } catch (Throwable ex) {
            MythicLogger.errorGenericConfig("Invalid Billboarding Option for Holograms.CastBar.InfoBillboard");
         }

         this.hologramCastingInfoOffset = HOLOGRAM_CASTBAR_INFO_OFFSET.get(this).floatValue();
         this.hologramHealthBarBackgroundColor = HOLOGRAM_HEALTHBAR_BACKGROUND.get(this).toBukkitColor();

         try {
            this.hologramHealthBarBillboarding = Billboard.valueOf(HOLOGRAM_HEALTHBAR_BILLBOARDING.get(this).toUpperCase());
         } catch (Throwable ex2) {
            MythicLogger.errorGenericConfig("Invalid Billboarding Option for Holograms.HealthBar.Billboard");
         }

         this.hologramHealthBarOffset = HOLOGRAM_HEALTHBAR_OFFSET.get(this).floatValue();
         this.hologramHealthBarLength = HOLOGRAM_HEALTHBAR_LENGTH.get(this);
         String hologramHealthBarScale = HOLOGRAM_HEALTHBAR_SCALE.get(this);
         if (hologramHealthBarScale.contains(",")) {
            String[] parts = hologramHealthBarScale.split(",");
            if (parts.length == 3) {
               try {
                  double x = Double.parseDouble(parts[0]);
                  double y = Double.parseDouble(parts[1]);
                  double z = Double.parseDouble(parts[2]);
                  this.hologramHealthBarScale = new AbstractVector(x, y, z);
               } catch (NumberFormatException ex3) {
                  MythicLogger.errorGenericConfig("Invalid Scale Option for Holograms.HealthBar.Scale, expected format: x,y,z");
               }
            } else {
               MythicLogger.errorGenericConfig("Invalid Scale Option for Holograms.HealthBar.Scale, expected 3 values but found: " + parts.length);
            }
         } else {
            try {
               Double val = Double.valueOf(hologramHealthBarScale);
               this.hologramHealthBarScale = new AbstractVector(val, val, val);
            } catch (Throwable ex4) {
               MythicLogger.errorGenericConfig("Invalid Scale Option for Holograms.HealthBar.Scale");
            }
         }

         this.hologramNameplateBackgroundColor = HOLOGRAM_NAMEPLATE_BACKGROUND.get(this).toBukkitColor();

         try {
            this.hologramNameplateBillboarding = Billboard.valueOf(HOLOGRAM_NAMEPLATE_BILLBOARDING.get(this).toUpperCase());
         } catch (Throwable ex5) {
            MythicLogger.errorGenericConfig("Invalid Billboarding Option for Holograms.Nameplate.Billboard");
         }

         this.hologramNameplateOffset = HOLOGRAM_NAMEPLATE_OFFSET.get(this).floatValue();
         this.hologramNameplateScale = HOLOGRAM_NAMEPLATE_SCALE.get(this);
         this.hologramSpeechBackgroundColor = HOLOGRAM_SPEECH_BACKGROUND.get(this).toBukkitColor();

         try {
            this.hologramSpeechBillboarding = Billboard.valueOf(HOLOGRAM_SPEECH_BILLBOARDING.get(this).toUpperCase());
         } catch (Throwable ex6) {
            MythicLogger.errorGenericConfig("Invalid Billboarding Option for Holograms.Speech.Billboard");
         }

         this.hologramSpeechOffset = HOLOGRAM_SPEECH_OFFSET.get(this).floatValue();
         this.hologramSpeechLinePrefix = HOLOGRAM_SPEECH_PREFIX.get(this);
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Loading WorldScaling Settings...");
      WorldScaling.reset();

      for (String world : WORLD_SCALING_NODES.get(this)) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "*** Loading WorldScaling for world " + world);
         WorldScaling.initialize(world, configMobsFile);
      }

      if (MythicBukkit.isVolatile()) {
         for (Entry<String, String> entry : SCALING_EQUATIONS.get(this).entrySet()) {
            String statName = StatManager.translateLegacyStatAliases(entry.getKey());
            String strScalingEquation = entry.getValue();
            if (strScalingEquation != null && !strScalingEquation.isEmpty() && !strScalingEquation.equalsIgnoreCase("NONE")) {
               Expression scalingEquation = new ExpressionBuilder(strScalingEquation.toLowerCase()).variables("l", "v").build();
               this.statScalingEquations.put(statName, scalingEquation);
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Loading Default Level Modifiers...");

      for (Entry<String, String> entryx : DEFAULT_LEVEL_MODIFIERS.get(this).entrySet()) {
         String statName = StatManager.translateLegacyStatAliases(entryx.getKey());
         this.statDefaultLevelModifiers.put(statName, entryx.getValue());
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "**** Default Level Modifiers - {0}: {1}", statName, entryx.getValue());
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "** Finished Load Default Level Modifiers!");
      updateItems = UPDATE_ITEMS.get(this);
      itemDefaultMaterial = ITEM_DEFAULT_MATERIAL.get(this);
      itemDefaultPreventCrafting = ITEM_DEFAULT_PREVENTCRAFTING.get(this);
      this.fancyDropsEnabledEverywhere = ITEMS_ENABLE_FANCY_DROPS.get(this);
      targetSelf = TARGET_SELF.get(this);
      targetPlayers = TARGET_PLAYERS.get(this);
      targetArmorStands = TARGET_ARMOR_STANDS.get(this);
      targetMarkers = TARGET_MARKERS.get(this);
      targetTextDisplays = TARGET_TEXT_DISPLAYS.get(this);
      targetItemDisplays = TARGET_ITEM_DISPLAYS.get(this);
      targetBlockDisplays = TARGET_BLOCK_DISPLAYS.get(this);
      targetInteractions = TARGET_INTERACTIONS.get(this);
      targetCreativeMode = TARGET_CREATIVE_MODE.get(this);
      targetSpectatorMode = TARGET_SPECTATOR_MODE.get(this);
      targetInvulnerable = TARGET_INVULNERABLE.get(this);
      targetCitizensNPCs = TARGET_CITIZENS_NPCS.get(this);
      targetAnimals = TARGET_ANIMALS.get(this);
      targetCreatures = TARGET_CREATURES.get(this);
      targetMonsters = TARGET_MONSTERS.get(this);
      targetWaterMobs = TARGET_WATER_MOBS.get(this);
      targetFlyingMobs = TARGET_FLYING_MOBS.get(this);
      targetSameFaction = TARGET_SAME_FACTION.get(this);
      targetOwner = TARGET_OWNER.get(this);
      targetNonMythic = TARGET_NON_MYTHIC.get(this);
      targetVillagers = TARGET_VILLAGERS.get(this);
      this.spawnersDisableSaving = SPAWNERS_DISABLE_SAVING.getset(this);
      this.vanillaSpawnsDisabled = VANILLA_SPAWNS_DISABLED.getset(this);
      this.randomSpawningGeneratorType = RS_GENERATOR.get(this);
      this.randomSpawningGenerationEnabled = RS_GENERATE_POINTS.getset(this);
      if (!this.randomSpawningGenerationEnabled && this.randomSpawningGeneratorType != GeneratorType.NONE) {
         this.randomSpawningGenerationEnabled = true;
      } else if (this.randomSpawningGenerationEnabled && this.randomSpawningGeneratorType == GeneratorType.NONE) {
         this.randomSpawningGeneratorType = GeneratorType.CLUSTER;
      }

      this.randomSpawningPlayerRadiusMax = RANDOM_SPAWNING_PLAYER_RADIUS_MAX.getset(this);
      this.randomSpawningPlayerRadiusMin = RANDOM_SPAWNING_PLAYER_RADIUS_MIN.getset(this);
      this.randomSpawningPlayerRadiusY = RANDOM_SPAWNING_PLAYER_RADIUS_Y.getset(this);
      this.randomSpawningGroupDistance = RS_GROUP_DISTANCE.getset(this);
      this.randomSpawningLimitMultiplier = RS_LIMIT_MULTIPLIER.getset(this);
      this.randomSpawningGroupMultiplier = RS_GROUP_MULTIPLIER.getset(this);
      this.randomSpawningLimit = RS_LIMIT.getset(this);
      this.randomSpawningLocalLimit = RS_LOCAL_LIMIT.getset(this);
      this.randomSpawningIgnoresVanilla = REPLACE_IGNORES_VANILLA.getset(this);
      this.randomSpawningIgnoresUnnatural = REPLACE_IGNORES_UNNATURAL.getset(this);
      this.randomSpawningReplaceObeysCap = REPLACE_OBEYS_CAP.getset(this);
      this.randomSpawningSharedCooldown = RS_SHARED_COOLDOWN.getset(this);
      this.randomSpawningPointsPerSecond = RS_POINTS_PER_SECOND_LAND.get(this);
      this.randomSpawningPointsPerSecondAir = RS_POINTS_PER_SECOND_AIR.get(this);
      this.randomSpawningPointsPerSecondSea = RS_POINTS_PER_SECOND_SEA.get(this);
      this.randomSpawningPointsPerSecondLava = RS_POINTS_PER_SECOND_LAVA.get(this);
      this.randomSpawningPointsPerSecondGround = RS_POINTS_PER_SECOND_GROUND.get(this);
      this.randomSpawningDespawnVanillaOutsideRadius = RS_DESPAWN_VANILLA_OUTSIDE_RADIUS.get(this);
      this.randomSpawningMaxGenerationTimeMillis = RS_MAX_GENERATION_TIME_MILLIS.get(this);
      this.randomSpawningMaxGenerationAttempts = RS_MAX_GENERATION_ATTEMPTS.getset(this);
   }

   public HashSet<Material> getTransparentBlocks() {
      return transparentBlocks;
   }

   public boolean shouldUpdateItems() {
      return updateItems;
   }

   @Override
   public MythicConfig createConfig(File file) {
      return new MythicConfigImpl(file);
   }

   @Override
   public MythicConfig createConfig(FileConfiguration fileConfiguration) {
      return new MythicConfigImpl(fileConfiguration);
   }

   @Override
   public MythicConfig createConfig(String node, FileConfiguration fileConfiguration) {
      return new MythicConfigImpl(node, fileConfiguration);
   }

   @Override
   public MythicConfig createConfig(String node, File file) {
      return new MythicConfigImpl(node, file);
   }

   @Override
   public MythicConfig createConfig(String node, File file, FileConfiguration fileConfiguration) {
      return new MythicConfigImpl(node, file, fileConfiguration);
   }

   @Override
   public MythicLineConfig createLineConfig(File file, String line) {
      return new MythicLineConfigImpl(file, line);
   }

   private <P extends IPlaceholder> P getOrCreatePlaceholderLine(Class<P> type, String key, Supplier<P> factory) {
      if (key == null) {
         return factory.get();
      } else {
         Map<String, IPlaceholder> typeCache = this.placeholderStringCache.computeIfAbsent(type, t -> new ConcurrentHashMap<>());
         IPlaceholder existing = typeCache.get(key);
         if (existing != null) {
            return (P)existing;
         } else {
            P created = factory.get();
            IPlaceholder prev = typeCache.putIfAbsent(key, created);
            if (prev != null) {
               if (created instanceof PlaceholderParser parser) {
                  this.plugin.getPlaceholderManager().unregisterParser(parser);
               }

               return (P)prev;
            } else {
               return created;
            }
         }
      }
   }

   @Override
   public PlaceholderAngle createPlaceholderAngle(String s, AngleUnit defaultUnit) {
      String cacheKey = s == null ? null : s + defaultUnit.name();
      return this.getOrCreatePlaceholderLine(PlaceholderAngle.class, cacheKey, () -> PlaceholderAngleImpl.of(s, defaultUnit));
   }

   @Override
   public PlaceholderBoolean createPlaceholderBoolean(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderBoolean.class, s, () -> PlaceholderBooleanImpl.of(s));
   }

   @Override
   public PlaceholderColor createPlaceholderColor(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderColor.class, s, () -> new PlaceholderColorImpl(this.createPlaceholderString(s)));
   }

   @Override
   public PlaceholderDouble createPlaceholderDouble(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderDouble.class, s, () -> PlaceholderDoubleImpl.of(s));
   }

   @Override
   public PlaceholderDuration createPlaceholderDuration(String s, DurationUnit defaultUnit) {
      DurationUnit unit = defaultUnit == null ? DurationUnit.SECONDS : defaultUnit;
      String cacheKey = s == null ? null : unit.name() + ":" + s;
      return this.getOrCreatePlaceholderLine(PlaceholderDuration.class, cacheKey, () -> PlaceholderDurationImpl.of(s, unit));
   }

   @Override
   public PlaceholderFloat createPlaceholderFloat(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderFloat.class, s, () -> PlaceholderFloatImpl.of(s));
   }

   @Override
   public PlaceholderInt createPlaceholderInt(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderInt.class, s, () -> PlaceholderIntImpl.of(s));
   }

   @Override
   public PlaceholderLong createPlaceholderLong(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderLong.class, s, () -> PlaceholderLongImpl.of(s));
   }

   @Override
   public PlaceholderString createPlaceholderString(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderString.class, s, () -> PlaceholderStringImpl.of(s));
   }

   @Override
   public PolymorphicPlaceholder createPolymorphicPlaceholder(String s) {
      return this.getOrCreatePlaceholderLine(PolymorphicPlaceholder.class, s, () -> new PolymorphicPlaceholderImpl(s));
   }

   @Override
   public PlaceholderTime createPlaceholderTime(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderTime.class, s, () -> PlaceholderTimeImpl.of(s));
   }

   @Override
   public PlaceholderVector createPlaceholderVector(String s) {
      return this.getOrCreatePlaceholderLine(PlaceholderVector.class, s, () -> PlaceholderVectorImpl.of(s));
   }

   @Override
   public MythicLineConfig createLineConfig(String line) {
      return new MythicLineConfigImpl(line);
   }

   public Optional<Expression> getStatScalingEquation(StatType statType) {
      return Optional.ofNullable(this.statScalingEquations.get(statType.getKey()));
   }

   public Optional<String> getStatDefaultLevelModifier(StatType statType) {
      return Optional.ofNullable(this.statDefaultLevelModifiers.get(statType.getKey()));
   }

   private void convertLegacyConfigValues() {
      File oldConfigFile = new File(this.getPlugin().getDataFolder(), "config.yml");
      if (oldConfigFile.exists() && oldConfigFile.length() != 0L) {
         LegacyConfigExecutor oldConfig = new LegacyConfigExecutor(this.getPlugin());
         oldConfig.loadSettings();
         GENERAL_ALLOW_METRICS.set(this, oldConfig.getAllowMetrics());
         GENERAL_CHECK_UPDATES.set(this, oldConfig.getAllowUpdateChecking());
         GENERAL_COMPATIB_MODE.set(this, oldConfig.getUseCompatibilityMode());
         GENERAL_ERROR_LOGGING.set(this, oldConfig.getDoErrorLogging());
         GENERAL_LOAD_EXAMPLES.set(this, oldConfig.getLoadExampleConfigs());
         DEBUG_LEVEL.set(this, oldConfig.getDebugLevel());
         DEBUG_MODE.set(this, oldConfig.isDebugMode());
         DEBUG_SPAWNERS.set(this, oldConfig.isDebugSpawners());
         CLOCK_SPEED.set(this, oldConfig.getClockIntervalMain());
         CLOCK_BARS.set(this, oldConfig.getBossBarUpdateInterval());
         CLOCK_SAVING.set(this, oldConfig.getClockIntervalSaving());
         CLOCK_SPAWNS.set(this, oldConfig.getSpawningInterval());
         CLOCK_RANDOM_SPAWNING.set(this, oldConfig.getClockIntervalRandomSpawning());
         CLOCK_SCANNER.set(this, oldConfig.getScanInterval());
         CLOCK_CLEANUP.set(this, oldConfig.getClnrInterval());
         COMPAT_HEROES_XP_ENABLE.set(this, oldConfig.compatHeroesXPEnable);
         COMPAT_HEROES_SHOW_XP_MESSAGE.set(this, oldConfig.compatHeroesShowXPMessage);
         COMPAT_HEROES_XP_MESSAGE_FORMAT.set(this, oldConfig.compatHeroesXPMessageFormat);
         COMPAT_MCMMO_SHOW_XP_MESSAGE.set(this, oldConfig.compatMcMMOShowXPMessage);
         COMPAT_MCMMO_XP_MESSAGE_FORMAT.set(this, oldConfig.compatMcMMOXPMessageFormat);
         COMPAT_SKILLAPI_SHOW_XP_MESSAGE.set(this, oldConfig.compatSkillAPIShowXPMessage);
         COMPAT_SKILLAPI_XP_MESSAGE_FORMAT.set(this, oldConfig.compatSkillAPIXPMessageFormat);
         COMPAT_VAULT_SHOW_MONEY_MESSAGE.set(this, oldConfig.compatVaultShowMoneyMessage);
         COMPAT_VAULT_MONEY_MESSAGE_FORMAT.set(this, oldConfig.compatVaultMoneyMessageFormat);
         MOBS_DESPAWN_BY_DEFAULT.set(this, oldConfig.getMobsDespawnByDefault());
         CANCEL_DAMAGE_IF_0.set(this, oldConfig.isCancelDamageIfZero());
         MOBS_KILL_MESSAGE_PREFIX.set(this, oldConfig.getKillMessagePrefix());
         PREVENT_OTHER_DROPS_BY_DEFAULT.set(this, oldConfig.getPreventOtherDropsByDefault());
         SCALING_EQUATION_DAMAGE.set(this, oldConfig.getScalingEquationDamage());
         SCALING_EQUATION_HEALTH.set(this, oldConfig.getScalingEquationHealth());
         UPDATE_ITEMS.set(this, oldConfig.updateItems);
         TARGET_SELF.set(this, oldConfig.targetSelf);
         TARGET_PLAYERS.set(this, oldConfig.targetPlayers);
         TARGET_ARMOR_STANDS.set(this, oldConfig.targetArmorStands);
         TARGET_MARKERS.set(this, oldConfig.targetMarkers);
         TARGET_CREATIVE_MODE.set(this, oldConfig.targetCreativeMode);
         TARGET_SPECTATOR_MODE.set(this, oldConfig.targetSpectatorMode);
         TARGET_CITIZENS_NPCS.set(this, oldConfig.targetCitizensNPCs);
         TARGET_ANIMALS.set(this, oldConfig.targetAnimals);
         TARGET_CREATURES.set(this, oldConfig.targetCreatures);
         TARGET_MONSTERS.set(this, oldConfig.targetMonsters);
         TARGET_WATER_MOBS.set(this, oldConfig.targetWaterMobs);
         TARGET_FLYING_MOBS.set(this, oldConfig.targetFlyingMobs);
         TARGET_SAME_FACTION.set(this, oldConfig.targetSameFaction);
         TARGET_OWNER.set(this, oldConfig.targetOwner);
         TARGET_NON_MYTHIC.set(this, oldConfig.targetNonMythic);
         TARGET_VILLAGERS.set(this, oldConfig.targetVillagers);
         RS_GENERATE_POINTS.set(this, oldConfig.rsGeneratePoints);
         RANDOM_SPAWNING_PLAYER_RADIUS_MAX.set(this, oldConfig.randomSpawningPlayerRadiusMax);
         RANDOM_SPAWNING_PLAYER_RADIUS_MIN.set(this, oldConfig.randomSpawningPlayerRadiusMin);
         RANDOM_SPAWNING_PLAYER_RADIUS_Y.set(this, oldConfig.randomSpawningPlayerRadiusY);
         RS_POINTS_PER_SECOND_LAND.set(this, oldConfig.rsPointsPerSecondLand);
         RS_POINTS_PER_SECOND_AIR.set(this, oldConfig.rsPointsPerSecondAir);
         RS_POINTS_PER_SECOND_SEA.set(this, oldConfig.rsPointsPerSecondSea);
         RS_POINTS_PER_SECOND_LAVA.set(this, oldConfig.rsPointsPerSecondLava);
         RS_POINTS_PER_SECOND_GROUND.set(this, oldConfig.rsPointsPerSecondGround);
         RS_DESPAWN_VANILLA_OUTSIDE_RADIUS.set(this, oldConfig.rsDespawnVanillaOutsideRadius);
         RS_MAX_GENERATION_TIME_MILLIS.set(this, oldConfig.rsMaxGenerationTimeMillis);
         VANILLA_SPAWNS_DISABLED.set(this, oldConfig.vanillaSpawnsDisabled);
         File newConfigFile = new File(this.getConfigFolder(), "config-mobs.yml");
         String newPath = "Configuration.MobLeveling.WorldScaling";
         FileConfiguration oldSection = YamlConfiguration.loadConfiguration(oldConfigFile);
         FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newConfigFile);
         if (oldSection.contains("Configuration.Mobs.Scaling")) {
            for (String world : oldSection.getConfigurationSection("Configuration.Mobs.Scaling").getKeys(false)) {
               Object value = oldSection.get("Configuration.Mobs.Scaling." + world);
               newConfig.set("Configuration.MobLeveling.WorldScaling." + world, value);
            }
         }

         try {
            newConfig.save(newConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }

         File oldConfigFileNew = new File(this.getPlugin().getDataFolder(), "config.yml.old");
         File oldDataFolder = new File(this.getPlugin().getDataFolder(), "SavedData");
         File newDataFolder = new File(this.getPlugin().getDataFolder(), "data");

         try {
            com.google.common.io.Files.move(oldConfigFile, oldConfigFileNew);
            if (oldDataFolder.exists() && oldDataFolder.isDirectory() && newDataFolder.exists() && newDataFolder.isDirectory()) {
               Folders.moveDirectoryContents(oldDataFolder, newDataFolder);
               Folders.deleteDirectory(oldDataFolder);
            }
         } catch (Exception ex) {
         }
      }
   }

   public synchronized int getIconGenerationModel() {
      return this.menuIconGenerationModel++;
   }

   @Generated
   @Override
   public String getPropertyNode() {
      return "Configuration";
   }

   static {
      for (Material m : Material.values()) {
         if (m.isTransparent() || !m.isSolid() || !m.isOccluding()) {
            transparentBlocks.add(m);
         }
      }
   }
}
