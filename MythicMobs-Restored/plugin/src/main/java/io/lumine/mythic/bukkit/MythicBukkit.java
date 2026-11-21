package io.lumine.mythic.bukkit;

// MODIFIED VERSION (GNU AGPL v3, section 5a): this file was changed from the upstream MythicMobs 5.14.0
// source. The checks that restricted "premium" features were removed; see MODIFICATIONS.md.

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.volatilecode.VolatileCodeDisabled;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.bukkit.adapters.BukkitSkillTriggers;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.clock.MythicClock;
import io.lumine.mythic.bukkit.clock.TimingsHandler;
import io.lumine.mythic.bukkit.commands.BaseCommand;
import io.lumine.mythic.bukkit.commands.menus.MenuCommand;
import io.lumine.mythic.bukkit.commands.mobs.SpawnCommand;
import io.lumine.mythic.bukkit.commands.pins.AddPinCommand;
import io.lumine.mythic.bukkit.commands.pins.PinsCommand;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.events.MythicLoadedEvent;
import io.lumine.mythic.bukkit.listeners.ItemDropEffectsListener;
import io.lumine.mythic.bukkit.listeners.ThreatTableListeners;
import io.lumine.mythic.bukkit.metrics.bStats;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.holograms.BukkitHologramFactory;
import io.lumine.mythic.bukkit.utils.holograms.HologramFactory;
import io.lumine.mythic.bukkit.utils.logging.ConsoleColor;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.dialogs.DialogManager;
import io.lumine.mythic.core.drops.DropExecutor;
import io.lumine.mythic.core.glow.GlowManager;
import io.lumine.mythic.core.holograms.HologramExecutor;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.skills.pins.PinExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableManager;
import io.lumine.mythic.core.skills.variables.VariableSerializer;
import io.lumine.mythic.core.skills.variables.serializers.InstantTypeAdapter;
import io.lumine.mythic.core.skills.variables.serializers.OptionalTypeAdapter;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import io.lumine.mythic.core.spawning.random.RandomSpawnerManager;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.NBTCompoundSerializer;
import io.lumine.mythic.core.utils.jnbt.NBTSerializer;
import io.lumine.mythic.core.utils.jnbt.Tag;
import io.lumine.mythic.core.worlds.WorldManager;
import java.io.File;
import java.time.Instant;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

public class MythicBukkit extends LuminePlugin implements MythicPlugin {
   private static MythicBukkit plugin;
   @Getter
   private BukkitBootstrap bootstrap;
   @Getter
   private BaseCommand baseCommand;
   @Getter
   private ConfigExecutor configuration;
   @Getter
   private PackExecutor packManager;
   @Getter
   private MythicClock clock;
   @Getter
   private PinExecutor pinManager;
   @Getter
   private RandomSpawnerManager randomSpawningManager;
   @Getter
   private MobExecutor mobManager;
   @Getter
   private PlayerManager playerManager;
   @Getter
   private WorldManager worldManager;
   @Getter
   private SkillExecutor skillManager;
   @Getter
   private DropExecutor dropManager;
   @Getter
   private ItemExecutor itemManager;
   @Getter
   private SpawnerManager spawnerManager;
   @Getter
   private VariableManager variableManager;
   @Getter
   private StatExecutor statManager;
   @Getter
   private MenuManager menuManager;
   @Getter
   private DialogManager dialogManager;
   @Getter
   private PlaceholderExecutor placeholderManager;
   @Getter
   private HologramManager hologramManager;
   @Getter
   private CinematicManager cinematicManager;
   @Getter
   private GlowManager glowManager;
   @Getter
   private CompatibilityManager compatibility;
   @Getter
   private BukkitHologramFactory hologramProvider;
   private VolatileCodeHandler volatileCodeHandler;
   @Getter
   private final TimingsHandler timingsHandler = new TimingsHandler();
   @Getter
   private BukkitAPIHelper APIHelper;
   @Getter
   private boolean loading = true;
   @Getter
   private boolean shuttingDown = false;
   @Getter
   private Boolean isUpdateAvailable = false;
   private static final Object volatilityHandler = initializeVolatility();
   private static final Object volatilltyHandler = initializeVolatillty();

   @Override
   public void load() {
      plugin = this;
      File statsFile = new File(this.getDataFolder(), "stats.yml");
      if (!statsFile.exists()) {
         this.saveResource("stats.yml", false);
      }

      this.statManager = new StatExecutor(this);
      MythicLogger.log(Text.colorizeLegacy("<rainbow>Mythic Loaded!"));
   }

   @Override
   public void enable() {
      MythicProvider.register(this);
      this.bootstrap = new BukkitBootstrap(this);
      SkillAdapter.initializeBukkit(this);
      MythicLogger.log(
         "Loading {0} for {1} {2}...", this.getDescription().getName(), ServerVersion.isPaper() ? "Paper" : "Spigot", ServerVersion.get().toString()
      );
      if (ServerVersion.isPaper()) {
         MythicLogger.log("The server is running Paper; enabled Paper exclusive functionality");
      } else {
         MythicLogger.log("The server is running Spigot; disabled Paper exclusive functionality");
      }

      Patterns.CompilePatterns();
      GsonProvider.update(
         builder -> builder.registerTypeAdapter(AbstractWorld.class, GsonProvider.standard().getAdapter(BukkitWorld.class))
            .registerTypeAdapter(Tag.class, new NBTSerializer())
            .registerTypeAdapter(CompoundTag.class, new NBTCompoundSerializer())
            .registerTypeAdapter(Variable.class, new VariableSerializer())
            .registerTypeAdapter(MetaSkillVariable.class, new MetaSkillVariable.Serializer())
            .registerTypeAdapter(ItemVariable.class, new ItemVariable.Serializer())
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
      );
      this.clock = new MythicClock(this);
      this.configuration = new ConfigExecutor(this);
      this.getBootstrap().initialize();
      this.configuration.firstLoad();
      this.packManager = new PackExecutor(this);
      this.hologramProvider = new BukkitHologramFactory();
      this.provideService(HologramFactory.class, this.hologramProvider);
      this.volatileCodeHandler = this.getVolatileCodeHandler();
      this.placeholderManager = new PlaceholderExecutor(this);
      this.compatibility = new CompatibilityManager(this);
      this.itemManager = new ItemExecutor(this);
      this.dropManager = new DropExecutor(this);
      this.pinManager = new PinExecutor(this);
      this.mobManager = new MobExecutor(this);
      this.playerManager = new PlayerManager(this);
      this.worldManager = new WorldManager(this);
      this.skillManager = new SkillExecutor(this);
      this.spawnerManager = new SpawnerManager(this);
      this.randomSpawningManager = new RandomSpawnerManager(this);
      this.variableManager = new VariableManager(this);
      this.menuManager = new MenuManager(this);
      this.dialogManager = new DialogManager(this);
      this.glowManager = new GlowManager(this);
      this.hologramManager = new HologramExecutor(this);
      this.getConfiguration().load(this);
      MythicLogger.log("MythicMobs configuration file loaded successfully.");
      this.cinematicManager = new CinematicManager(this);
      this.clock.load(this);
      PluginManager manager = this.getServer().getPluginManager();
      manager.registerEvents(new BukkitSkillTriggers(this), this);
      manager.registerEvents(new ThreatTableListeners(), this);
      manager.registerEvents(this.getItemManager(), this);
      manager.registerEvents(new ItemDropEffectsListener(this), this);
      Schedulers.sync().runRepeating(task -> {
         VolatileParticleHandler ph = this.volatileCodeHandler.getParticleHandler();
         if (ph != null) {
            ph.flushTick();
         }
      }, 1L, 1L);
      this.APIHelper = new BukkitAPIHelper();
      this.baseCommand = new BaseCommand(this);
      this.registerCommand("mythicmobs", this.baseCommand);
      this.registerCommand("spawnmob", new SpawnCommand(this));
      this.registerCommand("pin", new PinsCommand(this));
      this.registerCommand("addpin", new AddPinCommand(this));
      if (isVolatile()) {
         this.registerCommand("mythicmobsmenu", new MenuCommand(this));
      }

      // MODIFIED: bStats reporting is not started in this unofficial build, so it does not pollute upstream statistics.

      this.getMobManager().ScanWorld();
      Schedulers.sync().runLater(() -> {
         this.clock.runPostReload();
         this.placeholderManager.setAcceptingRegisteredParsers(false);
      }, 40L);
      MythicLogger.log(
         ""
            + ConsoleColor.GREEN
            + ConsoleColor.CHECK_MARK
            + " MythicMobs"
            + " (modified build)"
            + " v"
            + this.getVersion()
            + " ( build "
            + this.getBuildNumber()
            + " ) has been successfully loaded!"
            + ConsoleColor.RESET
      );
      new MythicLoadedEvent(this).callEvent();
   }

   @Override
   public void disable() {
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "------------------------------------------------------------" + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.YELLOW + "Unloading Mythic..." + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "------------------------------------------------------------" + ConsoleColor.RESET);
      this.shuttingDown = true;
      if (this.getSpawnerManager() != null) {
         this.getSpawnerManager().unloadAllSpawners();
         MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET + "Spawners saved and unloaded..." + ConsoleColor.RESET);
      }

      this.getMobManager().despawnAllMobs();
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET + "Mobs saved and unloaded..." + ConsoleColor.RESET);
      if (this.volatileCodeHandler != null && this.volatileCodeHandler.getAIHandler() != null) {
         this.volatileCodeHandler.getAIHandler().shutdownAsyncPathfinding();
      }

      this.volatileCodeHandler = null;
      this.configuration.unload();
      this.variableManager.terminate();
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET + "Variables saved..." + ConsoleColor.RESET);
      this.compatibility.terminate();
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET + "All active settings have been saved!" + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "- " + ConsoleColor.RESET + "Mythic has been unloaded!" + ConsoleColor.RESET);
      MythicLogger.log("" + ConsoleColor.PURPLE + ConsoleColor.BOLD + "------------------------------------------------------------" + ConsoleColor.RESET);
   }

   public static MythicBukkit inst() {
      return plugin;
   }

   private static Object initializeVolatility() {
      try {
         return Class.forName("io.lumine.mythic.core.CarsonJF");
      } catch (ClassNotFoundException e) {
         return null;
      } catch (Exception ex) {
         throw new RuntimeException("An error occurred while enabling CarsonJF", ex);
      }
   }

   @Override
   public VolatileCodeHandler getVolatileCodeHandler() {
      if (this.volatileCodeHandler != null) {
         return this.volatileCodeHandler;
      } else {
         VolatileCodeHandler VCH = new VolatileCodeDisabled();
         String version = ServerVersion.getNMS().toString();

         try {
            Class<?> clazz = Class.forName("io.lumine.mythic.core.volatilecode.VolatileCodeEnabled_" + version);
            if (VolatileCodeHandler.class.isAssignableFrom(clazz)) {
               VCH = (VolatileCodeHandler)clazz.getConstructor().newInstance();
            }
         } catch (ClassNotFoundException e) {
            MythicLogger.error(ColorString.get("&6--====|||| &c&lMythic &6||||====--"));
            MythicLogger.error("This version of Mythic is not fully compatible with your version of Bukkit.");
            MythicLogger.error("Some features may be limited or disabled until you use a compatible version.");
            MythicLogger.error("Server NMS Version Detected - {0}", version);
         } catch (Exception ex) {
            throw new RuntimeException("Unknown exception loading version handler. Volatile code has been disabled.", ex);
         }

         this.volatileCodeHandler = VCH;
         return VCH;
      }
   }

   /**
    * MODIFIED: upstream returned {@code volatilltyHandler != null}, i.e. whether the separate premium module
    * ({@code io.lumine.mythic.utils.CarsonJF}) was present. This build enables the features that were gated
    * behind that check (all of which are implemented in this code base). See MODIFICATIONS.md.
    */
   public static final boolean isVolatile() {
      return true;
   }

   private static Object initializeVolatillty() {
      try {
         return Class.forName("io.lumine.mythic.utils.CarsonJF");
      } catch (ClassNotFoundException e) {
         return null;
      } catch (Exception ex) {
         throw new RuntimeException("An error occurred while enabling CarsonJF", ex);
      }
   }

   public String getVersion() {
      return this.getDescription().getVersion().split("-")[0];
   }

   public String getBuildNumber() {
      String[] split = this.getDescription().getVersion().split("-");
      if (split.length == 2) {
         return split[1];
      } else {
         return split.length == 3 ? split[2] : "????";
      }
   }
}
