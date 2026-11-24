package net.Indyuce.mmocore;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.metrics.bukkit.Metrics;
import io.lumine.mythic.lib.player.modifier.PlayerModifier;
import io.lumine.mythic.lib.util.MMOPlugin;
import io.lumine.mythic.lib.version.SpigotPlugin;
import java.io.File;
import java.util.logging.Level;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.AttributeModifier;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import net.Indyuce.mmocore.command.MMOCoreCommandTreeRoot;
import net.Indyuce.mmocore.command.api.ToggleableCommand;
import net.Indyuce.mmocore.comp.citizens.CitizenInteractEventListener;
import net.Indyuce.mmocore.comp.citizens.CitizensMMOLoader;
import net.Indyuce.mmocore.comp.mythicmobs.MythicHook;
import net.Indyuce.mmocore.comp.mythicmobs.MythicMobsMMOLoader;
import net.Indyuce.mmocore.comp.placeholder.DefaultParser;
import net.Indyuce.mmocore.comp.placeholder.PlaceholderAPIParser;
import net.Indyuce.mmocore.comp.placeholder.PlaceholderParser;
import net.Indyuce.mmocore.comp.region.DefaultRegionHandler;
import net.Indyuce.mmocore.comp.region.RegionHandler;
import net.Indyuce.mmocore.comp.region.WorldGuardMMOLoader;
import net.Indyuce.mmocore.comp.region.WorldGuardRegionHandler;
import net.Indyuce.mmocore.comp.region.pvpmode.PvPModeListener;
import net.Indyuce.mmocore.comp.vault.VaultEconomy;
import net.Indyuce.mmocore.comp.vault.VaultMMOLoader;
import net.Indyuce.mmocore.guild.GuildModule;
import net.Indyuce.mmocore.guild.GuildModuleType;
import net.Indyuce.mmocore.guild.GuildRelationHandler;
import net.Indyuce.mmocore.guild.provided.Guild;
import net.Indyuce.mmocore.guild.provided.MMOCoreGuildModule;
import net.Indyuce.mmocore.guild.provided.YAMLGuildDataManager;
import net.Indyuce.mmocore.manager.ActionBarManager;
import net.Indyuce.mmocore.manager.AttributeManager;
import net.Indyuce.mmocore.manager.ClassManager;
import net.Indyuce.mmocore.manager.ConfigItemManager;
import net.Indyuce.mmocore.manager.ConfigManager;
import net.Indyuce.mmocore.manager.DropTableManager;
import net.Indyuce.mmocore.manager.ExperienceManager;
import net.Indyuce.mmocore.manager.InventoryManager;
import net.Indyuce.mmocore.manager.LootChestManager;
import net.Indyuce.mmocore.manager.MMOLoadManager;
import net.Indyuce.mmocore.manager.QuestManager;
import net.Indyuce.mmocore.manager.RestrictionManager;
import net.Indyuce.mmocore.manager.SkillManager;
import net.Indyuce.mmocore.manager.SkillTreeManager;
import net.Indyuce.mmocore.manager.SoundManager;
import net.Indyuce.mmocore.manager.StatManager;
import net.Indyuce.mmocore.manager.WaypointManager;
import net.Indyuce.mmocore.manager.data.DataProvider;
import net.Indyuce.mmocore.manager.data.GuildDataManager;
import net.Indyuce.mmocore.manager.data.LegacyDataProvider;
import net.Indyuce.mmocore.manager.data.PlayerDataManager;
import net.Indyuce.mmocore.manager.data.sql.SQLDataHandler;
import net.Indyuce.mmocore.manager.profession.AlchemyManager;
import net.Indyuce.mmocore.manager.profession.CustomBlockManager;
import net.Indyuce.mmocore.manager.profession.EnchantManager;
import net.Indyuce.mmocore.manager.profession.FishingManager;
import net.Indyuce.mmocore.manager.profession.ProfessionManager;
import net.Indyuce.mmocore.manager.profession.SmithingManager;
import net.Indyuce.mmocore.manager.social.BoosterManager;
import net.Indyuce.mmocore.manager.social.PartyManager;
import net.Indyuce.mmocore.manager.social.RequestManager;
import net.Indyuce.mmocore.party.PartyModule;
import net.Indyuce.mmocore.party.PartyModuleType;
import net.Indyuce.mmocore.party.PartyRelationHandler;
import net.Indyuce.mmocore.party.provided.MMOCorePartyModule;
import net.Indyuce.mmocore.script.mechanic.ExperienceMechanic;
import net.Indyuce.mmocore.script.mechanic.ManaMechanic;
import net.Indyuce.mmocore.script.mechanic.StaminaMechanic;
import net.Indyuce.mmocore.script.mechanic.StelliumMechanic;
import net.Indyuce.mmocore.skill.cast.SkillCastingMode;
import net.Indyuce.mmocore.skill.trigger.MMOCoreTriggerType;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class MMOCore extends MMOPlugin {
   public static MMOCore plugin;
   public final WaypointManager waypointManager = new WaypointManager();
   public final SoundManager soundManager = new SoundManager();
   public final RequestManager requestManager = new RequestManager();
   public final ConfigItemManager configItems = new ConfigItemManager();
   public final ActionBarManager actionBarManager = new ActionBarManager();
   public final SkillManager skillManager = new SkillManager();
   public final ClassManager classManager = new ClassManager();
   public final DropTableManager dropTableManager = new DropTableManager();
   public final BoosterManager boosterManager = new BoosterManager();
   public final AttributeManager attributeManager = new AttributeManager();
   public final PartyManager partyManager = new PartyManager();
   public final QuestManager questManager = new QuestManager();
   public final ProfessionManager professionManager = new ProfessionManager();
   public final ExperienceManager experience = new ExperienceManager();
   public final LootChestManager lootChests = new LootChestManager();
   public final MMOLoadManager loadManager = new MMOLoadManager();
   public final RestrictionManager restrictionManager = new RestrictionManager();
   public final SkillTreeManager skillTreeManager = new SkillTreeManager();
   public final StatManager statManager = new StatManager();
   public final GuildDataManager nativeGuildManager = new YAMLGuildDataManager();
   public final PlayerDataManager playerDataManager = new PlayerDataManager(this);
   @Deprecated
   public final DataProvider dataProvider = new LegacyDataProvider();
   public final CustomBlockManager mineManager = new CustomBlockManager();
   public final FishingManager fishingManager = new FishingManager();
   public final AlchemyManager alchemyManager = new AlchemyManager();
   public final EnchantManager enchantManager = new EnchantManager();
   public final SmithingManager smithingManager = new SmithingManager();
   @NotNull
   public ConfigManager configManager;
   public VaultEconomy economy;
   public RegionHandler regionHandler = new DefaultRegionHandler();
   public PlaceholderParser placeholderParser = new DefaultParser();
   @NotNull
   public PartyModule partyModule;
   public GuildModule guildModule;

   public MMOCore() {
      plugin = this;
   }

   public void onLoad() {
      this.getLogger().log(Level.INFO, "Plugin file is called '" + this.getFile().getName() + "'");
      MythicLib.plugin.getEntities().registerRelationHandler(new PartyRelationHandler());
      MythicLib.plugin.getEntities().registerRelationHandler(new GuildRelationHandler());
      PlayerModifier.registerPlayerModifierType("attribute", configObject -> new AttributeModifier(configObject), new String[0]);
      MMOCoreTriggerType.registerAll();
      MythicLib.plugin.getSkills().registerMechanic("mana", config -> new ManaMechanic(config), new String[0]);
      MythicLib.plugin.getSkills().registerMechanic("stamina", config -> new StaminaMechanic(config), new String[0]);
      MythicLib.plugin.getSkills().registerMechanic("stellium", config -> new StelliumMechanic(config), new String[0]);
      MythicLib.plugin.getSkills().registerMechanic("mmocore_experience", config -> new ExperienceMechanic(config), new String[0]);
      if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
         this.loadManager.registerLoader(new WorldGuardMMOLoader());
      }

      if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
         this.loadManager.registerLoader(new CitizensMMOLoader());
      }

      if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
         this.loadManager.registerLoader(new VaultMMOLoader());
      }

      if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
         this.loadManager.registerLoader(new MythicMobsMMOLoader());
      }
   }

   public void onEnable() {
      new SpigotPlugin(70575, this).checkForUpdate();
      new Metrics(this);
      this.saveDefaultConfig();
      int count = this.getConfig().contains("config-version", true) ? this.getConfig().getInt("config-version") : -1;
      int configVersion = this.getConfig().getDefaults().getInt("config-version");
      if (count != configVersion) {
         this.getLogger().warning("You may be using an outdated config.yml!");
         this.getLogger().warning("(Your config version: '" + count + "' | Expected config version: '" + configVersion + "')");
      }

      if (this.getConfig().isConfigurationSection("mysql") && this.getConfig().getBoolean("mysql.enabled")) {
         SQLDataSource sqlDataSource = new SQLDataSource(this);
         this.playerDataManager.setDataHandler(new SQLDataHandler(sqlDataSource));
      }

      if (this.getConfig().isConfigurationSection("default-playerdata")) {
         this.playerDataManager.loadDefaultData(this.getConfig().getConfigurationSection("default-playerdata"));
      }

      if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
         this.economy = new VaultEconomy();
      }

      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         this.placeholderParser = new PlaceholderAPIParser();
         this.getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
      }

      if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
         Bukkit.getPluginManager().registerEvents(new CitizenInteractEventListener(), this);
         this.getLogger().log(Level.INFO, "Hooked onto Citizens");
      }

      if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
         this.regionHandler = new WorldGuardRegionHandler();
         if (this.getConfig().getBoolean("pvp_mode.enabled")) {
            Bukkit.getPluginManager().registerEvents(new PvPModeListener(), this);
         }

         this.getLogger().log(Level.INFO, "Hooked onto WorldGuard");
      }

      if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
         Bukkit.getServer().getPluginManager().registerEvents(new MythicHook(), this);
         plugin.getLogger().log(Level.INFO, "Hooked onto MythicMobs");
      }

      (new BukkitRunnable() {
         public void run() {
            for (PlayerData player : PlayerData.getAll()) {
               if (player.isOnline() && !player.getPlayer().isDead()) {
                  for (PlayerResource resource : PlayerResource.values()) {
                     double regenAmount = player.getProfess().getHandler(resource).getRegen(player);
                     if (regenAmount != 0.0) {
                        resource.regen(player, regenAmount);
                     }
                  }
               }
            }
         }
      }).runTaskTimer(plugin, 100L, 20L);
      if (Bukkit.getPluginManager().getPlugin("MMOMana") != null) {
         this.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "MMOCore is not meant to be used with MMOItems ManaAndStamina");
         this.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Please read the installation guide!");
      } else {
         this.initializePlugin(false);

         try {
            String text = UtilityMethods.enumName(this.getConfig().getString("party-plugin"));
            PartyModuleType partyModuleType = PartyModuleType.valueOf(text);
            Validate.isTrue(partyModuleType.isValid(), "Plugin " + partyModuleType.getPluginName() + " is not installed");
            this.partyModule = partyModuleType.provideModule();
            this.getLogger().log(Level.INFO, "Hooked parties onto " + partyModuleType.getPluginName());
         } catch (Throwable exception) {
            this.getLogger().log(Level.WARNING, "Could not initialize party module: " + exception.getMessage());
            this.partyModule = new MMOCorePartyModule();
         }

         try {
            String text = UtilityMethods.enumName(this.getConfig().getString("guild-plugin"));
            GuildModuleType guildModuleType = GuildModuleType.valueOf(text);
            Validate.isTrue(guildModuleType.isValid(), "Plugin '" + guildModuleType.name() + "' is not installed");
            this.guildModule = guildModuleType.provideModule();
            this.getLogger().log(Level.INFO, "Hooked guilds onto " + guildModuleType.getPluginName());
         } catch (RuntimeException exception) {
            this.getLogger().log(Level.WARNING, "Could not initialize guild module: " + exception.getMessage());
            this.guildModule = new MMOCoreGuildModule();
         }

         try {
            SkillCastingMode skillCastingMode = SkillCastingMode.valueOf(UtilityMethods.enumName(this.getConfig().getString("skill-casting.mode")));
            skillCastingMode.setCurrent(this.getConfig().getConfigurationSection("skill-casting"));
         } catch (RuntimeException exception) {
            this.getLogger().log(Level.WARNING, "Could not load skill casting: " + exception.getMessage());
         }

         try {
            Class.forName("net.Indyuce.mmocore.MMOCoreBukkit").getConstructor(MMOCore.class).newInstance(this);
         } catch (Throwable exception) {
            exception.printStackTrace();
            throw new RuntimeException("Cannot run an API build on Spigot!");
         }

         this.playerDataManager.initialize(EventPriority.LOW, EventPriority.NORMAL);
         this.nativeGuildManager.load();
         ToggleableCommand.register();
         MMOCoreCommandTreeRoot mmoCoreCommandTreeRoot = new MMOCoreCommandTreeRoot();
         this.getCommand("mmocore").setExecutor(mmoCoreCommandTreeRoot);
         this.getCommand("mmocore").setTabCompleter(mmoCoreCommandTreeRoot);
      }
   }

   public void onDisable() {
      for (Guild guild : this.nativeGuildManager.getAll()) {
         this.nativeGuildManager.save(guild);
      }

      this.playerDataManager.close();
      this.mineManager.resetRemainingBlocks();
      this.lootChests.getActive().forEach(chest -> chest.expire(false));
   }

   public void initializePlugin(boolean clearBefore) {
      if (clearBefore) {
         this.reloadConfig();
      }

      this.configManager = new ConfigManager();
      this.skillManager.initialize(clearBefore);
      this.mineManager.initialize(clearBefore);
      this.partyManager.initialize(clearBefore);
      this.attributeManager.initialize(clearBefore);
      this.experience.initialize(clearBefore);
      this.dropTableManager.initialize(clearBefore);
      this.statManager.initialize(clearBefore);
      this.professionManager.initialize(clearBefore);
      this.skillTreeManager.initialize(clearBefore);
      this.classManager.initialize(clearBefore);
      this.questManager.initialize(clearBefore);
      this.lootChests.initialize(clearBefore);
      this.restrictionManager.initialize(clearBefore);
      this.waypointManager.initialize(clearBefore);
      this.requestManager.initialize(clearBefore);
      this.soundManager.initialize(clearBefore);
      this.configItems.initialize(clearBefore);
      InventoryManager.load();
      if (this.getConfig().isConfigurationSection("action-bar")) {
         this.actionBarManager.reload(this.getConfig().getConfigurationSection("action-bar"));
      }

      if (clearBefore) {
         PlayerData.getAll().forEach(PlayerData::reload);
      }
   }

   public static void log(String message) {
      log(Level.INFO, message);
   }

   public static void log(Level level, String message) {
      plugin.getLogger().log(level, message);
   }

   public File getJarFile() {
      return this.getFile();
   }

   public boolean hasEconomy() {
      return this.economy != null && this.economy.isValid();
   }
}
