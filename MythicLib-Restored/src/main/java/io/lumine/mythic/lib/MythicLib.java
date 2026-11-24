package io.lumine.mythic.lib;

import io.lumine.mythic.lib.api.crafting.recipes.MythicCraftingManager;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.MegaWorkbenchMapping;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.SuperWorkbenchMapping;
import io.lumine.mythic.lib.api.crafting.uifilters.MythicItemUIFilter;
import io.lumine.mythic.lib.api.event.armorequip.ArmorEquipEvent;
import io.lumine.mythic.lib.api.placeholders.MythicPlaceholders;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.HealthScaleCommand;
import io.lumine.mythic.lib.command.MMOTempStatCommand;
import io.lumine.mythic.lib.comp.FabledModule;
import io.lumine.mythic.lib.comp.McMMOModule;
import io.lumine.mythic.lib.comp.adventure.AdventureParser;
import io.lumine.mythic.lib.comp.anticheat.AntiCheatSupport;
import io.lumine.mythic.lib.comp.anticheat.SpartanPlugin;
import io.lumine.mythic.lib.comp.dualwield.DualWieldHook;
import io.lumine.mythic.lib.comp.dualwield.RealDualWieldHook;
import io.lumine.mythic.lib.comp.flags.FlagHandler;
import io.lumine.mythic.lib.comp.flags.FlagPlugin;
import io.lumine.mythic.lib.comp.flags.ResidenceFlags;
import io.lumine.mythic.lib.comp.flags.WorldGuardFlags;
import io.lumine.mythic.lib.comp.formula.FormulaParser;
import io.lumine.mythic.lib.comp.mythicmobs.MythicMobsAttackHandler;
import io.lumine.mythic.lib.comp.mythicmobs.MythicMobsHook;
import io.lumine.mythic.lib.comp.placeholder.DefaultPlaceholderParser;
import io.lumine.mythic.lib.comp.placeholder.MythicPlaceholderAPIHook;
import io.lumine.mythic.lib.comp.placeholder.PlaceholderAPIHook;
import io.lumine.mythic.lib.comp.placeholder.PlaceholderAPIParser;
import io.lumine.mythic.lib.comp.placeholder.PlaceholderParser;
import io.lumine.mythic.lib.comp.profile.LegacyProfiles;
import io.lumine.mythic.lib.comp.profile.ProfileMode;
import io.lumine.mythic.lib.comp.protocollib.DamageParticleCap;
import io.lumine.mythic.lib.glow.GlowModule;
import io.lumine.mythic.lib.glow.provided.MythicGlowModule;
import com.google.gson.Gson;
import io.lumine.mythic.lib.gui.PluginInventory;
import io.lumine.mythic.lib.hologram.HologramFactory;
import io.lumine.mythic.lib.hologram.HologramFactoryList;
import io.lumine.mythic.lib.hologram.factory.BukkitHologramFactory;
import io.lumine.mythic.lib.hologram.factory.LegacyBukkitHologramFactory;
import io.lumine.mythic.lib.listener.AttackEffects;
import io.lumine.mythic.lib.listener.CustomProjectileDamage;
import io.lumine.mythic.lib.listener.DamageReduction;
import io.lumine.mythic.lib.listener.ElementalDamage;
import io.lumine.mythic.lib.listener.MitigationMechanics;
import io.lumine.mythic.lib.listener.PlayerListener;
import io.lumine.mythic.lib.listener.PvpListener;
import io.lumine.mythic.lib.listener.SkillTriggers;
import io.lumine.mythic.lib.listener.event.AttackEventListener;
import io.lumine.mythic.lib.listener.option.FixAttributeModifiers;
import io.lumine.mythic.lib.listener.option.FixMovementSpeed;
import io.lumine.mythic.lib.listener.option.HealthScale;
import io.lumine.mythic.lib.listener.option.VanillaDamageModifiers;
import io.lumine.mythic.lib.manager.ConfigManager;
import io.lumine.mythic.lib.manager.DamageManager;
import io.lumine.mythic.lib.manager.ElementManager;
import io.lumine.mythic.lib.manager.EntityManager;
import io.lumine.mythic.lib.manager.FakeEventManager;
import io.lumine.mythic.lib.manager.IndicatorManager;
import io.lumine.mythic.lib.manager.JsonManager;
import io.lumine.mythic.lib.manager.ModifierManager;
import io.lumine.mythic.lib.manager.MythicLibCommandManager;
import io.lumine.mythic.lib.manager.SkillManager;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.metrics.bukkit.Metrics;
import io.lumine.mythic.lib.util.MMOPlugin;
import io.lumine.mythic.lib.util.gson.MythicLibGson;
import io.lumine.mythic.lib.util.loadingorder.DependencyCycleCheck;
import io.lumine.mythic.lib.util.loadingorder.DependencyNode;
import io.lumine.mythic.lib.util.network.MythicPacketSniffer;
import io.lumine.mythic.lib.version.ServerVersion;
import io.lumine.mythic.lib.version.SpigotPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicLib extends JavaPlugin {
   public static MythicLib plugin;
   private final DamageManager damageManager = new DamageManager();
   private final MythicLibCommandManager commandManager = new MythicLibCommandManager();
   private final EntityManager entityManager = new EntityManager();
   private final StatManager statManager = new StatManager();
   private final JsonManager jsonManager = new JsonManager();
   private final ConfigManager configManager = new ConfigManager();
   private final ElementManager elementManager = new ElementManager();
   private final SkillManager skillManager = new SkillManager();
   private final ModifierManager modifierManager = new ModifierManager();
   private final FlagHandler flagHandler = new FlagHandler();
   private final IndicatorManager indicatorManager = new IndicatorManager();
   private final FakeEventManager fakeEventManager = new FakeEventManager();
   private final List<MMOPlugin> mmoPlugins = new ArrayList<>();
   private Gson gson;
   private AntiCheatSupport antiCheatSupport;
   private ServerVersion version;
   private HologramFactory hologramFactory;
   private AttackEffects attackEffects;
   private MitigationMechanics mitigationMechanics;
   private AdventureParser adventureParser;
   private PlaceholderParser placeholderParser;
   private GlowModule glowModule;
   @Nullable
   private ProfileMode profileMode;

   public void onLoad() {
      plugin = this;
      this.getLogger().log(Level.INFO, "Plugin file is called '" + this.getFile().getName() + "'");

      try {
         this.version = new ServerVersion();
         this.getLogger().log(Level.INFO, "Detected Bukkit Version: " + this.version.getCraftBukkitVersion());
      } catch (Exception exception) {
         this.getLogger().log(Level.WARNING, "Your server version is not compatible.", exception);
         Bukkit.getPluginManager().disablePlugin(this);
         return;
      }

      if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
         this.flagHandler.registerPlugin(new WorldGuardFlags());
         this.getLogger().log(Level.INFO, "Hooked onto WorldGuard");
      }

      this.adventureParser = new AdventureParser();
   }

   public void onEnable() {
      new Metrics(this);
      this.gson = MythicLibGson.build();
      new SpigotPlugin(90306, this).checkForUpdate();
      this.saveDefaultConfig();
      int configVersion = this.getConfig().contains("config-version", true) ? this.getConfig().getInt("config-version") : -1;
      int defConfigVersion = this.getConfig().getDefaults().getInt("config-version");
      if (configVersion != defConfigVersion) {
         this.getLogger().warning("You may be using an outdated config.yml!");
         this.getLogger().warning("(Your config version: '" + configVersion + "' | Expected config version: '" + defConfigVersion + "')");
      }

      if (this.version.isUnder(1, 20, 5)) {
         new MythicPacketSniffer(this);
      }

      Bukkit.getServicesManager().register(HologramFactory.class, new LegacyBukkitHologramFactory(), this, ServicePriority.Lowest);
      if (this.version.isAbove(1, 19, 4)) {
         Bukkit.getServicesManager().register(HologramFactory.class, new BukkitHologramFactory(), this, ServicePriority.Low);
      }

      for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
         if (plugin instanceof MMOPlugin) {
            this.mmoPlugins.add((MMOPlugin)plugin);
         }
      }

      Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
      Bukkit.getPluginManager().registerEvents(this.damageManager, this);
      Bukkit.getPluginManager().registerEvents(new DamageReduction(), this);
      Bukkit.getPluginManager().registerEvents(this.attackEffects = new AttackEffects(), this);
      Bukkit.getPluginManager().registerEvents(new CustomProjectileDamage(), this);
      Bukkit.getPluginManager().registerEvents(this.mitigationMechanics = new MitigationMechanics(), this);
      Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
      Bukkit.getPluginManager().registerEvents(new MythicCraftingManager(), this);
      Bukkit.getPluginManager().registerEvents(new SkillTriggers(), this);
      Bukkit.getPluginManager().registerEvents(new ElementalDamage(), this);
      Bukkit.getPluginManager().registerEvents(new PvpListener(), this);
      ArmorEquipEvent.registerListener(this);
      if (this.getConfig().getBoolean("vanilla-damage-modifiers.enabled")) {
         Bukkit.getPluginManager().registerEvents(new VanillaDamageModifiers(this.getConfig().getConfigurationSection("vanilla-damage-modifiers")), this);
      }

      if (this.getConfig().getBoolean("health-scale.enabled")) {
         Bukkit.getPluginManager()
            .registerEvents(new HealthScale(this.getConfig().getDouble("health-scale.scale"), this.getConfig().getInt("health-scale.delay", 0)), this);
      }

      if (this.getConfig().getBoolean("fix-movement-speed")) {
         Bukkit.getPluginManager().registerEvents(new FixMovementSpeed(), this);
      }

      if (this.getConfig().getBoolean("fix_reset_attribute_modifiers.enabled")) {
         try {
            new FixAttributeModifiers(this.getConfig().getConfigurationSection("fix_reset_attribute_modifiers"));
         } catch (RuntimeException exception) {
            this.getLogger().log(Level.WARNING, "Could not enable fix_reset_attribute_modifiers: " + exception.getMessage());
         }
      }

      try {
         HologramFactoryList found = HologramFactoryList.valueOf(UtilityMethods.enumName(this.getConfig().getString("hologram-provider")));
         this.hologramFactory = found.provide();
         Bukkit.getServicesManager().register(HologramFactory.class, this.hologramFactory, this, ServicePriority.Normal);
         this.getLogger().log(Level.INFO, "Hooked onto " + found.getName() + " (holograms)");
      } catch (Throwable throwable) {
         this.hologramFactory = HologramFactoryList.LEGACY_ARMOR_STANDS.provide();
         this.getLogger()
            .log(
               Level.WARNING,
               "Could not hook onto hologram provider " + this.getConfig().getString("hologram-provider") + ", using default: " + throwable.getMessage()
            );
      }

      if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
         this.damageManager.registerHandler(new MythicMobsAttackHandler());
         Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);
         MythicItemUIFilter.register();
         this.getLogger().log(Level.INFO, "Hooked onto MythicMobs");
      }

      if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
         this.flagHandler.registerPlugin(new ResidenceFlags());
         this.getLogger().log(Level.INFO, "Hooked onto Residence");
      }

      if (Bukkit.getPluginManager().getPlugin("Spartan") != null) {
         this.antiCheatSupport = new SpartanPlugin();
         this.getLogger().log(Level.INFO, "Hooked onto Spartan");
      }

      if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
         if (this.getConfig().getBoolean("damage-particles-cap.enabled")) {
            new DamageParticleCap(this.getConfig().getInt("damage-particles-cap.max-per-tick"));
         }

         this.getLogger().log(Level.INFO, "Hooked onto ProtocolLib");
      }

      if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
         new McMMOModule();
         this.getLogger().log(Level.INFO, "Hooked onto mcMMO");
      }

      if (Bukkit.getPluginManager().getPlugin("Fabled") != null) {
         new FabledModule();
         this.getLogger().log(Level.INFO, "Hooked onto Fabled");
      }

      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         MythicPlaceholders.registerPlaceholder(new MythicPlaceholderAPIHook());
         new PlaceholderAPIHook().register();
         this.placeholderParser = new PlaceholderAPIParser();
         this.getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
      } else {
         this.placeholderParser = new DefaultPlaceholderParser();
      }

      if (Bukkit.getPluginManager().getPlugin("RealDualWield") != null) {
         Bukkit.getPluginManager().registerEvents(new RealDualWieldHook(), this);
         this.getLogger().log(Level.INFO, "Hooked onto RealDualWield");
      }

      if (Bukkit.getPluginManager().getPlugin("DualWield") != null) {
         Bukkit.getPluginManager().registerEvents(new DualWieldHook(), this);
         this.getLogger().log(Level.INFO, "Hooked onto DualWield");
      }

      Stack<DependencyNode> dependencyCycle = new DependencyCycleCheck().checkCycle();
      if (dependencyCycle != null) {
         this.getLogger().log(Level.WARNING, "Found a dependency cycle! Please make sure that the plugins involved load with no errors.");
         this.getLogger().log(Level.WARNING, "Plugin dependency cycle: " + dependencyCycle);
      }

      this.indicatorManager.load(this.getConfig());
      if (this.glowModule == null) {
         this.glowModule = new MythicGlowModule();
         this.glowModule.enable();
      }

      this.getCommand("mmotempstat").setExecutor(new MMOTempStatCommand());
      this.getCommand("healthscale").setExecutor(new HealthScaleCommand());
      this.getCommand("superworkbench").setExecutor(SuperWorkbenchMapping.SWB);
      Bukkit.getPluginManager().registerEvents(SuperWorkbenchMapping.SWB, this);
      this.getCommand("megaworkbench").setExecutor(MegaWorkbenchMapping.MWB);
      Bukkit.getPluginManager().registerEvents(MegaWorkbenchMapping.MWB, this);
      this.damageManager.initialize(false);
      this.commandManager.initialize(false);
      this.skillManager.initialize(false);
      this.elementManager.reload(false);
      Bukkit.getOnlinePlayers().forEach(MMOPlayerData::setup);
      Bukkit.getScheduler().runTaskTimer(this, MMOPlayerData::flushOfflinePlayerData, 72000L, 72000L);
      this.configManager.reload();
      this.statManager.initialize(false);
   }

   public void reload() {
      this.reloadConfig();
      this.statManager.initialize(true);
      this.attackEffects.reload();
      this.mitigationMechanics.reload();
      this.skillManager.initialize(true);
      this.configManager.reload();
      this.elementManager.reload(true);
      this.indicatorManager.reload(this.getConfig());

      for (MMOPlayerData online : MMOPlayerData.getLoaded()) {
         online.getStatMap().flushCache();
      }
   }

   public void onDisable() {
      UtilityMethods.closeOpenViewsOfType(PluginInventory.class);
      this.glowModule.disable();
   }

   public static MythicLib inst() {
      return plugin;
   }

   public Gson getGson() {
      return this.gson;
   }

   public ServerVersion getVersion() {
      return this.version;
   }

   @Deprecated
   public JsonManager getJson() {
      return this.jsonManager;
   }

   public FakeEventManager getFakeEvents() {
      return this.fakeEventManager;
   }

   @Deprecated
   public MythicLibCommandManager getCommand() {
      return this.getCommands();
   }

   public MythicLibCommandManager getCommands() {
      return this.commandManager;
   }

   public DamageManager getDamage() {
      return this.damageManager;
   }

   public EntityManager getEntities() {
      return this.entityManager;
   }

   public SkillManager getSkills() {
      return this.skillManager;
   }

   public ModifierManager getModifiers() {
      return this.modifierManager;
   }

   public ElementManager getElements() {
      return this.elementManager;
   }

   public StatManager getStats() {
      return this.statManager;
   }

   public ConfigManager getMMOConfig() {
      return this.configManager;
   }

   public FlagHandler getFlags() {
      return this.flagHandler;
   }

   public PlaceholderParser getPlaceholderParser() {
      return this.placeholderParser;
   }

   public AttackEffects getAttackEffects() {
      return this.attackEffects;
   }

   public AntiCheatSupport getAntiCheat() {
      return this.antiCheatSupport;
   }

   @Deprecated
   public FormulaParser getFormulaParser() {
      return FormulaParser.getInstance();
   }

   @Nullable
   public GlowModule getGlowing() {
      return this.glowModule;
   }

   public void useLegacyProfiles() {
      Validate.isTrue(this.profileMode == null, "Profiles have already been enabled/disabled");
      this.profileMode = ProfileMode.LEGACY;
      Bukkit.getPluginManager().registerEvents(new LegacyProfiles(), this);
      this.getLogger().log(Level.INFO, "Hooked onto spigot-based ProfileAPI");
   }

   public void useProxyProfiles() {
      Validate.isTrue(this.profileMode == null, "Profiles have already been enabled/disabled");
      this.profileMode = ProfileMode.PROXY;
      this.getLogger().log(Level.INFO, "Hooked onto proxy-based ProfileAPI");
   }

   public boolean hasProfiles() {
      return this.profileMode != null;
   }

   @Nullable
   public ProfileMode getProfileMode() {
      return this.profileMode;
   }

   @Deprecated
   public void handleFlags(FlagPlugin flagPlugin) {
      this.getFlags().registerPlugin(flagPlugin);
   }

   public boolean hasAntiCheat() {
      return this.antiCheatSupport != null;
   }

   public String parseColors(String format) {
      return this.adventureParser.parse(format);
   }

   public List<String> parseColors(String... format) {
      return this.parseColors(Arrays.asList(format));
   }

   public List<String> parseColors(List<String> format) {
      return new ArrayList<>(this.adventureParser.parse(format));
   }

   public AdventureParser getAdventureParser() {
      return this.adventureParser;
   }

   public File getJarFile() {
      return plugin.getFile();
   }

   @NotNull
   public List<MMOPlugin> getMMOPlugins() {
      return new ArrayList<>(this.mmoPlugins);
   }

   @NotNull
   public HologramFactory getHologramFactory() {
      return this.hologramFactory;
   }
}
